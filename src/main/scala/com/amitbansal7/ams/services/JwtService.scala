package com.amitbansal7.ams.services

import pdi.jwt.{Jwt, JwtAlgorithm}

object JwtService {

  val secretKey = "secret"

  def getJwtToken(id: String, email: String): String =
    Jwt.encode(s"""{"user":$id}""", secretKey, JwtAlgorithm.HS384)

}
