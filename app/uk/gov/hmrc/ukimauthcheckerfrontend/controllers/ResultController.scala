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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ukimauthcheckerfrontend.views.html.ResultView
import uk.gov.hmrc.ukimauthcheckerfrontend.connectors.PdsAuthCheckerConnector
import uk.gov.hmrc.ukimauthcheckerfrontend.config.ErrorHandler
import uk.gov.hmrc.ukimauthcheckerfrontend.models.{DatedAuthorisationRequest, Eori}

import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDate

@Singleton
class ResultController @Inject()(
                                  mcc: MessagesControllerComponents,
                                  resultView: ResultView,
                                  pdsAuthCheckerConnector: PdsAuthCheckerConnector,
                                  errorHandler: ErrorHandler
                                )(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    request.session.get("eori") match {
      case Some(eoriNumber) =>
        val datedAuthRequest = DatedAuthorisationRequest(
          eoris = Seq(Eori(eoriNumber)),
          date = LocalDate.now().toString
        )

        pdsAuthCheckerConnector.check(datedAuthRequest).flatMap {
          case Right(response) =>
            println(s"Response Results: ${response.results}")
            val isValid: Boolean = response.results.head.valid
            Future.successful(Ok(resultView(isValid = isValid, Some(eoriNumber))))
          case Left(validationError) =>
            errorHandler.standardErrorTemplate(
              pageTitle = "Validation Error",
              heading = "Validation Error",
              message = "An error occurred while validating your request"
            ).map(BadRequest(_))
        }.recoverWith {
          case ex: Exception =>
            errorHandler.standardErrorTemplate(
              pageTitle = "Internal Server Error",
              heading = "Internal Server Error",
              message = "An unexpected error occurred while processing your request."
            ).map(InternalServerError(_))
        }

      case None =>
        errorHandler.standardErrorTemplate(
          pageTitle = "Error",
          heading = "EORI Not Found",
          message = "The EORI number could not be found in the session. Please try again."
        ).map(Ok(_))
    }
  }
}