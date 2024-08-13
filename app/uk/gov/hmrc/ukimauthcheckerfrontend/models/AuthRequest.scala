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

package uk.gov.hmrc.ukimauthcheckerfrontend.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class AuthRequest(eoris: Seq[Eori], date: String)

object AuthRequest {
  implicit val format: OFormat[AuthRequest] =
    Json.format[AuthRequest]

}

case class DatedAuthorisationRequest(eoris: Seq[Eori], date: String)

object DatedAuthorisationRequest {
  implicit val format: OFormat[DatedAuthorisationRequest] =
    Json.format[DatedAuthorisationRequest]

  def createFromRequest(
                         request: AuthRequest
                       ): DatedAuthorisationRequest =
    DatedAuthorisationRequest(
      request.eoris,
      request.date
    )
}

case class PdsAuthCheckerRequest(
                                  validityDate: String,
                                  authType: String,
                                  eoris: Seq[Eori]
                                )

object PdsAuthCheckerRequest {
  implicit val format: OFormat[PdsAuthCheckerRequest] =
    Json.format[PdsAuthCheckerRequest]
}