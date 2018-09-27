package com.amitbansal.ams.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.{StatusCodes}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.services.UserService
import com.amitbansal.ams.config.JsonSupport._

import scala.concurrent.Future
import scala.concurrent.duration._

object UserRoutes {

  def route: Route = {
    pathPrefix("users") {
      (path("add") & post) {
        formField(
          'email,
          'password,
          'firstName,
          'lastName,
          'code,
          'department
        ) {
          (email, password, firstName, lastName, code, department) =>
            complete(StatusCodes.OK, UserService.addUser(email, password, firstName, lastName, code, department))
        }
      } ~
        (path("auth") & post) {
          formField('email.as[String], 'password.as[String]) { (email, password) =>
            complete(StatusCodes.OK, UserService.authenticateUser(email, password))
          }
        } ~
        (path("resetpass") & post) {
          formField('email, 'currentpass, 'newpass) { (email, currentpass, newpass) =>
            complete(StatusCodes.OK, UserService.resetPass(email, currentpass, newpass))
          }
        }
    }
  }
}
