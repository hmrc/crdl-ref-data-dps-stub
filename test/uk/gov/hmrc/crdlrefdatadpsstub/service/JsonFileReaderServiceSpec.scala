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

import org.mockito.Mockito.{when, reset}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.libs.json.Json
import uk.gov.hmrc.crdlrefdatadpsstub.models.CodeListCode.BC08

import java.io.FileNotFoundException
import org.scalatest.BeforeAndAfterEach

class JsonFileReaderServiceSpec extends AnyWordSpec with Matchers with BeforeAndAfterEach {
  val codeListCode      = BC08
  val codeListPath      = "resources/codeList/BC08/BC08_page1.json"
  val emptyCodeListPath = "resources/codeList/EmptyPage.json"
  val customsOfficePath = "resources/col/COL_page1.json"
  val emptyOfficePath   = "resources/col/EmptyPage.json"
  val validJson         = """{ "countrycode": "United Kingdom"}"""
  val mockFileReader    = mock[FileReader]

  override def beforeEach() = {
    reset(mockFileReader)
  }

  "JsonFileReaderServiceSpec.fetchCodeListJson" should {
    "read and parse JSON" in {
      when(mockFileReader.read(codeListPath)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchCodeListJson(codeListCode, startIndex = 0)
      result shouldBe Json.parse(validJson)
    }

    "calculate the correct page index" in {
      val page3Path = "resources/codeList/BC08/BC08_page3.json"
      when(mockFileReader.read(page3Path)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchCodeListJson(codeListCode, startIndex = 20)
      result shouldBe Json.parse(validJson)
    }

    "throw exception when JSON file is missing for a valid codeListCode" in {
      when(mockFileReader.read(codeListPath))
        .thenThrow(new RuntimeException("Simulated missing file"))
      val service = new JsonFileReaderService(mockFileReader)
      an[RuntimeException] mustBe thrownBy(
        service.fetchCodeListJson(codeListCode, startIndex = 0)
      )
    }

    "read and parse a paginated JSON" in {
      when(mockFileReader.read(codeListPath)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchCodeListJson(codeListCode, startIndex = 0)
      result shouldBe Json.parse(validJson)
    }

    "throw exception when a paginated JSON file is missing for a valid codeListCode" in {
      when(mockFileReader.read(codeListPath))
        .thenThrow(new RuntimeException("Simulated missing file"))
      val service = new JsonFileReaderService(mockFileReader)
      an[RuntimeException] mustBe thrownBy(
        service.fetchCodeListJson(codeListCode, startIndex = 0)
      )
    }

    "read and parse an Empty Page Json when there is no data available" in {
      when(mockFileReader.read(codeListPath))
        .thenThrow(new FileNotFoundException("Simulated missing file"))
      when(mockFileReader.read(emptyCodeListPath)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchCodeListJson(codeListCode, startIndex = 0)
      result shouldBe Json.parse(validJson)
    }
  }

  "JsonFileReaderServiceSpec.fetchCustomsOfficeJson" should {
    "read and parse JSON" in {
      when(mockFileReader.read(customsOfficePath)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchCustomsOfficeJson(startIndex = 0)
      result shouldBe Json.parse(validJson)
    }

    "calculate the correct page index" in {
      val page3Path = "resources/col/COL_page3.json"
      when(mockFileReader.read(page3Path)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchCustomsOfficeJson(startIndex = 20)
      result shouldBe Json.parse(validJson)
    }

    "throw exception when JSON file is missing for a valid codeListCode" in {
      when(mockFileReader.read(customsOfficePath))
        .thenThrow(new RuntimeException("Simulated missing file"))
      val service = new JsonFileReaderService(mockFileReader)
      an[RuntimeException] mustBe thrownBy(
        service.fetchCustomsOfficeJson(startIndex = 0)
      )
    }

    "read and parse a paginated JSON" in {
      when(mockFileReader.read(customsOfficePath)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchCustomsOfficeJson(startIndex = 0)
      result shouldBe Json.parse(validJson)
    }

    "throw exception when a paginated JSON file is missing for a valid codeListCode" in {
      when(mockFileReader.read(customsOfficePath))
        .thenThrow(new RuntimeException("Simulated missing file"))
      val service = new JsonFileReaderService(mockFileReader)
      an[RuntimeException] mustBe thrownBy(
        service.fetchCustomsOfficeJson(startIndex = 0)
      )
    }

    "read and parse an Empty Page Json when there is no data available" in {
      when(mockFileReader.read(customsOfficePath))
        .thenThrow(new FileNotFoundException("Simulated missing file"))
      when(mockFileReader.read(emptyOfficePath)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchCustomsOfficeJson(startIndex = 0)
      result shouldBe Json.parse(validJson)
    }
  }
}
