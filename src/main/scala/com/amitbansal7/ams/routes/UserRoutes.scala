package com.amitbansal.ams.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.services.UserService
import com.amitbansal.ams.config.JsonSupport._
import com.amitbansal.ams.services.UserService.UserServiceResponse
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class UserRoutes(userService: UserService) {

  def route: Route = {
    pathPrefix("users") {
      (path("add") & post) {
        formField(
          'email,
          'password,
          'firstName,
          'lastName,
          'code,
          'department,
          'shift,
          'designation
        ) {
            (email, password, firstName, lastName, code, department, shift, designation) =>
              complete(StatusCodes.OK, userService.addUser(email.toLowerCase, password, firstName, lastName, code, department, shift, designation))
          }
      } ~
        (path("auth") & post) {
          formField('email.as[String], 'password.as[String]) { (email, password) =>
            complete(StatusCodes.OK, userService.authenticateUser(email.toLowerCase, password))
          }
        } ~
        (path("resetpass") & post) {
          formField('email, 'currentpass, 'newpass) { (email, currentpass, newpass) =>
            complete(StatusCodes.OK, userService.resetPass(email.toLowerCase, currentpass, newpass))
          }
        } ~
        (path("reset") & put) {
          formField('firstName, 'lastName, 'email, 'password, 'newEmail, 'designation) { (firstName, lastName, email, password, newEmail, designation) =>
            complete(StatusCodes.OK, userService.reset(email.toLowerCase, newEmail.toLowerCase, firstName, lastName, password, designation))
          }
        } ~
        (path("isvalid") & get) {
          parameter('token) { token =>
            onSuccess(userService.isUserValid(token)) {
              case Some(user) => complete(StatusCodes.OK, user)
              case None => complete(StatusCodes.Unauthorized)
            }
          }
        }
    }
  }
}
