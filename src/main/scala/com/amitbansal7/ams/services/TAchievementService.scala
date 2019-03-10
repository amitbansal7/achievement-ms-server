package com.amitbansal7.ams.services

import com.amitbansal.ams.models.User
import com.amitbansal.ams.services.UserService
import com.amitbansal7.ams.models.TAchievement
import com.amitbansal7.ams.repositories.TAchievementRepository
import com.amitbansal7.ams.services.TAchievementService.TAchievementServiceResponse
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TAchievementService {

  case class TAchievementServiceResponse(bool: Boolean, message: String)

}

class TAchievementService(tachievementRepository: TAchievementRepository, userService: UserService, utils: Utils) {

  def add(token: String, taType: String, date: String, description: String, msi: Boolean, international: Boolean): Future[TAchievementServiceResponse] = {

    if (!TAchievement.taTypes.contains(taType))
      return Future {
        TAchievementServiceResponse(false, "Invalid Type")
      }
    else {
      userService.getUserFromToken(token).map {
        case Some(user) =>
          tachievementRepository.add(TAchievement(user._id, taType, date, description, msi, international))
          TAchievementServiceResponse(true, "Successfully added.")
        case None => TAchievementServiceResponse(false, "Access Denied")
      }
    }
  }

  def getAllByToken(token: String): Future[Seq[TAchievement]] = userService.getUserFromToken(token).map {
    case Some(user) => tachievementRepository.getAllByToken(user._id)
    case None => tachievementRepository.getAll
  }.flatMap(identity)

  def getAll(token: Option[String]): Future[Seq[TAchievement]] =
    token match {
      case Some(token) => getAllByToken(token)
      case None => tachievementRepository.getAll
    }

}
