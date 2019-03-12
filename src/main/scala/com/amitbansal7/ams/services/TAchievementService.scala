package com.amitbansal7.ams.services

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import com.amitbansal.ams.models.User
import com.amitbansal.ams.repositories.UserRepository
import com.amitbansal.ams.services.UserService
import com.amitbansal.ams.services.UserService.UserData
import com.amitbansal7.ams.models.TAchievement
import com.amitbansal7.ams.repositories.TAchievementRepository
import com.amitbansal7.ams.services.TAchievementService.{TAchievementServiceData, TAchievementServiceResponse}
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TAchievementService {

  case class TAchievementServiceResponse(bool: Boolean, message: String)

  case class TAchievementServiceData(bool: Boolean, user: Option[UserData], achs: Seq[TAchievement])

}

class TAchievementService(tAchievementRepository: TAchievementRepository, userService: UserService, utils: Utils, userRepository: UserRepository) {

  def add(token: String, taType: String, date: String, description: String, msi: Boolean, international: Boolean): Future[TAchievementServiceResponse] = {

    if (!TAchievement.taTypes.contains(taType))
      return Future {
        TAchievementServiceResponse(false, "Invalid Type")
      }
    else {
      userService.getUserFromToken(token).map {
        case Some(user) =>
          tAchievementRepository.add(TAchievement(user._id, taType, date, description, msi, international))
          TAchievementServiceResponse(true, "Successfully added.")
        case None => TAchievementServiceResponse(false, "Access Denied")
      }
    }
  }

  def update(token: String, id: String, taType: String, date: String, description: String, msi: Boolean, international: Boolean): Future[TAchievementServiceResponse] = {

    val objId = utils.checkObjectId(id)

    if (!objId.isDefined) return Future {
      TAchievementServiceResponse(false, "Invalid id")
    }

    val tAchFromId = tAchievementRepository.getOneById(objId.get)

    if (!TAchievement.taTypes.contains(taType))
      return Future {
        TAchievementServiceResponse(false, "Invalid Type")
      }
    else {
      userService.getUserFromToken(token).map {
        case Some(user) =>
          checkIfTAchBelongsToThisUser(tAchFromId, user) map {
            case true =>
              tAchievementRepository.update(objId.get, TAchievement(objId.get, user._id, taType, date, description, msi, international))
              TAchievementServiceResponse(true, "Successfully updated.")
            case false =>
              TAchievementServiceResponse(false, "Access Denied")
          }
        case None => Future {
          TAchievementServiceResponse(false, "Access Denied")
        }
      }
    }.flatMap(identity)
  }

  def getAllForUserId(userId: String): Future[TAchievementServiceData] = {
    utils.checkObjectId(userId) match {
      case Some(objId) =>
        val user = userRepository.getById(objId).map { u =>
          if (u != null)
            Some(UserData(u._id, u.email, u.firstName, u.lastName, u.department, u.shift))
          else None
        }
        user.map(u => tAchievementRepository.getAllByUserId(objId).map(d => TAchievementServiceData(u.isDefined, u, d))).flatMap(identity)

      case None => Future {
        TAchievementServiceData(false, None, List[TAchievement]())
      }
    }
  }

  def filterAll(fromDate: Option[String], toDate: Option[String], department: Option[String]) = ???

  def getAll(fromDate: Option[String], toDate: Option[String], department: Option[String]): Future[Seq[TAchievement]] = {
    //    val allUsers = userRepository.getAllUsers()

    val allTachs = tAchievementRepository.getAll
    //    val temp: String = allTachs.map(seq => seq.groupBy(_.user))
    //    println(allTachs.map(seq => seq.groupBy(_.user)).asInstanceOf[Map[String, Seq[TAchievement]]])
    allTachs
  }

  def checkIfTAchBelongsToThisUser(tAch: Future[TAchievement], user: User): Future[Boolean] =
    for {
      tAch <- tAch
    } yield (user._id == tAch.user)

  def deleteOne(id: String, token: String): Future[TAchievementServiceResponse] = {
    val userFromToken = userService.getUserFromToken(token)
    val objId = utils.checkObjectId(id)
    val tAchById = tAchievementRepository.getOneById(objId.get)

    if (!objId.isDefined) return Future {
      TAchievementServiceResponse(false, "Invalid id")
    }

    userFromToken.map {
      case Some(user) => checkIfTAchBelongsToThisUser(tAchById, user) map { res =>
        if (res) {
          tAchievementRepository.deleteOne(objId.get)
          TAchievementServiceResponse(true, "Deletion Successful")
        } else {
          TAchievementServiceResponse(false, "Access Denied")
        }
      }
      case None =>
        Future {
          TAchievementServiceResponse(false, "Access Denied")
        }
    }.flatMap(identity)
  }

}
