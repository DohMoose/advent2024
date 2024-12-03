package org.days.four

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.*
import com.github.h0tk3y.betterParse.lexer.*
import com.github.h0tk3y.betterParse.parser.*
import com.github.h0tk3y.betterParse.utils.Tuple2
import org.days.IDay

class Day : IDay {
  override fun partOne(input: String): String {
    val parsed = DayGrammar().parseToEnd(input)

    return parsed.t1.toString()
  }

  override fun partTwo(input: String): String {
    val parsed = DayGrammar().parseToEnd(input)

    return parsed.t1.toString()
  }
}

interface Item
class Number(val value: Tuple2<Int, Int>) : Item
class Enabled(val value: Boolean) : Item


class DayGrammar : Grammar<Tuple2<Int, Int>>() {
  private val num by regexToken("\\d{1,3}")
  private val newLine by regexToken("\\n")
  private val mul by literalToken("mul", true)
  val lpar by literalToken("(", true)
  val rpar by literalToken(")", true)

  val comma by literalToken(",", true)
  val anychar by regexToken(".*", true)

  val numParser by num use { text.toInt() }
  private val instruction by -mul and -lpar and numParser and -comma and numParser and -rpar

  private val instructions by instruction and -optional(anychar) and -optional(newLine)


  override val rootParser by instructions
}


