package com.amitbansal.ams.config

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.{ Marshaller, ToEntityMarshaller }
import akka.http.scaladsl.model.{ ContentType, HttpEntity, MediaTypes }
import com.amitbansal.ams.models.User
import com.amitbansal.ams.services.UserService
import com.amitbansal.ams.services.UserService.{ AuthRes, UserData }
import com.amitbansal7.ams.models.Achievement
import com.amitbansal7.ams.services.AchievementService
import com.amitbansal7.ams.services.AchievementService.AchievementServiceResponseToken
import org.mongodb.scala.bson.ObjectId
import spray.json.{ DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat }

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object ObjectIdSerializer extends RootJsonFormat[ObjectId] {
    override def read(json: JsValue): ObjectId = new ObjectId(json.toString)

    override def write(obj: ObjectId): JsValue = JsString(obj.toHexString)
  }

  implicit val userServiceResponseFormat = jsonFormat2(UserService.UserServiceResponse)
  implicit val AchievementServiceResponseFormat = jsonFormat2(AchievementService.AchievementServiceResponse)
  implicit val userFormat = jsonFormat6(User.apply)
  implicit val authResFormat = jsonFormat3(AuthRes)
  implicit val AchievementFormat = jsonFormat19(Achievement.apply)
  implicit val AchievementServiceResponseTokenFormat = jsonFormat2(AchievementServiceResponseToken)
  implicit val UserDataFormat = jsonFormat4(UserData)
  implicit val mapMarshaller: ToEntityMarshaller[Map[String, Any]] = Marshaller.opaque { map =>
    HttpEntity(ContentType(MediaTypes.`application/json`), map.toString)
  }

  //  class EnumJsonConverter[T <: scala.Enumeration](enu: T) extends RootJsonFormat[T#Value]{
  //    override def write(obj: T#Value): JsValue = JsString(obj.toString)
  //
  //    override def read(json: JsValue): T#Value = json match {
  //      case JsString(txt) => enu.withName(txt)
  //      case somethingElse => throw DeserializationException("Error while reading Enumeration")
  //    }
  //  }
  //  implicit val departmentFormat = new EnumJsonConverter(Department)
}