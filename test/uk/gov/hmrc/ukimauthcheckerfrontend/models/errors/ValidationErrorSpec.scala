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

package uk.gov.hmrc.ukimauthcheckerfrontend.models.errors

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import uk.gov.hmrc.ukimauthcheckerfrontend.models.errors._

class ErrorValidationErrorSpec extends AnyFlatSpec with Matchers {

  "ValidationError" should "serialize EoriValidationError correctly" in {
    val error = EoriValidationError("GB123456789000", "Invalid EORI number")
    val expectedJson = Json.obj(
      "eori" -> "GB123456789000",
      "validationError" -> "Invalid EORI number"
    )

    Json.toJson(error) shouldBe expectedJson
  }

  it should "serialize DateValidationError correctly" in {
    val error = DateValidationError("2024-02-30", "Invalid date")
    val expectedJson = Json.obj(
      "date" -> "2024-02-30",
      "validationError" -> "Invalid date"
    )

    Json.toJson(error) shouldBe expectedJson
  }
}

class EoriValidationErrorSpec extends AnyFlatSpec with Matchers {

  "EoriValidationError" should "be serializable to JSON" in {
    val error = EoriValidationError("GB123456789000", "Invalid EORI number")
    val expectedJson = Json.obj(
      "eori" -> "GB123456789000",
      "validationError" -> "Invalid EORI number"
    )

    Json.toJson(error) shouldBe expectedJson
  }
}

class DateValidationErrorSpec extends AnyFlatSpec with Matchers {

  "DateValidationError" should "be serializable to JSON" in {
    val error = DateValidationError("2024-02-30", "Invalid date")
    val expectedJson = Json.obj(
      "date" -> "2024-02-30",
      "validationError" -> "Invalid date"
    )

    Json.toJson(error) shouldBe expectedJson
  }
}