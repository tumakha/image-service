package com.tumakha.image

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.ContentTypeResolver
import akka.http.scaladsl.server.directives.RouteDirectives.complete

/**
 * @author Yuriy Tumakha
 */
class ImageRoutes(resolver: ContentTypeResolver) {

  private val baseImage = "/images/0.png"
  private val contentType = resolver(baseImage)
  private val distorter = new ImageDistorter(baseImage, colorMaxOffset = 3, outputFormat = "png")

  lazy val imageRoutes: Route =
    path("logo.png") {
      get {
        complete(HttpEntity(contentType, distorter.getDistortedImage))
      }
    }

}
