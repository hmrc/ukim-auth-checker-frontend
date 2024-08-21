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

class IndexControllerSpec
  extends AnyWordSpec
    with Matchers
    with GuiceOneAppPerSuite
    with Injecting {

  "IndexController" should {

    "render the index page on a GET" in {
      val controller = inject[IndexController]
      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)
      val result = controller.onPageLoad.apply(request)

      status(result) mustBe OK
      val page = contentAsString(result)
      val doc = Jsoup.parse(page)

      // Check that the correct title is rendered
      doc.select("title").text must include("Start Page")

      // Check that the main heading is correct
      doc.select("h1.govuk-heading-xl").text mustBe "ukim-auth-checker-frontend"

      // Check that the body contains the expected text from the messages file
      doc.select("p.govuk-body").text mustBe "This is your new service"
    }
  }
}
