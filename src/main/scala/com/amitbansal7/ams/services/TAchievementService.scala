package com.amitbansal7.ams.services

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import com.amitbansal.ams.models.User
import com.amitbansal.ams.repositories.UserRepository
import com.amitbansal.ams.services.UserService
import com.amitbansal.ams.services.UserService.UserData
import com.amitbansal7.ams.models.TAchievement
import com.amitbansal7.ams.repositories.TAchievementRepository
import com.amitbansal7.ams.services.TAchievementService._
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

object TAchievementService {

  case class TAchievementServiceResponse(bool: Boolean, message: String)

  case class TAchievementServiceData(bool: Boolean, user: Option[UserData], achs: Seq[TAchievement])

  case class TAchAggRes(user: UserData, data: Map[String, TAchLocations])

  case class TAchAllRes(id: ObjectId, email: String, firstName: String, lastName: String, department: String, shift: String, designation: String, achievements: Seq[TAchievement])

  case class TAchLocations(msi: TAchNatInt, others: TAchNatInt)

  case class TAchNatInt(int: Int, nat: Int)

}

class TAchievementService(tAchievementRepository: TAchievementRepository, userService: UserService, utils: Utils, userRepository: UserRepository) {

  def add(
    token: String,
    taType: String,
    international: Boolean,
    topic: String,
    published: String,
    sponsored: Option[Boolean],
    reviewed: Option[Boolean],
    date: String,
    description: Option[String],
    msi: Boolean,
  ): Future[TAchievementServiceResponse] = {

    if (!TAchievement.taTypes.contains(taType))
      return Future {
        TAchievementServiceResponse(false, "Invalid Type")
      }
    else {
      userService.getUserFromToken(token).map {
        case Some(user) =>
          tAchievementRepository.add(TAchievement(user._id, taType, international, topic, published, sponsored, reviewed, date, description, msi))
          TAchievementServiceResponse(true, "Successfully added.")
        case None => TAchievementServiceResponse(false, "Access Denied")
      }
    }
  }

  def update(
    token: String,
    id: String,
    taType: String,
    international: Boolean,
    topic: String,
    published: String,
    sponsored: Option[Boolean],
    reviewed: Option[Boolean],
    date: String,
    description: Option[String],
    msi: Boolean,
  ): Future[TAchievementServiceResponse] = {

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
              tAchievementRepository.update(objId.get, TAchievement(user._id, taType, international, topic, published, sponsored, reviewed, date, description, msi))
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
            Some(UserData(u._id, u.email, u.firstName, u.lastName, u.department, u.shift, u.designation))
          else None
        }
        user.map(u => tAchievementRepository.getAllByUserId(objId).map(d => TAchievementServiceData(u.isDefined, u, d))).flatMap(identity)

      case None => Future {
        TAchievementServiceData(false, None, List[TAchievement]())
      }
    }
  }

  def evalForOneUser(user: User, data: List[(String, Seq[TAchievement])]): TAchAggRes = {

    //(taType, TAchLocations)
    val mappedData = data.map { unit =>
      val (msi, others) = unit._2.partition(_.msi)
      val msiNatInt = msi.partition(_.international)
      val othersNatInt = others.partition(_.international)
      val msiLocations = TAchNatInt(msiNatInt._1.size, msiNatInt._2.size)
      val otherLocations = TAchNatInt(othersNatInt._1.size, othersNatInt._2.size)
      (unit._1, TAchLocations(msiLocations, otherLocations))
    }.toMap

    TAchAggRes(
      UserData(user._id, user.email, user.firstName, user.lastName, user.department, user.shift, user.designation),
      mappedData
    )
  }

  def getAll(fromDate: Option[String], toDate: Option[String], department: Option[String], taType: Option[String]): Future[Seq[TAchAllRes]] = {

    val allAchsFuture: Future[Seq[TAchievement]] = tAchievementRepository.getAll()
    val allAchsGroupedByUserFuture = allAchsFuture.map { all =>
      all.flatMap { ach =>
        if ((!fromDate.isDefined || (ach.date >= fromDate.get)) &&
          (!toDate.isDefined || (ach.date <= toDate.get)) &&
          (!taType.isDefined || (ach.taType == taType.get))
        ) List(ach)
        else List[TAchievement]()
      }
    }.map {
      all => all.groupBy(ach => ach.user)
    }

    val allUsersFuture = userRepository.getAllUsers()
    val allUsersFilteredByDeptFuture = allUsersFuture.map { users =>
      if (department.isDefined) users.filter(_.department == department.get)
      else users
    }

    val allUsersWithAchs = for {
      users <- allUsersFilteredByDeptFuture
      allAch <- allAchsGroupedByUserFuture
    } yield users.map { user =>
      TAchAllRes(user._id, user.email, user.firstName, user.lastName, user.department, user.shift, user.designation, allAch.getOrElse(user._id, List[TAchievement]()))
    }

    val allUsersWithAtleastOneAch = allUsersWithAchs.map {
      all => all.filter(_.achievements.size > 0)
    }

    return allUsersWithAtleastOneAch
  }

  def getAllAggregated(fromDate: Option[String], toDate: Option[String]) = {

    val allUsers = userRepository
      .getAllUsers()

    val userIdToUserMap = allUsers.map(users => users.map { user =>
      (user._id, user)
    }.toMap)

    val allTachs = tAchievementRepository.getAll.map { future =>
      future.filter { ach =>
        (!fromDate.isDefined || (fromDate.isDefined && ach.date >= fromDate.get)) &&
          (!toDate.isDefined || (toDate.isDefined && ach.date <= toDate.get))
      }
    }

    val groupedByUser = allTachs.map(d => d.groupBy(_.user).toList)

    val groupedByUserAndTaType = groupedByUser.map { future =>
      future.map { data => //(userId, Seq[Tachs])
        (data._1, data._2.groupBy(_.taType).toList)
      }
    }

    userIdToUserMap.map { map =>
      groupedByUserAndTaType.map { future =>
        future.map { grouped => //(userId, (taType, Seq[Achs]))
          evalForOneUser(map.get(grouped._1).get, grouped._2)
        }
      }
    }

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
      case Some(user) => checkIfTAchBelongsToThisUser(tAchById, user) map {
        case true =>
          tAchievementRepository.deleteOne(objId.get)
          TAchievementServiceResponse(true, "Deletion Successful")
        case false =>
          TAchievementServiceResponse(false, "Access Denied")
      }
      case None =>
        Future {
          TAchievementServiceResponse(false, "Access Denied")
        }
    }.flatMap(identity)
  }

}
