/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.crdlrefdatadpsstub.script

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import fs2.data.xml.*
import fs2.data.xml.scalaXml.*
import fs2.data.xml.xpath.literals.*
import fs2.data.xml.xpath.{XPath, filter}
import fs2.io.file.{Files, Path}
import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.syntax.*
import play.api.Environment

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.xml.{Elem, NodeSeq}

@Singleton
class RefDataToJsonConverter @Inject() (environment: Environment) {

  private val inputDateFormat  = DateTimeFormatter.ISO_LOCAL_DATE
  private val outputDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
  private val rdEntriesXPath   = xpath"//ns3:RDEntry"
  private val entriesPerPage   = 10
  private val recordMultiplier = 1
  private val maxPages         = 50

  private val basePath: String = environment.rootPath.getAbsolutePath

  case class DataItem(
    dataitem_name: String,
    dataitem_value: String
  )

  case class Language(
    lang_code: String,
    lang_desc: String
  )

  case class RDEntry(
    dataitem: List[DataItem],
    language: List[Language]
  )

  case class CodeListElement(
    code_list_code: String,
    code_list_name: String,
    rdentry: List[RDEntry],
    languagecode: String,
    snapshotversion: Int
  )

  case class Link(
    rel: String,
    href: String,
    title: Option[String] = None
  )

  case class RootJson(
    name: String,
    elements: List[CodeListElement],
    links: List[Link]
  )

  private case class FileMetadata(
    phase: String,
    domain: String,
    codeListName: String
  )

  implicit val dataItemEncoder: Encoder[DataItem]               = deriveEncoder[DataItem]
  implicit val languageEncoder: Encoder[Language]               = deriveEncoder[Language]
  implicit val rdEntryEncoder: Encoder[RDEntry]                 = deriveEncoder[RDEntry]
  implicit val codeListElementEncoder: Encoder[CodeListElement] = deriveEncoder[CodeListElement]
  implicit val linkEncoder: Encoder[Link] = (link: Link) =>
    Json
      .obj(
        "rel"  -> link.rel.asJson,
        "href" -> link.href.asJson
      )
      .deepMerge(
        link.title.map(t => Json.obj("title" -> t.asJson)).getOrElse(Json.obj())
      )
  implicit val rootJsonEncoder: Encoder[RootJson] = deriveEncoder[RootJson]

  def convertXmlToJson(): Future[String] = {
    val io = for {
      files     <- IO.pure(Files.forIO)
      inputPath <- IO.pure(Path(s"$basePath/conf/resources/input"))

      inputExists <- files.exists(inputPath)
      result <-
        if (!inputExists) {
          IO.pure("No input files to process")
        } else {
          for {
            folders <- files.list(inputPath).compile.toList
            folderNames <- folders
              .filterA(p => files.isDirectory(p))
              .map(_.map(_.fileName.toString))
            result <-
              if (folderNames.isEmpty) {
                IO.pure("No input files to process")
              } else {
                folderNames
                  .traverse(codeList => processFolder(codeList).map(count => (codeList, count)))
                  .map(results => buildSummary(results))
              }
          } yield result
        }
    } yield result

    io.unsafeToFuture()
  }

  private def buildSummary(results: List[(String, Int)]): String = {
    val summaryLines = results.map { case (codeList, count) =>
      if (count == 0) {
        s"$codeList -> ERROR (0 files generated)"
      } else {
        s"$codeList -> $count files generated"
      }
    }

    val header    = "RefData Conversion Summary:"
    val separator = "=" * 50

    (header :: separator :: summaryLines ::: List(separator)).mkString("\n")
  }

  private def processFolder(codeListCode: String): IO[Int] = {
    val files      = Files.forIO
    val inputPath  = Path(s"$basePath/conf/resources/input/$codeListCode")
    val outputPath = Path(s"$basePath/conf/resources/codeList/$codeListCode")

    for {
      outputExists <- files.exists(outputPath)
      _ <-
        if (outputExists) {
          files.deleteRecursively(outputPath)
        } else IO.unit
      _ <- files.createDirectories(outputPath)

      xmlFiles <- files
        .list(inputPath)
        .filter(p => p.fileName.toString.endsWith(".xml"))
        .compile
        .toList

      fileCount <- xmlFiles.headOption match {
        case Some(xmlFile) =>
          val xmlFileName = xmlFile.fileName.toString
          val metadata    = extractFileMetadata(xmlFileName)

          for {
            entries <- readAndParseXml(xmlFile, metadata)
            multipliedEntries = multiplyEntries(entries, recordMultiplier)
            count <- writePagedJsonFiles(
              codeListCode,
              metadata.codeListName,
              multipliedEntries,
              outputPath
            )
          } yield count

        case None =>
          IO.pure(0)
      }
    } yield fileCount
  }

  private def writePagedJsonFiles(
    codeListCode: String,
    codeListName: String,
    entries: List[RDEntry],
    outputPath: Path
  ): IO[Int] = {
    val files = Files.forIO

    val maxEntries     = maxPages * entriesPerPage
    val limitedEntries = entries.take(maxEntries)

    val totalPages = Math.min(
      Math.ceil(entries.size.toDouble / entriesPerPage).toInt,
      maxPages
    )

    if (limitedEntries.isEmpty) {
      IO.pure(0)
    } else {
      limitedEntries
        .grouped(entriesPerPage)
        .toList
        .zipWithIndex
        .traverse { case (pageEntries, idx) =>
          val pageNum = idx + 1

          val links = buildLinks(codeListCode, pageNum, totalPages)

          val json = RootJson(
            name = "iv_crdl_reference_data",
            elements = List(
              CodeListElement(
                code_list_code = codeListCode,
                code_list_name = codeListName,
                rdentry = pageEntries,
                languagecode = "EN",
                snapshotversion = 2
              )
            ),
            links = links
          )

          val jsonString = json.asJson.spaces2
          val outputFile = outputPath / s"${codeListCode}_page$pageNum.json"

          fs2.Stream
            .emit(jsonString)
            .through(fs2.text.utf8.encode)
            .through(files.writeAll(outputFile))
            .compile
            .drain
        }
        .map(_ => totalPages)
    }
  }

  private def extractFileMetadata(xmlFileName: String): FileMetadata = {
    val pattern = """RD_([^-]+)-P(\d+)_(.+)\.xml""".r

    xmlFileName match {
      case pattern(phase, domain, codeListName) =>
        FileMetadata(phase, domain, codeListName)
      case _ => FileMetadata("UNKNOWN", "0", xmlFileName.replace(".xml", ""))
    }
  }

  private def readAndParseXml(xmlPath: Path, metadata: FileMetadata): IO[List[RDEntry]] = {
    val files = Files.forIO

    val xmlEvents = files
      .readAll(xmlPath)
      .through(fs2.text.utf8.decode)
      .through(events[IO, String]())

    xmlEvents
      .through(filter.dom[Elem](rdEntriesXPath))
      .mapFilter(elem => parseRDEntry(elem, metadata))
      .compile
      .toList
  }

  private def multiplyEntries(entries: List[RDEntry], times: Int): List[RDEntry] = {
    if (times <= 1) entries
    else entries.flatMap(entry => List.fill(times)(entry))
  }

  private def parseDate(dateStr: String): String = {
    try {
      val date = LocalDate.parse(dateStr, inputDateFormat)
      date.format(outputDateFormat)
    } catch {
      case _: Exception => dateStr
    }
  }

  private def parseRDEntry(elem: Elem, metadata: FileMetadata): Option[RDEntry] = {
    val dataItemElems = elem \\ "dataItem"

    val dataItemsFromXml = dataItemElems.flatMap { dataItemElem =>
      for {
        name <- dataItemElem.attribute("name").map(_.text)
        value = dataItemElem.text.trim
        if value.nonEmpty
      } yield DataItem(name, value)
    }.toList

    if (dataItemsFromXml.isEmpty) {
      None
    } else {
      val state         = (elem \ "RDEntryStatus" \ "state").headOption.map(_.text)
      val activeFromRaw = (elem \ "RDEntryStatus" \ "activeFrom").headOption.map(_.text)
      val activeFrom    = activeFromRaw.map(parseDate)

      val statusItems = List(
        state.map(s => DataItem("RDEntryStatus_state", s)),
        activeFrom.map(af => DataItem("RDEntryStatus_activeFrom", af))
      ).flatten

      val metadataItems = List(
        DataItem("Phase", metadata.phase),
        DataItem("Domain", metadata.domain)
      )

      val allDataItems = dataItemsFromXml ++ statusItems ++ metadataItems

      val enDesc = (elem \ "LsdList" \ "description")
        .find(node => node.attribute("lang").exists(_.text == "en"))
        .map(_.text.trim)

      val languages = enDesc.map(desc => List(Language("en", desc))).getOrElse(List.empty)

      Some(RDEntry(allDataItems, languages))
    }
  }

  private def buildLinks(
    codeListCode: String,
    currentPage: Int,
    totalPages: Int
  ): List[Link] = {
    val baseUrl =
      s"https://vdp.nonprod.denodo.hip.ns2n.corp.hmrc.gov.uk:9443/server/central_reference_data_library/ws_iv_crdl_reference_data/views/iv_crdl_reference_data"

    val selfLink = Link(
      rel = "self",
      href =
        s"$baseUrl?%24orderby=snapshotversion+ASC&code_list_code=$codeListCode&%24count=$entriesPerPage"
    )

    val prevLink = if (currentPage > 1) {
      val prevStartIndex = (currentPage - 2) * entriesPerPage
      Some(
        Link(
          rel = "prev",
          href =
            s"?%24start_index=$prevStartIndex&%24orderby=snapshotversion+ASC&code_list_code=$codeListCode&%24count=$entriesPerPage",
          title = Some("Previous interval")
        )
      )
    } else None

    val nextLink = if (currentPage < totalPages) {
      val nextStartIndex = currentPage * entriesPerPage
      Some(
        Link(
          rel = "next",
          href =
            s"?%24start_index=$nextStartIndex&%24orderby=snapshotversion+ASC&code_list_code=$codeListCode&%24count=$entriesPerPage",
          title = Some("Next interval")
        )
      )
    } else None

    List(Some(selfLink), prevLink, nextLink).flatten
  }
}
