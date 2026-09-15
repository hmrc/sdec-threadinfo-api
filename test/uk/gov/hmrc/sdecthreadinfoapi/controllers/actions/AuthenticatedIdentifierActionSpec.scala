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

package uk.gov.hmrc.sdecthreadinfoapi.controllers.actions

import org.mockito.ArgumentMatchers.{any, argThat, eq as eqTo}
import org.mockito.Mockito.{never, verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.*
import play.api.mvc.Results.*
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.sdecthreadinfoapi.model.requests.IdentifierRequest

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedIdentifierActionSpec extends AnyWordSpec with Matchers with ScalaFutures with MockitoSugar {

  implicit val ec: ExecutionContext =
    ExecutionContext.global

  private val authConnector =
    mock[AuthConnector]

  private val parser =
    mock[BodyParsers.Default]

  private val action =
    new AuthenticatedIdentifierAction(
      authConnector,
      parser
    )

  "AuthenticatedIdentifierAction" should {

    "call the block with the internal id when authorised" in {

      val request =
        FakeRequest("GET", "/test")

      when(
        authConnector.authorise(
          any[Predicate],
          eqTo(Retrievals.internalId)
        )(
          any[HeaderCarrier],
          any[ExecutionContext]
        )
      ).thenReturn(
        Future.successful(Some("internal-id-123"))
      )

      val block =
        mock[
          IdentifierRequest[AnyContentAsEmpty.type] => Future[Result]
        ]

      when(
        block(any[IdentifierRequest[AnyContentAsEmpty.type]])
      ).thenReturn(
        Future.successful(Ok)
      )

      val result =
        action
          .invokeBlock(request, block)
          .futureValue

      result shouldBe Ok

      verify(block).apply(
        argThat((identifierRequest: IdentifierRequest[AnyContentAsEmpty.type]) =>
          identifierRequest.userId == "internal-id-123" &&
            identifierRequest.request == request
        )
      )
    }

    "return Unauthorized when there is no active session" in {

      val request =
        FakeRequest("GET", "/test")

      when(
        authConnector.authorise(
          any[Predicate],
          eqTo(Retrievals.internalId)
        )(
          any[HeaderCarrier],
          any[ExecutionContext]
        )
      ).thenReturn(
        Future.failed(
          new NoActiveSession("No active session") {}
        )
      )

      val block =
        mock[
          IdentifierRequest[AnyContentAsEmpty.type] => Future[Result]
        ]

      val result =
        action
          .invokeBlock(request, block)
          .futureValue

      result shouldBe Unauthorized

      verify(block, never())
        .apply(any[IdentifierRequest[AnyContentAsEmpty.type]])
    }

    "return Unauthorized when the user is not authorised" in {

      val request =
        FakeRequest("GET", "/test")

      when(
        authConnector.authorise(
          any[Predicate],
          eqTo(Retrievals.internalId)
        )(
          any[HeaderCarrier],
          any[ExecutionContext]
        )
      ).thenReturn(
        Future.failed(
          new AuthorisationException("Not authorised") {}
        )
      )

      val block =
        mock[
          IdentifierRequest[AnyContentAsEmpty.type] => Future[Result]
        ]

      val result =
        action
          .invokeBlock(request, block)
          .futureValue

      result shouldBe Unauthorized

      verify(block, never())
        .apply(any[IdentifierRequest[AnyContentAsEmpty.type]])
    }

    "throw UnauthorizedException when no internal id is returned" in {

      val request =
        FakeRequest("GET", "/test")

      when(
        authConnector.authorise(
          any[Predicate],
          eqTo(Retrievals.internalId)
        )(
          any[HeaderCarrier],
          any[ExecutionContext]
        )
      ).thenReturn(
        Future.successful(None)
      )

      val block =
        mock[
          IdentifierRequest[AnyContentAsEmpty.type] => Future[Result]
        ]

      val exception =
        action
          .invokeBlock(request, block)
          .failed
          .futureValue

      exception shouldBe a[uk.gov.hmrc.http.UnauthorizedException]

      verify(block, never())
        .apply(any[IdentifierRequest[AnyContentAsEmpty.type]])
    }

    "return the result produced by the block" in {

      val request =
        FakeRequest("GET", "/test")

      when(
        authConnector.authorise(
          any[Predicate],
          eqTo(Retrievals.internalId)
        )(
          any[HeaderCarrier],
          any[ExecutionContext]
        )
      ).thenReturn(
        Future.successful(Some("internal-id-123"))
      )

      val block =
        mock[
          IdentifierRequest[AnyContentAsEmpty.type] => Future[Result]
        ]

      when(
        block(any[IdentifierRequest[AnyContentAsEmpty.type]])
      ).thenReturn(
        Future.successful(Created("created"))
      )

      val result =
        action
          .invokeBlock(request, block)
          .futureValue

      result shouldBe Created("created")
    }
  }
}
