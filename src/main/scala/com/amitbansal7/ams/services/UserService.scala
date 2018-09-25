package com.amitbansal.ams.services

import com.amitbansal.ams.models.User
import com.amitbansal.ams.repositories.UserRepository

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import pdi.jwt.{Jwt, JwtAlgorithm, JwtHeader, JwtClaim, JwtOptions}


object UserService {

  val secretCode = "code"

  def existByEmail(email: String): Boolean = {
    val user = Await.result(UserRepository.getByEmail(email), 1 seconds)
    user != null
  }

  def getJwtToken(id: String, email: String):String =
    Jwt.encode("""{"user":1}""", "secret", JwtAlgorithm.HS384)

  def authenticateUser(email: String, password: String): Future[AuthRes] = {
    UserRepository.getByEmail(email).map {
      case user: User if user.password == User.getPasshash(password) =>
        AuthRes(true, "User is authenticated", getJwtToken(user._id.toHexString, user.email))
      case _ =>
        AuthRes(false, "User is not authenticated", "")
    }
  }

  def addUser(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    code: String):
  UserServiceResponse = {
    if (code != secretCode)
      return UserServiceResponse(false, s"Secret code doesn't match")

    if (existByEmail(email))
      return UserServiceResponse(false, s"User with email ${email} already exists")
    else {
      UserRepository.addUser(User.apply(email, password, firstName, lastName))
      UserServiceResponse(true, "Account successfully created")
    }
  }

  case class UserServiceResponse(bool: Boolean, message: String)
  case class AuthRes(bool: Boolean, message: String, jwt: String)

}
