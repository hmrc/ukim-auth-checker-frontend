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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.data.{Form, FormError}
import uk.gov.hmrc.ukimauthcheckerfrontend.forms.FormHelpers
import uk.gov.hmrc.ukimauthcheckerfrontend.models.FormValues

class FormHelpersSpec extends AnyFlatSpec with Matchers {

  "FormHelpers.mandatory" should "create a mapping that trims input" in {
    val form = Form(
      "test" -> FormHelpers.mandatory("test")
    )

    val result = form.bind(Map("test" -> "  value  "))
    result.value shouldBe Some("value")
  }

  it should "create a mapping that rejects empty input" in {
    val form = Form(
      "test" -> FormHelpers.mandatory("test")
    )

    val result = form.bind(Map("test" -> ""))
    result.errors should contain(FormError("test", "error.test.required"))
  }

  it should "create a mapping that accepts non-empty input" in {
    val form = Form(
      "test" -> FormHelpers.mandatory("test")
    )

    val result = form.bind(Map("test" -> "value"))
    result.value shouldBe Some("value")
  }

  it should "create a mapping that rejects input consisting only of whitespace" in {
    val form = Form(
      "test" -> FormHelpers.mandatory("test")
    )

    val result = form.bind(Map("test" -> "   "))
    result.errors should contain(FormError("test", "error.test.required"))
  }
}