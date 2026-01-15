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
  case CL008 extends CodeListCode("CL008")
  case CL009 extends CodeListCode("CL009")
  case CL010 extends CodeListCode("CL010")
  case CL016 extends CodeListCode("CL016")
  case CL017 extends CodeListCode("CL017")
  case CL019 extends CodeListCode("CL019")
  case CL030 extends CodeListCode("CL030")
  case CL038 extends CodeListCode("CL038")
  case CL042 extends CodeListCode("CL042")
  case CL048 extends CodeListCode("CL048")
  case CL056 extends CodeListCode("CL056")
  case CL076 extends CodeListCode("CL076")
  case CL094 extends CodeListCode("CL094")
  case CL101 extends CodeListCode("CL101")
  case CL112 extends CodeListCode("CL112")
  case CL116 extends CodeListCode("CL116")
  case CL147 extends CodeListCode("CL147")
  case CL152 extends CodeListCode("CL152")
  case CL165 extends CodeListCode("CL165")
  case CL167 extends CodeListCode("CL167")
  case CL178 extends CodeListCode("CL178")
  case CL180 extends CodeListCode("CL180")
  case CL181 extends CodeListCode("CL181")
  case CL182 extends CodeListCode("CL182")
  case CL190 extends CodeListCode("CL190")
  case CL198 extends CodeListCode("CL198")
  case CL199 extends CodeListCode("CL199")
  case CL213 extends CodeListCode("CL213")
  case CL214 extends CodeListCode("CL214")
  case CL215 extends CodeListCode("CL215")
  case CL217 extends CodeListCode("CL217")
  case CL218 extends CodeListCode("CL218")
  case CL219 extends CodeListCode("CL219")
  case CL226 extends CodeListCode("CL226")
  case CL228 extends CodeListCode("CL228")
  case CL229 extends CodeListCode("CL229")
  case CL230 extends CodeListCode("CL230")
  case CL231 extends CodeListCode("CL231")
  case CL232 extends CodeListCode("CL232")
  case CL234 extends CodeListCode("CL234")
  case CL235 extends CodeListCode("CL235")
  case CL236 extends CodeListCode("CL236")
  case CL239 extends CodeListCode("CL239")
  case CL244 extends CodeListCode("CL244")
  case CL248 extends CodeListCode("CL248")
  case CL251 extends CodeListCode("CL251")
  case CL252 extends CodeListCode("CL252")
  case CL286 extends CodeListCode("CL286")
  case CL289 extends CodeListCode("CL289")
  case CL296 extends CodeListCode("CL296")
  case CL326 extends CodeListCode("CL326")
  case CL347 extends CodeListCode("CL347")
  case CL349 extends CodeListCode("CL349")
  case CL380 extends CodeListCode("CL380")
  case CL437 extends CodeListCode("CL437")
  case CL505 extends CodeListCode("CL505")
  case CL560 extends CodeListCode("CL560")
  case CL561 extends CodeListCode("CL561")
  case CL580 extends CodeListCode("CL580")
  case CL581 extends CodeListCode("CL581")
  case CL704 extends CodeListCode("CL704")
  case CL716 extends CodeListCode("CL716")
  case CL750 extends CodeListCode("CL750")
  case CL752 extends CodeListCode("CL752")
  case CL754 extends CodeListCode("CL754")
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
