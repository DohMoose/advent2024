package org.days.day18

import org.io.Content
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals

class DayTest {
  private val content = Content.autoInit()

  @Test
  fun doesPartOneSample() {
    val fileContent = content.loadPartOneSample()

    assertEquals("22", Day().partOne(fileContent))
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
    val fileContent = content.loadPartOneSample()

    assertEquals("6,1", Day().partTwo(fileContent))
  }

  @Test
  fun doesPartTwo() {
    val fileContent = content.loadPartOne()

    assertDoesNotThrow {
      val result = Day().partTwo(fileContent)
      println(result)
    }
  }
}
