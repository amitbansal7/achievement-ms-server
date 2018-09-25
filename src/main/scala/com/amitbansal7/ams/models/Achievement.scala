package com.amitbansal7.ams.models

import com.amitbansal7.ams.models.Category.Category
import com.amitbansal7.ams.models.Department.{Department, Unknown, values}
import org.mongodb.scala.bson.ObjectId

object Department extends Enumeration{
  type Department = Value
  val ComputerScience, Education, Management, Unknown = Value

  def withNameWithDefault(name: String): Value =
    values.find(_.toString.toLowerCase == name.toLowerCase()).getOrElse(Unknown)
}

object Category extends Enumeration{
  type Category = Value
  val Sports, technical, Cultural, Others = Value

  def withNameWithDefault(name: String): Value =
    values.find(_.toString.toLowerCase == name.toLowerCase()).getOrElse(Others)
}

object Achievement{

  def apply(
    _id: ObjectId,
    rollno: String,
    department: Department,
    year: Int,
    date: String,
    venue: String,
    category: Category,
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
    department: Department,
    year: Int,
    date: String,
    venue: String,
    category: Category,
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
  department: Department,
  year: Int,
  date: String,
  venue: String,
  category: Category,
  participated: Boolean, //coordinated if false
  rating: Int,
  name: String,
  imageUrl: String,
  approved: Boolean,
  description: String,
  eventName: String
)
