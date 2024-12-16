package org.days.day15

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
import org.io.directions


data class Robot(var position: Position, var currentMove: Int, val movements: List<Position>)
data class Obstacle(var position: Position, val movable: Boolean)
typealias WidePosition = Pair<Position, Position>

data class WideObstacle(var position: WidePosition, val movable: Boolean)

typealias ObstacleMap = Map<Position, Obstacle>
typealias MutableObstacleMap = MutableMap<Position, Obstacle>

typealias WideObstacleMap = Map<Position, WideObstacle>
typealias MutableWideObstacleMap = MutableMap<Position, WideObstacle>

class Day : IDay {

  override fun partOne(input: String): String {
    val (robot, map) = parse(input)
    for (i in 1..robot.movements.count()) {
      move(robot, map)
    }
    display(robot, map)

    return gps(map).toString()
  }

  fun gps(map: ObstacleMap): Long {
    return map.values.filter {
      it.movable
    }.sumOf {
      it.position.x.toLong() + it.position.y.toLong() * 100.toLong()
    }
  }

  override fun partTwo(input: String): String {
    val (robot, map) = parseWide(input)
    for (i in 1..robot.movements.count()) {
      moveWide(robot, map)
    }
    displayWide(robot, map)

    return gpsWide(map).toString()
  }

  fun gpsWide(map: WideObstacleMap): Long {
    return map.values.distinct().filter {
      it.movable
    }.sumOf {
      it.position.first.x.toLong() + it.position.first.y.toLong() * 100.toLong()
    }
  }

  fun parse(input: String): Tuple2<Robot, MutableObstacleMap> {
    val (map, movements) = DayGrammar().parseToEnd(input)
    val obstacles = mutableMapOf<Position, Obstacle>()
    var robot: Robot? = null
    for (y in map.indices) {
      for (x in map[y].indices) {
        val position = Position(x, y)
        when (val item = position.get(map)) {
          is ObstacleItem -> obstacles[position] = Obstacle(position, item.moveable)
          is RobotItem -> robot = Robot(position, 0, movements)
          else -> {}
        }
      }
    }

    return Tuple2(robot!!, obstacles)
  }

  fun parseWide(input: String): Tuple2<Robot, MutableWideObstacleMap> {
    val (map, movements) = DayGrammar().parseToEnd(input)
    val obstacles = mutableMapOf<Position, WideObstacle>()
    var robot: Robot? = null
    for (y in map.indices) {
      for (x in map[y].indices) {
        val widePosition = WidePosition(Position(x * 2, y), Position(x * 2 + 1, y))
        val position = Position(x, y)
        when (val item = position.get(map)) {
          is ObstacleItem -> {
            obstacles[widePosition.first] = WideObstacle(widePosition, item.moveable)
            obstacles[widePosition.second] = WideObstacle(widePosition, item.moveable)
          }

          is RobotItem -> robot = Robot(Position(position.x * 2, position.y), 0, movements)
          else -> {}
        }
      }
    }

    return Tuple2(robot!!, obstacles)
  }

  fun move(robot: Robot, map: MutableObstacleMap) {
    val direction = robot.movements[robot.currentMove]
    var position = robot.position
    position = position + direction
    val moveables = mutableListOf<Obstacle>()
    while (map[position]?.movable == true) {
      moveables.add(map[position]!!)
      position += direction
    }
    // if we have an available space, shift
    if (map[position] == null) {
      moveables.asReversed().forEach {
        map.remove(it.position)
        map.set(it.position + direction, Obstacle(it.position + direction, it.movable))
      }
      robot.position += direction
    }
    robot.currentMove += 1
  }

  fun moveWide(robot: Robot, map: MutableWideObstacleMap) {
    val direction = robot.movements[robot.currentMove]

    val positions = mutableListOf(robot.position + direction)
    val moveables = mutableListOf<WideObstacle>()
    while (positions.isNotEmpty()) {
      val position = positions.removeFirst()
      if (map[position]?.movable == false) {
        robot.currentMove += 1
        return // as soon as we hit an immovable, we need to abandon
      }
      if (map[position]?.movable == true) {
        if (moveables.contains(map[position])) {
          continue
        }
        moveables.add(map[position]!!)

        if (directions[1] == direction || directions[3] == direction) { //left or right
          positions.add(position + direction * 2)
        } else { // up and down, look for both positions
          positions.add(moveables.last().position.first + direction)
          positions.add(moveables.last().position.second + direction)
        }
      }
    }

    moveables.asReversed().forEach {
      map.remove(it.position.first)
      map.remove(it.position.second)
      val newPosition = WidePosition(it.position.first + direction, it.position.second + direction)
      map.set(newPosition.first, WideObstacle(newPosition, it.movable))
      map.set(newPosition.second, WideObstacle(newPosition, it.movable))
    }
    robot.position += direction
    robot.currentMove += 1
  }

  val arrows = listOf("^", ">", "v", "<")
  fun displayMove(robot: Robot) {
    val arrow = arrows[directions.indexOf(robot.movements[robot.currentMove])]
    println("Move ${arrow}: ")
  }

  fun display(robot: Robot, map: ObstacleMap) {
    val maxX = map.keys.maxBy { it.x }.x
    val maxY = map.keys.maxBy { it.y }.y
    for (y in 0..maxY) {
      for (x in 0..maxX) {
        val position = Position(x, y)
        if (position == robot.position) {
          print("@")
          continue
        }
        if (map.containsKey(position)) {
          if (map[position]!!.movable) {
            print("0")
          } else {
            print("#")
          }
        } else {
          print(".")
        }
      }
      println("")
    }
    println("")
    println("")
  }

  fun displayWide(robot: Robot, map: WideObstacleMap) {
    val maxX = map.keys.maxBy { it.x }.x
    val maxY = map.keys.maxBy { it.y }.y
    for (y in 0..maxY) {
      for (x in 0..maxX) {
        val position = Position(x, y)
        if (position == robot.position) {
          print("@")
          continue
        }
        val obstacle = map.get(position)
        if (obstacle == null) {
          print(".")
        } else {
          if (!obstacle.movable) {
            print("#")
          } else {
            if (obstacle.position.first == position) {
              print("[")
            } else {
              print("]")
            }
          }
        }
      }

      println("")
    }
    println("")
    println("")
  }

  interface Item {}
  class ObstacleItem(val moveable: Boolean) : Item
  class DotItem() : Item
  class RobotItem() : Item

  class DayGrammar : Grammar<Tuple2<List<List<Item>>, List<Position>>>() {
    val immovableObstacle by literalToken("#")
    val movableObstacle by literalToken("O")
    val dot by literalToken(".")
    val robot by literalToken("@")
    val newLine by regexToken("\\n")

    val immovableObstacleParser by immovableObstacle use { ObstacleItem(false) }
    val movableObstacleParser by movableObstacle use { ObstacleItem(true) }

    val dotParser by dot use { DotItem() }
    val robotParser by robot use { RobotItem() }
    val up by literalToken("^")
    val upParser by up use { directions[0] }
    val right by literalToken(">")
    val rightParser by right use { directions[1] }
    val down by literalToken("v")
    val downParser by down use { directions[2] }
    val left by literalToken("<")
    val leftParser by left use { directions[3] }
    val itemParser by immovableObstacleParser or movableObstacleParser or dotParser or robotParser

    val lineParser by oneOrMore(itemParser)

    val directionParser = upParser or rightParser or downParser or leftParser
    val allDirectionsParser = oneOrMore(directionParser and -optional(newLine))
    override val rootParser by separatedTerms(lineParser, newLine) and -newLine and -newLine and allDirectionsParser
  }
}
