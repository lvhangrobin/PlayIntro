package controllers

import org.joda.time.{DateTime, LocalDate}
import org.joda.time.format.DateTimeFormat
import play.api.{Configuration, Logger}
import play.api.mvc.{Action, Controller}

import scala.util.Random
import javax.inject.Inject


class RandomController @Inject()(configuration: Configuration) extends Controller {

  val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val FUTURE_YEAR_IN_SECONDS = new DateTime().plusYears(20).getMillis / 1000
  val randomLogger = Logger(this.getClass)

  def index() = Authenticated {
    randomLogger.debug(s"${FUTURE_YEAR_IN_SECONDS.toInt}")
    val randomDate: LocalDate = new LocalDate(new Random().nextInt(FUTURE_YEAR_IN_SECONDS.toInt).toLong * 1000)
    val dateInput: String = dateFormatter.print(randomDate)

    Redirect(s"/currency/$dateInput")
  }

}
