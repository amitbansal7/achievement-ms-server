package com.amitbansal7.ams.routes

import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import com.amitbansal7.ams.services.TAchievementService
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.config.JsonSupport._

class TAchievementRoutes(tAchievementService: TAchievementService) {
  def route: Route = {
    pathPrefix("tachievements") {
      (path("all") & get) {
        parameter('token.?) { (token) =>
          complete(tAchievementService.getAll(token))
        }
      } ~ (path("add") & post) {
        formField(
          'token,
          'taType,
          'date,
          'description,
          'msi.as[Boolean],
          'international.as[Boolean]
        ) { (token, taType, date, description, msi, international) =>
            complete(tAchievementService.add(token, taType, date, description, msi, international))
          }
      }
    }
  }

}
