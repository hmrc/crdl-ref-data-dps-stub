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
import play.api.test.FakeRequest
import play.api.test.Helpers
import play.api.test.Helpers.*
import uk.gov.hmrc.crdlrefdatadpsstub.service.FileReader
import uk.gov.hmrc.crdlrefdatadpsstub.service.JsonFileReaderService

class CustomsOfficeListControllerSpec extends AnyWordSpec with Matchers {
  private val fakeRequest = FakeRequest("GET", "/")
  val mockFileReader      = mock[FileReader]
  val validJson           = """{ "customsOffice": "Newcastle"}"""
  when(mockFileReader.read("resources/col/COL_page1.json")).thenReturn(validJson)
  val jsonFileReaderService = new JsonFileReaderService(mockFileReader)
  private val controller =
    new CustomsOfficeListController(jsonFileReaderService, Helpers.stubControllerComponents())

  "GET /" should {
    "return 200 for a valid request" in {
      val result = controller.getCustomsOfficeList(None)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 for a valid request with startIndex" in {
      val jsonFileReaderService = new JsonFileReaderService(mockFileReader)
      val controller =
        new CustomsOfficeListController(jsonFileReaderService, Helpers.stubControllerComponents())
      val result = controller.getCustomsOfficeList(
        Some(0)
      )(fakeRequest)
      status(result) shouldBe Status.OK
    }

  }

}
