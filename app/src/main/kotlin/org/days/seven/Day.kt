package org.days.seven

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.utils.Tuple2
import org.days.IDay


class Day : IDay {
  override fun partOne(input: String): String {
    return DayGrammar().parseToEnd(input).filter { (target, terms) ->
      tryForTotal1(target, terms[0], terms.drop(1))
    }.sumOf { (total, _) ->
      total
    }.toString()
  }

  fun tryForTotal1(target: Long, total: Long, terms: List<Long>): Boolean {
    if (total > target) {
      return false
    }
    if (terms.count() == 1) {
      return total * terms[0] == target || total + terms[0] == target
    }

    return tryForTotal1(target, total * terms[0], terms.drop(1)) ||
            tryForTotal1(target, total + terms[0], terms.drop(1))
  }

  override fun partTwo(input: String): String {
    return DayGrammar().parseToEnd(input).filter { (target, terms) ->
      tryForTotal2(target, terms[0], terms.drop(1))
    }.sumOf { (total, _) ->
      total
    }.toString()
  }

  fun tryForTotal2(target: Long, total: Long, terms: List<Long>): Boolean {
    if (total > target) {
      return false
    }
    if (terms.count() == 1) {
      return total * terms[0] == target ||
              total + terms[0] == target ||
              combine(total, terms[0]) == target
    }

    return tryForTotal2(target, total * terms[0], terms.drop(1)) ||
            tryForTotal2(target, total + terms[0], terms.drop(1)) ||
            tryForTotal2(target, combine(total, terms[0]), terms.drop(1))
  }

  fun combine(a: Long, b: Long): Long {
    val combinedString = "$a$b"
    return combinedString.toLong()
  }

  interface Item
  class Number(val value: Int) : Item
  class Variable(val name: String) : Item

  class DayGrammar : Grammar<List<Tuple2<Long, List<Long>>>>() {
    val num by regexToken("\\d+")

    val space by literalToken(" ")
    val colon by literalToken(":")
    val word by regexToken("[A-Za-z]+")
    val newLine by regexToken("\\n")

    val numParser by num use { text.toLong() }

    val varParser by word use { Variable(text) }

    val lineParser by numParser and -colon and -space and separatedTerms(numParser, space)
    override val rootParser by separatedTerms(lineParser, newLine)
  }
}
