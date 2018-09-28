package com.amitbansal.ams.config

import java.util

import com.amitbansal.ams.models.User
import com.amitbansal7.ams.models.Achievement
import org.bson.codecs.Codec
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{ fromProviders, fromRegistries }
import org.bson.codecs.configuration.CodecRegistry

object MongoConfig {

  val USER_COLLECTION = "user"
  val ACHIEVEMT_COLLECTION = "achievement"

  val mongoClient = MongoClient()

  val userCodecRegistry = fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY)
  val achievementCodecRegistry = fromRegistries(fromProviders(classOf[Achievement]), userCodecRegistry)

  val db = mongoClient
    .getDatabase("ams")
    .withCodecRegistry(achievementCodecRegistry)

  val userCollection: MongoCollection[User] = db.getCollection(USER_COLLECTION)

  val achievementCollection: MongoCollection[Achievement] = db.getCollection(ACHIEVEMT_COLLECTION)

  def getachievementCollection = achievementCollection

  def getUserCollection = userCollection

}