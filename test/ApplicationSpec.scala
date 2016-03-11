import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

import TestData._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "Routes" should {

    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "HomeController" should {

    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) mustBe homePageResponse
    }

  }

  "CurrencyContoller" should {
    "render the date given" in {
      val currencyDate = route(app, FakeRequest(GET, "/currency/2016-03-11")).get

      status(currencyDate) mustBe OK
      contentType(currencyDate) mustBe Some("text/plain")
      contentAsString(currencyDate) mustBe currencyDateResponse20160311
    }
  }

  "CountController" should {

    "return an increasing count" in {
      contentAsString(route(app, FakeRequest(GET, "/count")).get) mustBe "0"
      contentAsString(route(app, FakeRequest(GET, "/count")).get) mustBe "1"
      contentAsString(route(app, FakeRequest(GET, "/count")).get) mustBe "2"
    }

  }

}
