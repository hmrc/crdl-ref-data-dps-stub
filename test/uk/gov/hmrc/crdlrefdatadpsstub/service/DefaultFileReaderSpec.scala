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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import java.io.FileNotFoundException
import play.api.libs.json.JsObject

class DefaultFileReaderSpec extends AnyWordSpec with Matchers {
  "DefaultFileReader" should {
    "read the contents of a resource file" in {
      val reader = new DefaultFileReader
      val result = reader.read("resources/codeList/CL239_page1.json")
      Json.parse(result) shouldBe a[JsObject]
    }

    "throw a FileNotFoundException when the file does not exist" in {
      val reader = new DefaultFileReader
      assertThrows[FileNotFoundException] {
        reader.read("resources/codeList/CL289_page1.json")
      }
    }
  }
}
