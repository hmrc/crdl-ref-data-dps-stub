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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.libs.json.JsValue
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}

class CodeListsControllerSpec extends AnyWordSpec with Matchers {

  private val fakeRequest = FakeRequest("GET", "/")
  private val controller  = new CodeListsController(Helpers.stubControllerComponents())

  "GET /" should {
    "return 200 for a valid codeListCode" in {
      val result = controller.getCodeListData(Some("BC08"))(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 400 on an invalid codeListCode" in {
      val result = controller.getCodeListData(Some("Test"))(fakeRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 400 on missing codeListCode parameter" in {
      val result = controller.getCodeListData(None)(fakeRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "throw an exception when JSON file is missing for a valid codeListCode" in {
      class FailingCodeListsController extends CodeListsController(stubControllerComponents()) {
        override def fetchJsonResponse(path: String): JsValue = {
          throw new RuntimeException(s"Simulated missing file: $path")
        }
      }
      val failingController = new FailingCodeListsController

      an[RuntimeException] mustBe thrownBy(
        failingController.getCodeListData(Some("BC08"))(fakeRequest)
      )
    }

    "throw and exception when JSON file is missing for a valid TESTONLY codeListCode" in {
      an[RuntimeException] mustBe thrownBy(
        controller.getCodeListData(Some("TESTONLY"))(fakeRequest)
      )
    }
  }
}
