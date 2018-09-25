package com.amitbansal.ams.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.config.JsonSupport._
import com.amitbansal.ams.models.User
import com.amitbansal7.ams.services.AchievementService

object AchievementRoutes {
  def route: Route = {
    pathPrefix("achievements") {
      (path("add") & post) {
        parameter(
          'rollno,
          'department,
          'year.as[Int],
          'date,
          'venue,
          'category,
          'participated.as[Boolean],
          'name,
          'description,
          'eventName,
        ) { (rollno, department, year, date, venue, category, participated, name, description, eventName) =>
          complete(
            StatusCodes.OK,
            AchievementService.addAchievement(rollno, department.toLowerCase, year, date, venue, category.toLowerCase, participated, name, description, eventName)
          )
        }
      } ~ (path("approve") & post) {
        parameter('id) { id =>
          AchievementService.approveAch(id)
          complete(StatusCodes.OK, "approved")
        }
      }
    }
  }
}
