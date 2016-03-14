package controllers

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.JsValue
import scala.concurrent.Future
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.Configuration

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's currency page.
 */
@Singleton
class CurrencyController @Inject() (configuration: Configuration, ws: WSClient) extends Controller {

  val currencyLogger = Logger(this.getClass)
  val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val initialDate = dateFormatter.parseLocalDate(configuration.getString("play.currency.earliestDate").get)

  /**
   * Display the date given
   */
  def date(dateInput: String) = Authenticated.async {

    try {
      val localDate: LocalDate = dateFormatter.parseLocalDate(dateInput)
      val currentDate: LocalDate = LocalDate.now()

      if (! localDate.isAfter(initialDate) && localDate.isBefore(currentDate)){
        currencyLogger.error(s"requested $dateInput is not in range")
        Future.successful(BadRequest(s"requested $dateInput is not in range"))
      } else {
        getFromFixIO(dateInput).map { json =>
          currencyLogger.info(s"the returned json is $json")
          Ok(s"TARDIS currency advisor for date $dateInput")
        }
      }
    } catch {
      case e: IllegalArgumentException => Future.successful(BadRequest(s"$dateInput is in wrong format"))
    }

  }

  private def getFromFixIO(date: String): Future[JsValue] = {
    val url = s"http://api.fixer.io/$date"
    ws.url(url).get().map(_.json)
  }
}
