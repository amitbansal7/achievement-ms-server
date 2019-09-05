package com.amitbansal.ams.services

import cats.data.OptionT
import com.amitbansal.ams.Application
import com.amitbansal.ams.models.User
import com.amitbansal.ams.repositories.UserRepository
import com.amitbansal7.ams.models.Achievement
import com.amitbansal7.ams.services.AchievementService.AchievementServiceResponse
import com.amitbansal7.ams.services.{ AchievementService, JwtService }
import org.mongodb.scala.bson.ObjectId
import cats.implicits._
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global
import pdi.jwt.{ Jwt, JwtAlgorithm, JwtClaim, JwtHeader, JwtOptions }

import scala.util.parsing.json.JSON

object UserService {

  case class UserData(id: ObjectId, email: String, firstName: String, lastName: String, department: String, shift: String, designation: String)

  case class UserServiceResponse(bool: Boolean, message: String)

  case class AuthRes(bool: Boolean, message: String, token: String)

}

class UserService(userRepository: UserRepository, jwtService: JwtService) {

  import UserService._

  val secretCode = Application.resource.getOrElse("inviteCode", "invalidCode").toString

  def existByEmail(email: String): Boolean = {
    val user = Await.result(userRepository.getByEmail(email), 1 seconds)
    user != null
  }

  def reset(email: String, newEmail: String, firstName: String, lastName: String, password: String, designation: String) = {
    userRepository.getByEmail(email).map {
      case user: User if user.password == User.getPasshash(password) =>
        if (email != newEmail && existByEmail(newEmail)) {
          UserServiceResponse(false, s"Email(${newEmail}) already in use")
        } else {
          userRepository.reset(email, newEmail, firstName, lastName, designation)
          UserServiceResponse(true, "Profile successfully saved")
        }
      case _ => UserServiceResponse(false, "Email or password doesn't match")
    }
  }

  def resetPass(email: String, currentPass: String, newPass: String) = {
    userRepository.getByEmail(email).map {
      case user: User if user.password.equals(User.getPasshash(currentPass)) =>
        userRepository.changePass(email, User.getPasshash(newPass))
        UserServiceResponse(true, "Password successfully changed")

      case _ => UserServiceResponse(false, "Email or password doesn't match")
    }
  }

  def authenticateUser(email: String, password: String): Future[AuthRes] = {
    userRepository.getByEmail(email).map {
      case user: User if user.password == User.getPasshash(password) =>
        AuthRes(true, "User is authenticated", jwtService.getJwtToken(user.email, user.department))
      case _ =>
        AuthRes(false, "User is not authenticated", "")
    }
  }

  def isUserValid(token: String): Future[Option[UserData]] = {
    OptionT(getUserFromToken(token)).map { user =>
      Some(UserData(user._id, user.email, user.firstName, user.lastName, user.department, user.shift, user.designation))
    }.getOrElse(None)
  }

  def getUserFromToken(token: String): Future[Option[User]] =
    jwtService.decodeToken(token) match {
      case Success(value) =>
        JSON.parseFull(value._2) match {
          case Some(map: Map[String, String]) =>
            map.get("user").map { email =>
              userRepository.getByEmail(email).map(u => Some(u))
            }.getOrElse(Future(None))
        }
      case _ => Future(None)
    }

  def addUser(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    code: String,
    department: String,
    shift: String,
    designation: String
  ): UserServiceResponse = {
    if (code != secretCode)
      return UserServiceResponse(false, s"Secret code doesn't match")

    if (!Achievement.departments.contains(department))
      return UserServiceResponse(false, "Not a valid department")

    if (!Achievement.shifts.contains(shift))
      return UserServiceResponse(false, "Invalid shift")

    if (existByEmail(email))
      return UserServiceResponse(false, s"User with email ${email} already exists")
    else {
      userRepository.addUser(User.apply(email, password, firstName, lastName, department, shift, designation))
      UserServiceResponse(true, "Account successfully created")
    }
  }
}
