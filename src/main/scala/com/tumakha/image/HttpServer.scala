package com.tumakha.image

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.ContentTypeResolver

import scala.util.{Failure, Success}

/**
 * @author Yuriy Tumakha
 */
object HttpServer extends App {

  private def startHttpServer(routes: Route, system: ActorSystem[_]): Unit = {
    implicit val classicSystem: akka.actor.ActorSystem = system.toClassic
    import system.executionContext

    val futureBinding = Http().bindAndHandle(routes, "localhost")
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  val rootBehavior = Behaviors.setup[Nothing] { context =>
    val routes = new ImageRoutes(ContentTypeResolver.Default)
    startHttpServer(routes.imageRoutes, context.system)
    Behaviors.empty
  }

  val system = ActorSystem[Nothing](rootBehavior, "AkkaHttpServer")

}
