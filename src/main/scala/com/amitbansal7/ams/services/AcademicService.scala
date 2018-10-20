package com.amitbansal7.ams.services

import com.amitbansal.ams.models.User
import com.amitbansal.ams.services.UserService
import com.amitbansal7.ams.models.Academic
import com.amitbansal7.ams.repositories.AcademicRepository
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AcademicService {

  def add(rollNo: String, name: String, batch: String, programme: String, token: String) = {

    if (!Academic.programmes.contains(programme))
      AcademicServiceResponse(false, "Invalid programme name.")

    val user: Future[Option[User]] = UserService.getUserFromToken(token)

    user.map {
      case Some(_) =>
        AcademicRepository.add(Academic(rollNo, name, batch, programme))
        AcademicServiceResponse(true, "Record successfully added.")
      case None =>
        AcademicServiceResponse(false, "Access denied.")
    }
  }

  def edit(id: String, rollNo: String, name: String, batch: String, programme: String, token: String): Future[AcademicServiceResponse] = {

    val objId = Utils.checkObjectId(id)

    if (!objId.isDefined)
      return Future(AcademicServiceResponse(false, "Invalid Id"))

    if (!Academic.programmes.contains(programme))
      AcademicServiceResponse(false, "Invalid programme name.")

    val user: Future[Option[User]] = UserService.getUserFromToken(token)

    user.map {
      case Some(_) =>
        AcademicRepository.update(objId.get, rollNo, name, batch, programme)
        AcademicServiceResponse(true, "Record successfully edited.")
      case None =>
        AcademicServiceResponse(false, "Access denied.")
    }
  }

  def getAll() = AcademicRepository.getAll()

  def deleteOne(id: String, token: String): Future[AcademicServiceResponse] = {
    val objId = Utils.checkObjectId(id)

    if (!objId.isDefined)
      return Future(AcademicServiceResponse(false, "Invalid Id"))

    val user: Future[Option[User]] = UserService.getUserFromToken(token)

    user.map {
      case Some(_) =>
        AcademicRepository.delete(objId.get)
        AcademicServiceResponse(true, "Successfully deleted")
      case _ =>
        AcademicServiceResponse(false, "Access denied.")
    }
  }

  case class AcademicServiceResponse(bool: Boolean, message: String)

}
