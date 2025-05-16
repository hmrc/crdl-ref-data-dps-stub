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

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.crdlrefdatadpsstub.models.CodeListCode

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Try, Success, Failure}

@Singleton()
class CodeListsController @Inject() (cc: ControllerComponents) extends BackendController(cc) {

  def getCodeListData(codeListCode: Option[String]): Action[AnyContent] = Action.async {
    implicit request =>
      codeListCode match {
        case Some(codeListCode) =>
          CodeListCode.fromString(codeListCode) match {
            case Some(codeListCode) =>
              val filePath = s"conf/resources/$codeListCode.json"
              Future.successful(Ok(fetchJsonResponse(filePath)))
            case None => Future.successful(BadRequest(s"Invalid code_list_code $codeListCode"))
          }
        case None => Future.successful(BadRequest(s"code_list_code parameter is missing"))
      }

  }

  private[controllers] def fetchJsonResponse(path: String): JsValue = {
    val tryJsonCodelist = Try {
      val sourcedFile = Source.fromFile(path)
      try {
        Json.parse(sourcedFile.mkString)
      } finally {
        sourcedFile.close()
      }
    }

    tryJsonCodelist match {
      case Success(jsonCodeList) => jsonCodeList
      case Failure(exception) =>
        throw new RuntimeException(s"Failed to read or parse JSON file at $path: $exception")
    }
  }

}
