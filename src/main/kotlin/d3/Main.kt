package dev.cypdashuhn.aoc25.d3

import dev.cypdashuhn.aoc25.getLines
import java.math.BigInteger
import kotlin.math.pow

data class Bank(
    val batteryJoltages: List<Int>
) {
    companion object {
        fun fromString(s: String): Bank {
            val joltages = s.map { it.toString().toInt() }
            return Bank(joltages)
        }
    }
}

fun getBanks() = getLines(3, "input").map(Bank::fromString)

fun main() {
    val res1 = getBanks().sumOf { calc1(it) }
    println(res1)

    val res2 = getBanks().sumOf { calc2(it) }
    println(res2)
}

fun calc1(bank: Bank): Int {
    (1..9).sortedDescending().forEach { i ->
        val jolIndexed = bank.batteryJoltages.withIndex()
        val currentHighest = jolIndexed.filter { it.index != bank.batteryJoltages.count() - 1 }.firstOrNull { it.value == i }
        if (currentHighest == null) return@forEach

        val filteredRange = jolIndexed.filter { it.index > currentHighest.index }
        val nextHighest = filteredRange.maxByOrNull { it.value }!!
        return currentHighest.value * 10 + nextHighest.value
    }
    throw IllegalStateException("shouldnt hit")
}

fun calc2(bank: Bank): BigInteger {
    var scope = bank.batteryJoltages
    var currentJoltage = 0.toBigInteger()
    (1..12).sortedDescending().forEach { i ->
        val res = nextScope(scope, i)
        scope = res.listLeft

        val mod = (10.0.pow((i-1).toDouble())).toBigDecimal().toBigInteger()
        val valueAtPosition = res.res.toBigInteger() * mod
        currentJoltage += valueAtPosition
    }

    return currentJoltage
}

data class ScopeResult(
    val res: Int,
    val listLeft: List<Int>
)

fun nextScope(list: List<Int>, digitsLeft: Int): ScopeResult {
    val indexed = list.withIndex()
    val length = list.count()

    val possibleOptions = indexed.filter { it.index <= length - digitsLeft }
    val highest = possibleOptions.maxBy { it.value }

    val listLeft = indexed.filter { it.index > highest.index }.map { it.value }
    return ScopeResult(highest.value, listLeft)
}