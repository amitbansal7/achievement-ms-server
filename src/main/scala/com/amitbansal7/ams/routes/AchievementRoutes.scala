package com.amitbansal.ams.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.config.JsonSupport._
import com.amitbansal.ams.models.User
import com.amitbansal7.ams.services.AchievementService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.reflect.io.File

object AchievementRoutes {
  def route: Route = {
    pathPrefix("achievements") {
      (toStrictEntity(2 seconds) & path("add") & post) {
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
        parameter('id) { id =>
          AchievementService.approveAch(id)
          complete(StatusCodes.OK, "approved")
        }
      } ~ (path("all") & get) {
        parameter('department) { department =>
          val f = AchievementService.getAllApproved(department.toLowerCase)
          complete(StatusCodes.OK, f.map(r => r))
        }
      } ~ (path("t") & post) {
        formField('name) { name =>
          fileUpload("image") {
            case (meta, file) =>
              println(file.toString() + " " + meta.toString + name)
              complete(StatusCodes.OK)
          }
        }
      }
    }
  }
}
