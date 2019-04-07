package com.amitbansal7.ams.models

import org.mongodb.scala.bson.ObjectId

object TAchievement {

  val taTypes = Set(
    "Book",
    "Journal",
    "Conference",
    "SeminarAttended"
  )

  def apply(
    _id: ObjectId,
    user: ObjectId,
    taType: String,
    international: Boolean,
    topic: String,
    published: String,
    sponsored: Option[Boolean],
    reviewed: Option[Boolean],
    date: String,
    description: Option[String],
    msi: Boolean,
    place: Option[String]
  ): TAchievement = new TAchievement(_id, user, taType, international, topic, published, sponsored, reviewed, date, description, msi, place)

  def apply(
    user: ObjectId,
    taType: String,
    international: Boolean,
    topic: String,
    published: String,
    sponsored: Option[Boolean],
    reviewed: Option[Boolean],
    date: String,
    description: Option[String],
    msi: Boolean,
    place: Option[String]
  ): TAchievement = new TAchievement(new ObjectId(), user, taType, international, topic, published, sponsored, reviewed, date, description, msi, place)
}

import TAchievement._

//Teacher achievement object
case class TAchievement(
    _id: ObjectId,
    user: ObjectId,
    taType: String,
    international: Boolean, // (international = True) (national = False).
    topic: String,
    published: String, //[Name of publisher, Place, Presented at, Presented At]
    sponsored: Option[Boolean],
    reviewed: Option[Boolean],
    date: String,
    description: Option[String],
    msi: Boolean,
    place: Option[String]
) {

}
