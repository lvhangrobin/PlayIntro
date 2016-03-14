package controllers

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import play.api.{Configuration, _}
import play.api.mvc._
import services.Client

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import javax.inject.{Inject, _}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's currency page.
 */
@Singleton
class CurrencyController @Inject() (configuration: Configuration, httpClient: Client) extends Controller {

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
        getMaxProfit(dateInput).map { currency =>
          Ok(f"If you go back to $dateInput you should buy ${currency._1} and sell them back when you come back with a profit of ${currency._2}%.2f")
        }
      }
    } catch {
      case e: IllegalArgumentException => Future.successful(BadRequest(s"$dateInput is in wrong format"))
    }

  }

  private def getFromFixIO(date: String): Future[Map[String, Double]] = {
    val url = s"http://api.fixer.io/$date"
    httpClient.get(url).map(r => (r \ "rates").as[Map[String, Double]])
  }

  private def getMaxProfit(past: String, today: String = "latest"): Future[(String, Double)] = {
    val pastRate = getFromFixIO(past)
    val todayRate = getFromFixIO(today)

    for {
      pastMapping <- pastRate
      todayMapping <- todayRate
    } yield {
      val pastInverse = pastMapping.mapValues(1 / _)
      val todayInverse = todayMapping.mapValues(1 / _)
      val mostProfitableCurrencyAndProfit = pastInverse.keySet.intersect(todayInverse.keySet).toList.map{ currency =>
        currency -> (todayInverse(currency) - pastInverse(currency))
      }.sortBy(_._2).last

      (
        mostProfitableCurrencyAndProfit._1,
        todayInverse(mostProfitableCurrencyAndProfit._1) / pastInverse(mostProfitableCurrencyAndProfit._1) - 1
      )
    }
  }
}
