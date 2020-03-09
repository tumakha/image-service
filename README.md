# image-service

![image-service](https://github.com/tumakha/image-service/workflows/Scala%20CI/badge.svg)

Image Service returns image with random distortion applied to each pixel in left half part.

Effect is more visible with setting colorMaxOffset = 30 and more.

## Run unit tests ##

    sbt test

## Run http service ##

    sbt run

## Image endpoint ##

http://localhost:8888/logo.png
