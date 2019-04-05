package com.amitbansal.ams.repositories

import com.amitbansal.ams.config.MongoConfig
import com.amitbansal.ams.models.User
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.mutable.Document
import org.mongodb.scala.model.Filters._

import scala.concurrent.Future

class UserRepository {

  val userCollection = MongoConfig.getUserCollection

  def getByEmail(email: String): Future[User] =
    userCollection.find(equal("email", email)).first().toFuture()

  def getById(id: ObjectId) =
    userCollection
      .find(Document("_id" -> id))
      .first().toFuture()

  def addUser(user: User) =
    userCollection.insertOne(user).toFuture()

  def changePass(email: String, newPass: String) =
    userCollection.updateOne(
      Document("email" -> email),
      Document("$set" -> Document("password" -> newPass))
    ).toFuture()

  def reset(email: String, newEmail: String, firstName: String, lastName: String, designation: String) =
    userCollection.updateOne(
      Document("email" -> email),
      Document("$set" ->
        Document(
          "email" -> newEmail,
          "firstName" -> firstName,
          "lastName" -> lastName,
          "designation" -> designation
        ))
    ).toFuture()

  def getAllUsers() =
    userCollection.find().toFuture()
}
