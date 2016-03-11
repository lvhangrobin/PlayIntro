package controllers

import play.api.mvc.Results._

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.mvc.Security.AuthenticatedBuilder

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller{

  val homeLogger = Logger(this.getClass)
  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    homeLogger.debug("test")
    homeLogger.info("info log")
    homeLogger.warn("warn log")
    homeLogger.error("error log")

    Ok("Welcome to the Time Advisor Research and Development Institute of Science")
  }

}

