import play.api.http.HttpErrorHandler
import play.api.mvc.{Results, Result, RequestHeader}
import play.api.mvc.Results._

import scala.concurrent.Future


class ErrorHandler extends HttpErrorHandler{

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    if (statusCode == 400) {
      Future.successful(Results.BadRequest(message))
    } else {
      Future.successful(Status(statusCode)("A client error occurred: " + message))
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(InternalServerError("A Server Error Occurred: " + exception.getMessage))
  }
}
