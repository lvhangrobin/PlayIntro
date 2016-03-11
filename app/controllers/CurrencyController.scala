package controllers

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import javax.inject._
import play.api._
import play.api.mvc._

import javax.swing.text.DateFormatter

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's currency page.
 */
@Singleton
class CurrencyController @Inject() extends Controller {

  val currencyLogger = Logger(this.getClass)
  val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  /**
   * Display the date given
   */
  def date(dateInput: String) = Action {

    try {
      val localDate: LocalDate = dateFormatter.parseLocalDate(dateInput)
      val currentDate: LocalDate = LocalDate.now()
      val initialDate: LocalDate = new LocalDate(2000, 1, 1)
      if (! localDate.isAfter(initialDate) && localDate.isBefore(currentDate)){
        currencyLogger.error(s"requested $dateInput is not in range")
        BadRequest(s"requested $dateInput is not in range")
      } else {
        Ok(s"TARDIS currency advisor for date $dateInput")
      }
    } catch {
      case e: IllegalArgumentException => BadRequest(s"$dateInput is in wrong format")
    }

  }

}
