package org.days.six

import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.separatedTerms
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.utils.Tuple2
import org.days.IDay


class Day : IDay {
  val directions = listOf(
    Pair(0, -1),
    Pair(1, 0),
    Pair(0, 1),
    Pair(-1, 0)
  )

  override fun partOne(input: String): String {
    val (startLocation, grid) = parse(input)
    var direction = 0
    var location = startLocation
    val moves = mutableListOf<Pair<Int, Int>>()

    while (isIndexInRange(grid, location.second, location.first)) {
      if (grid[location.second][location.first] == 0)
        moves.add(location)
      else {
        location = Pair(
          location.first - directions[direction].first, location.second - directions[direction].second
        )
        direction = (direction + 1) % 4
      }

      location = Pair(location.first + directions[direction].first, location.second + directions[direction].second)
    }

    return moves.distinct().count().toString()
  }

  fun isIndexInRange(grid: List<List<Int>>, rowIndex: Int, colIndex: Int): Boolean {
    return rowIndex in grid.indices && colIndex in grid[rowIndex].indices
  }

  override fun partTwo(input: String): String {
    val (startLocation, grid) = parse(input)
    var direction = 0
    var location = startLocation
    val moves = mutableListOf<Tuple2<Int, Pair<Int, Int>>>()

    while (isIndexInRange(grid, location.second, location.first)) {
      if (grid[location.second][location.first] == 0)
        moves.add(Tuple2(direction, location))
      else {
        location = Pair(
          location.first - directions[direction].first, location.second - directions[direction].second
        )
        direction = (direction + 1) % 4
      }

      location = Pair(location.first + directions[direction].first, location.second + directions[direction].second)
    }
    println("total moves ${moves.count()}")
    val mGrid = grid.map { it.toMutableList() }
    return findLoops(moves, mGrid).toString()
  }

  private fun findLoops(
    previousMoves: List<Tuple2<Int, Pair<Int, Int>>>,
    mGrid: List<MutableList<Int>>,
  ): Int {
    val obstacles = mutableSetOf<Pair<Int, Int>>()
    for (moveIndex in (0..(previousMoves.count() - 2))) {
      if ((moveIndex) % 100 == 0) {
        println("Done $moveIndex moves, ${obstacles.count()} obstacles found")
      }
      val nextMove = previousMoves[moveIndex + 1].t2
      if (obstacles.contains(nextMove)) {
        continue
      }
      val moves = previousMoves.take(moveIndex).map { it.t2 }.toMutableList()
      if (moves.contains(nextMove)) {
        continue;
      }
      var location = previousMoves[moveIndex].t2
      var direction = previousMoves[moveIndex].t1
      mGrid[nextMove.second][nextMove.first] = 1
      while (isIndexInRange(mGrid, location.second, location.first)) {
        if (mGrid[location.second][location.first] == 0) {
          moves.add(location)
          if (isInLoop(moves)) {
            obstacles.add(nextMove)
            break
          }
        } else {
          location = Pair(
            location.first - directions[direction].first, location.second - directions[direction].second
          )
          direction = (direction + 1) % 4
        }

        location = Pair(location.first + directions[direction].first, location.second + directions[direction].second)
      }
      mGrid[nextMove.second][nextMove.first] = 0
    }
    return obstacles.count()
  }

  fun isInLoop(list: List<Pair<Int, Int>>): Boolean {
    if (list.size < 5) {
      return false
    }

    val lastPair = list[list.size - 1]
    val secondLastPair = list[list.size - 2]
    val thirdLastPair = list[list.size - 3]

    for (i in 0 until list.size - 3) {
      if (list[i] == thirdLastPair && list[i + 1] == secondLastPair && list[i + 2] == lastPair) {
        return true
      }
    }
    return false
  }

  fun parse(input: String): Tuple2<Pair<Int, Int>, List<List<Int>>> {
    val result = DayGrammar().parseToEnd(input)

    val startLocation = findValue(result)!!

    val mapToInts = result.map { row ->
      row.map {
        if (it is Number) {
          it.value
        } else {
          0
        }
      }
    }

    return Tuple2(startLocation, mapToInts)
  }

  fun findValue(matrix: List<List<Item>>): Pair<Int, Int>? {
    for (rowIndex in matrix.indices) {
      val row = matrix[rowIndex]
      for (colIndex in row.indices) {
        if (row[colIndex] is Variable) {
          return Pair(colIndex, rowIndex)
        }
      }
    }
    return null // Return null if the value is not found
  }


  interface Item
  class Number(val value: Int) : Item
  class Variable(val name: String) : Item

  class DayGrammar : Grammar<List<List<Item>>>() {
    val clear by literalToken(".")
    val obstacle by literalToken("#")
    val start by literalToken("^")

    val newLine by regexToken("\\n")

    val clearParser by clear use { Number(0) }
    val obstacleParser by obstacle use { Number(1) }
    val startParser by start use { Variable("^") }

    val lineParser by oneOrMore(clearParser or obstacleParser or startParser)
    override val rootParser by separatedTerms(lineParser, newLine)
  }
}
