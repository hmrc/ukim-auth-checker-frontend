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
import uk.gov.hmrc.ukimauthcheckerfrontend.models.errors._

class ErrorSpec extends AnyFlatSpec with Matchers {

  "Error" should "be extended by InvalidAuthTokenPdsError" in {
    val error: Error = InvalidAuthTokenPdsError()
    error shouldBe an[Error]
    error shouldBe an[InvalidAuthTokenPdsError]
  }

  it should "be extended by ParseResponseFailure" in {
    val error: Error = ParseResponseFailure()
    error shouldBe an[Error]
    error shouldBe a[ParseResponseFailure]
  }

  "InvalidAuthTokenPdsError" should "be instantiable" in {
    val error = InvalidAuthTokenPdsError()
    error shouldBe an[InvalidAuthTokenPdsError]
  }

  it should "not equal other Error types" in {
    val error1 = InvalidAuthTokenPdsError()
    val error2 = ParseResponseFailure()
    error1 should not equal error2
  }

  "ParseResponseFailure" should "be instantiable" in {
    val error = ParseResponseFailure()
    error shouldBe a[ParseResponseFailure]
  }

  it should "not equal other Error types" in {
    val error1 = ParseResponseFailure()
    val error2 = InvalidAuthTokenPdsError()
    error1 should not equal error2
  }

  "Different instances of the same Error type" should "be equal" in {
    val error1 = InvalidAuthTokenPdsError()
    val error2 = InvalidAuthTokenPdsError()
    error1 should equal(error2)

    val error3 = ParseResponseFailure()
    val error4 = ParseResponseFailure()
    error3 should equal(error4)
  }
}