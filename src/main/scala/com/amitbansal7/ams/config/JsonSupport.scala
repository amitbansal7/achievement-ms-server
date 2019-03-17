package com.amitbansal.ams.config

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.{ Marshaller, ToEntityMarshaller }
import akka.http.scaladsl.model.{ ContentType, HttpEntity, MediaTypes }
import com.amitbansal.ams.models.User
import com.amitbansal.ams.services.UserService
import com.amitbansal.ams.services.UserService.{ AuthRes, UserData }
import com.amitbansal7.ams.models.{ Academic, Achievement, TAchievement }
import com.amitbansal7.ams.services.AcademicService.AcademicServiceResponse
import com.amitbansal7.ams.services.{ AcademicService, AchievementService }
import com.amitbansal7.ams.services.AchievementService.AchievementServiceResponseToken
import com.amitbansal7.ams.services.TAchievementService._
import org.mongodb.scala.bson.ObjectId
import spray.json.{ DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat }

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object ObjectIdSerializer extends RootJsonFormat[ObjectId] {
    override def read(json: JsValue): ObjectId = new ObjectId(json.toString)

    override def write(obj: ObjectId): JsValue = JsString(obj.toHexString)
  }

  implicit val userServiceResponseFormat = jsonFormat2(UserService.UserServiceResponse)
  implicit val AchievementServiceResponseFormat = jsonFormat2(AchievementService.AchievementServiceResponse)
  implicit val userFormat = jsonFormat7(User.apply)
  implicit val authResFormat = jsonFormat3(AuthRes)
  implicit val AchievementFormat = jsonFormat20(Achievement.apply)
  implicit val AcademicFormat = jsonFormat6(Academic.apply)
  implicit val AcademicServiceResp = jsonFormat2(AcademicServiceResponse)
  implicit val AchievementServiceResponseTokenFormat = jsonFormat2(AchievementServiceResponseToken)
  implicit val UserDataFormat = jsonFormat6(UserData)
  implicit val tAchievementFormat = jsonFormat7(TAchievement.apply)
  implicit val tAchievementServiceResponse = jsonFormat2(TAchievementServiceResponse.apply)
  implicit val tAchievementServiceDataFormat = jsonFormat3(TAchievementServiceData.apply)
  implicit val nnatIntFormat = jsonFormat2(TAchNatInt)
  implicit val tAchLocationsFormat = jsonFormat2(TAchLocations)
  implicit val tAchAllResUnitFormat = jsonFormat2(TAchAggRes)
  implicit val tAchAllResFormat = jsonFormat6(TAchAllRes)

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