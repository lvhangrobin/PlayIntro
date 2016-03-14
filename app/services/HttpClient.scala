package services

import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent._

trait Client {
  def get(url: String): Future[JsValue]
}

class HttpClient @Inject() (ws: WSClient) extends Client {
  def get(url: String): Future[JsValue] = ws.url(url).get().map(_.json)
}
