package com.amitbansal7.ams.models

import org.mongodb.scala.bson.ObjectId

object Achievement {

  val departments = Set("computerscience", "education", "management")
  val categories = Set("sports", "technical", "cultural", "others")
  val shifts = Set("morning", "evening")
  val sections = Set("A", "B", "C", "D")
  val semester = Set(1, 2, 3, 4, 5, 6)

  def apply(
    _id: ObjectId,
    title: String,
    rollNo: String,
    department: String,
    semester: Int,
    date: String,
    shift: String,
    section: String,
    sessionFrom: String,
    sessionTo: String,
    venue: String,
    category: String,
    participated: Boolean, //coordinated if false
    rating: Int,
    name: String,
    imageUrl: String,
    approved: Boolean,
    description: String,
    eventName: String
  ): Achievement = new Achievement(_id, title, rollNo, department, semester, date, shift, section, sessionFrom, sessionTo, venue, category, participated, //coordinated if false
    rating, name, imageUrl, approved, description, eventName)

  def apply(
    title: String,
    rollNo: String,
    department: String,
    semester: Int,
    date: String,
    shift: String,
    section: String,
    sessionFrom: String,
    sessionTo: String,
    venue: String,
    category: String,
    participated: Boolean, //coordinated if false
    name: String,
    imageUrl: String,
    description: String,
    eventName: String
  ): Achievement = new Achievement(new ObjectId(), title, rollNo, department, semester, date, shift, section, sessionFrom, sessionTo, venue, category, participated, //coordinated if false
    -1, name, imageUrl, false, description, eventName)

}

case class Achievement(
    _id: ObjectId,
    title: String,
    rollNo: String,
    department: String,
    semester: Int,
    date: String,
    shift: String,
    section: String,
    sessionFrom: String,
    sessionTo: String,
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
