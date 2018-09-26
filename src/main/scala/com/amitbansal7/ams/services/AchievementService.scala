package com.amitbansal7.ams.services

import com.amitbansal7.ams.models.Achievement
import com.amitbansal7.ams.repositories.AchievementRepository
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

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
    eventName: String):AchievementServiceResponse = {

    if(Achievement.departments.count(s => s == department) == 0)
      return AchievementServiceResponse(false, "Not a valid department")

    if(Achievement.categories.count(s => s == category) == 0)
      return AchievementServiceResponse(false, "Not a valid category")

    AchievementRepository
      .addAchievement(Achievement.apply(rollno, department, year, date, venue, category, participated, name, "", description, eventName))

    AchievementServiceResponse(true, "Achievement successfully added")
  }

  case class AchievementServiceResponse(bool: Boolean, message: String)

}
