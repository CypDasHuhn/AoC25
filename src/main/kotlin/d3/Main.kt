package dev.cypdashuhn.aoc25.d3

import dev.cypdashuhn.aoc25.getLines

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
    val res = getBanks().sumOf { calc1(it) }
    println(res)
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

fun calc2(bank: Bank): Int {

}