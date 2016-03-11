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
    }

  }

  "CurrencyContoller" should {


    "render the date given" in {
      val validDate = "2016-03-11"
      val currencyDate = route(app, fakeGetRequestWithAuth(s"/currency/$validDate")).get

      status(currencyDate) mustBe OK
      contentType(currencyDate) mustBe Some("text/plain")
      contentAsString(currencyDate) mustBe currencyDateResponse(validDate)
    }

    "render bad request when invalid date is given" in {
      val invalidDate = "2016-33-11"
      val currencyDate = route(app, fakeGetRequestWithAuth(s"/currency/$invalidDate")).get

      status(currencyDate) mustBe BAD_REQUEST
      contentType(currencyDate) mustBe Some("text/plain")
      contentAsString(currencyDate) mustBe invalidDateResponse(invalidDate)
    }

    "render bad request when date is not in range" in {
      val outOfRangeDate = "2005-11-11"
      val currencyDate = route(app, fakeGetRequestWithAuth(s"/currency/$outOfRangeDate")).get

      status(currencyDate) mustBe BAD_REQUEST
      contentType(currencyDate) mustBe Some("text/plain")
      contentAsString(currencyDate) mustBe outOfRangeDateResponse(outOfRangeDate)
    }
  }

  "CountController" should {

    "return an increasing count" in {
      contentAsString(route(app, FakeRequest(GET, "/count")).get) mustBe "0"
      contentAsString(route(app, FakeRequest(GET, "/count")).get) mustBe "1"
      contentAsString(route(app, FakeRequest(GET, "/count")).get) mustBe "2"
    }
  }

  "RandomController" should {

    "redirect to random currency date" in {
      val randomRedirect = route(app, fakeGetRequestWithAuth(s"/random")).get

      status(randomRedirect) mustBe SEE_OTHER
      contentType(randomRedirect) mustBe None
      header("Location", randomRedirect).get must startWith ("/currency/")
    }

    "redirect to unauthorized if with no auth" in {
      val randomRedirect = route(app, FakeRequest(GET, s"/random")).get

      status(randomRedirect) mustBe UNAUTHORIZED
    }

    "redirect to unauthorized if authentication failed" in {
      val randomRedirect = route(app, fakeGetRequestWithInvalidAuth(s"/random")).get

      status(randomRedirect) mustBe UNAUTHORIZED
    }
  }
}
