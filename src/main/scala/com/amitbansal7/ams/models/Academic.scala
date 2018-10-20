package com.amitbansal7.ams.models

import org.mongodb.scala.bson.ObjectId

object Academic {

  val programmes = Set("B. Ed.", "BBA (H) 4 years", "BBA (General)", "BBA (B&I)", "BBA (T&TM)", "BCA")

  def apply(
    _id: ObjectId,
    rollNo: String,
    name: String,
    batch: String,
    programme: String
  ): Academic = new Academic(_id, rollNo, name, batch, programme)

  def apply(
    rollNo: String,
    name: String,
    batch: String,
    programme: String
  ): Academic = new Academic(new ObjectId(), rollNo, name, batch, programme)

}

case class Academic(
    _id: ObjectId,
    rollNo: String,
    name: String,
    batch: String,
    programme: String
) {

}