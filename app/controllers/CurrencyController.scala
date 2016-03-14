package controllers

import models.Coordinate
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
  val forecastApiKey = configuration.getString("play.forecast.api_key").get

  /**
   * Display the date given
   */
  def date(dateInput: String, coord: Coordinate) = Authenticated.async {

    try {
      val localDate: LocalDate = dateFormatter.parseLocalDate(dateInput)
      val currentDate: LocalDate = LocalDate.now()

      if (! localDate.isAfter(initialDate) && localDate.isBefore(currentDate)){
        currencyLogger.error(s"requested $dateInput is not in range")
        Future.successful(BadRequest(s"requested $dateInput is not in range"))
      } else {

        for {
          (currency, profit)<- getMaxProfit(dateInput)
          recommendation <- getFromForecastIO(coord.lat, coord.long, localDate).map{case (temp,icon) => getRecommendation(temp, icon)}
        } yield Ok(
          f"""If you go back to $dateInput you should buy $currency
             |and sell them back when you come back with a profit of $profit%.2f.
             |Don't forget to bring your $recommendation""".stripMargin)

      }
    } catch {
      case e: IllegalArgumentException => Future.successful(BadRequest(e.getMessage))
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

  private def getFromForecastIO(lat: Double, long: Double, localDate: LocalDate): Future[(Double, String)] = {
    val formattedTime = s"${dateFormatter.print(localDate)}T00:00:00"
    val url = s"https://api.forecast.io/forecast/$forecastApiKey/$lat,$long,$formattedTime?units=si"
    httpClient.get(url).map(r =>
      (r \ "currently" \ "temperature").as[Double] -> (r \ "currently" \ "icon").as[String])
  }

  private def getRecommendation(temperature: Double, weather: String): String = {
    (temperature, weather) match {
      case (_, "rain") => "Umbrella"
      case (x, _) if x > 20 => "Shorts"
      case (x, _) if x <= 20 && x >= 10 => "Sweater"
      case (x, _) if x < 10 => "Winter Coat"
    }
  }
}
