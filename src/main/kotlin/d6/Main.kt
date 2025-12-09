package dev.cypdashuhn.aoc25.d6

import dev.cypdashuhn.aoc25.getLines
import kotlin.math.absoluteValue
import kotlin.math.pow

fun main() {
    val s =getLines(6, "test").map { parseLine(it) }

    return
    calc1()
    println("---")
    calc2()
}

fun calc1() {
    val grid = getGrid()
    val rows = grid.toRows()
    val sum = rows.resultSum()
    println(sum)
}

fun calc2() {
    val grid = getGrid()
    val rows = grid.toRows()
    /*val strangifiedRows = rows.map { row -> Row(row.numbers.strangify(), row.operator) }
    val sum = strangifiedRows.resultSum()
    println(sum)*/
}

fun getGrid() = Grid.fromList(getLines(6, "test"))

data class Position(
    val x: Int,
    val y: Int
)

enum class Operator(val char: Char, val math: (Long, Long) -> Long) {
    ADD('+', { a, b -> a + b }),
    MULTIPLY('*', { a, b -> a * b });

    companion object {
        fun fromChar(char: Char): Operator {
            return Operator.entries.first { it.char == char }
        }
    }
}

data class Grid(
    val grid: Map<Position, Long>,
    val operators: List<Operator>
) {
    companion object {
        fun fromList(lines: List<String>): Grid {
            val grid = mutableMapOf<Position, Long>()
            var operators: List<Operator>? = null
            val lastIndex = lines.count() - 1
            lines.withIndex().forEach { (y, line) ->
                val cleanedLine = line.trim().replace("\\s+".toRegex(), " ")
                val strings = cleanedLine.split(" ")
                if (y == lastIndex) {
                    operators = strings
                        .map { Operator.fromChar(it[0]) }
                    return@forEach
                }

                val numbers = strings.map { it.toLong() }

                numbers.withIndex().forEach { (x, num) ->
                    grid[Position(x, y)] = num
                }
            }

            return Grid(grid.toMap(), operators!!)
        }
    }

    fun toRows(): List<Row> {
        val xAmount = grid.maxBy { it.key.x }.key.x
        val yAmount = grid.maxBy { it.key.y }.key.y

        val rows = (0..xAmount).map { x ->
            val numbers = mutableListOf<Long>()
            (0..yAmount).forEach { y ->
                numbers += grid[Position(x, y)]!!
            }
            val operator = operators[x]

            Row(numbers, operator)
        }
        return rows
    }
}

data class Row(
    val numbers: List<Long>,
    val operator: Operator
) {
    fun result(): Long {
        var localRes: Long? = null
        numbers.map { it.toLong() }.forEach { num ->
            if (localRes == null) {
                localRes = num;
                return@forEach
            }

            localRes = operator.math(localRes, num)
        }
        return localRes!!
    }
}

fun List<Row>.resultSum() = this.sumOf { it.result() }

fun List<String>.strangify(): List<Long> {
    val newList = mutableMapOf<Int, String>()

    this.forEach { num ->
        val numPositions = num.count() - 1
        num.withIndex().forEach { (dIdx, digit) ->
            if (digit == ' ') return@forEach
            val revertedIdx = (dIdx - numPositions).absoluteValue

            if (!newList.containsKey(revertedIdx)) newList[revertedIdx] = ""

            newList[revertedIdx] += digit.toString()
        }
    }
    return newList.values.map(String::toLong)
}

val digits = 3
fun parseLine(line: String): List<String> {
    val lines = mutableListOf<String>()
    var pos = 0
    var buffer = ""
    line.forEach { s ->
        pos += 1
        if (pos == (digits + 1)) {
            lines += buffer
            buffer = ""
            pos = 0
            return@forEach
        }
        buffer += s
    }
    return lines
}