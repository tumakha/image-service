package com.tumakha.image

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream

import akka.http.scaladsl.model.{ContentType, MediaTypes}
import akka.http.scaladsl.model.ContentTypes.`text/plain(UTF-8)`
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.ContentTypeResolver
import akka.http.scaladsl.testkit.ScalatestRouteTest
import javax.imageio.ImageIO
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * @author Yuriy Tumakha
 */
class ImageRoutesSpec extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  lazy val routes: Route = new ImageRoutes(ContentTypeResolver.Default).imageRoutes

  "ImageRoutes" should "return image of expected size" in {
    Get("/logo.png") ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe ContentType(MediaTypes.`image/png`)

      val bytes = entityAs[Array[Byte]]
      val image: BufferedImage = ImageIO.read(new ByteArrayInputStream(bytes))

      image.getWidth shouldBe 200
      image.getHeight shouldBe 200
    }
  }

  it should "return error 404 for wrong path" in {
    Get("/wrongPath") ~> Route.seal(routes) ~> check {
      status shouldBe NotFound
      contentType shouldBe `text/plain(UTF-8)`
      entityAs[String] shouldBe "The requested resource could not be found."
    }
  }

}
