package org.days.day16

import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.separatedTerms
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import org.days.IDay
import org.io.Position
import org.io.directionIndexes
import org.io.directions
import org.io.eastIndex
import java.awt.Point
import java.util.*

interface Item
typealias Items = List<List<Item>>
typealias Nodes = Map<Position, Node>


data class Node(
  val position: Position,
  var g: Int = Int.MAX_VALUE,
  var h: Int = 0,
  var f: Int = Int.MAX_VALUE,
  var parent: Node? = null,
  var altParents: MutableSet<Node> = mutableSetOf(),
  var direction: Position? = null
)

class Day : IDay {
  override fun partOne(input: String): String {
    val items = DayGrammar().parseToEnd(input)

    val nodes = mutableMapOf<Position, Node>()
    items.forEachIndexed { y, row ->
      row.forEachIndexed { x, item ->
        val position = Position(x, y)
        if (position.get(items) !is ObstacleItem) {
          nodes[position] = Node(Position(x, y))
        }
      }
    }

    val reindeer = find<ReindeerItem>(items)
    val finish = find<FinishItem>(items)

    return aStar(nodes[reindeer]!!, nodes[finish]!!, nodes).last().g.toString()
  }

  fun aStar(start: Node, finish: Node, nodes: Nodes): List<Node> {
    val openSet = PriorityQueue<Node>(compareBy { it.f })
    val closedSet = mutableSetOf<Node>()

    start.g = 0
    start.h = heuristic(start, finish)
    start.f = start.h
    start.direction = directions[eastIndex]
    openSet.add(start)

    while (openSet.isNotEmpty()) {
      val current = openSet.poll()
      if (current == finish)
        return reconstructPath(current)

      closedSet.add(current)

      val neighbours = directions.map { dir ->
        Pair(dir, dir + current.position)
      }.filter {
        nodes.contains(it.second)
      }.map {
        Pair(it.first, nodes[it.second]!!)
      }
      for (neighbourPair in neighbours) {
        val neighbour = neighbourPair.second
        if (neighbour in closedSet) continue

        val moveCost = if (current.direction == neighbourPair.first) 1 else 1001
        val tentativeG = current.g + moveCost


        if (tentativeG < neighbour.g) {
          neighbour.parent = current
          neighbour.g = tentativeG
          neighbour.h = heuristic(neighbour, finish)
          neighbour.f = neighbour.g - neighbour.h
          neighbour.direction = neighbourPair.first

          if (neighbour !in openSet) {
            openSet.add(neighbour)
          }
        }
      }
    }

    return emptyList()
  }

  fun aStarAll(start: Node, finish: Node, nodes: Nodes): List<List<Node>> {
    val openSet = PriorityQueue<Node>(compareBy { it.f })
    val closedSet = mutableSetOf<Node>()
    val paths = mutableListOf<List<Node>>()
    var cheapestCost = Int.MAX_VALUE

    start.g = 0
    start.h = heuristic(start, finish)
    start.f = start.h
    start.direction = directions[eastIndex]
    openSet.add(start)


    while (openSet.isNotEmpty()) {
      val current = openSet.poll()
      if (current == finish) {
        if (current.g < cheapestCost) {
          cheapestCost = current.g
          paths.clear()
        }
        if (current.g == cheapestCost) {
          paths.add(reconstructPath(current))
        }
        continue
      }

      closedSet.add(current)

      val neighbours = directions.map { dir ->
        Pair(dir, dir + current.position)
      }.filter {
        nodes.contains(it.second)
      }.map {
        Pair(it.first, nodes[it.second]!!)
      }
      for (neighbourPair in neighbours) {
        val neighbour = neighbourPair.second
        val moveCost = if (current.direction == neighbourPair.first) 1 else 1001
        val tentativeG = current.g + moveCost

        if (neighbour in closedSet) {
          if (tentativeG == neighbour.g + 1000) {
            neighbour.altParents.add(current)
          }
          continue
        }

        if (tentativeG < neighbour.g) {
          neighbour.parent = current
          neighbour.g = tentativeG
          neighbour.h = heuristic(neighbour, finish)
          neighbour.f = neighbour.g - neighbour.h
          neighbour.direction = neighbourPair.first

          if (neighbour !in openSet) {
            openSet.add(neighbour)
          }
        }
      }
    }
    return paths
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

  fun returnAllNodes(goal: Node): Set<Node> {
    val path = mutableSetOf<Node>()
    var current: Node? = goal
    while (current != null) {
      path.add(current)
      path.addAll(current.altParents.flatMap { returnAllNodes(it).toList() })
      current = current.parent
    }
    return path
  }

  fun heuristic(a: Node, finish: Node): Int {
    return a.position.euclideanDistance(finish.position)
  }

  override fun partTwo(input: String): String {
    val items = DayGrammar().parseToEnd(input)
    val nodes = mutableMapOf<Position, Node>()
    items.forEachIndexed { y, row ->
      row.forEachIndexed { x, item ->
        val position = Position(x, y)
        if (position.get(items) !is ObstacleItem) {
          nodes[position] = Node(Position(x, y))
        }
      }
    }

    val reindeer = find<ReindeerItem>(items)
    val finish = find<FinishItem>(items)
    val path = aStarAll(nodes[reindeer]!!, nodes[finish]!!, nodes).first()
    val specators = returnAllNodes(path.last()).map { it.position }.toSet()
    displaySpectators(specators, items)
    return specators.count()
      .toString() // this is wrong, need to verify by hand and remove the mistakes. Mine was 11 off, lool
  }

  fun displaySpectators(path: Set<Position>, items: Items) {
    println("")
    println("")
    for (y in items.indices) {
      val positions = mutableListOf<Position>()
      for (x in items[y].indices) {
        val position = Position(x, y)
        if (path.contains(position)) {
          positions.add(position)
          print(wrapInRed(wrapInBold("0")))
        } else {
          when (position.get(items)) {
            is ObstacleItem -> print("#")
            is ReindeerItem -> print("S")
            is FinishItem -> print("E")
            else -> print(".")
          }
        }
      }
      print(positions.joinToString(", ") { it.toString() })
      println("")
    }
    println("")
    println("")
  }

  inline fun <reified T : Item> find(items: Items): Position {
    for (y in items.indices) {
      for (x in items[y].indices) {
        if (items[y][x] is T) {
          return Position(x, y)
        }
      }
    }
    throw Exception("Could not find")
  }

  fun wrapInBold(text: String): String {
    return "\u001B[1m$text\u001B[0m"
  }

  fun wrapInRed(text: String): String {
    return "\u001B[31m$text\u001B[0m"
  }


  class DotItem : Item
  class ObstacleItem : Item
  class ReindeerItem : Item
  class FinishItem : Item

  class DayGrammar : Grammar<List<List<Item>>>() {
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
