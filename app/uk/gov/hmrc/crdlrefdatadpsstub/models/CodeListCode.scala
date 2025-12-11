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

import play.api.mvc.QueryStringBindable

enum CodeListCode(val codeListCode: String) {
  case BC01  extends CodeListCode("BC01")
  case BC03  extends CodeListCode("BC03")
  case BC08  extends CodeListCode("BC08")
  case BC09  extends CodeListCode("BC09")
  case BC11  extends CodeListCode("BC11")
  case BC12  extends CodeListCode("BC12")
  case BC15  extends CodeListCode("BC15")
  case BC17  extends CodeListCode("BC17")
  case BC22  extends CodeListCode("BC22")
  case BC26  extends CodeListCode("BC26")
  case BC34  extends CodeListCode("BC34")
  case BC35  extends CodeListCode("BC35")
  case BC36  extends CodeListCode("BC36")
  case BC37  extends CodeListCode("BC37")
  case BC40  extends CodeListCode("BC40")
  case BC41  extends CodeListCode("BC41")
  case BC43  extends CodeListCode("BC43")
  case BC46  extends CodeListCode("BC46")
  case BC51  extends CodeListCode("BC51")
  case BC52  extends CodeListCode("BC52")
  case BC57  extends CodeListCode("BC57")
  case BC58  extends CodeListCode("BC58")
  case BC66  extends CodeListCode("BC66")
  case BC67  extends CodeListCode("BC67")
  case BC106 extends CodeListCode("BC106")
  case BC107 extends CodeListCode("BC107")
  case BC108 extends CodeListCode("BC108")
  case BC109 extends CodeListCode("BC109")
  case CL231 extends CodeListCode("CL231")
  case CL239 extends CodeListCode("CL239")
  case CL380 extends CodeListCode("CL380")
  case E200  extends CodeListCode("E200")
}

object CodeListCode {
  private def fromString(codeListCode: String): Option[CodeListCode] =
    CodeListCode.values.find(_.codeListCode == codeListCode)

  given bindable: QueryStringBindable.Parsing[CodeListCode] =
    new QueryStringBindable.Parsing[CodeListCode](
      value =>
        fromString(value).getOrElse(throw new IllegalArgumentException("Unknown code list code")),
      _.codeListCode,
      (code, e) => s"Cannot parse parameter $code as CodeListCode: ${e.getMessage}"
    )
}
