package com.tumakha.image

import java.io.{FileNotFoundException, InputStream}

/**
 * @author Yuriy Tumakha
 */
trait ResourceSupport {

  def resourceAsStream(resourceName: String): InputStream =
    Option(getClass.getResourceAsStream(resourceName))
      .getOrElse(throw new FileNotFoundException(resourceName))

}
