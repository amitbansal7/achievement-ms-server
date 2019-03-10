package com.amitbansal.ams.config

import java.util

import com.amitbansal.ams.models.User
import com.amitbansal7.ams.models.{ Academic, Achievement, TAchievement }
import org.bson.codecs.Codec
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{ fromProviders, fromRegistries }
import org.bson.codecs.configuration.CodecRegistry

object MongoConfig {

  val USER_COLLECTION = "user"
  val ACHIEVEMT_COLLECTION = "achievement"
  val ACADEMIC_COLLECTION = "academic"
  val T_ACHIEVEMENT = "tAchievement"

  val mongoClient = MongoClient()

  val userCodecRegistry = fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY)
  val achievementCodecRegistry = fromRegistries(fromProviders(classOf[Achievement]), userCodecRegistry)
  val academicCodecRegistry = fromRegistries(fromProviders(classOf[Academic]), achievementCodecRegistry)
  val tAchievementsCodecRegistry = fromRegistries(fromProviders(classOf[TAchievement]), academicCodecRegistry)

  val db = mongoClient
    .getDatabase("ams")
    .withCodecRegistry(tAchievementsCodecRegistry)

  val userCollection: MongoCollection[User] = db.getCollection(USER_COLLECTION)

  val achievementCollection: MongoCollection[Achievement] = db.getCollection(ACHIEVEMT_COLLECTION)

  val academicCollection: MongoCollection[Academic] = db.getCollection(ACADEMIC_COLLECTION)

  val tAchievementsCollection: MongoCollection[TAchievement] = db.getCollection(T_ACHIEVEMENT)

  def getachievementCollection = achievementCollection

  def getAcademicCollection = academicCollection

  def getUserCollection = userCollection

  def getTAchievementsCollection = tAchievementsCollection

}