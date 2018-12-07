package com.amitbansal7.ams.services

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.amitbansal7.ams.models.Achievement
import com.amitbansal7.ams.repositories.AchievementRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import java.io.{ File, FileInputStream, InputStream }
import java.nio.file.Files

import akka.http.scaladsl.server.directives.FileInfo
import com.amitbansal.ams.models.User
import com.amitbansal.ams.repositories.UserRepository
import com.amitbansal.ams.services.UserService
import com.amitbansal7.ams.services.AchievementService.{ AchievementServiceResponseToken }
import org.mongodb.scala.bson.ObjectId
import pdi.jwt.{ Jwt, JwtAlgorithm }
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import scala.util.parsing.json.JSON
import scala.util.{ Failure, Random, Success }

object AchievementService {

  // /mnt/data/static
  val baseStaticPath = "static/"

  def filterByfields(
    achs: Future[Seq[Achievement]],
    rollno: Option[String],
    department: Option[String],
    semester: Option[Int],
    dateFrom: Option[String],
    dateTo: Option[String],
    shift: Option[String],
    section: Option[String],
    sessionFrom: Option[String],
    sessionTo: Option[String],
    category: Option[String]
  ): Future[Seq[Achievement]] = {
    achs.map { achss =>
      for {
        a: Achievement <- achss;
        if ((!rollno.isDefined || (rollno.isDefined && rollno.get == a.rollNo)) &&
          (!semester.isDefined || (semester.isDefined && semester.get.equals(a.semester))) &&
          (!dateFrom.isDefined || (semester.isDefined && dateFrom.get <= a.date)) &&
          (!dateTo.isDefined || (dateTo.isDefined && dateTo.get >= a.date)) &&
          (!shift.isDefined || (shift.isDefined && shift.get.equals(a.shift))) &&
          (!section.isDefined || (section.isDefined && section.get.equals(a.section))) &&
          (!sessionFrom.isDefined || (sessionFrom.isDefined && sessionFrom.get.equals(a.sessionFrom))) &&
          (!sessionTo.isDefined || (sessionTo.isDefined && sessionTo.get.equals(a.sessionTo))) &&
          (!category.isDefined || (category.isDefined && category.get.equals(a.category))))
      } yield a
    }
  }

  def getAllApproved(
    rollno: Option[String],
    department: Option[String],
    semester: Option[Int],
    dateFrom: Option[String],
    dateTo: Option[String],
    shift: Option[String],
    section: Option[String],
    sessionFrom: Option[String],
    sessionTo: Option[String],
    category: Option[String]
  ): Future[Seq[Achievement]] = {

    department match {
      case Some(dept) => filterByfields(
        AchievementRepository.findAllApprovedByDepartment(dept.toLowerCase), rollno, department, semester, dateFrom, dateTo, shift, section, sessionFrom, sessionTo, category
      )
      case None => filterByfields(
        AchievementRepository.findAllApproved(), rollno, department, semester, dateFrom, dateTo, shift, section, sessionFrom, sessionTo, category
      )
    }
  }

  def toggleApproved(id: String, token: String, action: Boolean): Future[AchievementServiceResponse] = {
    val objId = Utils.checkObjectId(id)

    if (!objId.isDefined)
      return Future {
        AchievementServiceResponse(false, "Invalid Id")
      }

    val ach: Future[Achievement] = AchievementRepository.findById(objId.get)
    val user: Future[Option[User]] = UserService.getUserFromToken(token)

    user.map {
      case Some(u) =>
        ach.map(a =>
          if (a.isInstanceOf[Achievement] && a.department == u.department && u.shift == a.shift) {
            if (action)
              AchievementRepository.approveByUser(objId.get, u._id.toHexString)
            else AchievementRepository.approve(objId.get, action)

            AchievementServiceResponse(true, "Done")
          } else {
            AchievementServiceResponse(false, "Access denied")
          })
      case _ => Future(AchievementServiceResponse(false, "No user found"))
    }.flatMap(identity)
  }

  def approveAch(id: String, token: String) = toggleApproved(id, token, true)

  def unApproveAch(id: String, token: String) = toggleApproved(id, token, false)

  def deleteAch(id: String, token: String): Future[AchievementServiceResponse] = {

    val objId = Utils.checkObjectId(id)

    if (!objId.isDefined)
      return Future {
        AchievementServiceResponse(false, "Invalid Id")
      }

    val user: Future[Option[User]] = UserService.getUserFromToken(token)
    val ach: Future[Achievement] = AchievementRepository.findById(objId.get)

    user.map {
      case Some(u) =>
        ach.map(a =>
          if (a.isInstanceOf[Achievement] && a.department == u.department && a.shift == u.shift) {
            AchievementRepository.deleteOne(objId.get)
            AchievementServiceResponse(true, "Done")
          } else {
            AchievementServiceResponse(false, "Access denied")
          })
      case _ => Future(AchievementServiceResponse(false, "No user found"))
    }.flatMap(identity)

  }

  def getOne(id: String): Option[Future[Achievement]] = {
    val objId = Utils.checkObjectId(id)
    if (!objId.isDefined) None
    else {
      val ach = AchievementRepository.findById(objId.get)
      val res: Future[Achievement] = ach.map {
        case a: Achievement if a.approved =>
          val user: Future[User] = UserRepository.getById(Utils.checkObjectId(a.approvedBy.get).get)
          user.map { u =>
            if (u != null) Achievement.apply(a, Some(u.email))
            else Achievement.apply(a, None)
          }
        case a: Achievement => Future {
          a
        }
      }.flatMap(identity)
      Some(res)

    }
  }

  def getAllUnapproved(
    token: String,
    rollno: Option[String],
    semester: Option[Int],
    dateFrom: Option[String],
    dateTo: Option[String],
    shift: Option[String],
    section: Option[String],
    sessionFrom: Option[String],
    sessionTo: Option[String],
    category: Option[String]
  ) = {
    UserService.getUserFromToken(token).map {
      case Some(user) =>
        val data = AchievementRepository
          .findAllByUnApprovedDepartmentAndDepartment(user.department, user.shift)
          .map(d => filterByfields(Future(d), rollno, None, semester, dateFrom, dateTo, shift, section, sessionFrom, sessionTo, category))
          .flatMap(identity)

        data.map(d => AchievementServiceResponseToken(true, d))
      case None => Future(AchievementServiceResponseToken(false, List()))
    }
  }

  def addAchievement(
    title: String,
    rollNo: String,
    department: String,
    semester: Int,
    date: String,
    shift: String,
    section: String,
    sessionFrom: String,
    sessionTo: String,
    venue: String,
    category: String,
    participated: Boolean, //coordinated if false
    name: String,
    description: String,
    eventName: String,
    file: File,
    meta: FileInfo
  ): AchievementServiceResponse = {

    if (!Achievement.shifts.contains(shift))
      return AchievementServiceResponse(false, "invalid shift")

    if (!Achievement.sections.contains(section))
      return AchievementServiceResponse(false, "invalid section")

    if (!Achievement.semester.contains(semester))
      return AchievementServiceResponse(false, "invalid semester")

    if (!Achievement.departments.contains(department))
      return AchievementServiceResponse(false, "invalid department")

    if (!Achievement.categories.contains(category))
      return AchievementServiceResponse(false, "invalid category")

    if (!meta.contentType.toString().startsWith("image"))
      return AchievementServiceResponse(false, "Invalid file type")

    val imageRes = ImageCompressionService.processImage(file)

    //    if (!imageRes.bool)
    //      return AchievementServiceResponse(false, imageRes.message)

    val str = Random
      .alphanumeric
      .take(7).toList
      .foldLeft("")((acc, ch) => acc + ch)

    val fileName = (str + meta.getFileName).replace(" ", "-")
    val outFile = new File(baseStaticPath + fileName)

    val path = Paths.get(baseStaticPath + fileName)
    Files.write(path, imageRes.buffer)
    val res = AwsS3Service.uploadImage(path.toFile, fileName)
    if(!res)
      return AchievementServiceResponse(false, "Failed to upload image, try again later.")
    ////    Files.write(outFile.toPath, imageRes.buffer)
    //    Files.copy(file.toPath, outFile.toPath)
    //        Files.copy(file.buffer, outFile.toPath)
    file.delete()

    AchievementRepository
      .addAchievement(Achievement.apply(title, rollNo, department, semester, date, shift, section, sessionFrom, sessionTo, venue, category, participated, name, fileName, description, eventName))

    AchievementServiceResponse(true, "Achievement successfully added")
  }

  case class AchievementServiceResponse(bool: Boolean, message: String)

  case class AchievementServiceResponseToken(bool: Boolean, data: Seq[Achievement])

}
