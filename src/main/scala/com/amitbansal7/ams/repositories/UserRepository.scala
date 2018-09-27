package com.amitbansal.ams.repositories

import com.amitbansal.ams.config.MongoConfig
import com.amitbansal.ams.models.User
import org.mongodb.scala.bson.collection.mutable.Document
import org.mongodb.scala.model.Filters._

import scala.concurrent.Future

object UserRepository {

  val userCollection = MongoConfig.getUserCollection

  def getByEmail(email: String): Future[User] =
    userCollection.find(equal("email", email)).first().toFuture()

  def addUser(user: User) =
    userCollection.insertOne(user).toFuture()

  def changePass(email: String, newPass: String) =
    userCollection.updateOne(
      Document("email" -> email),
      Document("$set" -> Document("password" -> newPass))
    ).toFuture()

}
