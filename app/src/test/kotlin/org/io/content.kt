package org.io

import java.nio.file.Files
import java.nio.file.Paths

class Content(private val day: Int) {
  fun load_part_one(): String {
    return load(1, false)
  }

  fun load_part_one_sample(): String {
    return load(1, true)
  }

  fun load_part_two(): String {
    return load(2, false)
  }

  fun load_part_two_sample(): String {
    return load(2, true)
  }

  fun load(part: Int, sample: Boolean): String {
    val sampleSuffix = if (sample) "_sample" else ""

    val filePath =
      Paths.get(javaClass.classLoader.getResource("inputs/day${day}/part_${part}${sampleSuffix}.txt")!!.toURI())
    return Files.readString(filePath)
  }
}

