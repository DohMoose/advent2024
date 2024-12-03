package org.days.three

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.*
import com.github.h0tk3y.betterParse.lexer.*
import com.github.h0tk3y.betterParse.parser.*
import com.github.h0tk3y.betterParse.utils.Tuple2
import org.days.IDay

class Day : IDay {
  override fun partOne(input: String): String {
    val parsed = parse(input)

    return parsed.sumOf {
      it.t1 * it.t2
    }.toString()
  }

  override fun partTwo(input: String): String {
    val parsed = parse2(input)

    return parsed.sumOf {
      it.t1 * it.t2
    }.toString()
  }


  private fun findAllOccurrences(mainString: String, subString: String): List<Int> {
    val indices = mutableListOf<Int>()
    var index = mainString.indexOf(subString)
    while (index >= 0) {
      indices.add(index)
      index = mainString.indexOf(subString, index + 1)
    }
    return indices
  }


  fun parse(input: String): List<Tuple2<Int, Int>> {
    val grammar = Day1Grammar()
    val occurancesOfMul = findAllOccurrences(input, "mul(")
    return occurancesOfMul.map {
      val substring = input.substring(it)
      val closing = substring.indexOf(")")
      grammar.tryParseToEnd(substring.substring(0, closing + 1))
    }.filterIsInstance<Parsed<Tuple2<Int, Int>>>().map {
      it.toParsedOrThrow().value
    }
  }

  fun findAllOccurrences2(mainString: String, subString: String, secondSubString: String): List<Int> {
    val indices = mutableListOf<Int>()
    var index = minOf(mainString.indexOf(subString), mainString.indexOf(secondSubString))
    while (index >= 0) {
      indices.add(index)
      val subIndex = mainString.indexOf(subString, index + 1)
      val secondSubIndex = mainString.indexOf(secondSubString, index + 1)
      index = if (subIndex < 0)
        secondSubIndex
      else if (secondSubIndex < 0)
        subIndex
      else
        minOf(mainString.indexOf(subString, index + 1), mainString.indexOf(secondSubString, index + 1))

    }
    return indices
  }

  fun parse2(input: String): List<Tuple2<Int, Int>> {
    var enabled = true
    val grammar = Day2Grammar()
    val occurancesOfInst = findAllOccurrences2(input, "mul(", "do")
    val what = occurancesOfInst.map {
      val substring = input.substring(it)
      val closing = substring.indexOf(")")
      val fragment = substring.substring(0, closing + 1)
      grammar.tryParseToEnd(fragment)
    }.filterIsInstance<Parsed<Item>>().map {
      it.toParsedOrThrow().value
    }.filter {
      when (it) {
        is Enabled -> {
          enabled = it.value
          false
        }

        is Number -> enabled
        else -> false
      }

    }.map { it as Number }
      .map { it.value }

    return what
  }

}

interface Item
class Number(val value: Tuple2<Int, Int>) : Item
class Enabled(val value: Boolean) : Item


class Day1Grammar : Grammar<Tuple2<Int, Int>>() {
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


class Day2Grammar : Grammar<Item>() {
  private val num by regexToken("\\d{1,3}")
  private val newLine by regexToken("\\n")
  private val mul by literalToken("mul", true)

  private val enable by literalToken("do()")
  private val disable by literalToken("don't()")
  val lpar by literalToken("(", true)
  val rpar by literalToken(")", true)

  val comma by literalToken(",", true)
  val anychar by regexToken(".*", true)

  //  val numParser by num use { Number(text.toInt()) }
  val enableParser by enable use { Enabled(true) }
  val disableParser by disable use { Enabled(false) }


  private val instruction by -mul and -lpar and num and -comma and num and -rpar use {
    Number(
      Tuple2(
        t1.text.toInt(),
        t2.text.toInt()
      )
    )
  }

  val itemParser = instruction or enableParser or disableParser

  private val instructions by itemParser and -optional(anychar) and -optional(newLine)


  override val rootParser by instructions
}
