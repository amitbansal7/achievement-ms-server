package com.amitbansal7.ams.services

import org.mongodb.scala.bson.ObjectId

class Utils {

  def checkObjectId(id: String): Option[ObjectId] = {
    try {
      val objId = new ObjectId(id)
      Some(objId)
    } catch {
      case _ => None
    }
  }

}
