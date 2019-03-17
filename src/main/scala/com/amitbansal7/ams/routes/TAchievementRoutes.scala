package com.amitbansal7.ams.routes

import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import com.amitbansal7.ams.services.TAchievementService
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.config.JsonSupport._

class TAchievementRoutes(tAchievementService: TAchievementService) {
  def route: Route = {
    pathPrefix("tachievements") {

      //Get all achievement by userid
      (path("allUserid") & get) {
        parameter('userId) { (userId) =>
          complete(tAchievementService.getAllForUserId(userId))
        }
        //get all aggregated(counted) data
      } ~ (path("allagg") & get) {
        parameter('fromDate.?, 'toDate.?) { (fromDate, toDate) =>
          complete(tAchievementService.getAllAggregated(fromDate, toDate))
        }
        //get all users with their achievements, optional filters: fromDate, toDate, department
      } ~ (path("all") & get) {
        parameter('fromDate.?, 'toDate.?, 'department.?) { (fromDate, toDate, department) =>
          complete(tAchievementService.getAll(fromDate, toDate, department))
        }
        //Delete an achievement with achievement id and valid token
      } ~ (path("delete") & delete) {
        parameter('id, 'token) { (id, token) =>
          complete(tAchievementService.deleteOne(id, token))
        }
        //update an achievement with token and id.
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
        //add an achievement using valid token.
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
