package uk.gov.hmrc.ukimauthcheckerfrontend.controllers

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import play.api.test.Helpers._
import play.api.test._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ukimauthcheckerfrontend.connectors.Connector
import uk.gov.hmrc.ukimauthcheckerfrontend.config.ErrorHandler
import uk.gov.hmrc.ukimauthcheckerfrontend.views.html.ResultView
import uk.gov.hmrc.ukimauthcheckerfrontend.models._
import java.time.{LocalDate, ZonedDateTime}
import play.twirl.api.{Html, HtmlFormat}
import play.api.i18n.Messages

import scala.concurrent.{ExecutionContext, Future}

class ResultControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val messages: Messages = stubMessages()

  // Mock dependencies
  val mockConnector: Connector = mock[Connector]
  val mockResultView: ResultView = mock[ResultView]
  val mockErrorHandler: ErrorHandler = mock[ErrorHandler]

  // Instantiate the controller with mocked dependencies
  val controller = new ResultController(
    stubMessagesControllerComponents(),
    mockResultView,
    mockConnector,
    mockErrorHandler
  )

  "ResultController" should {
    "return OK and render the result view when auth request is successful" in {
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

      when(mockConnector.validateCustoms(any[AuthRequest])(any[HeaderCarrier]))
        .thenReturn(Future.successful(Right(authResponse)))

      when(mockResultView.apply(eqTo(true), eqTo(Some(eoriNumber)))(any(), any()))
        .thenReturn(Html("Successful result"))

      val result = controller.onPageLoad()(FakeRequest().withSession("eori" -> eoriNumber))

      status(result) shouldBe OK
      contentAsString(result) shouldBe "Successful result"
      verify(mockResultView).apply(eqTo(true), eqTo(Some(eoriNumber)))(any(), any())
    }

    "return Error view when EORI is not found in the session" in {
      when(mockErrorHandler.standardErrorTemplate(any(), any(), any())(any()))
        .thenReturn(Future.successful(Html("EORI Not Found")))

      val result = controller.onPageLoad()(FakeRequest())

      status(result) shouldBe OK
      contentAsString(result) shouldBe "EORI Not Found"
      verify(mockErrorHandler).standardErrorTemplate(
        eqTo("Error"),
        eqTo("EORI Not Found"),
        eqTo("The EORI number could not be found in the session. Please try again.")
      )(any())
    }
  }
}