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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ukimauthcheckerfrontend.connectors.Connector
import uk.gov.hmrc.ukimauthcheckerfrontend.config.ErrorHandler
import uk.gov.hmrc.ukimauthcheckerfrontend.views.html.ResultView
import uk.gov.hmrc.ukimauthcheckerfrontend.models.{AuthRequest, AuthResponse, AuthResponseResult, Eori}
import uk.gov.hmrc.ukimauthcheckerfrontend.models.errors.ParseResponseFailure
import java.time.{LocalDate, ZonedDateTime}
import play.twirl.api.HtmlFormat
import play.api.i18n.Messages
import scala.concurrent.{ExecutionContext, Future}

class ResultControllerSpec extends AnyFlatSpec with Matchers with MockitoSugar {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request: Request[_] = FakeRequest()
  implicit val messages: Messages = stubMessages()

  // Mock dependencies
  private val mockConnector = mock[Connector]
  private val mockResultView = mock[ResultView]
  private val mockErrorHandler = mock[ErrorHandler]

  // Instantiate the controller with mocked dependencies
  private val controller = new ResultController(
    stubMessagesControllerComponents(),
    mockResultView,
    mockConnector,
    mockErrorHandler
  )

  "ResultController" should "return OK and render the result view when auth request is successful" in {
    // Arrange
    val eoriNumber = "GB1234567890"
    val authRequest = AuthRequest(
      validityDate = LocalDate.now(),
      authType = "UKIM",
      eoris = Seq(Eori(eoriNumber))
    )

    val authResponse = AuthResponse(
      processingDate = ZonedDateTime.now(),
      authType = "UKIM",
      results = Seq(AuthResponseResult(Eori(eoriNumber), valid = true, code = 200))
    )

    when(mockConnector.validateCustoms(authRequest))
      .thenReturn(Future.successful(Right(authResponse)))

    when(mockResultView(isValid = true, Some(eoriNumber)))
      .thenReturn(HtmlFormat.empty) // Mock HTML content for simplicity

    // Act
    val result = controller.onPageLoad.apply(FakeRequest().withSession("eori" -> eoriNumber))

    // Assert
    status(result) shouldBe OK
    contentAsString(result) shouldBe "" // Adjust based on your view's expected output
  }

  it should "return INTERNAL_SERVER_ERROR when auth request fails" in {
    // Arrange
    val eoriNumber = "GB1234567890"
    val authRequest = AuthRequest(
      validityDate = LocalDate.now(),
      authType = "UKIM",
      eoris = Seq(Eori(eoriNumber))
    )

    when(mockConnector.validateCustoms(authRequest))
      .thenReturn(Future.successful(Left(ParseResponseFailure())))

    when(mockErrorHandler.standardErrorTemplate(
      pageTitle = "Error",
      heading = "Authentication Failure",
      message = "An error occurred while processing your request. Please try again later."
    )).thenReturn(Future.successful(HtmlFormat.empty)) // Mock HTML content for simplicity

    // Act
    val result = controller.onPageLoad.apply(FakeRequest().withSession("eori" -> eoriNumber))

    // Assert
    status(result) shouldBe INTERNAL_SERVER_ERROR
    contentAsString(result) shouldBe "" // Adjust based on your view's expected output
  }

  it should "return Error view when EORI is not found in the session" in {
    // Arrange
    when(mockErrorHandler.standardErrorTemplate(
      pageTitle = "Error",
      heading = "EORI Not Found",
      message = "The EORI number could not be found in the session. Please try again."
    )).thenReturn(Future.successful(HtmlFormat.empty)) // Mock HTML content for simplicity

    // Act
    val result = controller.onPageLoad.apply(FakeRequest())

    // Assert
    status(result) shouldBe OK
    contentAsString(result) shouldBe "" // Adjust based on your view's expected output
  }
}
