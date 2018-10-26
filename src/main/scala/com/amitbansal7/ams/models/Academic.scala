package com.amitbansal7.ams.models

import org.mongodb.scala.bson.ObjectId

object Academic {

  val programmes = Set("B. Ed.", "BBA (H) 4 years", "BBA (General)", "BBA (B&I)", "BBA (T&TM)", "BCA", "B.Com (H)")

  val categories = Set("goldmedalist", "exemplary", "both")

  def apply(
    _id: ObjectId,
    rollNo: String,
    name: String,
    batch: String,
    programme: String,
    category: String
  ): Academic = new Academic(_id, rollNo, name, batch, programme, category)

  def apply(
    rollNo: String,
    name: String,
    batch: String,
    programme: String,
    category: String
  ): Academic = new Academic(new ObjectId(), rollNo, name, batch, programme, category)

}

case class Academic(
    _id: ObjectId,
    rollNo: String,
    name: String,
    batch: String,
    programme: String,
    category: String
) {

}