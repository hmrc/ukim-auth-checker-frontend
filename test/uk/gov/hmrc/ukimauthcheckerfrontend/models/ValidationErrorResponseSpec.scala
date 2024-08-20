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
import play.api.libs.json._
import uk.gov.hmrc.ukimauthcheckerfrontend.models._

class ValidationErrorResponseSpec extends AnyFlatSpec with Matchers {

  "AuthorisedBadRequestCode" should "be serializable to JSON" in {
    val code: AuthorisedBadRequestCode = AuthorisedBadRequestCode.InvalidFormat
    Json.toJson(code) shouldBe JsString("INVALID_FORMAT")
  }

  it should "be deserializable from JSON" in {
    val json = JsString("INVALID_FORMAT")
    json.as[AuthorisedBadRequestCode] shouldBe AuthorisedBadRequestCode.InvalidFormat
  }

  it should "fail to deserialize from invalid JSON" in {
    val invalidJson = JsString("UNKNOWN_CODE")
    an[JsResultException] should be thrownBy invalidJson.as[AuthorisedBadRequestCode]
  }

  "ValidationErrorResponse" should "be serializable to JSON" in {
    val eoriError = EoriValidationError("GB123456789000", "Invalid EORI format")
    val dateError = DateValidationError("2024-03-15", "Invalid date format")
    val response = ValidationErrorResponse(
      code = AuthorisedBadRequestCode.InvalidFormat,
      message = "Validation failed",
      validationErrors = Seq(eoriError, dateError)
    )

    val json = Json.toJson(response)
    json shouldBe Json.obj(
      "code" -> "INVALID_FORMAT",
      "message" -> "Validation failed",
      "validationErrors" -> Json.arr(
        Json.obj(
          "eori" -> "GB123456789000",
          "validationError" -> "Invalid EORI format"
        ),
        Json.obj(
          "date" -> "2024-03-15",
          "validationError" -> "Invalid date format"
        )
      )
    )
  }

  it should "be deserializable from JSON" in {
    val json = Json.obj(
      "code" -> "INVALID_FORMAT",
      "message" -> "Validation failed",
      "validationErrors" -> Json.arr(
        Json.obj(
          "eori" -> "GB123456789000",
          "validationError" -> "Invalid EORI format"
        ),
        Json.obj(
          "date" -> "2024-03-15",
          "validationError" -> "Invalid date format"
        )
      )
    )

    val response = json.as[ValidationErrorResponse]
    response.code shouldBe AuthorisedBadRequestCode.InvalidFormat
    response.message shouldBe "Validation failed"
    response.validationErrors should have size 2
    response.validationErrors(0) shouldBe an[EoriValidationError]
    response.validationErrors(1) shouldBe a[DateValidationError]
  }

  it should "fail to deserialize from JSON with invalid code" in {
    val json = Json.obj(
      "code" -> "UNKNOWN_CODE",
      "message" -> "Validation failed",
      "validationErrors" -> Json.arr()
    )

    an[JsResultException] should be thrownBy json.as[ValidationErrorResponse]
  }

  it should "fail to deserialize from JSON with missing fields" in {
    val json = Json.obj(
      "message" -> "Validation failed",
      "validationErrors" -> Json.arr()
    )

    an[JsResultException] should be thrownBy json.as[ValidationErrorResponse]
  }
}