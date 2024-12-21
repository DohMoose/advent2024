package org.io

import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.absoluteValue

typealias Grid<T> = List<List<T>>

val directions = listOf(
  Position(0, -1),   // Up
  Position(1, 0),   // Right
  Position(0, 1),  // Down
  Position(-1, 0),  // Left
)
val northIndex = 0
val eastIndex = 1
val southIndex = 2
val westIndex = 3
val directionIndexes = listOf(northIndex, eastIndex, southIndex, westIndex)
val north = directions[northIndex]
val east = directions[eastIndex]
val south = directions[southIndex]
val west = directions[westIndex]

data class Position(val x: Int, val y: Int) {
  val neighbours: List<Position>
    get() {
      return directions.map { it + this }
    }

  fun inGrid(bottomRight: Position): Boolean {
    return x >= 0 && y >= 0 && x <= bottomRight.x && y <= bottomRight.y
  }

  fun <T> inGrid(grid: Grid<T>): Boolean {
    return inGrid(Position(grid[0].indices.count() - 1, grid.indices.count() - 1))
  }

  fun euclideanDistance(other: Position): Int {
    return (x - other.x).absoluteValue + (y - other.y).absoluteValue
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

  operator fun div(scalar: Int): Position {
    return Position(this.x / scalar, this.y / scalar)
  }

  override fun toString(): String {
    return "${x},${y}"
  }


  fun <T> get(grid: Grid<T>): T {
    return grid[y][x]
  }
}

fun <T> displayGrid(grid: Grid<T>, display: (node: T) -> String) {
  println("")
  println("")
  for (y in grid.indices) {
    for (x in grid[y].indices) {
      val position = Position(x, y)
      print(display(position.get(grid)))
    }
    println("")
  }

  println("")
  println("")
}


class Content(private val day: Int) {

  companion object {
    @JvmStatic
    fun autoInit(): Content {
      val callingClassName = Thread.currentThread().stackTrace[2].className
      val callingPackageName = Class.forName(callingClassName).`package`?.name
      val day = callingPackageName!!.substringAfterLast('.').removePrefix("day")
      return Content(day.toInt())
    }
  }

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

