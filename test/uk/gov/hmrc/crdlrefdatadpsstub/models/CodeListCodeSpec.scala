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

package uk.gov.hmrc.crdlrefdatadpsstub.models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.crdlrefdatadpsstub.models.CodeListCode.{BC08, BC36}

class CodeListCodeSpec extends AnyWordSpec with Matchers {
  "CodeListCode" should {
    "have a query sting bindable in scope" in {

      assertCompiles("""
          |import play.api.mvc.QueryStringBindable
          |summon[QueryStringBindable[CodeListCode]]
          |""".stripMargin)
    }

    "bind known codes from a query string" in {
      CodeListCode.bindable.bind(
        "code_list_code",
        Map("code_list_code" -> Seq("BC08"))
      ) mustBe Some(Right(BC08))
    }

    "bind unknown codes from a query string" in {
      CodeListCode.bindable.bind(
        "code_list_code",
        Map("code_list_code" -> Seq("Unknown"))
      ) mustBe Some(
        Left("Cannot parse parameter code_list_code as CodeListCode: Unknown code list code")
      )
    }

    "bind nothing when the code list code is missing in the query string" in {
      CodeListCode.bindable.bind("code_list_code", Map.empty) mustBe None
      CodeListCode.bindable.bind("code_list_code", Map("code_list_code" -> Seq.empty)) mustBe None
    }

    "unbind code list codes from a query string" in {
      CodeListCode.bindable.unbind("code_list_code", BC08) mustBe "code_list_code=BC08"
      CodeListCode.bindable.unbind("code_list_code", BC36) mustBe "code_list_code=BC36"
    }

  }

}
