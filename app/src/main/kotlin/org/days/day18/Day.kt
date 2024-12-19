package org.days.day18

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.utils.Tuple2
import org.days.IDay
import org.io.Grid
import org.io.Position
import org.io.directions
import org.io.displayGrid
import java.util.*
import kotlin.math.max


data class Node(
  val position: Position,
  var valid: Boolean = true,
  var g: Int = Int.MAX_VALUE,
  var h: Int = 0,
  var f: Int = Int.MAX_VALUE,
  var parent: Node? = null,
  var altParents: MutableSet<Node> = mutableSetOf(),
  var direction: Position? = null
)

class Day : IDay {
  override fun partOne(input: String): String {
    // definitely an astar algorithm
    val (grid, bytes) = parse(input)
    displayGrid(grid, ::display)

    val takeAmount = if (grid.indices.count() < 10) {
      12
    } else {
      1024
    }
    bytes.take(takeAmount).forEach {
      it.get(grid).valid = false
    }
    displayGrid(grid, ::display)

    val path =
      aStar(Position(0, 0).get(grid), Position(grid.indices.count() - 1, grid.indices.count() - 1).get(grid), grid)
    displayGrid(grid) { display(it, path) }
    return (path.count() - 1).toString()
  }

  override fun partTwo(input: String): String {
    // definitely an astar algorithm
    val (grid, bytes) = parse(input)

    val takeAmount = if (grid.indices.count() < 10) {
      12
    } else {
      1024
    }
    bytes.take(takeAmount).forEach {
      it.get(grid).valid = false
    }
    bytes.drop(takeAmount).forEachIndexed { index, byte ->
      println("Processing $byte : ${index + takeAmount}")
      byte.get(grid).valid = false
      resetScores(grid)
      val path = aStar(
        Position(0, 0).get(grid),
        Position(grid.indices.count() - 1, grid.indices.count() - 1).get(grid),
        grid
      )
      if (path.isEmpty()) {
        return byte.toString()
      }
    }

    return "fail"
  }

  fun display(node: Node, path: List<Node>): String {
    if (path.contains(node)) {
      return "0"
    }
    return when (node.valid) {
      true -> "."
      false -> "#"
    }
  }

  fun display(node: Node): String {
    return when (node.valid) {
      true -> "."
      false -> "#"
    }
  }

  fun resetScores(grid: Grid<Node>) {
    grid.forEach {
      it.forEach { node ->
        node.g = Int.MAX_VALUE
        node.h = 0
        node.f = Int.MAX_VALUE
      }
    }
  }

  fun aStar(start: Node, finish: Node, grid: Grid<Node>): List<Node> {
    val openSet = PriorityQueue<Node>(compareBy { it.f })
    val closedSet = mutableSetOf<Node>()

    start.g = 0
    start.h = heuristic(start, finish)
    start.f = start.h
    openSet.add(start)

    while (openSet.isNotEmpty()) {
      val current = openSet.poll()
      if (current == finish)
        return reconstructPath(current)

      closedSet.add(current)

      val neighbours = directions.map { dir ->
        Pair(dir, dir + current.position)
      }.filter {
        it.second.inGrid(grid)
      }.map {
        Pair(it.first, it.second.get(grid))
      }.filter {
        it.second.valid
      }

      for (neighbourPair in neighbours) {
        val neighbour = neighbourPair.second
        if (neighbour in closedSet) continue

        val moveCost = 1
        val tentativeG = current.g + moveCost

        if (tentativeG < neighbour.g) {
          neighbour.parent = current
          neighbour.g = tentativeG
          neighbour.h = heuristic(neighbour, finish)
          neighbour.f = neighbour.g - neighbour.h

          if (neighbour !in openSet) {
            openSet.add(neighbour)
          }
        }
      }
    }

    return emptyList()
  }

  fun reconstructPath(goal: Node): List<Node> {
    val path = mutableListOf<Node>()
    var current: Node? = goal
    while (current != null) {
      path.add(current)
      current = current.parent
    }
    return path.reversed()
  }

  fun heuristic(a: Node, finish: Node): Int {
    return a.position.euclideanDistance(finish.position)
  }

  fun parse(input: String): Tuple2<Grid<Node>, List<Position>> {
    val bytes = DayGrammar().parseToEnd(input)
    val size = bytes.map { max(it.x, it.y) }.max()
    val grid = (0..size).map { y ->
      (0..size).map { x -> Node(Position(x, y)) }
    }
    return Tuple2(grid, bytes)
  }

  class DayGrammar : Grammar<List<Position>>() {
    val num by regexToken("\\d+")
    val comma by literalToken(",")
    val newLine by regexToken("\\n")

    val numParser by num use { text.toInt() }


    val lineParser by numParser and -comma and numParser map { Position(it.t1, it.t2) }

    override val rootParser by separatedTerms(lineParser, newLine)
  }
}
