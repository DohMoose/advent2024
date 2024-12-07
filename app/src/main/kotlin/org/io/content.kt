package org.io

import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths

class Content(private val day: Int) {
  fun loadPartOne(): String {
    return load(1, false)
  }

  fun loadPartOneSample(): String {
    return load(1, true)
  }

  fun loadPartTwo(): String {
    return load(2, false)
  }

  fun loadPartTwoSample(): String {
    return load(2, true)
  }

  fun load(part: Int, sample: Boolean): String {
    val sampleSuffix = if (sample) "_sample" else ""

    val inputStream = javaClass.classLoader.getResourceAsStream("inputs/day${day}/part_${part}${sampleSuffix}.txt")
    if (inputStream != null) {
      val content = inputStream.bufferedReader().use { it.readText() }
      return content.trimEnd()
    } else {
      println("Resource not found")
      throw (FileNotFoundException("inputs/day${day}/part_${part}${sampleSuffix}.txt"))
    }
  }
}

