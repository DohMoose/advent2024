package org.days.four

import org.days.IDay

class Day : IDay {
  override fun partOne(input: String): String {
    val grid = parse(input)
    var count = 0
    for (y in grid.indices) {
      for (x in grid[0].indices) {
        count += (countXmases(grid, x, y))
      }
    }

    return count.toString()
  }

  override fun partTwo(input: String): String {
    val grid = parse(input)
    var count = 0
    for (y in grid.indices) {
      for (x in grid[0].indices) {
        if (isXmas(grid, x, y)) {
          count += 1
        }
      }
    }

    return count.toString()
  }

  fun isXmas(grid: List<List<Char>>, x: Int, y: Int): Boolean {
    val charAtBottomRightInRange = isIndexInRange(grid, x + 2, y + 2)
    if (!charAtBottomRightInRange) {
      return false
    }
    val isMiddleLetterA = grid[y + 1][x + 1] == 'A'
    if (!isMiddleLetterA) {
      return false
    }
    val charAtTopLeft = grid[y][x]
    val charAtBottomRight = grid[y + 2][x + 2]
    val isOppositeDifferent =
      (charAtTopLeft == 'M' && charAtBottomRight == 'S') || (charAtTopLeft == 'S' && charAtBottomRight == 'M')

    if (!isOppositeDifferent)
      return false

    return arrayOf(grid[y + 2][x], grid[y][x + 2]).toSet() == arrayOf('M', 'S').toSet()
  }

  val directions = listOf(
    Pair(0, 1),   // Up
    Pair(0, -1),  // Down
    Pair(-1, 0),  // Left
    Pair(1, 0),   // Right
    Pair(-1, 1),  // Up-Left
    Pair(1, 1),   // Up-Right
    Pair(-1, -1), // Down-Left
    Pair(1, -1)   // Down-Right
  )

  val letters = "XMAS".toList()

  fun countXmases(grid: List<List<Char>>, x: Int, y: Int): Int {
    if (grid[y][x] != 'X') {
      return 0
    }
    var remainingDirections = directions.toList()
    for (i in 1..3) {
      val letter = letters[i]
      remainingDirections = remainingDirections.filter {
        val curX = x + (it.second * i)
        val curY = y + (it.first * i)
        val inRange = isIndexInRange(grid, curY, curX)
        inRange && grid[curY][curX] == letter
      }.toList()
    }
    return remainingDirections.count()
  }

  fun isIndexInRange(grid: List<List<Char>>, rowIndex: Int, colIndex: Int): Boolean {
    return rowIndex in grid.indices && colIndex in grid[rowIndex].indices
  }

  fun parse(input: String): List<List<Char>> {
    return input.trimEnd().lines().map {
      it.toList()
    }
  }
}
