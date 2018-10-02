package com.amitbansal7.ams.services

import pdi.jwt.{ Jwt, JwtAlgorithm, JwtClaim }
import scala.concurrent.duration._

object JwtService {

  val secretKey = "secret"

  val tokenExpiry = (30 days).toSeconds

  def getJwtToken(email: String, department: String): String = {
    Jwt.encode(
      JwtClaim({ s"""{"user":"$email", "dept":"$department"}""" }).issuedNow.expiresIn(tokenExpiry),
      secretKey,
      JwtAlgorithm.HS384
    )
  }

  def decodeToken(token: String) = {
    Jwt.decodeRawAll(token, JwtService.secretKey, Seq(JwtAlgorithm.HS384))
  }
}
