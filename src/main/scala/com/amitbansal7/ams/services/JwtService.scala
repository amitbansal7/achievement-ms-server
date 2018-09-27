package com.amitbansal7.ams.services

import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import scala.concurrent.duration._


object JwtService {

  val secretKey = "secret"

  def getJwtToken(email: String): String = {
    Jwt.encode(JwtClaim({s"""{"user":"$email"}"""}).issuedNow.expiresIn(10 * 24 * 60 * 60), secretKey, JwtAlgorithm.HS384)
  }

  def decodeToken(token: String) = {
    Jwt.decodeRawAll(token, JwtService.secretKey, Seq(JwtAlgorithm.HS384))
  }
}
