package org.days.eight

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.utils.Tuple2
import org.days.IDay
import org.io.Position

class Day : IDay {
  override fun partOne(input: String): String {
    val (bottomRight, map) = parse(DayGrammar().parseToEnd(input))
    return map.flatMap {
      antiNodes(bottomRight, it.value)
    }.distinct().count().toString()
  }

  fun antiNodes(bottomRight: Position, locations: List<Position>): List<Position> {
    return locations.flatMapIndexed { currentIndex, a ->
      locations.filterIndexed { index, _ ->
        index != currentIndex
      }.map { b ->
        antiNode(a, b)
      }.filter { an ->
        an.inGrid(bottomRight)
      }
    }
  }

  fun antiNode(a: Position, b: Position): Position {
    return a + a - b
  }

  override fun partTwo(input: String): String {
    val (bottomRight, map) = parse(DayGrammar().parseToEnd(input))
    return map.flatMap {
      antiNodes2(bottomRight, it.value)
    }.distinct().count().toString()
  }

  fun antiNodes2(bottomRight: Position, locations: List<Position>): List<Position> {
    return locations.flatMapIndexed { currentIndex, a ->
      locations.filterIndexed { index, _ ->
        index != currentIndex
      }.flatMap { b ->
        generateAntiNodes(bottomRight, a, b).toList()
      }
    }
  }

  fun generateAntiNodes(bottomRight: Position, a: Position, b: Position): Sequence<Position> = sequence {
    var steps = 0
    while (true) {
      val an = a + ((a - b) * steps)
      if (an.inGrid(bottomRight)) {
        yield(an)
        steps += 1
      } else {
        break
      }
    }
  }

  fun parse(matrix: List<List<Item>>): Tuple2<Position, Map<String, List<Position>>> {
    val valueLocationMap = mutableMapOf<String, MutableList<Position>>()

    for (rowIndex in matrix.indices) {
      for (colIndex in matrix[rowIndex].indices) {
        val value = matrix[rowIndex][colIndex]
        if (value is Variable) {
          val location = Position(colIndex, rowIndex)
          valueLocationMap.computeIfAbsent(value.name) { mutableListOf() }.add(location)
        }
      }
    }
    val bottomRight = Position(matrix.indices.count() - 1, matrix[0].indices.count() - 1)
    return Tuple2(bottomRight, valueLocationMap)
  }

  fun printAntiNodes(frequency: String, bottomRight: Position, nodes: List<Position>, antiNodes: List<Position>) {
    for (y in 0..bottomRight.y) {
      for (x in 0..bottomRight.x) {
        val position = Position(x, y)
        when {
          nodes.contains(position) -> print(frequency)
          antiNodes.contains(position) -> print("#")
          else -> print(".")
        }
      }
      println()
    }
    println()
    println()
    println()
  }

  interface Item
  class Dot : Item
  class Variable(val name: String) : Item


  class DayGrammar : Grammar<List<List<Item>>>() {
    val digit by regexToken("\\d")

    val letter by regexToken("[A-Za-z]")

    val dot by literalToken(".")
    val hash by literalToken("#")
    val newLine by regexToken("\\n")

    val varParser by (digit or letter) use { Variable(text) }
    val dotParser by (dot or hash) use { Dot() }

    val lineParser by oneOrMore(varParser or dotParser)
    override val rootParser by separatedTerms(lineParser, newLine)
  }
}
