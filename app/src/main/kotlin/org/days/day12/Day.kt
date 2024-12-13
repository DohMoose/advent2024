package org.days.day12

import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.separatedTerms
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.regexToken
import org.days.IDay
import org.io.Position

class Day : IDay {
  override fun partOne(input: String): String {
    val visited = mutableSetOf<Plant>()
    val plants = toPlants(DayGrammar().parseToEnd(input))

    val regions = mutableListOf<List<Plant>>()

    for (row in plants) {
      for (plant in row) {
        if (visited.contains(plant)) {
          continue
        }
        regions.add(walkRegion(plant, visited, plants).toList())
      }
    }
    return regions.sumOf {
      area(it) * perimeter(it)
    }.toString()
  }

  fun walkRegion(plant: Plant, visited: MutableSet<Plant>, plants: List<List<Plant>>): Sequence<Plant> = sequence {
    if (!visited.contains(plant)) {
      yield(plant)
      visited.add(plant)
      for (neighbour in plant.neighbours.flatMap { walkRegion(it.get(plants), visited, plants) }) {
        yield(neighbour)
      }
    }
  }

  fun area(region: List<Plant>): Int {
    return region.count()
  }

  fun perimeter(region: List<Plant>): Int {
    return region.map { 4 - it.neighbours.count() }.sum()
  }


  override fun partTwo(input: String): String {
    val visited = mutableSetOf<Plant>()
    val plants = toPlants(DayGrammar().parseToEnd(input))

    val regions = mutableListOf<List<Plant>>()

    for (row in plants) {
      for (plant in row) {
        if (visited.contains(plant)) {
          continue
        }
        regions.add(walkRegion(plant, visited, plants).toList())
      }
    }
    return regions.sumOf {
      area(it) * countPoints(plants, it)
    }.toString()
  }


  fun getDir(plant: Position, neighbour: Position): Int {
    return directions.indexOfFirst { dir ->
      plant + dir == neighbour
    }
  }

  fun countPoints(grid: List<List<Plant>>, region: List<Plant>): Int {
    val edges = region.filter { it.neighbours.count() < 4 }
    val explodedPoints = mutableListOf<Pair<Double, Double>>()
    edges.forEach {
      val edgesPoints = mutableListOf<Pair<Double, Double>>()
      exploded(upDown, leftRight, it, edgesPoints, grid)
      exploded(leftRight, upDown, it, edgesPoints, grid)
      explodedPoints.addAll(edgesPoints)
    }


    printPoints(explodedPoints.groupingBy { it }.eachCount())


    return explodedPoints.count() / 2
  }

  fun printPoints(points: Map<Pair<Double, Double>, Int>) {
    println()
    val minX = points.keys.map { it.first }.min()
    var y = points.keys.map { it.second }.min()

    val xMax = points.keys.map { it.first }.max()
    val yMax = points.keys.map { it.second }.max()

    while (y <= yMax) {
      var x = minX
      while (x <= xMax) {
        if (points.contains(Pair(x, y))) {
          print(points[Pair(x, y)])
        } else {
          print(".")
        }
        x += 1
      }
      println()
      y += 1
    }

  }

  private fun exploded(
    directionaily1: List<Position>,
    directionaily2: List<Position>,
    it: Plant,
    explodedPoints: MutableList<Pair<Double, Double>>,
    grid: List<List<Plant>>
  ) {
    directionaily1.filter { dir ->
      !it.neighbours.contains(it.position + dir)
    }.map { dir ->
      directionaily2.filter { dir2 ->
        val neighbour = it.neighbours.firstOrNull { n -> n == it.position + dir2 }
        neighbour?.get(grid)?.neighbours?.contains(neighbour + dir)// the neighbour is also open
          ?: true
      }.map { dir2 ->
        val divisor = 2.0
        val pos = it.position + dir / divisor.toInt()
        val point = Pair(pos.x + (dir.x / divisor) + (dir2.x / divisor), pos.y + (dir.y / divisor) + (dir2.y / divisor))
        explodedPoints.add(point)
      }
    }
  }

  val directions = listOf(
    Position(0, -1),   // Up
    Position(1, 0),   // Right
    Position(0, 1),  // Down
    Position(-1, 0),  // Left
  )

  val upDown = listOf(directions[0], directions[2])
  val leftRight = listOf(directions[1], directions[3])

  data class Plant(val letter: String, val position: Position, val neighbours: List<Position>)

  fun toPlants(raw: List<List<String>>): List<List<Plant>> {
    return raw.mapIndexed { y, row ->
      row.mapIndexed { x, letter ->
        Plant(letter, Position(x, y), directions.mapNotNull { dir ->
          if (isIndexInRange(raw, x + dir.x, y + dir.y) && raw[y + dir.y][x + dir.x] == raw[y][x])
            Position(x + dir.x, y + dir.y)
          else
            null
        })
      }
    }
  }

  fun <T> isIndexInRange(grid: List<List<T>>, x: Int, y: Int): Boolean {
    return y in grid.indices && x in grid[y].indices
  }

  class DayGrammar : Grammar<List<List<String>>>() {
    val plant by regexToken("[A-Z]")

    val plantParser by plant use { text }
    val newLine by regexToken("\\n")
    val lineParser by oneOrMore(plantParser)
    override val rootParser by separatedTerms(lineParser, newLine)
  }
}
