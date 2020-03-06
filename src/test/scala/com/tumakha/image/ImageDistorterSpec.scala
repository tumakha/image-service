package com.tumakha.image

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream

import javax.imageio.ImageIO
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * @author Yuriy Tumakha
 */
class ImageDistorterSpec extends AnyFlatSpec with Matchers with ResourceSupport {

  val baseImage = "/images/0.png"
  val colorMaxOffset = 3

  def getRGB(argb: Int): List[Int] = List((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF)

  "ImageDistorter" should "return image bytes with random distortion applied to left half" in {
    val distorter = new ImageDistorter(baseImage, colorMaxOffset, outputFormat = "png")
    val bytes = distorter.getDistortedImage

    val origImage: BufferedImage = ImageIO.read(resourceAsStream(baseImage))
    val distortedImage: BufferedImage = ImageIO.read(new ByteArrayInputStream(bytes))

    distortedImage.getWidth shouldBe origImage.getWidth
    distortedImage.getHeight shouldBe origImage.getHeight

    val width: Int = origImage.getWidth / 2
    val height: Int = origImage.getHeight
    val origRightHalf: Array[Int] = origImage.getRGB(width, 0, width, height, null, 0, width)
    val distortedRightHalf: Array[Int] = distortedImage.getRGB(width, 0, width, height, null, 0, width)

    distortedRightHalf shouldBe origRightHalf

    val origLeftHalf: Array[Int] = origImage.getRGB(0, 0, width, height, null, 0, width)
    val distortedLeftHalf: Array[Int] = distortedImage.getRGB(0, 0, width, height, null, 0, width)

    val diff = distortedLeftHalf.zip(origLeftHalf).flatMap {
      case (dist, orig) =>
        val distortedRGB = getRGB(dist)
        val origRGB = getRGB(orig)
        distortedRGB.zip(origRGB).map(pair => pair._2 - pair._1)
    }
    val diffSize = diff.length

    diff.forall(_ >= 0) shouldBe true
    diffSize shouldBe origLeftHalf.length * 3

    val countDiffs = diff.groupBy(identity).view.mapValues(_.length).toMap
    println(countDiffs)

    countDiffs.size shouldBe colorMaxOffset + 1

    val avgPerPart: Double = 1.0 / countDiffs.size

    countDiffs.values.foreach(cnt =>
      cnt.toDouble / diffSize shouldBe avgPerPart +- 0.05 // expected less then 5% deviation on random allocation
    )
  }

}
