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

fun main() {
    val keys = getLines(9, "input").map(Pos::fromString)

    // First pass: shrink by X
    val xShrinked = shrinkByAxis(
        items = keys,
        sortBy = { it.x },
        { it.localPos.x },
        getOtherCoord = { it.y },
        getDiff = { prev, next -> next.x - prev.x },
        createShrinkedPos = { localX, y, jumpX -> ShrinkedPos(Pos(localX, y), Pos(jumpX, 0)) }
    )

    // Second pass: shrink by Y
    val yShrinked = shrinkByAxis(
        items = xShrinked.keys.toList(),
        sortBy = { it.localPos.y },
        { it.localPos.y },
        getOtherCoord = { it },
        getDiff = { prev, next -> next.localPos.y - prev.localPos.y },
        createShrinkedPos = { localY, shrinkedPos, jumpY ->
            ShrinkedPos(Pos(shrinkedPos.localPos.x, localY), Pos(shrinkedPos.jump.x, jumpY))
        }
    )

    printGrid(yShrinked)
}

fun <T, U> shrinkByAxis(
    items: List<T>,
    sortBy: (T) -> Long,
    valueBy: (ShrinkedPos) -> Long,
    getOtherCoord: (T) -> U,
    getDiff: (T, T) -> Long,
    createShrinkedPos: (localCoord: Long, otherData: U, jump: Long) -> ShrinkedPos
): MutableMap<ShrinkedPos, State> {
    val result = mutableMapOf<ShrinkedPos, State>()
    val buffer = mutableListOf<T>()

    fun flush(jump: Long) {
        val maxCoord = result.keys.maxOfOrNull(valueBy) ?: -1
        buffer.forEach { item ->
            result[createShrinkedPos(maxCoord + 1, getOtherCoord(item), jump)] = State.RED
        }
        buffer.clear()
    }

    items.sortedBy(sortBy)
        .zipWithNext()
        .forEachIndexed { idx, (prev, next) ->
            val diff = getDiff(prev, next)
            val isLast = idx == items.size - 2

            buffer += prev

            if (diff != 0L) {
                flush(diff)
            }

            if (isLast) {
                buffer += next
                flush(0)
            }
        }

    return result
}

fun printGrid(grid: Map<ShrinkedPos, State>) {
    val maxX = grid.maxOf { it.key.localPos.x }
    val maxY = grid.maxOf { it.key.localPos.y }

    (0..maxY).forEach { y ->
        (0..maxX).forEach { x ->
            val hasCell = grid.keys.any { it.localPos == Pos(x, y) }
            print(if (hasCell) "#" else " ")
        }
        println()
    }
}