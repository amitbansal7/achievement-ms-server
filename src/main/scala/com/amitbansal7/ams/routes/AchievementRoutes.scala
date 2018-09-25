package com.amitbansal.ams.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

object AchievementRoutes {
  def route:Route = {
    (path("hey") & get){
      complete(StatusCodes.OK, "hey")
    }
  }
}
