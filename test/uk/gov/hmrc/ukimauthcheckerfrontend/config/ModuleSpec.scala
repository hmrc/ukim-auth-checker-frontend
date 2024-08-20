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

package uk.gov.hmrc.ukimauthcheckerfrontend.config

import com.google.inject.{Guice, Injector, AbstractModule}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class ModuleSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "Module" should {

    "bind AppConfig as an eager singleton" in {
      // Mock the Configuration and ServicesConfig
      val mockConfig = mock[Configuration]
      val mockServicesConfig = mock[ServicesConfig]

      // Define what the mocks should return
      when(mockConfig.getOptional[Boolean]("features.welsh-language-support"))
        .thenReturn(Some(true)) // or Some(false) depending on your test case
      when(mockServicesConfig.baseUrl("pds-auth-checker-api"))
        .thenReturn("http://localhost:10161")

      // Create a custom Guice module to bind the mocks
      class TestModule extends AbstractModule {
        override def configure(): Unit = {
          bind(classOf[Configuration]).toInstance(mockConfig)
          bind(classOf[ServicesConfig]).toInstance(mockServicesConfig)
          bind(classOf[AppConfig]).asEagerSingleton()
        }
      }

      // Create the injector with the custom module
      val injector: Injector = Guice.createInjector(new TestModule)

      // Attempt to retrieve an instance of AppConfig from the injector
      val appConfigInstance = injector.getInstance(classOf[AppConfig])

      // Check that the instance is not null
      appConfigInstance must not be null

      // Verify that the instance is indeed an eager singleton
      val anotherAppConfigInstance = injector.getInstance(classOf[AppConfig])
      appConfigInstance mustBe theSameInstanceAs(anotherAppConfigInstance)
    }
  }
}
