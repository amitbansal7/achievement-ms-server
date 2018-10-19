package com.amitbansal.ams.services

import com.amitbansal.ams.models.User
import com.amitbansal.ams.repositories.UserRepository
import com.amitbansal7.ams.models.Achievement
import com.amitbansal7.ams.services.AchievementService.AchievementServiceResponse
import com.amitbansal7.ams.services.{ AchievementService, JwtService }

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global
import pdi.jwt.{ Jwt, JwtAlgorithm, JwtClaim, JwtHeader, JwtOptions }

object UserService {

  val secretCode = "code"

  def existByEmail(email: String): Boolean = {
    val user = Await.result(UserRepository.getByEmail(email), 1 seconds)
    user != null
  }

  def reset(email: String, newEmail: String, firstName: String, lastName: String, password: String) = {
    UserRepository.getByEmail(email).map {
      case user: User if user.password == User.getPasshash(password) =>
        UserRepository.reset(email, newEmail, firstName, lastName)
        UserServiceResponse(true, "Profile successfully saved")

      case _ => UserServiceResponse(false, "Email or password doesn't match")
    }
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
        AuthRes(true, "User is authenticated", JwtService.getJwtToken(user.email, user.department))
      case _ =>
        AuthRes(false, "User is not authenticated", "")
    }
  }

  def isUserValid(token: String): Future[Option[UserData]] = {
    val userF = AchievementService.getUserFromToken(token)
    userF.map {
      case Some(user) => Some(UserData(user.email, user.firstName, user.lastName, user.department))
      case None => None
    }
  }

  def addUser(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    code: String,
    department: String
  ): UserServiceResponse = {
    if (code != secretCode)
      return UserServiceResponse(false, s"Secret code doesn't match")

    if (!Achievement.departments.contains(department))
      return UserServiceResponse(false, "Not a valid department")

    if (existByEmail(email))
      return UserServiceResponse(false, s"User with email ${email} already exists")
    else {
      UserRepository.addUser(User.apply(email, password, firstName, lastName, department))
      UserServiceResponse(true, "Account successfully created")
    }
  }

  case class UserData(email: String, firstName: String, lastName: String, department: String)

  case class UserServiceResponse(bool: Boolean, message: String)

  case class AuthRes(bool: Boolean, message: String, token: String)

}
