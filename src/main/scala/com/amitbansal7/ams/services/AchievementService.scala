package com.amitbansal7.ams.services

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.amitbansal7.ams.models.Achievement
import com.amitbansal7.ams.repositories.AchievementRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import java.io.{File, FileInputStream, InputStream}
import java.nio.file.Files

import akka.http.scaladsl.server.directives.FileInfo
import com.amitbansal.ams.repositories.UserRepository
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.util.parsing.json.JSON
import scala.util.{Failure, Random, Success}

object AchievementService {

  def approveAch(id: String) =
    AchievementRepository.approve(id, true)

  def getAllApproved(department: String) =
    AchievementRepository.findAllApprovedByDepartment(department)

  def getAllUnapproved(token: String) = {
    JwtService.decodeToken(token) match {
      case Success(value) =>
        JSON.parseFull(value._2) match {
          case Some(map: Map[String, String]) =>
            map.get("user") match {
              case Some(email) =>
                UserRepository.getByEmail(email).map { u =>
                  AchievementRepository.findAllByUnApprovedDepartment(u.department)
                }.flatMap(identity).map(d => AchievementServiceResponseToken(true, d))
              case None => Future(AchievementServiceResponseToken(false, List()))
            }
        }
      case Failure(exception) => Future(AchievementServiceResponseToken(false, List()))
    }
  }

  def addAchievement(
    rollno: String,
    department: String,
    year: Int,
    date: String,
    venue: String,
    category: String,
    participated: Boolean,
    name: String,
    description: String,
    eventName: String,
    file: File,
    meta: FileInfo
  ): AchievementServiceResponse = {

    if (Achievement.departments.count(s => s == department) == 0)
      return AchievementServiceResponse(false, "Not a valid department")

    if (Achievement.categories.count(s => s == category) == 0)
      return AchievementServiceResponse(false, "Not a valid category")

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
      .addAchievement(Achievement.apply(rollno, department, year, date, venue, category, participated, name, fileName, description, eventName))

    AchievementServiceResponse(true, "Achievement successfully added")
  }

  case class AchievementServiceResponse(bool: Boolean, message: String)

  case class AchievementServiceResponseToken(bool: Boolean, data: Seq[Achievement])

}
