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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{Json, JsError, JsSuccess}

class AuthResponseResultSpec extends AnyFlatSpec with Matchers {

  "AuthResponseResult" should "serialize to JSON correctly" in {
    // Arrange
    val authResponseResult = AuthResponseResult(
      eori = Eori("GB1234567890"),
      valid = true,
      code = 200
    )

    val expectedJson = Json.parse(
      """{
        |  "eori": "GB1234567890",
        |  "valid": true,
        |  "code": 200
        |}""".stripMargin
    )

    // Act
    val json = Json.toJson(authResponseResult)

    // Assert
    json shouldBe expectedJson
  }

  it should "fail to deserialize from invalid JSON" in {
    // Arrange
    val invalidJson = Json.parse(
      """{
        |  "eori": "GB1234567890",
        |  "valid": true
        |}""".stripMargin
    )

    // Act
    val result = invalidJson.validate[AuthResponseResult]

    // Assert
    result shouldBe a[JsError]
  }
}