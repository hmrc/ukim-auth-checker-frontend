/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.ukimauthcheckerfrontend.controllers

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import play.api.test.Helpers._
import play.api.test._
import uk.gov.hmrc.ukimauthcheckerfrontend.connectors.PdsAuthCheckerConnector
import uk.gov.hmrc.ukimauthcheckerfrontend.config.ErrorHandler
import uk.gov.hmrc.ukimauthcheckerfrontend.views.html.ResultView
import uk.gov.hmrc.ukimauthcheckerfrontend.models._
import scala.concurrent.{ExecutionContext, Future}
import play.twirl.api.Html
import java.time.{LocalDate, LocalDateTime}

class ResultControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val messages: play.api.i18n.Messages = stubMessages()

  // Mock dependencies
  val mockPdsAuthCheckerConnector: PdsAuthCheckerConnector = mock[PdsAuthCheckerConnector]
  val mockResultView: ResultView = mock[ResultView]
  val mockErrorHandler: ErrorHandler = mock[ErrorHandler]

  // Instantiate the controller with mocked dependencies
  val controller = new ResultController(
    stubMessagesControllerComponents(),
    mockResultView,
    mockPdsAuthCheckerConnector,
    mockErrorHandler
  )

  "ResultController" should {

    "return OK and render the result view when EORI is in session and connector returns a valid response" in {
      val eoriNumber = "GB1234567890"
      val datedAuthRequest = DatedAuthorisationRequest(
        eoris = Seq(Eori(eoriNumber)),
        date = LocalDate.now().toString
      )

      val authResponse = AuthResponse(
        processingDate = LocalDateTime.now(),
        authType = "UKIM",
        results = Seq(AuthCheckerResult(Eori(eoriNumber), valid = true, code = 200))
      )

      when(mockPdsAuthCheckerConnector.check(eqTo(datedAuthRequest))(any(), any()))
        .thenReturn(Future.successful(Right(authResponse)))

      when(mockResultView.apply(eqTo(true), eqTo(Some(eoriNumber)))(any(), any()))
        .thenReturn(Html("Successful result"))

      val result = controller.onPageLoad()(FakeRequest().withSession("eori" -> eoriNumber))

      status(result) shouldBe OK
      contentAsString(result) shouldBe "Successful result"
      verify(mockResultView).apply(eqTo(true), eqTo(Some(eoriNumber)))(any(), any())
    }

    "return BadRequest and render error view when connector returns a validation error" in {
      val eoriNumber = "GB1234567890"
      val datedAuthRequest = DatedAuthorisationRequest(
        eoris = Seq(Eori(eoriNumber)),
        date = LocalDate.now().toString
      )

      val validationErrors = Seq(
        EoriValidationError(rawEori = eoriNumber, errorMessage = "Invalid EORI format"),
        DateValidationError(rawDateIsoString = "invalid-date", errorMessage = "Invalid date format")
      )

      val validationErrorResponse = ValidationErrorResponse(
        code = AuthorisedBadRequestCode.InvalidFormat,
        message = "Validation error details",
        validationErrors = validationErrors
      )

      when(mockPdsAuthCheckerConnector.check(eqTo(datedAuthRequest))(any(), any()))
        .thenReturn(Future.successful(Left(validationErrorResponse)))

      when(mockErrorHandler.standardErrorTemplate(
        eqTo("Validation Error"),
        eqTo("Validation Error"),
        eqTo("An error occurred while validating your request")
      )(any()))
        .thenReturn(Future.successful(Html("Validation Error")))

      val result = controller.onPageLoad()(FakeRequest().withSession("eori" -> eoriNumber))

      status(result) shouldBe BAD_REQUEST
      contentAsString(result) shouldBe "Validation Error"
    }

    "return InternalServerError and render error view when an exception is thrown" in {
      val eoriNumber = "GB1234567890"
      val datedAuthRequest = DatedAuthorisationRequest(
        eoris = Seq(Eori(eoriNumber)),
        date = LocalDate.now().toString
      )

      when(mockPdsAuthCheckerConnector.check(eqTo(datedAuthRequest))(any(), any()))
        .thenReturn(Future.failed(new RuntimeException("Internal error")))

      when(mockErrorHandler.standardErrorTemplate(
        eqTo("Internal Server Error"),
        eqTo("Internal Server Error"),
        eqTo("An unexpected error occurred while processing your request.")
      )(any()))
        .thenReturn(Future.successful(Html("Internal Server Error")))

      val result = controller.onPageLoad()(FakeRequest().withSession("eori" -> eoriNumber))

      status(result) shouldBe INTERNAL_SERVER_ERROR
      contentAsString(result) shouldBe "Internal Server Error"
    }

    "return OK and render error view when EORI is not found in session" in {
      when(mockErrorHandler.standardErrorTemplate(
        eqTo("Error"),
        eqTo("EORI Not Found"),
        eqTo("The EORI number could not be found in the session. Please try again.")
      )(any()))
        .thenReturn(Future.successful(Html("EORI Not Found")))

      val result = controller.onPageLoad()(FakeRequest())

      status(result) shouldBe OK
      contentAsString(result) shouldBe "EORI Not Found"
    }
  }
}
