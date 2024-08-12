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
import uk.gov.hmrc.ukimauthcheckerfrontend.models._
import java.time.ZonedDateTime

class AuthResponseSpec extends AnyFlatSpec with Matchers {

  "AuthResponse" should "serialize to JSON correctly" in {
    // Arrange
    val processingDate = ZonedDateTime.parse("2024-08-12T10:15:30Z")
    val results = Seq(AuthResponseResult(Eori("GB1234567890"), valid = true, code = 200))
    val authResponse = AuthResponse(
      processingDate = processingDate,
      authType = "UKIM",
      results = results
    )

    val expectedJson = Json.parse(
      """{
        |  "processingDate": "2024-08-12T10:15:30Z",
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

    // Act
    val json = Json.toJson(authResponse)

    // Assert
    json shouldBe expectedJson
  }

  it should "fail to deserialize from invalid JSON" in {
    // Arrange
    val invalidJson = Json.parse(
      """{
        |  "processingDate": "2024-08-12T10:15:30Z",
        |  "authType": "UKIM"
        |}""".stripMargin
    )

    // Act
    val result = invalidJson.validate[AuthResponse]

    // Assert
    result shouldBe a[JsError]
  }
}
