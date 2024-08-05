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

package uk.gov.hmrc.ukimauthcheckerfrontend.forms

import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid}
import uk.gov.hmrc.ukimauthcheckerfrontend.forms.FormHelpers.mandatory
import uk.gov.hmrc.ukimauthcheckerfrontend.models.FormValues

object EoriForm {
  private val isEoriCountryCodeValid = Constraint[String] { eori: String =>
    if (eori.replaceAll(" ", "").matches("""^(?i)(GB|XI).*"""))
      Valid
    else Invalid("error.country-code")
  }

  private val isEoriNumberValid = Constraint[String] { eori: String =>
    if (
      eori.replaceAll(" ", "").matches("""^(?i)(GB|XI)[0-9]{12}([0-9]{3})?$""")
    )
      Valid
    else Invalid("error.eori-number")
  }

  val form: Form[FormValues] = Form(
    mapping(
      "eori-input" -> mandatory("eori")
        .verifying(isEoriCountryCodeValid)
        .verifying(isEoriNumberValid)
    )(eoriEntered => FormValues(eoriEntered))(eori => Some(eori.value))
  )
}
