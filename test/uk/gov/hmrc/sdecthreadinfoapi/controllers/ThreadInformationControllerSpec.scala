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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.sdecthreadinfoapi.model.ThreadInformation
import uk.gov.hmrc.sdecthreadinfoapi.service.ThreadInformationServiceAlgebra

import scala.concurrent.Future

class ThreadInformationControllerSpec extends AnyWordSpec with Matchers {

  private val threadIfo =
    ThreadInformation(threadId = 4, staffId = 5, email = s"staff-5@test.com")

  private val tis = new ThreadInformationServiceAlgebra {

    override def getThreadInfoByThreadId(threadId: Long): Future[ThreadInformation] =
      Future.successful(threadIfo)
  }

  private val controller =
    new ThreadInformationController(Helpers.stubControllerComponents(), tis)

  "GET /4" should:
      "return 200" in:
          val fakeRequest = FakeRequest("GET", "/4")
          val result      = controller.getThreadInformation(4)(fakeRequest)
          status(result) shouldBe Status.OK
          val json           = contentAsJson(result)
          val returnedObject = json.as[ThreadInformation]
          returnedObject shouldBe threadIfo
}
