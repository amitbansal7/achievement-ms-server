package com.amitbansal.ams.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.services.UserService
import com.amitbansal.ams.config.JsonSupport._

object UserRoutes {

  def route:Route = {
    pathPrefix("users"){
      (path("add") & post){
        parameter(
          'email,
          'password,
          'firstName,
          'lastName,
          'code
        ){
          (email, password, firstName, lastName, code) =>
            complete(StatusCodes.OK, UserService.addUser(email, password, firstName, lastName, code))
        }
      }~
        (path("auth")& get){
          parameter('email, 'password){(email, password) =>
            complete(StatusCodes.OK, UserService.authenticateUser(email, password))
          }
        }
    }
  }
}
