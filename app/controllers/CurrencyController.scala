package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's currency page.
 */
@Singleton
class CurrencyController @Inject() extends Controller {

  val currencyLogger = Logger(this.getClass)
  /**
   * Display the date given
   */
  def date(dateInput: String) = Action {
    currencyLogger.debug("test")
    Ok(s"TARDIS currency advisor for date $dateInput")
  }

}
