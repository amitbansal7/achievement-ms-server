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
import com.amitbansal7.ams.services.AchievementService.AchievementServiceResponseToken
import org.mongodb.scala.bson.ObjectId
import pdi.jwt.{ Jwt, JwtAlgorithm }
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import cats.data.OptionT
import cats.instances.future._
import scala.util.parsing.json.JSON
import scala.util.{ Failure, Random, Success }

object AchievementService {

  case class AchievementServiceResponse(bool: Boolean, message: String)

  case class AchievementServiceResponseToken(bool: Boolean, data: Seq[Achievement])

}

class AchievementService(userService: UserService, achievementRepository: AchievementRepository, awsS3Service: AwsS3Service, imageCompressionService: ImageCompressionService, utils: Utils, userRepository: UserRepository) {

  import AchievementService._

  // /mnt/data/static
  val baseStaticPath = "/mnt/data/static/"

  def paginate(achs: Seq[Achievement], offset: Option[Int], limit: Option[Int]): Seq[Achievement] = {
    val sortedAchs = achs.sortWith(_.date > _.date)
    if (offset.isDefined && limit.isDefined) {
      sortedAchs.toList.drop(offset.get).take(limit.get)
    } else sortedAchs
  }

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
          (!dateFrom.isDefined || (dateFrom.isDefined && dateFrom.get <= a.date)) &&
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
    category: Option[String],
    offset: Option[Int],
    limit: Option[Int]
  ): Future[Seq[Achievement]] = {
    department.map { dept =>
      filterByfields(
        achievementRepository.findAllApprovedByDepartment(dept.toLowerCase), rollno, department, semester, dateFrom, dateTo, shift, section, sessionFrom, sessionTo, category
      )
    }.getOrElse {
      filterByfields(
        achievementRepository.findAllApproved(offset, limit), rollno, department, semester, dateFrom, dateTo, shift, section, sessionFrom, sessionTo, category
      )
    }.map {
      paginate(_, offset, limit)
    }
  }

  def toggleApproved(id: String, token: String, action: Boolean): Future[AchievementServiceResponse] = {
    val objId = utils.checkObjectId(id)

    if (!objId.isDefined)
      return Future {
        AchievementServiceResponse(false, "Invalid Id")
      }

    val ach: Future[Achievement] = achievementRepository.findById(objId.get)
    val user: Future[Option[User]] = userService.getUserFromToken(token)

    user.map {
      case Some(u) =>
        ach.map(a =>
          if (a.isInstanceOf[Achievement] && a.department == u.department && u.shift == a.shift) {
            if (action)
              achievementRepository.approveByUser(objId.get, u._id.toHexString)
            else achievementRepository.approve(objId.get, action)

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

    val objId = utils.checkObjectId(id)

    if (!objId.isDefined)
      return Future {
        AchievementServiceResponse(false, "Invalid Id")
      }

    val user: Future[Option[User]] = userService.getUserFromToken(token)
    val ach: Future[Achievement] = achievementRepository.findById(objId.get)

    user.map {
      case Some(u) =>
        ach.map(a =>
          if (a.isInstanceOf[Achievement] && a.department == u.department && a.shift == u.shift) {
            achievementRepository.deleteOne(objId.get)
            AchievementServiceResponse(true, "Done")
          } else {
            AchievementServiceResponse(false, "Access denied")
          })
      case _ => Future(AchievementServiceResponse(false, "No user found"))
    }.flatMap(identity)

  }

  def getOne(id: String): Option[Future[Achievement]] = {
    utils.checkObjectId(id).map { oId =>
      val ach = achievementRepository.findById(oId)
      val res: Future[Achievement] = ach.map {
        case a: Achievement if a.approved =>
          val user: Future[User] = userRepository.getById(utils.checkObjectId(a.approvedBy.get).get)
          user.map { u =>
            if (u != null) Achievement.apply(a, Some(u.email))
            else Achievement.apply(a, None)
          }
        case a: Achievement => Future {
          a
        }
      }.flatMap(identity)
      Some(res)
    }.getOrElse(None)
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
    category: Option[String],
    offset: Option[Int],
    limit: Option[Int]
  ) = {
    OptionT(userService.getUserFromToken(token)).map { user =>
      val data = achievementRepository
        .findAllByUnApprovedDepartmentAndDepartment(user.department, user.shift)
        .map(d => filterByfields(Future(d), rollno, None, semester, dateFrom, dateTo, shift, section, sessionFrom, sessionTo, category))
        .flatMap(identity)

      data.map(d => AchievementServiceResponseToken(true, paginate(d, offset, limit)))
    }.getOrElse {
      Future(AchievementServiceResponseToken(false, List()))
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

    val imageRes = imageCompressionService.processImage(file)

    if (!imageRes.bool)
      return AchievementServiceResponse(false, imageRes.message)

    val str = Random
      .alphanumeric
      .take(7).toList
      .foldLeft("")((acc, ch) => acc + ch)

    val fileName = (str + meta.getFileName).replace(" ", "-")
    val outFile = new File(baseStaticPath + fileName)

    val path = Paths.get(baseStaticPath + fileName)
    Files.write(path, imageRes.buffer)

    //aws>>>>>
    //    val res = awsS3Service.uploadImage(path.toFile, fileName)
    //    if (!res)
    //      return AchievementServiceResponse(false, "Failed to upload image, try again later.")

    file.delete()

    achievementRepository
      .addAchievement(Achievement.apply(title, rollNo, department, semester, date, shift, section, sessionFrom, sessionTo, venue, category, participated, name, fileName, description, eventName))

    AchievementServiceResponse(true, "Achievement successfully added")
  }
}
