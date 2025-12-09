package dev.cypdashuhn.aoc25.d9

import dev.cypdashuhn.aoc25.getLines
import kotlin.math.absoluteValue

fun getInts() = getLines(9, "test").map { it.split(",").map { it.toInt() } }.map { it[0] to it[1] }

fun main() {
    val ints = getInts().withIndex()
    val e = ints.flatMap { (idx, pair) ->
        ints.filter { it.index < idx }.map { pair to it.value }
    }.map {
        val x = (it.first.first - it.second.first).absoluteValue.toLong()
        val y = (it.first.second - it.second.second).absoluteValue.toLong() + 1
        x * y
    }.sortedDescending()
    val r = e.max()
    println(r)
}