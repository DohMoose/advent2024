package org.days.day19

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.utils.Tuple2
import org.days.IDay


class Day : IDay {
  override fun partOne(input: String): String {
    val (towels, patterns) = DayGrammar().parseToEnd(input)
    val failedPatterns = mutableSetOf<String>()
    return patterns.mapIndexed { index, it ->
      println("Checking pattern ${index + 1}")
      findPattern(it, towels, failedPatterns)
    }.filter {
      it
    }.count()
      .toString()
  }

  fun findPattern(pattern: String, towels: List<String>, failedPatterns: MutableSet<String>): Boolean {
    if (failedPatterns.contains(pattern)) {
      return false
    }
    for (towel in towels) {
      if (pattern == towel) {
        return true
      }
    }
    val success = towels.any { towel ->
      pattern.startsWith(towel) && findPattern(pattern.drop(towel.count()), towels, failedPatterns)
    }
    if (success) {
      return true
    } else {
      failedPatterns.add(pattern)
      return false
    }
  }

  override fun partTwo(input: String): String {

    val (towels, patterns) = DayGrammar().parseToEnd(input)

    return patterns.mapIndexed { index, it ->
      val failedPatterns = mutableSetOf<String>()
      val cc = countPatterns(it, towels, failedPatterns, mutableMapOf())
      println("Checked pattern ${index + 1}: ${cc}")
      cc
    }.sum().toString()
  }

  fun countPatterns(
    pattern: String,
    towels: List<String>,
    failedPatterns: MutableSet<String>,
    successfulSubPatterns: MutableMap<String, Long>,
  ): Long {
    if (pattern.isEmpty()) {
      return 1
    }

    return towels.asSequence().filter {
      pattern.startsWith(it)
    }.map {
      pattern.drop(it.count())
    }.filterNot { subPattern ->
      failedPatterns.contains(subPattern)
    }.map { subPattern ->
      if (successfulSubPatterns.contains(subPattern)) {
        successfulSubPatterns[subPattern]!!
      } else {
        val count = countPatterns(subPattern, towels, failedPatterns, successfulSubPatterns)
        if (count > 0) {
          successfulSubPatterns.computeIfAbsent(subPattern) { count }
        } else {
          failedPatterns.add(subPattern)
        }
        count
      }
    }.sum()
  }

  class DayGrammar : Grammar<Tuple2<List<String>, List<String>>>() {
    val word by regexToken("[A-Za-z]+")
    val newLine by regexToken("\\n")
    val comma by literalToken(", ")

    val varParser by word use { text }

    val towelParser by separatedTerms(varParser, comma)

    val patternParser by separatedTerms(varParser, newLine)
    override val rootParser by towelParser and -newLine and -newLine and patternParser map {
      Tuple2(it.t1.sorted(), it.t2)
    }
  }
}
