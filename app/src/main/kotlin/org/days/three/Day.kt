package org.days.three

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.regexToken
import org.days.IDay

class Day : IDay {
  override fun partOne(input: String): String {
    val result = DayGrammar().parseToEnd(input)

    return result.count {
      validate(it)
    }.toString()
  }

  override fun partTwo(input: String): String {
    val result = DayGrammar().parseToEnd(input)

    return result.count {
      validateAllCombos(it)
    }.toString()
  }

  private fun validate(list: List<Int>): Boolean {
    val pairs = list.zipWithNext()
    val allPositive = pairs.all { (a, b) -> b - a in 1..3 }
    val allNegative = pairs.all { (a, b) -> a - b in 1..3 }

    return allPositive || allNegative
  }

  private fun validateWithRemoved(list: List<Int>, removeAt: Int): Boolean {
    val mutableList = list.toMutableList()
    mutableList.removeAt(removeAt)
    return validate(mutableList)
  }

  private fun validateAllCombos(list: List<Int>): Boolean {
    if (validate(list))
      return true

    return list.indices.any {
      validateWithRemoved(list, it)
    }
  }
}

class DayGrammar : Grammar<List<List<Int>>>() {
  private val num by regexToken("\\d+")
  private val newLine by regexToken("\\n")
  private val whitespace by regexToken("\\s+")
  private val numParser by num use { text.toInt() }
  private val lineParser by separatedTerms(numParser, whitespace)

  override val rootParser by separatedTerms(lineParser, newLine)
}
