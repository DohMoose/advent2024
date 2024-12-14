package org.days.day13

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.utils.Tuple2
import com.github.h0tk3y.betterParse.utils.Tuple3
import com.github.h0tk3y.betterParse.utils.Tuple4
import org.days.IDay
import org.io.Position

class Day : IDay {
  override fun partOne(input: String): String {

    val clawMachines = DayGrammar().parseToEnd(input)
    return clawMachines.sumOf {

      val r = findCheapest(it, 0)
      if (r == null) {
        0
      } else {
        r.first * 3 + r.second
      }
    }.toString()
    //return findCheapest(94, 34, 22, 67, 8400, 5400).toString()
  }

  fun findCheapest(cm: ClawMachine, adder: Long): Pair<Long, Long>? {
    // it's a algebra

    //  Button A: X+94, Y+34
    //  Button B: X+22, Y+67
    //  Prize: X=8400, Y=5400


    // 94a = 8400 - 22b
    // 34a = 5400 - 67b
    // 22b = 8400 - 94a
    // 67b = 5400 - 34a
    // b =  8400 - 94a / 22
    // b = 5400 - 34a / 67

    //  (totalX - aX * a) / bX = (totalY - aY * a) / bY
    // totalX * bY - totalY * bX = bY * aX * a - bX * aY * a
    // totalX * bY - totalY * bX / (bY * aX) - (bX * aY)


    // (285600 - 748b) = 507600 - 6298b
    //  6298b - 748b   = 507600 - 285600
    // 5550b = 222000

    val totalX = cm.totalX + adder
    val totalY = cm.totalY + adder


    val (a, aRemainder) = divideWithRemainder(totalX * cm.bY - totalY * cm.bX, (cm.bY * cm.aX - cm.bX * cm.aY))
    if (aRemainder != 0.toLong()) {
      return null
    }
    val (b, bRemainder) = divideWithRemainder(totalX - cm.aX * a, cm.bX)
    if (bRemainder != 0.toLong()) {
      return null
    }
    return Pair(a, b)


//
//            for (a in 1.toLong() until 100) {
//              if (a * cm.aX > totalX || a * cm.aY > totalY) {
//                break
//              }
//              for (b in 0.toLong() until 100) {
//                val currentX = (a * cm.aX + b * cm.bX)
//                val currentY = (a * cm.aY + b * cm.bY)
//                if (currentX > totalX || currentY > totalY) {
//                  break
//                }
//                if (currentX == totalX && currentY == totalY) {
//                  return Pair(a, b)
//                }
//              }
//            }
//    return null
  }

  fun divideWithRemainder(dividend: Long, divisor: Long): Pair<Long, Long> {
    val quotient = dividend / divisor
    val remainder = dividend % divisor
    return Pair(quotient, remainder)
  }

  fun gcd(a: Long, b: Long): Long {
    return if (b == 0.toLong()) a else gcd(b, a % b)
  }

  fun gcd(vararg numbers: Long): Long {
    return numbers.reduce { acc, number -> gcd(acc, number) }
  }

  override fun partTwo(input: String): String {
    // it's a algebra

    //    Button A: X+69, Y+23
    //    Button B: X+27, Y+71
    //    Prize: X=18641, Y=10279
    // 94a + 22b = 8400
    // 34a + 67b = 5400
    // solve for smallest a
    // 128a + 89b =
    // 23a - 22b = 4181
    val clawMachines = DayGrammar().parseToEnd(input)
    return clawMachines.sumOf {

      val r = findCheapest(it, 10000000000000)
      if (r == null) {
        0
      } else {
        r.first * 3 + r.second
      }
    }.toString()

    return input
  }

  data class ClawMachine(val aX: Long, val aY: Long, val bX: Long, val bY: Long, val totalX: Long, val totalY: Long)

  class DayGrammar : Grammar<List<ClawMachine>>() {
    val buttonA by literalToken("Button A: ")

    val buttonB by literalToken("Button B: ")
    val xLabel by literalToken("X+")
    val yLabel by literalToken("Y+")
    val moveValue by regexToken("\\d+")
    val comma by literalToken(", ")
    val prize by literalToken("Prize: ")
    val xPrizeLabel by literalToken("X=")
    val yPrizeLabel by literalToken("Y=")

    val newLine by regexToken("\\n")

    val moveValueParser = moveValue use { text.toLong() }
    val buttonALine by -buttonA and -xLabel and moveValueParser and -comma and -yLabel and moveValueParser and -newLine
    val buttonBLine by -buttonB and -xLabel and moveValueParser and -comma and -yLabel and moveValueParser and -newLine
    val prizeLine by -prize and -xPrizeLabel and moveValueParser and -comma and -yPrizeLabel and moveValueParser and -optional(
      newLine
    )

    val clawMachine = buttonALine and buttonBLine and prizeLine map {
      ClawMachine(
        it.t1.t1,
        it.t1.t2,
        it.t2.t1,
        it.t2.t2,
        it.t3.t1,
        it.t3.t2
      )
    }

    //
    override val rootParser by separatedTerms(clawMachine, oneOrMore(newLine))
  }
}
