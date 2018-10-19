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
    rating, name, imageUrl, approved, description, eventName, None)

  def apply(
    ach: Achievement,
    approvedBy: Option[String] //email of user
  ): Achievement = new Achievement(ach._id, ach.title, ach.rollNo, ach.department, ach.semester, ach.date, ach.shift, ach.section, ach.sessionFrom, ach.sessionTo, ach.venue, ach.category, ach.participated, ach.rating, ach.name, ach.imageUrl, ach.approved, ach.description, ach.eventName, approvedBy)

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
    eventName: String,
    approvedBy: Option[String] //email of user
  ): Achievement = new Achievement(_id, title, rollNo, department, semester, date, shift, section, sessionFrom, sessionTo, venue, category, participated, //coordinated if false
    rating, name, imageUrl, approved, description, eventName, approvedBy)

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
  ): Achievement = Achievement(new ObjectId(), title, rollNo, department, semester, date, shift, section, sessionFrom, sessionTo, venue, category, participated, //coordinated if false
    -1, name, imageUrl, false, description, eventName, None)

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
    eventName: String,
    approvedBy: Option[String] //email of user
) {

}
