package org.days.day17

import org.io.Content
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals

class DayTest {
  private val content = Content.autoInit()
  val operators = listOf(Day.Adv(), Day.Bxl(), Day.Bst(), Day.Jnz(), Day.Bxc(), Day.Out(), Day.Bdv(), Day.Cdv())
  val operatorMap = operators.associateBy { it.opCode }

  @Test
  fun doesPartOneSample() {
    val fileContent = content.loadPartOneSample()

    assertEquals("4,6,3,5,6,3,5,2,1,0", Day().partOne(fileContent))
  }

  @Test
  fun doesPartOne() {
    val fileContent = content.loadPartOne()

    assertDoesNotThrow {
      val result = Day().partOne(fileContent)
      println(result)
    }
  }

  @Test
  fun doesPartTwoSample() {
    val fileContent = content.loadPartTwoSample()

    assertEquals("117440", Day().partTwo(fileContent))
  }

  @Test
  fun doesPartTwo() {
    val fileContent = content.loadPartOne()

    val result = Day().partTwo(fileContent)
    println(result)
  }

  @Test
  fun AdvOperatesSmall() {
    val adv = Day.Adv()
    val (setRegister, output, move) = adv.operate(2, mapOf(Pair("A", 100)))

    assertEquals(Pair("A", 25.toLong()), setRegister)
  }

  @Test
  fun AdvTruncates() {
    val adv = Day.Adv()
    val (setRegister, _, _) = adv.operate(2, mapOf(Pair("A", 11)))
    assertEquals(Pair("A", 2.toLong()), setRegister)

    val (setRegister2, _, _) = adv.operate(2, mapOf(Pair("A", 9)))
    assertEquals(Pair("A", 2.toLong()), setRegister2)
  }

  @Test
  fun bxlXors() {
    val bxl = Day.Bxl()
    val (setRegister, _, _) = bxl.operate(2, mapOf(Pair("B", 11)))
    assertEquals(Pair("B", 9.toLong()), setRegister)

    val (setRegister2, _, _) = bxl.operate(4, mapOf(Pair("A", 9), Pair("B", 11)))
    assertEquals(Pair("B", 15.toLong()), setRegister2)
  }

  @Test
  fun bstMmodulos() {
    val operator = Day.Bst()
    val (setRegister, _, _) = operator.operate(5, mapOf(Pair("B", 11)))
    assertEquals(Pair("B", 3.toLong()), setRegister)

    val (setRegister2, _, _) = operator.operate(2, mapOf(Pair("A", 9), Pair("B", 11)))
    assertEquals(Pair("B", 2.toLong()), setRegister2)
  }

  @Test
  fun jnzJumps() {
    val operator = Day.Jnz()
    val (setRegister, _, movePoints) = operator.operate(5, mapOf(Pair("A", 0)))
    assertEquals(null, setRegister)
    assertEquals(12, movePoints(10))

    val (setRegister2, _, movePoints2) = operator.operate(5, mapOf(Pair("A", 3)))
    assertEquals(null, setRegister2)
    assertEquals(5, movePoints2(10))
  }

  @Test
  fun bxcXors() {
    val operator = Day.Bxc()
    val (setRegister, _, _) = operator.operate(5, mapOf(Pair("B", 11), Pair("C", 2)))
    assertEquals(Pair("B", 9.toLong()), setRegister)
  }

  @Test
  fun bdvAdvances() {
    val operator = Day.Bdv()
    val (setRegister, output, move) = operator.operate(2, mapOf(Pair("A", 100)))

    assertEquals(Pair("B", 25.toLong()), setRegister)
  }

  @Test
  fun cdvAdvances() {
    val operator = Day.Cdv()
    val (setRegister, output, move) = operator.operate(2, mapOf(Pair("A", 100)))

    assertEquals(Pair("C", 25.toLong()), setRegister)
  }


//  @Test
//  fun simpleTests() {
//    var operator = operatorMap[2]!!
//    var (setRegister, output, move) = operator.operate(6, mapOf(Pair("C", 9)))
//    assertEquals(Pair("B", 1), setRegister!!)
//
//  }
}
