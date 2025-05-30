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
import uk.gov.hmrc.crdlrefdatadpsstub.models.CodeListCode.BC08

import java.io.FileNotFoundException

class JsonFileReaderServiceSpec extends AnyWordSpec with Matchers {
  val codeListCode  = BC08
  val path          = s"conf/resources/$codeListCode.json"
  val paginatedPath = s"conf/resources/paginated/${codeListCode}_page1.json"
  "JsonFileReaderServiceSpec" should {
    "read and parse JSON" in {
      val validJson      = """{ "countrycode": "United Kingdom"}"""
      val mockFileReader = mock[FileReader]
      when(mockFileReader.read(path)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchJsonResponse(codeListCode)
      result shouldBe Json.parse(validJson)
    }

    "throw exception when JSON file is missing for a valid codeListCode" in {
      val mockFileReader = mock[FileReader]
      when(mockFileReader.read(path)).thenThrow(new RuntimeException("Simulated missing file"))
      val service = new JsonFileReaderService(mockFileReader)
      an[RuntimeException] mustBe thrownBy(
        service.fetchJsonResponse(codeListCode)
      )
    }

    "read and parse a paginated JSON" in {
      val validJson      = """{ "countrycode": "United Kingdom"}"""
      val mockFileReader = mock[FileReader]
      when(mockFileReader.read(paginatedPath)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchPaginatedJsonResponse(codeListCode, 10)
      result shouldBe Json.parse(validJson)
    }

    "throw exception when a paginated JSON file is missing for a valid codeListCode" in {
      val mockFileReader = mock[FileReader]
      when(mockFileReader.read(paginatedPath))
        .thenThrow(new RuntimeException("Simulated missing file"))
      val service = new JsonFileReaderService(mockFileReader)
      an[RuntimeException] mustBe thrownBy(
        service.fetchPaginatedJsonResponse(codeListCode, 10)
      )
    }

    "read and parse an Empty Page Json when there is no data available" in {
      val validJson      = """{ "countrycode": "United Kingdom"}"""
      val emptyPagePath  = "conf/resources/paginated/EmptyPage.json"
      val mockFileReader = mock[FileReader]
      when(mockFileReader.read(paginatedPath))
        .thenThrow(new FileNotFoundException("Simulated missing file"))
      when(mockFileReader.read(emptyPagePath)).thenReturn(validJson)
      val service = new JsonFileReaderService(mockFileReader)
      val result  = service.fetchPaginatedJsonResponse(codeListCode, 10)
      result shouldBe Json.parse(validJson)
    }
  }
}
