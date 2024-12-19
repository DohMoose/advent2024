package org.days.day17

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.LiteralToken
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.utils.Tuple
import com.github.h0tk3y.betterParse.utils.Tuple2
import org.days.IDay
import java.lang.Exception
import kotlin.math.abs

typealias Registers = Map<String, Long>

class Day : IDay {

  interface Operator {
    val opCode: Long
    val operand: Operand

    fun operate(literal: Long, registers: Registers): OpResult
  }

  interface Operand {
    fun getValue(literal: Long, registers: Registers): Long
  }

  class Combo : Operand {
    override fun getValue(literal: Long, registers: Registers): Long {
      return when (literal) {
        in 0.toLong()..3.toLong() -> literal
        4.toLong() -> registers["A"]!!
        5.toLong() -> registers["B"]!!
        6.toLong() -> registers["C"]!!
        else -> throw Exception("Uknown literal")
      }
    }
  }

  class Literal : Operand {
    override fun getValue(literal: Long, registers: Registers): Long {
      return literal
    }
  }

  data class OpResult(
    val setRegister: Pair<String, Long>?,
    val output: Long? = null,
    val movePoints: (pointer: Long) -> Long
  )

  class Adv(override val opCode: Long = 0, override val operand: Operand = Combo()) : Operator {
    override fun operate(literal: Long, registers: Registers): OpResult {
      val denominator = Math.pow(2.toDouble(), operand.getValue(literal, registers).toDouble())
      val result = registers["A"]!! / denominator
      val truncated = result.toLong()
      return OpResult(Pair("A", truncated)) { it + 2 }
    }
  }

  class Bxl(override val opCode: Long = 1, override val operand: Operand = Literal()) : Operator {
    override fun operate(literal: Long, registers: Registers): OpResult {
      val result = registers["B"]!! xor operand.getValue(literal, registers)
      return OpResult(Pair("B", result)) { it + 2 }
    }
  }

  class Bst(override val opCode: Long = 2, override val operand: Operand = Combo()) : Operator {
    override fun operate(literal: Long, registers: Registers): OpResult {
      val result = operand.getValue(literal, registers) % 8
      return OpResult(Pair("B", result)) { it + 2 }
    }
  }

  class Jnz(override val opCode: Long = 3, override val operand: Operand = Literal()) : Operator {
    override fun operate(literal: Long, registers: Registers): OpResult {
      val registerA = registers["A"]!!
      if (registerA == 0.toLong()) {
        return OpResult(null) { it + 2 }
      }

      return OpResult(null) { operand.getValue(literal, registers) }
    }
  }

  class Bxc(override val opCode: Long = 4, override val operand: Operand = Literal()) : Operator {
    override fun operate(literal: Long, registers: Registers): OpResult {
      val result = registers["B"]!! xor registers["C"]!!
      return OpResult(Pair("B", result)) { it + 2 }
    }
  }

  class Out(override val opCode: Long = 5, override val operand: Operand = Combo()) : Operator {
    override fun operate(literal: Long, registers: Registers): OpResult {
      return OpResult(null, (operand.getValue(literal, registers) % 8)) { it + 2 }
    }
  }

  class Bdv(override val opCode: Long = 6, override val operand: Operand = Combo()) : Operator {
    override fun operate(literal: Long, registers: Registers): OpResult {
      val denominator = Math.pow(2.toDouble(), operand.getValue(literal, registers).toDouble())
      val result = registers["A"]!! / denominator
      val truncated = result.toLong()
      return OpResult(Pair("B", truncated)) { it + 2 }
    }
  }

  class Cdv(override val opCode: Long = 7, override val operand: Operand = Combo()) : Operator {
    override fun operate(literal: Long, registers: Registers): OpResult {
      val denominator = Math.pow(2.toDouble(), operand.getValue(literal, registers).toDouble())
      val result = registers["A"]!! / denominator
      val truncated = result.toLong()
      return OpResult(Pair("C", truncated)) { it + 2 }
    }
  }


  val operators = listOf(Adv(), Bxl(), Bst(), Jnz(), Bxc(), Out(), Bdv(), Cdv())
  val operatorMap = operators.associateBy { it.opCode }
  override fun partOne(input: String): String {
    // code up the instructions in open-close
    val (rawRegisters, rawProgram) = DayGrammar().parseToEnd(input)
    return runProgram(rawRegisters, rawProgram).joinToString(",") { it.toString() }
  }

  override fun partTwo(input: String): String {
    val (rawRegisters, rawProgram) = DayGrammar().parseToEnd(input)
    val mutRegisters = rawRegisters.toMutableMap()

    // This is nuts
    // I noticed that if you increase the number by one (up to 8), it only increases the left most digit (or lowest digit)
    // from 8 to 64 it changes in the second digit and so on
    // So we step through looking for digits that match and multuplying the previous digits by 8 to the power of the place
    // There's more than one digit that works though, so we need ot keep a track off all "paths" util we find the winner
    rawProgram.asReversed().foldIndexed(listOf(emptyList<Long>())) { bigIndex, accList, cur ->
      val extras = emptyList<Long>()
      accList.flatMap { acc ->
        val baseNumber = acc.foldIndexed(0.toLong()) { index, fa, fc ->
          fa + fc * Math.pow(8.toDouble(), (index + 1).toDouble()).toLong()
        }

        val alloutputs = mutableListOf<List<Long>>()
        (0..7).map { i ->
          val output = runWithA(mutRegisters, i + baseNumber, rawProgram);
          alloutputs.add(output)
          if (output == rawProgram.drop(rawProgram.count() - bigIndex - 1)) {
            if (bigIndex == rawProgram.count() - 1)
              return (i + baseNumber).toString() // holy moly we found it
            alloutputs.forEach {
              println(it.joinToString(","))
            }
            listOf(i.toLong()) + acc
          } else {
            null
          }
        }.filterNotNull()

      }
    }

    return "fail"
  }

  fun gcd(a: Long, b: Long): Long {
    return if (b == 0.toLong()) abs(a) else gcd(b, a % b)
  }

  // Function to calculate LCM of two numbers
  fun lcm(a: Long, b: Long): Long {
    return abs(a * b) / gcd(a, b)
  }

  fun runWithA(mutRegisters: MutableMap<String, Long>, a: Long, rawProgram: List<Long>): List<Long> {
    mutRegisters["A"] = a
    return runProgram(mutRegisters, rawProgram)
  }

  fun runProgram(rawRegisters: Map<String, Long>, rawProgram: List<Long>): List<Long> {
    val registers = rawRegisters.toMutableMap()
    val totalOutputs = mutableListOf<Long>()
    val program = rawProgram.zipWithNext()
    var instructionPointer = 0.toLong()
    while (instructionPointer < program.count()) {
      val (opCode, literal) = program[instructionPointer.toInt()]
      val operator = operatorMap[opCode]!!
      val (registerUpdate, output, jump) = operator.operate(literal, registers)
      if (registerUpdate != null) {
        registers[registerUpdate.first] = registerUpdate.second
      }
      if (output != null) {
        totalOutputs.add(output)
      }
      instructionPointer = jump(instructionPointer)
    }
    return totalOutputs
  }

  fun findRepeatingPattern(numbers: List<Long>): List<Long>? {
    val n = numbers.size

    for (patternLength in 1..n / 2) {
      var isPattern = true
      for (i in patternLength until n) {
        if (numbers[i] != numbers[i % patternLength]) {
          isPattern = false
          break
        }
      }
      if (isPattern) {
        return numbers.subList(0, patternLength)
      }
    }

    return null
  }


  interface Item
  class Number(val value: Int) : Item
  class Variable(val name: String) : Item

  class DayGrammar : Grammar<Tuple2<Map<String, Long>, List<Long>>>() {
    val registerALabel by literalToken("Register A: ")
    val registerBLabel by literalToken("Register B: ")
    val registerCLabel by literalToken("Register C: ")

    //    and literalToken(" ") and (literalToken("A") or literalToken("B") or literalToken(
//    "C"
//    )) and literalToken(
//    ": "
//    )
    val comma by literalToken(",")

    val program by literalToken("Program: ")

    val num by regexToken("\\d+")
    val word by regexToken("[A-Za-z]+")
    val newLine by regexToken("\\n")

    val numParser by num use { text.toLong() }

    val registerALine by -registerALabel and numParser and -newLine
    val registerBLine by -registerBLabel and numParser and -newLine
    val registerCLine by -registerCLabel and numParser and -newLine
    val registerlines by registerALine and registerBLine and registerCLine
    val registerParser by registerlines map {
      mapOf(Pair("A", it.t1), Pair("B", it.t2), Pair("C", it.t3))
    }
    val programLine by -program and separatedTerms(numParser, comma)

    override val rootParser by registerParser and -newLine and programLine
  }
}
