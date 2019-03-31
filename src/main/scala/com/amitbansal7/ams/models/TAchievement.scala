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
    date: String,
    description: String,
    msi: Boolean,
    international: Boolean // (international = True) (national = False)
  ): TAchievement = new TAchievement(_id, user, taType, date, description, msi, international)

  def apply(
    user: ObjectId,
    taType: String,
    date: String,
    description: String,
    msi: Boolean,
    international: Boolean // (international = True) (national = False)
  ): TAchievement = new TAchievement(new ObjectId(), user, taType, date, description, msi, international)
}

import TAchievement._

//Teacher achievement object
case class TAchievement(
    _id: ObjectId,
    user: ObjectId,
    taType: String,
    date: String,
    description: String,
    msi: Boolean,
    international: Boolean // (international = True) (national = False)
) {

}
