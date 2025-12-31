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

package uk.gov.hmrc.crdlrefdatadpsstub.service

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import uk.gov.hmrc.crdlrefdatadpsstub.models.CodeListCode

import java.io.FileNotFoundException
import javax.inject.Inject
import scala.util.control.NonFatal

class JsonFileReaderService @Inject() (fileReader: FileReader) {
  def pageIndexFor(startIndex: Int) = (startIndex / 10) + 1

  def fetchCodeListJson(
    codeListCode: CodeListCode,
    startIndex: Int
  ) = fetchResponseJson(
    s"resources/codeList/${codeListCode.codeListCode}/${codeListCode.codeListCode}_page${pageIndexFor(startIndex)}.json",
    "resources/codeList/EmptyPage.json"
  )

  def fetchCustomsOfficeJson(
    startIndex: Int
  ) = fetchResponseJson(
    s"resources/col/COL_page${pageIndexFor(startIndex)}.json",
    "resources/col/EmptyPage.json"
  )

  def fetchResponseJson(
    pagePath: String,
    emptyPagePath: String
  ): JsValue = {
    try Json.parse(fileReader.read(pagePath))
    catch {
      case notFoundException: FileNotFoundException =>
        Json.parse(fileReader.read(emptyPagePath))
      case NonFatal(exception) =>
        throw new RuntimeException(
          s"Failed to read or parse JSON file at $pagePath: $exception"
        )
    }
  }
}
