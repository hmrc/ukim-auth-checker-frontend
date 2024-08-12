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
import java.time.LocalDate

class AuthRequestSpec extends AnyFlatSpec with Matchers {

  "AuthRequest" should "serialize to JSON correctly" in {
    // Arrange
    val eoris = Seq(Eori("GB1234567890"), Eori("GB0987654321"))
    val authRequest = AuthRequest(
      validityDate = LocalDate.of(2024, 8, 12),
      authType = "UKIM",
      eoris = eoris
    )

    val expectedJson = Json.parse(
      """{
        |  "validityDate": "2024-08-12",
        |  "authType": "UKIM",
        |  "eoris": ["GB1234567890", "GB0987654321"]
        |}""".stripMargin
    )

    // Act
    val json = Json.toJson(authRequest)

    // Assert
    json shouldBe expectedJson
  }

  it should "fail to deserialize from invalid JSON" in {
    // Arrange
    val invalidJson = Json.parse(
      """{
        |  "validityDate": "2024-08-12",
        |  "authType": "UKIM"
        |}""".stripMargin
    )

    // Act
    val result = invalidJson.validate[AuthRequest]

    // Assert
    result shouldBe a[JsError]
  }
}
