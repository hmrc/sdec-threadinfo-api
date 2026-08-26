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
import play.api.Logging
import play.api.libs.json.*
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.sdecthreadinfoapi.controllers.actions.IdentifierAction
import uk.gov.hmrc.sdecthreadinfoapi.exceptions.{
  InvalidThreadReferenceException,
  ThreadReferenceNotFoundException
}
import uk.gov.hmrc.sdecthreadinfoapi.model.ThreadReference
import uk.gov.hmrc.sdecthreadinfoapi.service.ThreadReferenceServiceAlgebra

import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class ThreadReferenceController @Inject() (
    val authConnector: AuthConnector,
    identity: IdentifierAction,
    cc: ControllerComponents,
    threadReferenceService: ThreadReferenceServiceAlgebra
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with AuthorisedFunctions
    with Logging {

  def getThreadReference(threadId: String): Action[AnyContent] = {
    logger.info(s"getThreadReference: Getting ThreadInformation for $threadId")
    identity.async { implicit request =>
      request.headers.headers.foreach { case (name, value) =>
        logger.warn(s"Header: $name = $value")
      }
      logger.info(s"User ID is [${request.userId}]")
      threadReferenceService
        .getThreadInfoByThreadId(threadId)
        .map(tr => Ok(Json.toJson(tr)))
        .recover {
          case e: InvalidThreadReferenceException =>
            logger.warn(s"Thread Reference ID $threadId was invalid")
            BadRequest(Json.obj("message" -> e.getMessage))

          case e: ThreadReferenceNotFoundException =>
            logger.warn(s"Thread Reference ID $threadId was not found")
            NotFound(Json.obj("message" -> e.getMessage))
        }
    }
  }
}
