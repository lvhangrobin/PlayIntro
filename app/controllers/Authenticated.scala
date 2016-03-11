package controllers

import play.api.mvc.Results._
import play.api.mvc.Security.AuthenticatedBuilder

object Authenticated extends AuthenticatedBuilder[String] (
  request => request.headers.get("api-key").filter(key => key.matches("^[0-9|a-z|A-Z]{32}$")),
  _ => Unauthorized("invalid api key")
)
