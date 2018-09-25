package com.amitbansal.ams.config

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.amitbansal.ams.models.User
import com.amitbansal.ams.services.UserService
import com.amitbansal.ams.services.UserService.AuthRes
import com.amitbansal7.ams.services.AchievementService
import org.mongodb.scala.bson.ObjectId
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object ObjectIdSerializer extends RootJsonFormat[ObjectId] {
    override def read(json: JsValue): ObjectId = new ObjectId(json.toString)

    override def write(obj: ObjectId): JsValue = JsString(obj.toHexString)
  }

//  class EnumJsonConverter[T <: scala.Enumeration](enu: T) extends RootJsonFormat[T#Value]{
//    override def write(obj: T#Value): JsValue = JsString(obj.toString)
//
//    override def read(json: JsValue): T#Value = json match {
//      case JsString(txt) => enu.withName(txt)
//      case somethingElse => throw DeserializationException("Error while reading Enumeration")
//    }
//  }

  implicit val userServiceResponseFormat = jsonFormat2(UserService.UserServiceResponse)
  implicit val AchievementServiceResponseFormat = jsonFormat2(AchievementService.AchievementServiceResponse)
  implicit val userFormat = jsonFormat5(User.apply)
  implicit val authResFormat = jsonFormat3(AuthRes)
//  implicit val departmentFormat = new EnumJsonConverter(Department)
}