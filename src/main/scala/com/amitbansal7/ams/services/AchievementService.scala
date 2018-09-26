package com.amitbansal7.ams.services

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.amitbansal7.ams.models.Achievement
import com.amitbansal7.ams.repositories.AchievementRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
import java.io.{File, FileInputStream, InputStream}
import java.nio.file.Files

import akka.http.scaladsl.server.directives.FileInfo

import scala.util.Random

object AchievementService {

  def approveAch(id: String) =
    AchievementRepository.approve(id, true)


  def getAllApproved(department: String) =
    AchievementRepository.findAllApprovedByDepartment(department)

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

}
