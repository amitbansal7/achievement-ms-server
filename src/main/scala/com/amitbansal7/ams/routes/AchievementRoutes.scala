package com.amitbansal.ams.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.config.JsonSupport._
import com.amitbansal.ams.models.User
import com.amitbansal7.ams.models.Achievement
import com.amitbansal7.ams.services.AchievementService
import com.amitbansal7.ams.services.AchievementService.AchievementServiceResponseToken

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.reflect.io.File
import scala.util.{Failure, Success}

object AchievementRoutes {
  def route: Route = {
    pathPrefix("achievements") {
      (path("add") & post) {
        formField(
          'title,
          'rollNo,
          'department,
          'semester.as[Int],
          'date,
          'shift,
          'section,
          'sessionFrom,
          'sessionTo,
          'venue,
          'category,
          'participated.as[Boolean],
          'name,
          'description,
          'eventName,
        ) { (title, rollNo, department, semester, date, shift, section, sessionFrom, sessionTo, venue, category, participated, name, description, eventName) =>
          uploadedFile("image") {
            case (meta, file) =>
              complete(
                StatusCodes.OK,
                AchievementService.addAchievement(title, rollNo, department, semester, date, shift, section, sessionFrom, sessionTo, venue, category, participated, name, description, eventName, file, meta)
              )
          }
        }
      } ~ (path("approve") & post) {
        parameter('id, 'token) { (id, token) =>
          complete(StatusCodes.OK, AchievementService.approveAch(id, token))
        }
      } ~ (path("unapprove") & post) {
        parameter('id, 'token) { (id, token) =>
          complete(StatusCodes.OK, AchievementService.unApproveAch(id, token))
        }
      } ~ (path("delete") & post) {
        parameter('id, 'token) { (id, token) =>
          complete(StatusCodes.OK, AchievementService.deleteAch(id, token))
        }
      } ~ (path("get" / Segment) & get) { id =>
        val res = AchievementService.getOne(id)

        if (!res.isDefined) complete(StatusCodes.NotFound)
        else onSuccess(res.get){
          case ach:Achievement => complete(StatusCodes.OK, ach)
          case _ => complete(StatusCodes.NotFound)
        }
      } ~ (path("all") & get) {
        parameter(
          'rollNo.?,
          'department.?,
          'semester.as[Int].?,
          'dateFrom.?,
          'dateTo.?,
          'shift.?,
          'section.?,
          'sessionFrom.?,
          'sessionTo.?,
          'category.?,
          'offset.as[Int].?,
          'limit.as[Int].?
        ) { (rollNo, department, semester, dateFrom, dateTo, shift, section, sessionFrom, sessionTo, category, offset, limit) =>
          val f = AchievementService.getAllApproved(rollNo, department, semester, dateFrom, dateTo, shift, section, sessionFrom, sessionTo, category, offset, limit)
          complete(StatusCodes.OK, f.map(r => r))
        }
      } ~ (path("unapproved") & get) {
        parameter(
          'token,
          'rollNo.?,
          'semester.as[Int].?,
          'dateFrom.?,
          'dateTo.?,
          'shift.?,
          'section.?,
          'sessionFrom.?,
          'sessionTo.?,
          'category.?,
          'offset.as[Int].?,
          'limit.as[Int].?
        ) { (token, rollNo, semester, dateFrom, dateTo, shift, section, sessionFrom, sessionTo, category, offset, limit)=>
          complete(StatusCodes.OK, AchievementService.getAllUnapproved(token, rollNo, semester, dateFrom, dateTo, shift, section, sessionFrom, sessionTo, category, offset, limit))
        }
      }
    }
  }
}
