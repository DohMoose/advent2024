package org.days.three

import org.io.Content
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals

class DayTest {
  val content = Content(3)

  fun classUnderTest(): Day {
    return Day()
  }

  @Test
  fun doesPartOneSample() {
    val fileContent = content.load_part_one_sample()

    val classUnderTest = classUnderTest()
    assertEquals("2", classUnderTest.partOne(fileContent))
  }

  @Test
  fun doesPartOne() {
    val fileContent = content.load_part_one()

    val classUnderTest = classUnderTest()
    assertDoesNotThrow {
      val result = classUnderTest.partOne(fileContent)
      println(result)

    }
  }

  @Test
  fun doesPartTwoSample() {
    val fileContent = content.load_part_two_sample()

    val classUnderTest = classUnderTest()
    assertEquals("4", classUnderTest.partTwo(fileContent))
  }

  @Test
  fun doesPartTwo() {
    val fileContent = content.load_part_one()

    val classUnderTest = classUnderTest()
    assertDoesNotThrow {
      val result = classUnderTest.partTwo(fileContent)
      println(result)

    }
  }
}
