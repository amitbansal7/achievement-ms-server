package com.amitbansal7.ams.models

import org.mongodb.scala.bson.ObjectId

object TAchievement {

  val taTypes = Set(
    "PaperPublishedInJournal",
    "PaperPublishedInConferenceProceedings",
    "FdpConferenceSeminarAttended",
    "PaperPresentedInSeminarConference",
    "OneWeekFDPAttended",
    "PaperPresentedInSeminarConference",
    "Book"
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
    msi: Boolean
  ): TAchievement = new TAchievement(_id, user, taType, international, topic, published, sponsored, reviewed, date, description, msi)

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
    msi: Boolean
  ): TAchievement = new TAchievement(new ObjectId(), user, taType, international, topic, published, sponsored, reviewed, date, description, msi)
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
    msi: Boolean
) {

}
