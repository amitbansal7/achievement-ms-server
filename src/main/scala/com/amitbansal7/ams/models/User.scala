package com.amitbansal.ams.models

import org.mongodb.scala.bson.ObjectId
import org.apache.commons.codec.digest.DigestUtils

object User {

  def getPasshash(password: String): String =
    DigestUtils.sha256Hex(password)

  def apply(
    _id: ObjectId,
    email: String,
    password: String,
    firstName: String,
    lastName: String
  ): User = new User(_id, email, password, firstName, lastName)


  def apply(
    email: String,
    password: String,
    firstName: String,
    lastName: String
  ): User = {
    new User(new ObjectId(), email, getPasshash(password), firstName, lastName)
  }
}

case class User(
  _id: ObjectId,
  email: String,
  password: String,
  firstName: String,
  lastName: String
) {

}
