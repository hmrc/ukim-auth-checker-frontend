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
import java.time.LocalDateTime

class AuthResponseSpec extends AnyFlatSpec with Matchers {

  "AuthResponse" should "serialize to JSON correctly" in {
    val processingDate = LocalDateTime.parse("2024-08-12T10:15:30")
    val results = Seq(AuthCheckerResult(Eori("GB1234567890"), valid = true, code = 200))
    val authResponse = AuthResponse(
      processingDate = processingDate,
      authType = "UKIM",
      results = results
    )

    val expectedJson = Json.parse(
      """{
        |  "processingDate": "2024-08-12T10:15:30",
        |  "authType": "UKIM",
        |  "results": [
        |    {
        |      "eori": "GB1234567890",
        |      "valid": true,
        |      "code": 200
        |    }
        |  ]
        |}""".stripMargin
    )

    val json = Json.toJson(authResponse)

    json shouldBe expectedJson
  }

  it should "fail to deserialize from invalid JSON" in {
    val invalidJson = Json.parse(
      """{
        |  "processingDate": "2024-08-12T10:15:30",
        |  "authType": "UKIM"
        |}""".stripMargin
    )

    invalidJson.validate[AuthResponse] shouldBe a[JsError]
  }
}

class AuthCheckerResultSpec extends AnyFlatSpec with Matchers {

  "AuthCheckerResult" should "serialize to JSON correctly" in {
    val authCheckerResult = AuthCheckerResult(
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

    val json = Json.toJson(authCheckerResult)

    json shouldBe expectedJson
  }

  it should "fail to deserialize from invalid JSON" in {
    val invalidJson = Json.parse(
      """{
        |  "eori": "GB1234567890",
        |  "valid": true
        |}""".stripMargin
    )

    invalidJson.validate[AuthCheckerResult] shouldBe a[JsError]
  }
}
