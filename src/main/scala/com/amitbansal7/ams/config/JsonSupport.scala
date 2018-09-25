package com.amitbansal.ams.config

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.amitbansal.ams.models.User
import com.amitbansal.ams.services.UserService
import com.amitbansal.ams.services.UserService.AuthRes
import org.mongodb.scala.bson.ObjectId
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object ObjectIdSerializer extends RootJsonFormat[ObjectId] {
    override def read(json: JsValue): ObjectId = new ObjectId(json.toString)

    override def write(obj: ObjectId): JsValue = JsString(obj.toHexString)
  }

  implicit val userServiceResponseFormat = jsonFormat2(UserService.UserServiceResponse)
  implicit val userFormat = jsonFormat5(User.apply)
  implicit val authResFormat = jsonFormat3(AuthRes)
}