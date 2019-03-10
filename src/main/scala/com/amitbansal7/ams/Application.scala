package com.amitbansal.ams

import akka.actor.ActorSystem

import scala.util.{ Failure, Success }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.duration._
import scala.io.StdIn
import akka.http.scaladsl.server.Directives._
import com.amitbansal.ams.routes.{ AchievementRoutes, UserRoutes }
import com.amitbansal7.ams.routes.AcademicRoutes
import com.amitbansal7.ams.utils
import com.amitbansal7.ams.utils.CORSHandler
import akka.event.Logging
import scala.util.parsing.json.JSON

object Application extends CORSHandler {

  val host = "0.0.0.0"

  val port = 8080

  val json = JSON.parseFull(scala.io.Source.fromResource("data.json").getLines().toList.mkString)

  val resource = json.map {
    case map: Map[String, String] => map
    case _ => {
      println("data.json parsing failed")
      Map[String, Any]()
    }
  }.get

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("achievement-management-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    implicit val timeout = Timeout(5 seconds)

    import com.amitbansal7.ams.Modules._

    val route: Route = corsHandler {
      toStrictEntity(30 seconds) {
        (path("") & get) {
          complete(StatusCodes.OK, "Server is up and running..")
        } ~
          achievementRoutes.route ~ userRoutes.route ~ academicRoutes.route ~ tAchievementRoutes.route
      }
    }

    val bindingFuture = Http().bindAndHandle(route, host, port)

    bindingFuture.onComplete {
      case Success(_) => println(s"Server is running at ${host}:${port}\nHit return to terminate..")
      case Failure(e) => println(s"could not start application: {}", e.getMessage)
    }

    StdIn.readLine()
    bindingFuture.flatMap(_.unbind())
    system.terminate()
    println("Server is closed.")

  }
}
