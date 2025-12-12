package dev.cypdashuhn.aoc25.d12

import dev.cypdashuhn.aoc25.getLines

typealias Grid = Array<Array<Boolean>>
typealias ShapeIndex = Byte

data class Shape(
    val index: ShapeIndex,
    val data: Grid
) {
    val fieldAmount by lazy { data.flatMap { it.toList() }.count { it }.toByte() }
    companion object {
        fun fromStrings(list: List<String>): Shape {
            val index = list.first()[0].toString().toByte()
            val shape = list.drop(1).map { it.map { c -> c == '#' }.toTypedArray() }.toTypedArray()
            return Shape(index, shape)
        }

        data class ShapeList(val shapes: List<Shape>, val remaining: List<String>)

        fun shapeListFromList(list: List<String>): ShapeList {
            val shapes = mutableListOf<Shape>()
            val buffer = mutableListOf<String>()
            fun flush() { shapes += fromStrings(buffer); buffer.clear() }

            list.withIndex().forEach { (idx, s) ->
                if(s.isEmpty()) { flush(); return@forEach }

                if (buffer.isEmpty()) {
                    val isShapeStart = s.contains(":") && !s.contains("x")
                    if (isShapeStart) buffer += s
                    else return ShapeList(shapes, list.drop(idx))
                } else buffer += s
            }

            throw IllegalStateException("not here")
        }
    }

    val rotated by lazy {
        fun rotate(d: Byte): Shape {
            if (d.toInt() == 0) return this

            val height = data.size
            val width = data[0].size

            var current = data

            repeat(d.toInt()) {
                val newHeight = current[0].size
                val newWidth = current.size
                val rotated = Array(newHeight) { Array(newWidth) { false } }

                for (y in current.indices) {
                    for (x in current[y].indices) {
                        // Rotate 90 degrees clockwise: (x, y) -> (y, width-1-x)
                        val newX = y
                        val newY = newWidth - 1 - x
                        rotated[newY][newX] = current[y][x]
                    }
                }
                current = rotated
            }

            return Shape(index, current)
        }

        (0..3).associate { it.toByte() to rotate(it.toByte()) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Shape

        if (index != other.index) return false
        if (!data.contentDeepEquals(other.data)) return false
        if (fieldAmount != other.fieldAmount) return false

        return true
    }

    override fun hashCode(): Int {
        var result: Int = index.toInt()
        result = (31 * result + data.contentDeepHashCode())
        result = (31 * result + fieldAmount)
        return result
    }
}

typealias Amount = Byte
data class Region(
    val x: Byte,
    val y: Byte,
    val neededShapes: Map<Shape, Amount>
) {
    companion object {
        fun fromString(s: String, shapes: List<Shape>): Region {
            val dimensions = s.substringBefore(": ")
            val (x, y) = dimensions.split("x").map { it.toByte() }
            val amounts = s
                .drop(dimensions.count() + 2)
                .split(" ")
                .map { it.toByte() }
                .withIndex()
                .associate { (idx, amount) -> shapes.first { it.index.toInt() == idx } to amount }

            return Region(x, y, amounts)
        }

        fun fromList(list: List<String>, shapes: List<Shape>): List<Region> = list.map { fromString(it, shapes) }
    }

    fun tryToFit(): Boolean {
        val fieldsLeft = x * y.toInt()
        val fieldsOccupied = neededShapes.map { (shape, amount) -> shape.fieldAmount * amount}.sum()
        if (fieldsOccupied > fieldsLeft) return false

        return true
    }

    fun bruteForce(
        board: Grid,
        neededShapes: Map<Shape, Amount>,
        contender: List<Shape>
    ) {

    }

    fun rankShapes(board: Grid, list: List<Shape>): List<Shape> {
        return list
            .withIndex()
            .flatMap { a -> a.value.rotated.values.map { a.index * 4 + it.index to it } }
            .sortedBy { it.first }
            .map { it.second }
    }
}

val test = false
val path = if (test) "test" else "input"
fun data() = getLines(12, path)
val shapeRes = Shape.shapeListFromList(data())
val shapes = shapeRes.shapes
val regions = Region.fromList(shapeRes.remaining, shapeRes.shapes)

fun main() {
    findCombinations()
    return
    val fit = regions.map { it.tryToFit() }.count()
    println(fit)
}

fun backtrack(
    state: MutableList<Int>,  // current partial solution
    candidates: List<Int>,     // possible choices at each step
    isValid: (MutableList<Int>) -> Boolean,  // checks if current state is legal
    isComplete: (MutableList<Int>) -> Boolean,  // checks if we reached the goal
    results: MutableList<List<Int>>  // stores all solutions found
) {
    // Base case: found a complete solution
    if (isComplete(state)) {
        results.add(state.toList())  // save a copy
        return
    }

    // Try each possible next choice
    for (candidate in candidates) {
        // Add candidate to current state
        state.add(candidate)

        // Only continue if this state is valid
        if (isValid(state)) {
            backtrack(state, candidates, isValid, isComplete, results)
        }

        // BACKTRACK: remove the candidate (go one step back)
        state.removeLast()
    }
}

fun findCombinations() {
    val results = mutableListOf<List<Int>>()

    backtrack(
        state = mutableListOf(),
        candidates = (1..9).toList(),
        isValid = { state ->
            // Valid if digits are ascending
            state.zipWithNext().all { (a, b) -> a < b }
        },
        isComplete = { state -> state.size == 3 },
        results = results
    )

    println(results)  // [[1,2,3], [1,2,4], ..., [7,8,9]]
}