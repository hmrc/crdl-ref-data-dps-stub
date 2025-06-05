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

import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.crdlrefdatadpsstub.models.CodeListCode
import uk.gov.hmrc.crdlrefdatadpsstub.service.JsonFileReaderService

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton()
class CodeListsController @Inject() (
  jsonFileReaderService: JsonFileReaderService,
  cc: ControllerComponents
) extends BackendController(cc) {

  def getCodeListData(
    codeListCode: Option[CodeListCode],
    lastUpdatedDate: Option[String],
    startIndex: Option[Int],
    count: Option[Int],
    orderBy: Option[String]
  ): Action[AnyContent] = Action.async { implicit request =>
    codeListCode match {
      case Some(codeListCode) =>
        (lastUpdatedDate, startIndex) match {
          case (None, None) =>
            Future.successful(Ok(jsonFileReaderService.fetchJsonResponse(Some(codeListCode))))
          case (Some(_), Some(startIndex)) =>
            Future.successful(
              Ok(jsonFileReaderService.fetchPaginatedJsonResponse(Some(codeListCode), startIndex))
            )
          case _ =>
            Future.successful(BadRequest("Missing or invalid parameters"))
        }
      case None => Future.successful(BadRequest("Missing or invalid code_list_code"))
    }

  }

}
