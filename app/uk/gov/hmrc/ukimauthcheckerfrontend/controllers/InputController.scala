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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ukimauthcheckerfrontend.forms.EoriForm
import uk.gov.hmrc.ukimauthcheckerfrontend.views.html.InputView
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class InputController @Inject() (
    mcc: MessagesControllerComponents,
    inputView: InputView
) extends FrontendController(mcc) {
  val onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(inputView(EoriForm.form)))
  }
  def postInputEori = Action.async { implicit request =>
    EoriForm.form
      .bindFromRequest()
      .fold(
        errors => {
          Future.successful(BadRequest(inputView(errors)))
        },
        form => {
          Future.successful(Redirect(routes.ResultController.onPageLoad))
        }
      )
  }
}
