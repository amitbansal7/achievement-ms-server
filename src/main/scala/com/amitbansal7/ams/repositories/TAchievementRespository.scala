package com.amitbansal7.ams.repositories

import com.amitbansal.ams.config.MongoConfig
import com.amitbansal7.ams.models.TAchievement
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.mutable.Document

class TAchievementRepository {

  val tAchievementsCollection = MongoConfig.getTAchievementsCollection

  def add(tAchievement: TAchievement) =
    tAchievementsCollection.insertOne(tAchievement).toFuture()

  def getAll =
    tAchievementsCollection.find().toFuture()

  def getAllByToken(userId: ObjectId) =
    tAchievementsCollection
      .find(Document("user" -> userId))
      .toFuture()

}
