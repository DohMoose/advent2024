package org.io

import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths

data class Position(val x: Int, val y: Int) {
  fun inGrid(bottomRight: Position): Boolean {
    return x >= 0 && y >= 0 && x <= bottomRight.x && y <= bottomRight.y
  }

  operator fun plus(other: Position): Position {
    return Position(this.x + other.x, this.y + other.y)
  }

  operator fun minus(other: Position): Position {
    return Position(this.x - other.x, this.y - other.y)
  }

  operator fun times(scalar: Int): Position {
    return Position(this.x * scalar, this.y * scalar)
  }
}

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

