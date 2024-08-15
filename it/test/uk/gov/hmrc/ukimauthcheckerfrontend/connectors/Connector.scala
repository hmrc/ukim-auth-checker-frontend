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

package uk.gov.hmrc.ukimauthcheckerfrontend.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import play.api.Configuration
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}
import uk.gov.hmrc.ukimauthcheckerfrontend.config.AppConfig
import uk.gov.hmrc.ukimauthcheckerfrontend.models._
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.ExecutionContext.Implicits.global

class PdsAuthCheckerConnectorSpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with HttpClientV2Support
    with IntegrationPatience
    with WireMockSupport {

  private val pdsAuthCheckerPath = "/authorisations"

  private val configuration = Configuration(
    "appName" -> "ukim-auth-checker-frontend",
    "microservice.services.pds-auth-checker-api.host" -> wireMockHost,
    "microservice.services.pds-auth-checker-api.port" -> wireMockPort
  )

  private val servicesConfig = new ServicesConfig(configuration)
  private val appConfig = new AppConfig(configuration, servicesConfig)

  private val connector = new PdsAuthCheckerConnectorImpl(httpClientV2, appConfig)

  "PdsAuthCheckerConnector" when {
    "check is called" should {
      "return a successful response for a valid request" in {
        val date = LocalDate.now().toString
        val eoris = Seq(Eori("GB123456789000"), Eori("XI987654321000"))
        val request = DatedAuthorisationRequest(eoris, date)
        val responseData = AuthResponse(
          LocalDateTime.now(),
          "UKIM",
          eoris.map(eori => AuthCheckerResult(eori, true, 0))
        )

        givenPdsAuthCheckerReturns(
          200,
          pdsAuthCheckerPath,
          Json.toJson(responseData).toString()
        )

        val response = connector.check(request)(HeaderCarrier(), global).futureValue

        response shouldBe Right(responseData)
      }

      "return a ValidationErrorResponse for an invalid EORI" in {
        val date = LocalDate.now().toString
        val invalidEori = Eori("INVALID_EORI")
        val request = DatedAuthorisationRequest(Seq(invalidEori), date)
        val errorResponse = ValidationErrorResponse(
          AuthorisedBadRequestCode.InvalidFormat,
          "Invalid request format",
          Seq(
            EoriValidationError(invalidEori.value, "EORI format invalid")
          )
        )

        givenPdsAuthCheckerReturns(
          400,
          pdsAuthCheckerPath,
          Json.toJson(errorResponse).toString()
        )

        val response = connector.check(request)(HeaderCarrier(), global).futureValue

        response shouldBe Left(errorResponse)
      }

      "handle ValidationErrorResponse with DateValidationError" in {
        val invalidDate = "2024-13-32" // Invalid date
        val request = DatedAuthorisationRequest(Seq(Eori("GB123456789000")), invalidDate)
        val errorResponse = ValidationErrorResponse(
          AuthorisedBadRequestCode.InvalidFormat,
          "Invalid request format",
          Seq(
            DateValidationError(invalidDate, "Invalid date format")
          )
        )

        givenPdsAuthCheckerReturns(
          400,
          pdsAuthCheckerPath,
          Json.toJson(errorResponse).toString()
        )

        val response = connector.check(request)(HeaderCarrier(), global).futureValue

        response shouldBe Left(errorResponse)
      }

      "send the correct PdsAuthCheckerRequest" in {
        val date = LocalDate.now().toString
        val eoris = Seq(Eori("GB123456789000"), Eori("XI987654321000"))
        val request = DatedAuthorisationRequest(eoris, date)
        val expectedPdsRequest = PdsAuthCheckerRequest(
          validityDate = date,
          authType = "UKIM",
          eoris = eoris
        )

        givenPdsAuthCheckerReturns(
          200,
          pdsAuthCheckerPath,
          Json.toJson(AuthResponse(LocalDateTime.now(), "UKIM", Seq())).toString()
        )

        connector.check(request)(HeaderCarrier(), global).futureValue

        wireMockServer.verify(
          postRequestedFor(urlEqualTo(pdsAuthCheckerPath))
            .withRequestBody(equalToJson(Json.toJson(expectedPdsRequest).toString()))
        )
      }

      "fail for unexpected status codes" in {
        val date = LocalDate.now().toString
        val request = DatedAuthorisationRequest(Seq(Eori("GB123456789000")), date)

        givenPdsAuthCheckerReturns(
          500,
          pdsAuthCheckerPath,
          "Internal Server Error"
        )

        connector.check(request)(HeaderCarrier(), global).failed.futureValue shouldBe a[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }
  }

  private def givenPdsAuthCheckerReturns(status: Int, url: String, body: String): Unit =
    wireMockServer.stubFor(
      post(urlEqualTo(url))
        .willReturn(
          aResponse()
            .withStatus(status)
            .withBody(body)
        )
    )
}