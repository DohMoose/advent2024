package org.days.two

import org.io.Content
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals

class DayTest {
  val content = Content(2)

  fun classUnderTest(): Day {
    return Day()
  }

  @Test
  fun doesPartOneSample() {
    val fileContent = content.loadPartOneSample()

    val classUnderTest = classUnderTest()
    assertEquals("2", classUnderTest.partOne(fileContent))
  }

  @Test
  fun doesPartOne() {
    val fileContent = content.loadPartOne()

    val classUnderTest = classUnderTest()
    assertDoesNotThrow {
      val result = classUnderTest.partOne(fileContent)
      println(result)

    }
  }

  @Test
  fun doesPartTwoSample() {
    val fileContent = content.loadPartTwoSample()

    val classUnderTest = classUnderTest()
    assertEquals("4", classUnderTest.partTwo(fileContent))
  }

  @Test
  fun doesPartTwo() {
    val fileContent = content.loadPartOne()

    val classUnderTest = classUnderTest()
    assertDoesNotThrow {
      val result = classUnderTest.partTwo(fileContent)
      println(result)

    }
  }
}
