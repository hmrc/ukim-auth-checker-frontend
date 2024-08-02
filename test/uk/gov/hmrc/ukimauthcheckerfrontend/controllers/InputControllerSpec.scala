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

package uk.gov.hmrc.ukimauthcheckerfrontend.controllers

import org.jsoup.Jsoup
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.ukimauthcheckerfrontend.forms.EoriForm
import uk.gov.hmrc.ukimauthcheckerfrontend.views.html.InputView

class InputControllerSpec
    extends AnyWordSpec
    with Matchers
    with GuiceOneAppPerSuite
    with Injecting {

  "InputController" should {

    "render the input page on a GET" in {
      val controller = inject[InputController]
      val request = FakeRequest(GET, routes.InputController.onPageLoad.url)
      val result = controller.onPageLoad.apply(request)

      status(result) mustBe OK
      val page = contentAsString(result)
      page must include(
        "What is the EORI number of the business you want to check?"
      )
      page must include(
        "The first 2 letters are the country code, like GB or XI. This is followed by 12 digits, like GB123456123456."
      )
    }

    "handle a valid form submission on POST" in {
      val controller = inject[InputController]
      val request = FakeRequest(POST, routes.InputController.postInputEori.url)
        .withFormUrlEncodedBody("eori-input" -> "GB123456789000")
      val result = controller.postInputEori.apply(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(
        routes.ResultController.onPageLoad.url
      )
    }

    "handle an invalid country code form submission on POST" in {
      val controller = inject[InputController]
      val request = FakeRequest(POST, routes.InputController.postInputEori.url)
        .withFormUrlEncodedBody("eori-input" -> "FR123456123456")
      val result = controller.postInputEori.apply(request)

      status(result) mustBe BAD_REQUEST
      val doc = Jsoup.parse(contentAsString(result))

      doc
        .select(".govuk-error-summary__title")
        .text mustEqual "There is a problem"
      doc
        .select(".govuk-error-summary__body")
        .text must include("EORI number must start with GB or XI")
      doc.select("#eori-input-error").text must include(
        "Error: Re-enter the EORI number with the correct prefix"
      )
    }
    "handle an invalid EORI number form submission on POST" in {
      val controller = inject[InputController]
      val request = FakeRequest(POST, routes.InputController.postInputEori.url)
        .withFormUrlEncodedBody("eori-input" -> "GB****")
      val result = controller.postInputEori.apply(request)

      status(result) mustBe BAD_REQUEST
      val doc = Jsoup.parse(contentAsString(result))

      doc
        .select(".govuk-error-summary__title")
        .text mustEqual "There is a problem"
      doc
        .select(".govuk-error-summary__body")
        .text must include("EORI number must be 12 or 15 digits long")
      doc.select("#eori-input-error").text must include(
        "Error: Re-enter an EORI number with the correct number of digits"
      )
    }
    "handle an empty form submission on POST" in {
      val controller = inject[InputController]
      val request = FakeRequest(POST, routes.InputController.postInputEori.url)
        .withFormUrlEncodedBody("eori-input" -> "")
      val result = controller.postInputEori.apply(request)

      status(result) mustBe BAD_REQUEST
      val doc = Jsoup.parse(contentAsString(result))

      doc
        .select(".govuk-error-summary__title")
        .text mustEqual "There is a problem"
      doc
        .select(".govuk-error-summary__body")
        .text must include("You must enter an EORI number to continue")
      doc.select("#eori-input-error").text must include(
        "Error: Enter an EORI number"
      )
    }
  }
}
