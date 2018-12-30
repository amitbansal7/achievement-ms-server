package com.amitbansal7.ams

import com.amitbansal.ams.repositories.UserRepository
import com.amitbansal.ams.routes.{ AchievementRoutes, UserRoutes }
import com.amitbansal.ams.services.UserService
import com.amitbansal7.ams.repositories.{ AcademicRepository, AchievementRepository }
import com.amitbansal7.ams.routes.AcademicRoutes
import com.amitbansal7.ams.services._
import com.softwaremill.macwire._

object Modules {

  val academicRepository = wire[AcademicRepository]
  val achievementRepository = wire[AchievementRepository]
  val userRepository = wire[UserRepository]

  val utils = wire[Utils]
  val awsS3Service = wire[AwsS3Service]
  val jwtService = wire[JwtService]
  val imageCompressionService = wire[ImageCompressionService]

  val userService = wire[UserService]
  val academicService = wire[AcademicService]
  val achievementService = wire[AchievementService]

  val userRoutes = wire[UserRoutes]
  val academicRoutes = wire[AcademicRoutes]
  val achievementRoutes = wire[AchievementRoutes]

}
