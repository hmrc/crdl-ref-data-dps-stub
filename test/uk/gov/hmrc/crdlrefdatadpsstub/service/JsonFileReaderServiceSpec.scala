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

import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.libs.json.Json

class JsonFileReaderServiceSpec extends AnyWordSpec with Matchers {
  val path = "conf/resources/BC08.json"
  "JsonFileReaderServiceSpec" should {
    "read and parse JSON" in {
      val validJson      = """{ "countrycode": "United Kingdom"}"""
      val mockFileReader = mock[FileReader]
      when(mockFileReader.read(path)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchJsonResponse(path)
      result shouldBe Json.parse(validJson)
    }

    "throw exception when JSON file is missing for a valid codeListCode" in {
      val mockFileReader = mock[FileReader]
      when(mockFileReader.read(path)).thenThrow(new RuntimeException("Simulated missing file"))
      val service = new JsonFileReaderService(mockFileReader)
      an[RuntimeException] mustBe thrownBy(
        service.fetchJsonResponse(path)
      )
    }
  }
}
