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
import play.api.data.FormError
import uk.gov.hmrc.ukimauthcheckerfrontend.models.FormValues

class EoriFormSpec extends AnyFlatSpec with Matchers {

  "EoriForm" should "accept valid GB EORI numbers" in {
    val validGbEoris = Seq(
      "GB123456789000",
      "GB123456789000123",
      "gb123456789000",
      "gb123456789000123"
    )

    validGbEoris.foreach { eori =>
      val result = EoriForm.form.bind(Map("eori-input" -> eori))
      result.errors shouldBe empty
      result.value shouldBe Some(FormValues(eori))
    }
  }

  it should "accept valid XI EORI numbers" in {
    val validXiEoris = Seq(
      "XI123456789000",
      "XI123456789000123",
      "xi123456789000",
      "xi123456789000123"
    )

    validXiEoris.foreach { eori =>
      val result = EoriForm.form.bind(Map("eori-input" -> eori))
      result.errors shouldBe empty
      result.value shouldBe Some(FormValues(eori))
    }
  }

  it should "reject EORI numbers with invalid country codes" in {
    val invalidEoris = Seq(
      "FR123456789000",
      "US123456789000123",
      "XX123456789000"
    )

    invalidEoris.foreach { eori =>
      val result = EoriForm.form.bind(Map("eori-input" -> eori))
      result.errors should contain(FormError("eori-input", "error.country-code"))
    }
  }

  it should "trim whitespace from valid EORI numbers" in {
    val eoriWithSpaces = "  GB123456789000  "
    val result = EoriForm.form.bind(Map("eori-input" -> eoriWithSpaces))
    result.errors shouldBe empty
    result.value shouldBe Some(FormValues("GB123456789000"))
  }
}