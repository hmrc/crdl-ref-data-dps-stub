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
  private val recordMultiplier = 1 // Change this to generate multiple set of test files

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

  case class FileMetadata(
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
                  .traverse_(processFolder)
                  .map(_ => "RefData is converted to stubs successfully")
              }
          } yield result
        }
    } yield result

    io.unsafeToFuture()
  }

  private def processFolder(codeListCode: String): IO[Unit] = {
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

      _ <- xmlFiles.headOption match {
        case Some(xmlFile) =>
          val xmlFileName = xmlFile.fileName.toString
          val metadata    = extractFileMetadata(xmlFileName)

          for {
            entries <- readAndParseXml(xmlFile, metadata)
            multipliedEntries = multiplyEntries(entries, recordMultiplier)
            _ <- writePagedJsonFiles(
              codeListCode,
              metadata.codeListName,
              multipliedEntries,
              outputPath
            )
          } yield ()

        case None =>
          IO.unit
      }
    } yield ()
  }

  private def extractFileMetadata(xmlFileName: String): FileMetadata = {
    // Pattern: RD_<Phase>-P<Domain>_<CodeListName>.xml
    // Example: RD_NCTS-P6_DeclarationType.xml
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
    for {
      dataItemElem <- (elem \\ "dataItem").headOption // This should work for local name
      name <- dataItemElem.attribute("name").map(_.text) // Use attribute() instead of \ "@name"
      code <- Some(dataItemElem.text.trim).filter(_.nonEmpty)
      state <- (elem \ "RDEntryStatus" \ "state").headOption.map(_.text)
      activeFromRaw <- (elem \ "RDEntryStatus" \ "activeFrom").headOption.map(_.text)
      activeFrom = parseDate(activeFromRaw)
      enDesc <- (elem \ "LsdList" \ "description")
        .find(node => node.attribute("lang").exists(_.text == "en"))
        .map(_.text.trim)
    } yield {
      val dataItems = List(
        DataItem(name, code),
        DataItem("RDEntryStatus_state", state),
        DataItem("RDEntryStatus_activeFrom", activeFrom),
        DataItem("Phase", metadata.phase),
        DataItem("Domain", metadata.domain)
      )

      val languages = List(Language("en", enDesc))

      RDEntry(dataItems, languages)
    }
  }

  private def writePagedJsonFiles(
    codeListCode: String,
    codeListName: String,
    entries: List[RDEntry],
    outputPath: Path
  ): IO[Unit] = {
    val files      = Files.forIO
    val totalPages = Math.ceil(entries.size.toDouble / entriesPerPage).toInt

    entries.grouped(entriesPerPage).toList.zipWithIndex.traverse_ { case (pageEntries, idx) =>
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
