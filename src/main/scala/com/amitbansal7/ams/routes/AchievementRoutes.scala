package com.amitbansal.ams.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.config.JsonSupport._
import com.amitbansal.ams.models.User
import com.amitbansal7.ams.services.AchievementService
import com.amitbansal7.ams.services.AchievementService.AchievementServiceResponseToken

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.reflect.io.File
import scala.util.{Failure, Success}

object AchievementRoutes {
  def route: Route = {
    pathPrefix("achievements") {
      (path("add") & post) {
        formField(
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
          uploadedFile("image") {
            case (meta, file) =>
              complete(
                StatusCodes.OK,
                AchievementService.addAchievement(rollno, department.toLowerCase, year, date, venue, category.toLowerCase, participated, name, description, eventName, file, meta)
              )
          }
        }
      } ~ (path("approve") & post) {
        parameter('id, 'token) { (id, token) =>
          complete(StatusCodes.OK, AchievementService.approveAch(id, token))
        }
      } ~ (path("all") & get) {
        parameter('department.?) { department =>
          val f = AchievementService.getAllApproved(department)
          complete(StatusCodes.OK, f.map(r => r))
        }
      } ~ (path("unapproved") & get) {
        parameter('token) { token =>
          complete(StatusCodes.OK, AchievementService.getAllUnapproved(token))
        }
      }
    }
  }
}
