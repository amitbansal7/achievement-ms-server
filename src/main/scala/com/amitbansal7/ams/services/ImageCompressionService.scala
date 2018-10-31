package com.amitbansal7.ams.services

import java.awt.Color
import java.io._
import java.net.URLConnection

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import java.awt.Dimension
import java.nio.file.Files

object ImageCompressionService {

  def processImage(file: File): CompressedResult = {
    try {
      val buffer = Files.readAllBytes(file.toPath)
      val inputStream = new BufferedInputStream(new ByteArrayInputStream(buffer))
      var mimeType = ""
      mimeType = URLConnection.guessContentTypeFromStream(inputStream)

      if (mimeType == null || !mimeType.startsWith("image")) {
        return CompressedResult(false, "Invalid file format", null, null)
      }

      val extension = mimeType.split("/").toList.toArray.apply(1)

      if (extension.equals("gif")) {
        return CompressedResult(false, "GIF not supported", null, null)
      }

      if (!extension.equals("png") && !extension.equals("jpeg") && !extension.equals("jpg")) {
        return CompressedResult(false, "Image format not supported", null, null)
      }
      var bufferedImage = ImageIO.read(inputStream)
      inputStream.close();
      //png to jpg
      if (extension.equals("png")) {
        val newBufferedImage = new BufferedImage(bufferedImage.getWidth, bufferedImage.getHeight, BufferedImage.TYPE_INT_RGB)
        newBufferedImage.createGraphics.drawImage(bufferedImage, 0, 0, Color.BLACK, null)
        bufferedImage = newBufferedImage
      }
      val width = bufferedImage.getWidth
      val height = bufferedImage.getHeight

      val dimension = new Dimension(width, height)
      //Compress image
      val compressed = new ByteArrayOutputStream
      val outputStream = ImageIO.createImageOutputStream(compressed)
      val jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next
      val jpgWriteParam = jpgWriter.getDefaultWriteParam
      jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
      jpgWriteParam.setCompressionQuality(0.7f)
      jpgWriter.setOutput(outputStream)
      jpgWriter.write(null, new IIOImage(bufferedImage, null, null), jpgWriteParam)
      jpgWriter.dispose()
      val newBuffer = compressed.toByteArray

      compressed.close
      outputStream.close

      CompressedResult(true, "Image compression Successful", dimension, newBuffer)
    } catch {
      case e: Exception =>
        return CompressedResult(false, "Invalid file." + e.getMessage, null, null)
    }
  }

  case class CompressedResult(bool: Boolean, message: String, dimension: Dimension, buffer: Array[Byte])

}
