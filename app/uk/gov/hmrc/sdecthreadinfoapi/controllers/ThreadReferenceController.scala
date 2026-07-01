/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.sdecthreadinfoapi.controllers

import jakarta.inject.Inject
import play.api.libs.json.*
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.sdecthreadinfoapi.repo.ThreadReferenceRepository

import javax.inject.Singleton
import scala.concurrent.Future

@Singleton
class ThreadReferenceController @Inject() (
    cc: ControllerComponents,
    threadReferenceRepository: ThreadReferenceRepository
) extends BackendController(cc) {

  def getThreadReference(threadId: String): Action[AnyContent] = {
    Action.async { implicit request =>
      threadReferenceRepository.getThreadReference(threadId) match {
        case Some(threadReference) =>
          Future.successful(Ok(Json.toJson(threadReference)))

        case None =>
          Future.successful(
            NotFound(
              Json.obj("message" -> s"Thread reference not found for id: $threadId")
            )
          )
      }
    }
  }
}
