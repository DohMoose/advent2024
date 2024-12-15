package org.days.day14

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.utils.Tuple2
import com.github.h0tk3y.betterParse.utils.Tuple3
import com.github.h0tk3y.betterParse.utils.Tuple4
import org.days.IDay
import org.io.Position

data class Robot(var position: Position, val velocity: Position)
typealias Robots = List<Robot>

class Day : IDay {

  override fun partOne(input: String): String {
    val robots = DayGrammar().parseToEnd(input)
    val gridSize = if (robots.count() == 12) {
      Position(11, 7)
    } else {
      Position(101, 103)
    }
    display(robots, gridSize, 0)
    val maxSeconds = 100
    for (seconds in 1..maxSeconds) {
      robots.forEach {
        move(it, gridSize)

      }
    }

    return countQuandrants(robots, gridSize).toString()
  }

  override fun partTwo(input: String): String {
    val robots = DayGrammar().parseToEnd(input)
    val gridSize = if (robots.count() == 12) {
      Position(11, 7)
    } else {
      Position(101, 103)
    }
    val maxSeconds = 100000
    for (seconds in 1..maxSeconds) {
      robots.forEach {
        move(it, gridSize)
      }
      val big = biggestContigousGroup(robots)
      if (big > 100) {
        display(robots, gridSize, maxSeconds)
        println("Biggest group is ${big}")
        return seconds.toString()
      }
    }
    return 1.toString()
  }

  val directions = listOf(
    Position(0, -1),   // Up
    Position(1, 0),   // Right
    Position(0, 1),  // Down
    Position(-1, 0),  // Left
  )

  fun biggestContigousGroup(robots: Robots): Int {
    val visited = mutableSetOf<Position>()
    val positions = robots.map { it.position }.toHashSet()
    fun findConnected(position: Position): Int {
      if (visited.contains(position))
        return 0
      visited.add(position)
      return 1 + directions
        .map { position + it }
        .filter { positions.contains(it) }
        .sumOf { findConnected(it) }
    }

    return positions.maxOf { findConnected(it) }
  }

  fun countQuandrants(robots: Robots, gridSize: Position): Int {
    val robotMap = robots.groupingBy { it.position }.eachCount()
    val halfX = gridSize.x / 2
    val halfY = gridSize.y / 2
    val topLeft = robotMap.filter {
      it.key.x < halfX && it.key.y < halfY
    }.values.sum()
    val topRight = robotMap.filter {
      it.key.x > halfX && it.key.y < halfY
    }.values.sum()
    val bottomRight = robotMap.filter {
      it.key.x > halfX && it.key.y > halfY
    }.values.sum()
    val bottomLeft = robotMap.filter {
      it.key.x < halfX && it.key.y > halfY
    }.values.sum()
    return topLeft * topRight * bottomRight * bottomLeft
  }


  fun move(robot: Robot, gridSize: Position) {
    val rawPosition = robot.position + robot.velocity
    val teleportedPosition = Position(teleport(rawPosition.x, gridSize.x), teleport(rawPosition.y, gridSize.y))
    robot.position = teleportedPosition
  }

  fun teleport(pos: Int, max: Int): Int {
    if (pos < 0) {
      return max + pos
    }
    return pos % max
  }

  fun display(robots: Robots, gridSize: Position, seconds: Int) {
    println("After ${seconds} seconds")
    val robotMap = robots.groupingBy { it.position }.eachCount()
    for (y in 0 until gridSize.y) {
      for (x in 0 until gridSize.x) {
        val count = robotMap.getOrDefault(Position(x, y), 0)
        if (count == 0) {
          print(".")
        } else {
          print(count)
        }
      }
      println("")
    }
    println("")
  }


  class DayGrammar : Grammar<Robots>() {
    val p by literalToken("p=")
    val v by literalToken(" v=")
    val number by regexToken("-?\\d+")
    val newLine by regexToken("\\n")

    val comma by literalToken(",")

    val numberParser by number use { text.toInt() }
    val numberPair by numberParser and -comma and numberParser map { Position(it.t1, it.t2) }

    var lineParser = -p and numberPair and -v and numberPair map { Robot(it.t1, it.t2) }

    override val rootParser by separatedTerms(lineParser, newLine)
  }
}
