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
import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.ukimauthcheckerfrontend.models._

class ErrorValidationErrorResponseSpec extends AnyFlatSpec with Matchers {

  "ValidationErrorResponse" should "be serializable to and from JSON" in {
    val eoriError = EoriValidationError("GB123456789000", "Invalid EORI number")
    val dateError = DateValidationError("2024-02-30", "Invalid date")

    val response = ValidationErrorResponse(
      code = AuthorisedBadRequestCode.InvalidFormat,
      message = "Invalid format",
      validationErrors = Seq(eoriError, dateError)
    )

    val json = Json.obj(
      "code" -> "INVALID_FORMAT",
      "message" -> "Invalid format",
      "validationErrors" -> Json.arr(
        Json.obj("eori" -> "GB123456789000", "validationError" -> "Invalid EORI number"),
        Json.obj("date" -> "2024-02-30", "validationError" -> "Invalid date")
      )
    )

    Json.toJson(response) shouldBe json
    json.validate[ValidationErrorResponse] shouldBe JsSuccess(response)
  }
}

class AuthorisedBadRequestCodeSpec extends AnyFlatSpec with Matchers {
  it should "fail to parse an unknown code" in {
    val json = Json.toJson("UNKNOWN_CODE")
    json.validate[AuthorisedBadRequestCode].isError shouldBe true
  }
}

class ErrorsValidationErrorSpec extends AnyFlatSpec with Matchers {

  "EoriValidationError" should "be serializable to and from JSON" in {
    val error = EoriValidationError("GB123456789000", "Invalid EORI number")
    val json = Json.obj("eori" -> "GB123456789000", "validationError" -> "Invalid EORI number")

    Json.toJson(error) shouldBe json
    json.validate[ValidationError] shouldBe JsSuccess(error)
  }

  "DateValidationError" should "be serializable to and from JSON" in {
    val error = DateValidationError("2024-02-30", "Invalid date")
    val json = Json.obj("date" -> "2024-02-30", "validationError" -> "Invalid date")

    Json.toJson(error) shouldBe json
    json.validate[ValidationError] shouldBe JsSuccess(error)
  }

  "ValidationError" should "fail to parse an unknown error type" in {
    val json = Json.obj("unknown" -> "error")
    json.validate[ValidationError].isError shouldBe true
  }
}