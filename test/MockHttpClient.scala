import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import services.HttpClient
import scala.concurrent.Future
import javax.inject.Inject

class MockHttpClient @Inject() (ws: WSClient) extends HttpClient(ws){
  override def get(url: String) = url match {
    case "http://api.fixer.io/2011-01-01" => Future.successful(Json.obj("rates" -> Json.obj("GBK" -> 0.7, "MXN" -> 19.7)))
    case "http://api.fixer.io/latest" => Future.successful(Json.obj("rates" -> Json.obj("GBK" -> 1.0, "MXN" -> 19.8)))
  }
}
