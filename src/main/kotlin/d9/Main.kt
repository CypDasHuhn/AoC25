package dev.cypdashuhn.aoc25.d9

import dev.cypdashuhn.aoc25.getLines

enum class State {
    RED,
    GREEN,
    EMPTY
}
data class Pos(
    val x: Long,
    val y: Long,
) {
    companion object {
        fun fromString(s: String): Pos {
            val (x, y) = s.split(",")
            return Pos(x.toLong(), y.toLong())
        }
    }
}
data class ShrinkedPos(
    val localPos: Pos,
    val jump: Pos
)
fun getGrid() = getLines(9, "test").map(Pos::fromString).associateWith { State.RED }

fun main() {
    val grid = getGrid()
    val keys = getLines(9, "test").map(Pos::fromString)
    val shrinkedGrid = mutableMapOf<ShrinkedPos, State>()
    val xStart = keys.minOf { it.x }
    val yStart = keys.minOf { it.y }

    val toPickUp = mutableListOf<Pos>()
    keys
        .sortedBy { it.x }
        .zipWithNext()
        .withIndex()
        .forEach { (idx, nums) ->
            val (prev, next) = nums
            val isLast = idx == keys.count() - 2

            val xDiff = next.x - prev.x

            fun add(jump: Long) {
                val highestX = shrinkedGrid.keys.maxOfOrNull { it.localPos.x } ?: -1
                toPickUp.forEach {
                    shrinkedGrid[ShrinkedPos(Pos(highestX + 1, it.y), Pos(jump, 0))] = State.RED
                }
                toPickUp.clear()
            }

            if (xDiff == 0.toLong()) toPickUp += prev
            else {
                toPickUp += prev
                add(xDiff)
            }

            if (isLast) {
                toPickUp += next
                add(0)
            }
        }

    (0..shrinkedGrid.maxOf { it.key.localPos.x }).forEach { x ->
        (0..shrinkedGrid.maxOf { it.key.localPos.y }).forEach { y ->
            val state = shrinkedGrid.filter { it.key.localPos == Pos(x.toLong(), y.toLong()) }.map { it.value }.firstOrNull()
            if (state == null) print(" ")
            else print("#")
        }
        println()
    }
    println("---")
    val shrinkedKeys = shrinkedGrid.keys.toList()
    shrinkedGrid.clear()
    val yToPickUp = mutableListOf<ShrinkedPos>()
    shrinkedKeys.sortedBy { it.localPos.y }.zipWithNext().withIndex().forEach { (idx, nums) ->
        val (prevP, nextP) = nums
        val (prev, prevJump) = prevP
        val (next, nextJump) = nextP
        val isLast = idx == keys.count() - 2

        val yDiff = next.y - prev.y

        fun add(jumpY: Long) {
            val highestY = shrinkedGrid.keys.maxOfOrNull { it.localPos.y } ?: -1
            yToPickUp.forEach { (pos, jump) ->
                shrinkedGrid[ShrinkedPos(Pos(pos.x, highestY + 1), Pos(jump.x, jumpY))] = State.RED
            }
            yToPickUp.clear()
        }

        if (yDiff == 0.toLong()) yToPickUp += prevP
        else {
            yToPickUp += prevP
            add(yDiff)
        }

        if (isLast) {
            yToPickUp += nextP
            add(0)
        }
    }

    (0..shrinkedGrid.maxOf { it.key.localPos.x }).forEach { x ->
        (0..shrinkedGrid.maxOf { it.key.localPos.y }).forEach { y ->
            val state = shrinkedGrid.filter { it.key.localPos == Pos(x.toLong(), y.toLong()) }.map { it.value }.firstOrNull()
            if (state == null) print(" ")
            else print("#")
        }
        println()
    }

    println("lol")
}

/*
 0-3
 0-5
 1-1
 1-3
 2-5
 2-7
 3-1
 3-7


0-1
0-2
1-0
1-1
2-2
2-3
3-0
3-3

 */