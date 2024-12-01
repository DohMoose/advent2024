package org.days.one

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.utils.Tuple2
import org.days.IDay
import kotlin.math.absoluteValue

class Day : IDay {

  fun lists(input: String): Tuple2<List<Int>, List<Int>> {
    val result = DayGrammar().parseToEnd(input)

    val first = result.map { it.t1 }.sorted()
    val second = result.map { it.t2 }.sorted()

    return Tuple2(first, second)
  }

  override fun partOne(input: String): String {
    val (first, second) = lists(input)

    return first
      .zip(second).sumOf {
        (it.first - it.second).absoluteValue
      }
      .toString()
  }

  override fun partTwo(input: String): String {
    val (first, second) = lists(input)

    val secondFrequency = second.groupingBy { it }.eachCount()

    return first.sumOf {
      it * (secondFrequency[it] ?: 0)
    }.toString()
  }

  class DayGrammar : Grammar<List<Tuple2<Int, Int>>>() {
    val num by regexToken("\\d+")
    val newLine by regexToken("\\n")
    val whitespace by regexToken("\\s+")
    val numParser by num use { text.toInt() }
    val lineParser by numParser and -whitespace and numParser

    override val rootParser by separatedTerms(lineParser, newLine)
  }
}