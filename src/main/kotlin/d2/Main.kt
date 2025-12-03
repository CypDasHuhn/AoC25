package dev.cypdashuhn.aoc25.d2

import dev.cypdashuhn.aoc25.getLines
import dev.cypdashuhn.aoc25.getLinesStream

fun main() {
    calc1()
    println("---")
    calc2()
}

fun calc1() {
    val regex = """^([0-9]+)\1{1}$""".toRegex()
    val id = getIds().sumOf { it.sumOfMatchingIds(regex) }
    println(id)
}

fun calc2() {
    val regex = """^([0-9]+)\1+$""".toRegex()
    val id = getIds().sumOf { it.sumOfMatchingIds(regex) }
    println(id)
}

fun getIds(): List<Id> {
    val lines = getLines(2, "input")
    assert(lines.size == 1) { "should be just one" }

    val first = lines[0]
    val ids = first
        .split(",")
        .map(Id::fromString)
    return ids
}

data class Id(
    val first: Long,
    val second: Long,
    val range: LongRange
) {
    companion object {
        fun fromString(s: String): Id {
            val parts = s.split("-")
            assert(parts.size == 2) { "Must be size of 2!" }

            val first = parts[0].toLong()
            val second = parts[1].toLong()
            val range = first..second

            return Id(first, second, range)
        }

        fun doesIdRepeat(id: String, regex: Regex): Boolean {
            val matches = regex.containsMatchIn(id)
            return matches
        }
    }

    fun allMatchingIds(regex: Regex): List<Long> {
        return range
            .map(Long::toString)
            .filter { doesIdRepeat(it, regex) }
            .map(String::toLong)
    }

    fun sumOfMatchingIds(regex: Regex): Long {
        val ids = allMatchingIds(regex)
        return ids.sum()
    }
}