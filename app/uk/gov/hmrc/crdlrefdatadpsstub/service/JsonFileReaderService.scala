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

import play.api.libs.json.{JsValue, Json}

import javax.inject.Inject
import scala.util.{Failure, Success, Try}

class JsonFileReaderService @Inject() (fileReader: FileReader) {

  def fetchJsonResponse(path: String): JsValue = {
    Try(Json.parse(fileReader.read(path))) match {
      case Success(jsonCodeList) => jsonCodeList
      case Failure(exception) =>
        throw new RuntimeException(s"Failed to read or parse JSON file at $path: $exception")
    }
  }

}
