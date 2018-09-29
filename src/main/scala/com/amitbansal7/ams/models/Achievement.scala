package com.amitbansal7.ams.models

import org.mongodb.scala.bson.ObjectId

object Achievement {

  val departments = Set("computerscience", "education", "management")
  val categories = Set("sports", "technical", "cultural", "others")

  def apply(
    _id: ObjectId,
    rollno: String,
    department: String,
    year: Int,
    date: String,
    venue: String,
    category: String,
    participated: Boolean, //coordinated if false
    rating: Int,
    name: String,
    imageUrl: String,
    approved: Boolean,
    description: String,
    eventName: String
  ): Achievement = new Achievement(_id, rollno, department, year, date, venue, category, participated, //coordinated if false
    rating, name, imageUrl, approved, description, eventName)

  def apply(
    rollno: String,
    department: String,
    year: Int,
    date: String,
    venue: String,
    category: String,
    participated: Boolean, //coordinated if false
    name: String,
    imageUrl: String,
    description: String,
    eventName: String
  ): Achievement = new Achievement(new ObjectId(), rollno, department, year, date, venue, category, participated, //coordinated if false
    0, name, imageUrl, false, description, eventName)
}

case class Achievement(
    _id: ObjectId,
    rollno: String,
    department: String,
    year: Int,
    date: String,
    venue: String,
    category: String,
    participated: Boolean, //coordinated if false
    rating: Int,
    name: String,
    imageUrl: String,
    approved: Boolean,
    description: String,
    eventName: String
) {

}
