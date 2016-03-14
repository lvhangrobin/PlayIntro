package models

import play.api.mvc.QueryStringBindable


case class Coordinate(lat: Double = 49.26382D, long: Double = -123.104321D)

object Coordinate {

  implicit def queryStringBindable(implicit doubleBinder: QueryStringBindable[Double]) = new QueryStringBindable[Coordinate] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Coordinate]] = {
      (doubleBinder.bind("lat", params), doubleBinder.bind("long", params)) match {

        case (Some(Right(lat)), Some(Right(long))) if lat >= -90 && lat <= 90 && long >= -180 && long <= 180 =>
          Some(Right(Coordinate(lat, long)))
        case (None, None) =>
          Some(Right(Coordinate()))
        case _ =>
          Some(Left("Unable to bind an Coordinate"))
      }
    }

    override def unbind(key: String, coord: Coordinate): String = {
      doubleBinder.unbind("lat", coord.lat) + "&" + doubleBinder.unbind("long", coord.long)
    }
  }
}