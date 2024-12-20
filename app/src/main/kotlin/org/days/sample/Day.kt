package org.days.sample

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.regexToken
import org.days.IDay


class Day : IDay {
  override fun partOne(input: String): String {
    return input
  }

  override fun partTwo(input: String): String {
    return input
  }

  interface Item
  class Number(val value: Int) : Item
  class Variable(val name: String) : Item

  class DayGrammar : Grammar<List<List<Item>>>() {
    val num by regexToken("\\d+")
    val word by regexToken("[A-Za-z]+")
    val newLine by regexToken("\\n")

    val numParser by num use { Number(text.toInt()) }

    val varParser by word use { Variable(text) }

    val lineParser by oneOrMore(numParser or varParser)
    override val rootParser by separatedTerms(lineParser, newLine)
  }
}
