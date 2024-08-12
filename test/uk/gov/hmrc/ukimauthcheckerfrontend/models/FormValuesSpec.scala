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
import play.api.libs.json.{Json, JsError, JsSuccess, OFormat}

class FormValuesSpec extends AnyFlatSpec with Matchers {

  implicit val format: OFormat[FormValues] = Json.format[FormValues]

  it should "fail to deserialize from invalid JSON" in {
    val invalidJson = Json.parse(
      """123"""
    )

    val result = invalidJson.validate[FormValues]

    result shouldBe a[JsError]
  }

  it should "create FormValues from Boolean correctly" in {
    val trueFormValue = FormValues(true)
    val falseFormValue = FormValues(false)

    trueFormValue shouldBe FormValues("true")
    falseFormValue shouldBe FormValues("false")
  }
}