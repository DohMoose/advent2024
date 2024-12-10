package org.days.day10

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.regexToken
import org.days.IDay
import org.io.Position

class Day : IDay {
  override fun partOne(input: String): String {
    val locations = toLocations(DayGrammar().parseToEnd(input))
    val trailHeads = trailHeads(locations)
    return trailHeads.sumOf {
      val set: MutableSet<Position> = mutableSetOf()
      explore(it, locations, set)
      set.count()

    }.toString()
  }

  override fun partTwo(input: String): String {
    // each grid item will have height and elevation change in each direction
    val locations = toLocations(DayGrammar().parseToEnd(input))
    val trailHeads = trailHeads(locations)
    return trailHeads.sumOf {
      explore2(it, locations)
    }.toString()
  }

  fun explore2(
    current: Location,
    locations: List<List<Location>>,
  ): Int {
    if (current.height == 9)
      return 1

    return current.elevations.mapIndexed { index, elevation ->
      if (elevation == 1) {
        val nextPos = (current.position + directions[index])
        //  if (!previousLocations.contains(nextPos)) {
        explore2(locations[nextPos.y][nextPos.x], locations)
        // }
      } else {
        0
      }
    }.sum()
  }

  fun explore(
    current: Location,
    locations: List<List<Location>>,
    summits: MutableSet<Position>
  ) {
    if (current.height == 9)
      summits.add(current.position)

    current.elevations.forEachIndexed { index, elevation ->
      if (elevation == 1) {
        val nextPos = (current.position + directions[index])
        explore(locations[nextPos.y][nextPos.x], locations, summits)
      }
    }
  }

  fun trailHeads(locations: List<List<Location>>): List<Location> {
    return locations.flatMap { row ->
      row.filter { location -> location.height == 0 }
    }
  }

  fun toLocations(raw: List<List<Int>>): List<List<Location>> {
    return raw.mapIndexed { y, row ->
      row.mapIndexed { x, height ->
        Location(raw[y][x], Position(x, y), directions.map { dir ->
          if (isIndexInRange(raw, x + dir.x, y + dir.y))
            raw[y + dir.y][x + dir.x] - raw[y][x]
          else
            null
        })
      }
    }
  }

  val directions = listOf(
    Position(0, 1),   // Up
    Position(0, -1),  // Down
    Position(-1, 0),  // Left
    Position(1, 0),   // Right
  )

  fun <T> isIndexInRange(grid: List<List<T>>, x: Int, y: Int): Boolean {
    return y in grid.indices && x in grid[y].indices
  }

  data class Location(val height: Int, val position: Position, val elevations: List<Int?>)

  class DayGrammar : Grammar<List<List<Int>>>() {
    val digit by regexToken("\\d")
    val newLine by regexToken("\\n")

    val number by digit use { text.toInt() }

    val lineParser by oneOrMore(number)
    override val rootParser by separatedTerms(lineParser, newLine)
  }
}
