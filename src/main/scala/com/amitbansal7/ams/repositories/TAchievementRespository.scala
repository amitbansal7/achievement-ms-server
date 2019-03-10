package com.amitbansal7.ams.repositories

import com.amitbansal.ams.config.MongoConfig
import com.amitbansal7.ams.models.TAchievement

class TAchievementRepository {

  val tAchievementsCollection = MongoConfig.getTAchievementsCollection

  def add(tAchievement: TAchievement) =
    tAchievementsCollection.insertOne(tAchievement).toFuture()

  def getAll =
    tAchievementsCollection.find().toFuture()

}
