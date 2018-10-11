package com.amitbansal7.ams.repositories

import com.amitbansal.ams.config.MongoConfig
import com.amitbansal7.ams.models.Achievement
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.mutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

object AchievementRepository {

  val achievementCollection = MongoConfig.getachievementCollection

  def addAchievement(ach: Achievement) =
    achievementCollection.insertOne(ach).toFuture()

  def findById(id: ObjectId) =
    achievementCollection
      .find(Document("_id" -> id))
      .first().toFuture()

  def approve(id: String, approved: Boolean): Future[result.UpdateResult] =
    achievementCollection.updateOne(
      Document("_id" -> new ObjectId(id)),
      Document("$set" -> Document("approved" -> approved))
    ).toFuture()

  def findAllByUnApprovedDepartment(department: String) =
    achievementCollection
      .find(
        Document("department" -> department, "approved" -> false)
      ).toFuture()

  def deleteOne(id: String) =
    achievementCollection.
      deleteOne(
        Document("_id" -> new ObjectId(id))
      ).toFuture()

  def findAllApprovedByDepartment(department: String) =
    achievementCollection
      .find(
        Document("department" -> department, "approved" -> true)
      ).toFuture()

  def findAllApproved() =
    achievementCollection
      .find(
        Document("approved" -> true)
      ).toFuture()
}
