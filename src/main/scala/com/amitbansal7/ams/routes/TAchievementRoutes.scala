package com.amitbansal7.ams.routes

import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import com.amitbansal7.ams.services.TAchievementService
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.config.JsonSupport._

class TAchievementRoutes(tAchievementService: TAchievementService) {
  def route: Route = {
    pathPrefix("tachievements") {
      (path("allUserid") & get) {
        parameter('userId) { (userId) =>
          complete(tAchievementService.getAllForUserId(userId))
        }
      } ~ (path("all") & get) {
        parameter('fromDate.?, 'toDate.?) { (fromDate, toDate) =>
          complete(tAchievementService.getAll(fromDate, toDate))
        }
      } ~ (path("delete") & delete) {
        parameter('id, 'token) { (id, token) =>
          complete(tAchievementService.deleteOne(id, token))
        }
      } ~ (path("update") & put) {
        formField(
          'token,
          'id,
          'taType,
          'date,
          'description,
          'msi.as[Boolean],
          'international.as[Boolean]
        ) { (token, id, taType, date, description, msi, international) =>
            complete(tAchievementService.update(token, id, taType, date, description, msi, international))
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
