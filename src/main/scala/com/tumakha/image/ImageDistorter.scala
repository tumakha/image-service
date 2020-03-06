package com.tumakha.image

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

import javax.imageio.ImageIO

import scala.util.Random


/**
 * The left half of the origImage should have random distortion applied to each pixel.
 *
 * The distortion effect should be such that the red, green and blue components of each pixels are between n-3 and n
 * where n is the original integer representation of the pixel’s colour component. 
 *
 * @author Yuriy Tumakha
 */
class ImageDistorter(imageResource: String, colorMaxOffset: Int, outputFormat: String) extends ResourceSupport {

  private val origImage: BufferedImage = ImageIO.read(resourceAsStream(imageResource))
  private val width: Int = origImage.getWidth / 2
  private val height: Int = origImage.getHeight
  private val startX: Int = 0
  private val startY: Int = 0
  private val offset: Int = 0
  private val random = new Random()
  private val origLeftHalf: Array[Int] = origImage.getRGB(startX, startY, width, height, null, offset, width)

  def getDistortedImage: Array[Byte] = {
    val distortedLeftHalf: Array[Int] = origLeftHalf.map(distortRGB)

    val image = cloneImage
    image.setRGB(startX, startY, width, height, distortedLeftHalf, offset, width)

    val os = new ByteArrayOutputStream()
    ImageIO.write(image, outputFormat, os)
    os.toByteArray
  }

  private def cloneImage: BufferedImage = {
    val newImage = new BufferedImage(origImage.getWidth, origImage.getHeight, BufferedImage.TYPE_INT_ARGB);
    val g = newImage.createGraphics
    g.drawImage(origImage, 0, 0, null)
    g.dispose()
    newImage
  }

  private def distortRGB(argb: Int): Int = {
    def randomizeColor(color: Int): Int = Math.max(0, color - random.nextInt(colorMaxOffset + 1))

    val (alpha, red, green, blue) = ((argb >> 24) & 0xFF, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF)
    alpha << 24 | randomizeColor(red) << 16 | randomizeColor(green) << 8 | randomizeColor(blue)
  }

}
