package com.amitbansal7.ams.repositories

import com.amitbansal.ams.config.MongoConfig
import com.amitbansal7.ams.models.Achievement
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.mutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._

import scala.concurrent.Await
import scala.concurrent.duration._

object AchievementRepository {

  val achievementCollection = MongoConfig.getachievementCollection

  def addAchievement(ach: Achievement) =
    achievementCollection.insertOne(ach).toFuture()

  def approve(id: String, approved: Boolean) =
    achievementCollection.updateOne(
      Document("_id" -> new ObjectId(id)),
      Document("$set" -> Document("approved" -> approved))
    ).toFuture()

  def findAllByDepartment(department: String) =
    achievementCollection
      .find(
        Document("department"->department)
      ).toFuture()

  def findAllApprovedByDepartment(department: String) =
    achievementCollection
      .find(
        Document("department"->department, "approved" -> true)
      ).toFuture()

}
