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
import uk.gov.hmrc.ukimauthcheckerfrontend.models.errors.ErrorDetail
import play.api.libs.json._
import java.time.Instant

class ErrorDetailSpec extends AnyFlatSpec with Matchers {

  "ErrorDetail" should "be instantiable with all fields" in {
    val timestamp = Instant.now()
    val errorDetail = ErrorDetail(
      timestamp = timestamp,
      errorCode = "ERR001",
      errorMessage = "An error occurred",
      sourcePDSFaultDetails = "PDS fault details"
    )

    errorDetail.timestamp shouldBe timestamp
    errorDetail.errorCode shouldBe "ERR001"
    errorDetail.errorMessage shouldBe "An error occurred"
    errorDetail.sourcePDSFaultDetails shouldBe "PDS fault details"
  }

  it should "be serializable to JSON" in {
    val timestamp = Instant.parse("2024-03-15T12:00:00Z")
    val errorDetail = ErrorDetail(
      timestamp = timestamp,
      errorCode = "ERR001",
      errorMessage = "An error occurred",
      sourcePDSFaultDetails = "PDS fault details"
    )

    val json = Json.toJson(errorDetail)
    json shouldBe Json.obj(
      "timestamp" -> "2024-03-15T12:00:00Z",
      "errorCode" -> "ERR001",
      "errorMessage" -> "An error occurred",
      "sourcePDSFaultDetails" -> "PDS fault details"
    )
  }

  it should "be deserializable from JSON" in {
    val json = Json.obj(
      "timestamp" -> "2024-03-15T12:00:00Z",
      "errorCode" -> "ERR001",
      "errorMessage" -> "An error occurred",
      "sourcePDSFaultDetails" -> "PDS fault details"
    )

    val errorDetail = json.as[ErrorDetail]
    errorDetail.timestamp shouldBe Instant.parse("2024-03-15T12:00:00Z")
    errorDetail.errorCode shouldBe "ERR001"
    errorDetail.errorMessage shouldBe "An error occurred"
    errorDetail.sourcePDSFaultDetails shouldBe "PDS fault details"
  }

  it should "fail to deserialize from invalid JSON" in {
    val invalidJson = Json.obj(
      "timestamp" -> "invalid date",
      "errorCode" -> "ERR001",
      "errorMessage" -> "An error occurred",
      "sourcePDSFaultDetails" -> "PDS fault details"
    )

    an[JsResultException] should be thrownBy invalidJson.as[ErrorDetail]
  }

  it should "fail to deserialize from JSON with missing fields" in {
    val incompleteJson = Json.obj(
      "errorCode" -> "ERR001",
      "errorMessage" -> "An error occurred"
    )

    an[JsResultException] should be thrownBy incompleteJson.as[ErrorDetail]
  }
}