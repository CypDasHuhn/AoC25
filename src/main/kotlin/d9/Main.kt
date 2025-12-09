package dev.cypdashuhn.aoc25.d9

import dev.cypdashuhn.aoc25.getLines
import kotlin.math.absoluteValue

fun getInts() = getLines(9, "input").map { it.split(",").map { it.toInt() } }.map { it[0] to it[1] }

data class Boundary <T> (
    val idx1: Int,
    val idx2: Int,
    val p1: T,
    val p2: T
)

fun main() {
    val ints = getInts().withIndex()

    val o1 = ints.flatMap { (idx, pair) -> ints.filter { it.index < idx }.map { Boundary(idx, it.index, pair, it.value) }}
    val o2 = o1.map {
        val x = (it.p2.first - it.p1.first).absoluteValue.toLong() + 1
        val y = (it.p2.second - it.p1.second).absoluteValue.toLong() + 1
        Boundary(it.idx1, it.idx2, x, y)
    }
    val o3 = o2.map { it.p1 * it.p2 }
    val o4 = o3.sortedDescending()

    // 11 - 2 = 9 ; 7 - 3 = 4; 10 * 5 = 50
    // 11 - 2 = 9 ; 5 - 1 = 4; 10 * 5 = 50

    val e = ints.flatMap { (idx, pair) ->
        ints.filter { it.index < idx }.map { pair to it.value }
    }.map {
        val x = (it.first.first - it.second.first).absoluteValue.toLong() + 1
        val y = (it.first.second - it.second.second).absoluteValue.toLong() + 1
        x * y
    }.sortedDescending()
    val r = e.max()
    println(r)
}