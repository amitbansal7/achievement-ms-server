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
import com.amitbansal7.ams.services.AchievementService.{ AchievementServiceResponseToken, getUserFromToken }
import pdi.jwt.{ Jwt, JwtAlgorithm }

import scala.util.parsing.json.JSON
import scala.util.{ Failure, Random, Success }

object AchievementService {

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

    def filterByfields(achs: Future[Seq[Achievement]]): Future[Seq[Achievement]] = {
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

    department match {
      case Some(dept) => filterByfields(AchievementRepository.findAllApprovedByDepartment(dept.toLowerCase))
      case None => filterByfields(AchievementRepository.findAllApproved())
    }
  }

  def getUserFromToken(token: String): Future[Option[User]] =
    JwtService.decodeToken(token) match {
      case Success(value) =>
        JSON.parseFull(value._2) match {
          case Some(map: Map[String, String]) =>
            map.get("user") match {
              case Some(email) => UserRepository.getByEmail(email).map(u => Some(u))
              case _ => Future(None)
            }
        }
      case _ => Future(None)
    }

  def toggleApproved(id: String, token: String, action: Boolean): Future[AchievementServiceResponse] = {
    val ach: Future[Achievement] = AchievementRepository.findById(id)
    val user: Future[Option[User]] = getUserFromToken(token)

    user.map {
      case Some(u) =>
        ach.map(a =>
          if (a.isInstanceOf[Achievement] && a.department == u.department) {
            AchievementRepository.approve(id, action)
            AchievementServiceResponse(true, "Done")
          } else {
            AchievementServiceResponse(false, "Access denied")
          })
      case _ => Future(AchievementServiceResponse(false, "No user found"))
    }.flatMap(identity)
  }

  def approveAch(id: String, token: String) = toggleApproved(id, token, true)

  def unApproveAch(id: String, token: String) = toggleApproved(id, token, false)

  def deleteAch(id: String, token: String) = {
    val user: Future[Option[User]] = getUserFromToken(token)
    val ach: Future[Achievement] = AchievementRepository.findById(id)

    user.map {
      case Some(u) =>
        ach.map(a =>
          if (a.isInstanceOf[Achievement] && a.department == u.department) {
            AchievementRepository.deleteOne(id)
            AchievementServiceResponse(true, "Done")
          } else {
            AchievementServiceResponse(false, "Access denied")
          })
      case _ => Future(AchievementServiceResponse(false, "No user found"))
    }.flatMap(identity)

  }

  def getAllUnapproved(token: String) = {
    getUserFromToken(token).map {
      case Some(user) =>
        AchievementRepository
          .findAllByUnApprovedDepartment(user.department)
          .map(d => AchievementServiceResponseToken(true, d))
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

    val str = Random
      .alphanumeric
      .take(7).toList
      .foldLeft("")((acc, ch) => acc + ch)

    val fileName = (str + meta.getFileName).replace(" ", "-")
    val outFile = new File("static/" + fileName)

    Files.copy(file.toPath, outFile.toPath)
    file.delete()

    AchievementRepository
      .addAchievement(Achievement.apply(title, rollNo, department, semester, date, shift, section, sessionFrom, sessionTo, venue, category, participated, name, fileName, description, eventName))

    AchievementServiceResponse(true, "Achievement successfully added")
  }

  case class AchievementServiceResponse(bool: Boolean, message: String)

  case class AchievementServiceResponseToken(bool: Boolean, data: Seq[Achievement])

}
