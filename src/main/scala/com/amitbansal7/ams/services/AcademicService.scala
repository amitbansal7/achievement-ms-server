package com.amitbansal7.ams.services

import com.amitbansal.ams.models.User
import com.amitbansal.ams.services.UserService
import com.amitbansal7.ams.models.Academic
import com.amitbansal7.ams.repositories.AcademicRepository
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AcademicService {
  case class AcademicServiceResponse(bool: Boolean, message: String)
}

class AcademicService(academicRepository: AcademicRepository, userService: UserService, utils: Utils) {

  import AcademicService._
  def add(rollNo: String, name: String, batch: String, programme: String, category: String, token: String): Future[AcademicServiceResponse] = {

    if (!Academic.programmes.contains(programme))
      return Future {
        AcademicServiceResponse(false, "Invalid programme name.")
      }

    if (!Academic.categories.contains(category))
      return Future(AcademicServiceResponse(false, "Invalid Category"))

    val user: Future[Option[User]] = userService.getUserFromToken(token)

    user.map {
      case Some(_) =>
        academicRepository.add(Academic(rollNo, name, batch, programme, category))
        AcademicServiceResponse(true, "Record successfully added.")
      case None =>
        AcademicServiceResponse(false, "Access denied.")
    }
  }

  def edit(id: String, rollNo: String, name: String, batch: String, programme: String, category: String, token: String): Future[AcademicServiceResponse] = {

    val objId = utils.checkObjectId(id)

    if (!objId.isDefined)
      return Future(AcademicServiceResponse(false, "Invalid Id"))

    if (!Academic.programmes.contains(programme))
      return Future(AcademicServiceResponse(false, "Invalid programme name."))

    if (!Academic.categories.contains(category))
      return Future(AcademicServiceResponse(false, "Invalid Category"))

    val user: Future[Option[User]] = userService.getUserFromToken(token)

    user.map {
      case Some(_) =>
        academicRepository.update(objId.get, rollNo, name, batch, programme, category)
        AcademicServiceResponse(true, "Record successfully edited.")
      case None =>
        AcademicServiceResponse(false, "Access denied.")
    }
  }

  def getAll(programme: Option[String], batch: Option[String], category: Option[String]) = {
    academicRepository.getAll().map { ach =>
      for {
        a <- ach
        if ((!programme.isDefined || (programme.isDefined && a.programme == programme.get)) &&
          (!batch.isDefined || (batch.isDefined && a.batch == batch.get)) &&
          (!category.isDefined || (category.isDefined && a.category == category.get)))
      } yield a
    }.map(seq => seq.sortBy(a => a.batch > a.batch))
  }

  def deleteOne(id: String, token: String): Future[AcademicServiceResponse] = {
    val objId = utils.checkObjectId(id)

    if (!objId.isDefined)
      return Future(AcademicServiceResponse(false, "Invalid Id"))

    val user: Future[Option[User]] = userService.getUserFromToken(token)

    user.map {
      case Some(_) =>
        academicRepository.delete(objId.get)
        AcademicServiceResponse(true, "Successfully deleted")
      case _ =>
        AcademicServiceResponse(false, "Access denied.")
    }
  }

}
