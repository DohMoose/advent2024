package org.days.five

import com.github.h0tk3y.betterParse.utils.Tuple2
import org.days.IDay

class Day : IDay {
  override fun partOne(input: String): String {
    val (rules, updates) = parse(input.trimEnd())

    val value = updates.filter { it ->
      it.zipWithNext().all { (first, second) ->
        rules[first]?.contains(second) ?: false
      }
    }.sumOf {
      it[it.count() / 2]
    }

    return value.toString()
  }

  override fun partTwo(input: String): String {
    val (rules, updates) = parse(input.trimEnd())

    val value = updates.filterNot { it ->
      it.zipWithNext().all { (first, second) ->
        rules[first]?.contains(second) ?: false
      }
    }.map {
      it.sortedWith(Comparator { first, second ->
        when {
          rules[first]?.contains(second) == true -> -1
          first == second -> 0
          else -> 1
        }

      })
    }.sumOf {
      it[it.count() / 2]
    }
    return value.toString()
  }


  fun parse(input: String): Tuple2<Map<Int, List<Int>>, List<List<Int>>> {
    val parts = input.split("\n\n")
    val rawRules = parts[0].split("\n").map {
      it.split("|").map {
        it.toInt()
      }
    }
    val updates = parts[1].split("\n").map {
      it.split(",")
    }.map {
      it.map {
        it.toInt()
      }
    }

    val rules = rawRules.fold(mutableMapOf<Int, MutableList<Int>>()) { acc, list ->
      if (!acc.containsKey(list.first())) {
        acc[list.first()] = mutableListOf()
      }
      acc[list.first()]!!.add(list.last())
      acc
    }.mapValues { entry ->
      entry.value.toList()
    }.toMap()

    return Tuple2(rules, updates)
  }
}
