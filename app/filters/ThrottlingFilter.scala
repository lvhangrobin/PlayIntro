package filters

import akka.stream.Materializer
import org.joda.time.DateTime
import play.api.Configuration
import play.api.cache.{CacheApi, NamedCache}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import javax.inject._

/**
 * This is a simple filter that adds a header to all requests. It's
 * added to the application's list of filters by the
 * [[ThrottlingFilter]] class.
 *
 * @param mat This object is needed to handle streaming of requests
 * and responses.
 * @param exec This class is needed to execute code asynchronously.
 * It is used below by the `map` method.
 */
@Singleton
class ThrottlingFilter @Inject()(
    implicit override val mat: Materializer,
    exec: ExecutionContext,
    @NamedCache("throttling-cache") throttlingCache: CacheApi,
    configuration: Configuration) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val maxRequests = configuration.getInt("play.application.maxRequests").getOrElse(3)
    val apiKey: String = requestHeader.headers.get("api-key").getOrElse(requestHeader.remoteAddress)
    val now = DateTime.now()

    val cachedResult = throttlingCache.get[Seq[DateTime]](apiKey)
    val newCachedResult = cachedResult.map(_.dropWhile(_.plusMinutes(1).isBefore(now)) :+ now).getOrElse(Seq(now))
    throttlingCache.set(apiKey, newCachedResult, 1.minutes)

    val isThrottled = newCachedResult.length >= maxRequests

    if (isThrottled)
      Future.successful(Results.TooManyRequests)
    else
      nextFilter(requestHeader)
  }
}
