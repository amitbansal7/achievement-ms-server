package com.amitbansal.ams.services

import com.amitbansal.ams.models.User
import com.amitbansal.ams.repositories.UserRepository
import com.amitbansal7.ams.services.JwtService

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader, JwtOptions}


object UserService {

  val secretCode = "code"

  def existByEmail(email: String): Boolean = {
    val user = Await.result(UserRepository.getByEmail(email), 1 seconds)
    user != null
  }

  def resetPass(email: String, currentPass: String, newPass: String) = {
    UserRepository.getByEmail(email).map {
      case user: User if user.password.equals(User.getPasshash(currentPass)) =>
        UserRepository.changePass(email, User.getPasshash(newPass))
        UserServiceResponse(true, "Password successfully changed")

      case _ => UserServiceResponse(false, "Email or password doesn't match")
    }
  }

  def authenticateUser(email: String, password: String): Future[AuthRes] = {
    UserRepository.getByEmail(email).map {
      case user: User if user.password == User.getPasshash(password) =>
        AuthRes(true, "User is authenticated", JwtService.getJwtToken(user.email))
      case _ =>
        AuthRes(false, "User is not authenticated", "")
    }
  }

  def addUser(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    code: String,
    department: String):
  UserServiceResponse = {
    if (code != secretCode)
      return UserServiceResponse(false, s"Secret code doesn't match")

    if (existByEmail(email))
      return UserServiceResponse(false, s"User with email ${email} already exists")
    else {
      UserRepository.addUser(User.apply(email, password, firstName, lastName, department))
      UserServiceResponse(true, "Account successfully created")
    }
  }

  case class UserServiceResponse(bool: Boolean, message: String)

  case class AuthRes(bool: Boolean, message: String, token: String)

}
