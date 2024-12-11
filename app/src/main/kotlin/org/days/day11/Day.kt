package org.days.day11

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import org.days.IDay
import org.io.Position

class Day : IDay {
  override fun partOne(input: String): String {
    var stones = DayGrammar().parseToEnd(input)
    for (i in 1..25) {
      stones = nextGenSimple(stones).toList()
    }
    return stones.count().toString();
  }

  fun nextGenSimple(input: List<Long>): Sequence<Long> = sequence {
    input.forEach {
      when {
        it == 0.toLong() -> yield(1)
        evenDigits(it) -> {
          val (left, right) = splitNumber(it)
          yield(left)
          yield(right)
        }

        else -> yield(it * 2024)
      }
    }
  }

  override fun partTwo(input: String): String {
    var stones =
      DayGrammar().parseToEnd(input).associateWith { 1.toLong() }

    for (i in 1..75) {

      stones = nextGenEfficient(stones)
      println(i.toString() + "=" + stones.values.sum())
    }
    return stones.values.sum().toString()
  }

  fun nextGenEfficient(input: Map<Long, Long>): Map<Long, Long> {
    val newMap = mutableMapOf<Long, Long>()
    input.forEach { (key, value) ->
      when {
        key == 0.toLong() -> {
          newMap[1] = newMap.getOrDefault(1, 0) + value
        }

        evenDigits(key) -> {
          val (left, right) = splitNumber(key)
          newMap[left] = newMap.getOrDefault(left, 0) + value
          newMap[right] = newMap.getOrDefault(right, 0) + value
        }

        else -> newMap[key * 2024] = newMap.getOrDefault(key * 2024, 0) + value
      }
    }
    return newMap
  }

  fun evenDigits(number: Long): Boolean {
    return number.toString().length % 2 == 0
  }

  fun splitNumber(number: Long): Pair<Long, Long> {
    val asString = number.toString()
    return Pair(asString.substring(0, asString.length / 2).toLong(), asString.substring(asString.length / 2).toLong())
  }

  class DayGrammar : Grammar<List<Long>>() {
    val digit by regexToken("\\d+")
    val spacer by literalToken(" ")

    val numParser by digit use { text.toLong() }
    override val rootParser by separatedTerms(numParser, spacer)
  }
}
