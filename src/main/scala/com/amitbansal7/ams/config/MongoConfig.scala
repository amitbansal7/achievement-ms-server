package com.amitbansal.ams.config

import com.amitbansal.ams.models.User
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}

object MongoConfig {

  val USER_COLLECTION = "user"
  val ACHIEVEMT_COLLECTION = "achievement"

  val mongoClient = MongoClient()

  val userCodecRegistry = fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY)

  val db = mongoClient
    .getDatabase("ams")
    .withCodecRegistry(userCodecRegistry)

  val userCollection: MongoCollection[User] = db.getCollection(USER_COLLECTION)

  def getUserCollection = userCollection

}