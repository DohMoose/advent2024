package org.days.five

import org.io.Content
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals

class DayTest {
  private val content = Content(5)

  @Test
  fun doesPartOneSample() {
    val fileContent = content.loadPartOneSample()

    assertEquals("143", Day().partOne(fileContent))
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

    assertEquals("123", Day().partTwo(fileContent))
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
