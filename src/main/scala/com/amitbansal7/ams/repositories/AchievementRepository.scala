package com.amitbansal7.ams.repositories

import com.amitbansal.ams.config.MongoConfig
import com.amitbansal7.ams.models.Achievement

object AchievementRepository {

  val achievementCollection = MongoConfig.getachievementCollection

  def addAchievement(ach: Achievement) =
    achievementCollection.insertOne(ach).toFuture()

}
