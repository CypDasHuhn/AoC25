package dev.cypdashuhn.aoc25.d4

import dev.cypdashuhn.aoc25.getLines

enum class Position(val char: Char) {
    EMPTY('.'),
    PAPER('@');

    companion object {
        fun fromChar(char: Char): Position {
            return Position.entries.first { it.char == char }
        }
    }
}

fun main() {
    calc1()
}

fun calc1() {
    val grid = getGrid()
    println(grid.allAccessiblePapers())
}

data class Grid(
    val map: MutableMap<Pair<Int, Int>, Position> = mutableMapOf()
) {
    companion object {
        fun fromLines(lines: List<String>): Grid {
            val grid = Grid()
            lines.withIndex().forEach { (idx, line) ->
                grid.addLine(line, idx)
            }
            return grid
        }
    }

    fun addLine(line: String, lineNumber: Int) {
        line.withIndex().forEach { (idx, char) ->
            val x = idx;
            val y = lineNumber;
            val position = Position.fromChar(char)

            map[x to y] = position
        }
    }

    fun isAccessible(x: Int, y: Int): Boolean {
        val neighbours = mutableListOf<Pair<Int,Int>>()
        (x-1..x+1).map { it to (y-1..y+1) }.forEach { x ->
            x.second.forEach { y -> neighbours += x.first to y }
        }
        neighbours.remove(x to y)

        val amountOfPaper = neighbours.map {
            val pos = map[it]
            when (pos) {
                Position.PAPER -> 1
                Position.EMPTY -> 0
                null -> 0
            }
        }.sum()

        return amountOfPaper < 4
    }

    fun allAccessiblePapers(): Int {
        return map.entries
            .filter { it.value == Position.PAPER }
            .map { isAccessible(it.key.first, it.key.second) }
            .count { it }
    }
}

fun getGrid() = Grid.fromLines(getLines(4, "input"))