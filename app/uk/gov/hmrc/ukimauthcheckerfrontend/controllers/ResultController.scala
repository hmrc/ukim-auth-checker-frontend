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
import uk.gov.hmrc.ukimauthcheckerfrontend.connectors.Connector
import uk.gov.hmrc.ukimauthcheckerfrontend.config.ErrorHandler
import uk.gov.hmrc.ukimauthcheckerfrontend.models.{AuthRequest, AuthResponse, Eori}

import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDate

@Singleton
class ResultController @Inject()(
                                  mcc: MessagesControllerComponents,
                                  resultView: ResultView,
                                  Connector: Connector,
                                  errorHandler: ErrorHandler
                                )(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    request.session.get("eori") match {
      case Some(eoriNumber) =>
        val authRequest = AuthRequest(
          validityDate = LocalDate.now(),
          authType = "UKIM",
          eoris = Seq(Eori(eoriNumber))
        )

        Connector.validateCustoms(authRequest).map {
          case Right(response: AuthResponse) =>

            val isValid: Boolean = response.results.head.valid
            Ok(resultView(isValid = isValid, Some(eoriNumber)))
          case Left(_) =>
            InternalServerError("An error occurred while processing your request.")
        }.recover {
          case ex: Exception =>
            InternalServerError("An error occurred while processing your request.")
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
