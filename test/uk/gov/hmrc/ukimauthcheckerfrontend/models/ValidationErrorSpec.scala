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

class ValidationErrorSpec extends AnyFlatSpec with Matchers {

  "EoriValidationError" should "be serializable to JSON" in {
    val error = EoriValidationError("GB123456789000", "Invalid EORI format")
    val json = Json.toJson(error)
    json shouldBe Json.obj(
      "eori" -> "GB123456789000",
      "validationError" -> "Invalid EORI format"
    )
  }

  it should "be deserializable from JSON" in {
    val json = Json.obj(
      "eori" -> "GB123456789000",
      "validationError" -> "Invalid EORI format"
    )
    val error = json.as[EoriValidationError]
    error shouldBe EoriValidationError("GB123456789000", "Invalid EORI format")
  }

  "DateValidationError" should "be serializable to JSON" in {
    val error = DateValidationError("2024-03-15", "Invalid date format")
    val json = Json.toJson(error)
    json shouldBe Json.obj(
      "date" -> "2024-03-15",
      "validationError" -> "Invalid date format"
    )
  }

  it should "be deserializable from JSON" in {
    val json = Json.obj(
      "date" -> "2024-03-15",
      "validationError" -> "Invalid date format"
    )
    val error = json.as[DateValidationError]
    error shouldBe DateValidationError("2024-03-15", "Invalid date format")
  }

  "ValidationError" should "be serializable to JSON" in {
    val eoriError: ValidationError = EoriValidationError("GB123456789000", "Invalid EORI format")
    val dateError: ValidationError = DateValidationError("2024-03-15", "Invalid date format")

    Json.toJson(eoriError) shouldBe Json.obj(
      "eori" -> "GB123456789000",
      "validationError" -> "Invalid EORI format"
    )

    Json.toJson(dateError) shouldBe Json.obj(
      "date" -> "2024-03-15",
      "validationError" -> "Invalid date format"
    )
  }

  it should "be deserializable from JSON" in {
    val eoriJson = Json.obj(
      "eori" -> "GB123456789000",
      "validationError" -> "Invalid EORI format"
    )
    val dateJson = Json.obj(
      "date" -> "2024-03-15",
      "validationError" -> "Invalid date format"
    )

    eoriJson.as[ValidationError] shouldBe EoriValidationError("GB123456789000", "Invalid EORI format")
    dateJson.as[ValidationError] shouldBe DateValidationError("2024-03-15", "Invalid date format")
  }

  it should "fail to deserialize from invalid JSON" in {
    val invalidJson = Json.obj(
      "unknown" -> "field",
      "validationError" -> "Some error"
    )

    an[JsResultException] should be thrownBy invalidJson.as[ValidationError]
  }

  it should "fail to deserialize from non-object JSON" in {
    val invalidJson = JsString("Not an object")

    an[JsResultException] should be thrownBy invalidJson.as[ValidationError]
  }
}