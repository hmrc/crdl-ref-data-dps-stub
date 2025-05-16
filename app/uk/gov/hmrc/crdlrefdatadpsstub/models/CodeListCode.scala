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

enum CodeListCode(val codeListCode: String) {
  case BC08     extends CodeListCode("BC08")
  case BC36     extends CodeListCode("BC36")
  case TESTONLY extends CodeListCode("TESTONLY")
}

object CodeListCode {
  def fromString(codeListCode: String): Option[CodeListCode] =
    CodeListCode.values.find(_.codeListCode == codeListCode)
}
