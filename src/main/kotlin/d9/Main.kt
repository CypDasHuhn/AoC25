package dev.cypdashuhn.aoc25.d9

import dev.cypdashuhn.aoc25.getLines
import java.io.File
import kotlin.math.max
import kotlin.math.min

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

    drawGrid(yShrinked)
    interpolateGrid(yShrinked)
    printGrid(yShrinked)
    correctJumps(yShrinked)
    e(yShrinked)
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

fun printGrid(grid: Map<ShrinkedPos, State>, filename: String = "grid.txt") {
    val maxX = grid.maxOf { it.key.localPos.x }
    val maxY = grid.maxOf { it.key.localPos.y }

    val output = buildString {
        (0..maxY).forEach { y ->
            (0..maxX).forEach { x ->
                val cell = grid.filter { it.key.localPos == Pos(x, y) }.map { it.value }.firstOrNull()
                append(when (cell) {
                    null -> " "
                    State.RED -> "#"
                    State.GREEN -> "O"
                    State.EMPTY -> "."
                })
            }
            appendLine()
        }
    }

    File(filename).writeText(output)
    println("Grid saved to $filename")
}
enum class Direction(val dx: Int, val dy: Int) {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    fun turnLeft() = when(this) {
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        EAST -> NORTH
    }

    fun turnRight() = when(this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }
}

fun drawGrid(grid: MutableMap<ShrinkedPos, State>) {
    val entries = grid.entries.toList()
    var current = entries.minBy { it.key.localPos.y }
    val wentOverEntries = mutableListOf<Pos>()
    var direction = Direction.EAST

    fun findNeighbor(dir: Direction): MutableMap.MutableEntry<ShrinkedPos, State>? {
        val current = current.key.localPos

        return entries
            .filter { it.key != current }
            .filter { entry ->
                val pos = entry.key.localPos
                when (dir) {
                    Direction.NORTH -> pos.x == current.x && pos.y < current.y
                    Direction.SOUTH -> pos.x == current.x && pos.y > current.y
                    Direction.EAST -> pos.y == current.y && pos.x > current.x
                    Direction.WEST -> pos.y == current.y && pos.x < current.x
                }
            }
            .minByOrNull { entry ->
                val pos = entry.key.localPos
                when (dir) {
                    Direction.NORTH -> current.y - pos.y  // Closest below us
                    Direction.SOUTH -> pos.y - current.y  // Closest above us
                    Direction.EAST -> pos.x - current.x   // Closest to right
                    Direction.WEST -> current.x - pos.x   // Closest to left
                }
            }
    }

    var finished = false
    while(!finished) {
        fun walk(entry: MutableMap.MutableEntry<ShrinkedPos, State>) {
            val xSame = current.key.localPos.x == entry.key.localPos.x

            if (xSame) {
                (min(current.key.localPos.y, entry.key.localPos.y)..max(current.key.localPos.y, entry.key.localPos.y)).forEach { y ->
                    val pos = Pos(current.key.localPos.x, y)
                    val key = entries.firstOrNull { it.key.localPos == pos }
                    if (key == null) {
                        grid[ShrinkedPos(pos, Pos(-1, -1))] = State.GREEN
                    }
                }
            } else {
                (min(current.key.localPos.x, entry.key.localPos.x)..max(current.key.localPos.x, entry.key.localPos.x)).forEach { x ->
                    val pos = Pos(x, current.key.localPos.y)
                    val key = entries.firstOrNull { it.key.localPos == pos }
                    if (key == null) {
                        grid[ShrinkedPos(pos, Pos(-1, -1))] = State.GREEN
                    }
                }
            }

            if (wentOverEntries.contains(entry.key.localPos)) finished = true
            wentOverEntries += current.key.localPos
            current = entry
        }

        val left = findNeighbor(direction.turnLeft())
        val forward = findNeighbor(direction)
        val right = findNeighbor(direction.turnRight())

        when {
            left != null -> {
                direction = direction.turnLeft()
                walk(left)
            }
            forward != null -> {
                walk(forward)
            }
            right != null -> {
                direction = direction.turnRight()
                walk(right)
            }
            else -> throw IllegalStateException("No valid move from ${current.key.localPos}")
        }
    }
}

fun interpolateGrid(grid: MutableMap<ShrinkedPos, State>) {
    val maxX = grid.maxBy { it.key.localPos.x }.key.localPos.x
    val minX = grid.minBy { it.key.localPos.x }.key.localPos.x
    val maxY = grid.maxBy { it.key.localPos.y }.key.localPos.y
    val minY = grid.minBy { it.key.localPos.y }.key.localPos.y

    (minX..maxX).forEach { x ->
        var foundBorder = false
        val buffer = mutableListOf<Pos>()
        (minY..maxY).forEach { y ->
            val pos = Pos(x, y)
            val entry = grid.filter { it.key.localPos == pos && it.value != State.EMPTY }.map { it }.firstOrNull()
            val exists = entry != null

            if (!exists) grid[ShrinkedPos(pos, Pos(-1, -1))] = State.EMPTY

            if (!exists && foundBorder) {
                buffer += pos
            } else if (exists) {
                if (foundBorder && buffer.isNotEmpty()) {
                    buffer.forEach {
                        grid[ShrinkedPos(it, Pos(-1, -1))] = State.GREEN
                    }
                    buffer.clear()
                }
                foundBorder = true
            }
        }
    }
}

fun correctJumps(grid: MutableMap<ShrinkedPos, State>) {
    val maxX = grid.maxBy { it.key.localPos.x }.key.localPos.x
    val minX = grid.minBy { it.key.localPos.x }.key.localPos.x
    val maxY = grid.maxBy { it.key.localPos.y }.key.localPos.y
    val minY = grid.minBy { it.key.localPos.y }.key.localPos.y

    // Correct X jumps
    val xCorrected = mutableMapOf<ShrinkedPos, State>()
    (minX..maxX).forEach { x ->
        val entries = grid.filter { it.key.localPos.x == x }.map { it }
        val jumpX = entries.first { it.key.jump.x > -1 }.key.jump.x

        entries.forEach { (key, value) ->
            val newKey = ShrinkedPos(key.localPos, Pos(jumpX, key.jump.y))
            xCorrected[newKey] = value
        }
    }

    // Correct Y jumps
    val yCorrected = mutableMapOf<ShrinkedPos, State>()
    (minY..maxY).forEach { y ->
        val entries = xCorrected.filter { it.key.localPos.y == y }.map { it }
        val jumpY = entries.first { it.key.jump.y > -1 }.key.jump.y

        entries.forEach { (key, value) ->
            val newKey = ShrinkedPos(key.localPos, Pos(key.jump.x, jumpY))
            yCorrected[newKey] = value
        }
    }

    grid.clear()
    grid.putAll(yCorrected)
}

fun e(grid: MutableMap<ShrinkedPos, State>) {
    val entries = grid.entries.filter { it.value == State.RED }.withIndex()
    val combs = entries.flatMap { e -> entries.filter { it.index < e.index }.map { e.value to  it.value } }
    val legalCombs = combs.map { (e1, e2) ->
        val e1x = max(e1.key.localPos.x, e2.key.localPos.x)
        val e1y = max(e1.key.localPos.y, e2.key.localPos.y)
        val e2x = min(e1.key.localPos.x, e2.key.localPos.x)
        val e2y = min(e1.key.localPos.y, e2.key.localPos.y)

        val inBetween = grid.entries.filter {
            val x1 = it.key.localPos.x
            val y1 = it.key.localPos.y

            x1 in e2x..e1x && y1 in e2y..e1y
        }
        if (inBetween.any { it.value == State.EMPTY }) return@map (e1 to e2) to (-1).toLong()

        val xSize1 = inBetween.filter { it.key.localPos.y == e2y }.sortedBy { it.key.localPos.x }
        val xSize2 = xSize1.take(xSize1.count() - 1).sumOf { it.key.jump.x } + 1
        val ySize1 = inBetween.filter { it.key.localPos.x == e2x }.sortedBy { it.key.localPos.y }
        val ySize2 = ySize1.take(ySize1.count() - 1).sumOf { it.key.jump.y } + 1
        val e = xSize2 * ySize2
        (e1 to e2) to e
    }.sortedByDescending { it.second }
    println(legalCombs.first())
}