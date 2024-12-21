package org.days.day20

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.utils.Tuple2
import com.github.h0tk3y.betterParse.utils.Tuple3
import org.days.IDay
import org.days.day16.Day
import org.io.*
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max

interface Item
data class Node(
  val position: Position,
  var valid: Boolean = true,
  var g: Int = Int.MAX_VALUE,
  var h: Int = 0,
  var f: Int = Int.MAX_VALUE,
  var parent: Node? = null,
  var altParents: MutableSet<Node> = mutableSetOf(),
  var direction: Position? = null
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Node) return false

    return (position == other.position)
  }

  override fun hashCode(): Int {
    return 31 * position.hashCode()
  }
}

class Day : IDay {
  override fun partOne(input: String): String {
    val (grid, start, finish) = parse(input)

    val noCheatingPath = aStar(start, finish, grid)

    val pathLookup =
      noCheatingPath.mapIndexed { index, it -> Pair(index, it) }.associateBy { it.second.position }

    val minCheat = if (grid.indices.count() > 20) 100 else 2
    val result = noCheatingPath.mapIndexed { index, node ->
      directions.filter filter@{ direction ->
        val obstacle = node.position + direction
        if (obstacle.get(grid).valid) {
          // not a wall
          return@filter false
        }
        val pos = node.position + (direction * 2)
        val cheatNode = pathLookup[pos] ?: return@filter false

        cheatNode.first - index - 2 >= minCheat
      }.count()
    }.sum()


    return result.toString()
  }

  override fun partTwo(input: String): String {
    val (grid, start, finish) = parse(input)

    val noCheatingPath = aStar(start, finish, grid)

    val pathLookup =
      noCheatingPath.mapIndexed { index, it -> Pair(index, it) }.associateBy { it.second.position }

    val minCheat = if (grid.indices.count() > 20) 100 else 50
    return noCheatingPath.take(noCheatingPath.count() - minCheat).mapIndexed { index, node ->
      val cheatPositions = getPossibleCheatPositions(node.position, 20)
        .mapNotNull { pathLookup[it] }

      cheatPositions.count { cheatNode ->
        cheatNode.first - index - cheatNode.second.position.euclideanDistance(node.position) >= minCheat
      }
    }.sum().toString()
  }

  fun getPossibleCheatPositions(position: Position, maxLength: Int): List<Position> {
    return (0 - maxLength..maxLength).flatMap { x ->
      val yMax = (maxLength - x.absoluteValue)
      (0 - yMax..yMax).map { y ->
        position + Position(x, y)
      }.filterNot {
        it.euclideanDistance(position) < 2
      }
    }
  }

  fun display(node: Node, path: List<Node>, start: Node, finish: Node): String {
    if (path.contains(node)) {
      return "0"
    }
    if (node == start) {
      return "S"
    }
    if (node == finish) {
      return "E"
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

  fun parse(input: String): Tuple3<List<List<Node>>, Node, Node> {
    val items = DayGrammar().parseToEnd(input)
    var start: Node? = null
    var finish: Node? = null
    val grid = items.mapIndexed { y, row ->
      row.mapIndexed { x, item ->
        val position = Position(x, y)
        when (item) {
          is DotItem -> Node(position, true)
          is ObstacleItem -> Node(position, false)
          is ReindeerItem -> {
            start = Node(position, true)
            start!!
          }

          is FinishItem -> {
            finish = Node(position, true)
            finish!!
          }

          else -> throw Exception("Unknown Type")
        }
      }
    }

    return Tuple3(grid, start!!, finish!!)
  }


  class DotItem : Item
  class ObstacleItem : Item
  class ReindeerItem : Item
  class FinishItem : Item

  class DayGrammar : Grammar<Grid<Item>>() {
    val obstacle by literalToken("#")
    val dot by literalToken(".")
    val reindeerStart by literalToken("S")
    val finish by literalToken("E")

    val obstacleParser by obstacle use { ObstacleItem() }
    val dotParser by dot use { DotItem() }
    val reindeerParser by reindeerStart use { ReindeerItem() }
    val finishParser by finish use { FinishItem() }
    val newLine by regexToken("\\n")


    val lineParser by oneOrMore(obstacleParser or dotParser or reindeerParser or finishParser)
    override val rootParser by separatedTerms(lineParser, newLine)
  }
}
