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

package uk.gov.hmrc.crdlrefdatadpsstub.controllers

import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.crdlrefdatadpsstub.models.CodeListCode.BC08
import uk.gov.hmrc.crdlrefdatadpsstub.service.{FileReader, JsonFileReaderService}

class CodeListsControllerSpec extends AnyWordSpec with Matchers {

  private val fakeRequest = FakeRequest("GET", "/")
  val mockFileReader      = mock[FileReader]
  val validJson           = """{ "countrycode": "United Kingdom"}"""
  when(mockFileReader.read("conf/resources/paginated/codeList/BC08_page1.json")).thenReturn(validJson)
  val jsonFileReaderService = new JsonFileReaderService(mockFileReader)
  private val controller =
    new CodeListsController(jsonFileReaderService, Helpers.stubControllerComponents())

  "GET /" should {
    "return 200 for a valid codeListCode" in {
      val result = controller.getCodeListData(Some(BC08), None, None, None, None)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 for a valid codeListCode snapshot request" in {
      when(mockFileReader.read("conf/resources/paginated/codeList/BC08_page1.json"))
        .thenReturn(validJson)
      val jsonFileReaderService = new JsonFileReaderService(mockFileReader)
      val controller =
        new CodeListsController(jsonFileReaderService, Helpers.stubControllerComponents())
      val result = controller.getCodeListData(
        Some(BC08),
        Some("2025-05-28T00:00:00Z"),
        Some(0),
        None,
        None
      )(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 400 on missing codeListCode parameter" in {
      val result = controller.getCodeListData(None, None, None, None, None)(fakeRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 400 on having codeListCode but missing startIndex parameter" in {
      val result =
        controller.getCodeListData(Some(BC08), Some("TestDate"), None, None, None)(fakeRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}
