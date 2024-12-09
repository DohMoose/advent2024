package org.days.day9

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.regexToken
import org.days.IDay

class Day : IDay {
  override fun partOne(input: String): String {
    val raw = DayGrammar().parseToEnd(input)
    val (files, freeSpaces) = raw.splitByIndex()
    val bigList = bigOlList(files, freeSpaces).toMutableList()

    var left = 0
    var right = bigList.count() - 1

    while (right > left) {
      while (bigList[left] != null && right > left) {
        left += 1
      }
      while (bigList[right] != null && bigList[left] == null && right > left) {
        bigList[left] = bigList[right]
        bigList[right] = null
        right -= 1
        left += 1
      }
      while (bigList[right] == null) {
        right -= 1
      }
    }

    val result = bigList.filterNotNull().mapIndexed { index, value ->
      index.toLong() * value.toLong()
    }.sum()
    return result.toString()
  }

  fun formatBigList(list: List<Int?>): String {
    return list.joinToString("-") {
      when (it) {
        null -> "."
        else -> it.toString()
      }
    }
  }

  fun bigOlList(files: List<Int>, freeSpaces: List<Int>): List<Int?> {
    val filesLength = files.count()
    val freeSpacesLength = freeSpaces.count()
    val zipped = files.zip(freeSpaces).flatMapIndexed { index, (file, freeSpace) ->
      List(file) { index } + List(freeSpace) { null }
    }
    return if (filesLength > freeSpacesLength) {
      zipped + List(files.last()) { filesLength - 1 }
    } else {
      zipped
    }
  }

  fun reversedFiles(files: List<Int?>): Sequence<List<Int?>> = sequence {
    val rFiles = files.reversed()
    var cur = rFiles[0]
    var curList = mutableListOf(cur)
    var index = 1
    while (index < rFiles.count()) {
      if (rFiles[index] == cur) {
        curList.add(cur)
      } else {
        yield(curList)
        cur = rFiles[index]
        curList = mutableListOf(cur)
      }
      index += 1
    }
    yield(curList)
  }

  override fun partTwo(input: String): String {
    val raw = DayGrammar().parseToEnd(input)
    val (files, freeSpaces) = raw.splitByIndex()
    val bigList = bigOlList(files, freeSpaces).toMutableList()

    var right = bigList.count() - 1

    for (file in reversedFiles(bigList)) {
      right -= file.count()
      if (file[0] != null) {
        moveFile(right, bigList, file)
      }
    }

    val result = bigList.mapIndexed { index, value ->
      if (value == null) {
        0
      } else {
        index.toLong() * value.toLong()
      }
    }.sum()
    return result.toString()
  }

  private fun moveFile(
    right: Int,
    bigList: MutableList<Int?>,
    item: List<Int?>
  ) {
    var left = 0
    // look from the left but stop if we reach our file
    while (right >= left) {
      // skip over already defragged blocks
      while (bigList[left] != null && right >= left) {
        left += 1
      }
      var space = 0
      // now look empty blocks
      while (bigList[left] == null && right >= left) {
        left += 1
        space += 1
        // we found a space big enough, move the file
        if (space == item.count()) {
          for (i in item.indices) {
            bigList[left - space + i] = item[i]
            bigList[right + 1 + i] = null
          }
          return
        }
      }
    }
  }

  fun <T> List<T>.splitByIndex(): Pair<List<T>, List<T>> {
    val evenIndexedItems = mutableListOf<T>()
    val oddIndexedItems = mutableListOf<T>()

    for (i in this.indices) {
      if (i % 2 == 0) {
        evenIndexedItems.add(this[i])
      } else {
        oddIndexedItems.add(this[i])
      }
    }

    return Pair(evenIndexedItems, oddIndexedItems)
  }

  class DayGrammar : Grammar<List<Int>>() {
    val digit by regexToken("\\d")

    val number by digit use { text.toInt() }

    val lineParser by oneOrMore(number)
    override val rootParser by lineParser
  }
}
