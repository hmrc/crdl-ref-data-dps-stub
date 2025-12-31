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

package uk.gov.hmrc.crdlrefdatadpsstub.controllers.test

import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.crdlrefdatadpsstub.script.RefDataToJsonConverter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TestOnlyControllerSpec extends AnyWordSpec with Matchers with ScalaFutures {
  private val fakeRequest                   = FakeRequest("GET", "/")
  val mockConverter: RefDataToJsonConverter = mock[RefDataToJsonConverter]
  private val controller =
    new TestOnlyController(Helpers.stubControllerComponents(), mockConverter)

  "POST /generate-code-list-data" should {
    "return 200 for a successful conversion" in {
      when(mockConverter.convertXmlToJson())
        .thenReturn(Future.successful("RefData is converted to stubs successfully"))

      val result = controller.generateCodeListData(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe "RefData is converted to stubs successfully"
    }

    "return 500 when conversion fails" in {
      when(mockConverter.convertXmlToJson())
        .thenReturn(Future.failed(new RuntimeException("Conversion failed")))

      val result = controller.generateCodeListData(fakeRequest)

      whenReady(result.failed) { exception =>
        exception shouldBe a[RuntimeException]
        exception.getMessage shouldBe "Conversion failed"
      }
    }

  }
}
