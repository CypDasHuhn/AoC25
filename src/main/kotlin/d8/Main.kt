package dev.cypdashuhn.aoc25.d8

import dev.cypdashuhn.aoc25.getLines
import kotlin.math.pow
import kotlin.math.sqrt

data class Pos3D(
    val x: Long,
    val y: Long,
    val z: Long
) {
    companion object {
        fun fromString(s: String): Pos3D {
            val (x, y, z) = s.split(",")
            return Pos3D(x.toLong(), y.toLong(), z.toLong())
        }
    }

    fun distance(p2: Pos3D): Double {
        val dx = (x - p2.x).toDouble().pow(2.0)
        val dy = (y - p2.y).toDouble().pow(2.0)
        val dz = (z - p2.z).toDouble().pow(2.0)
        val sum = dx + dy + dz;
        val root = sqrt(sum)
        return root
    }
}

fun getPositions() = getLines(8, "input").map { Pos3D.fromString(it) }

data class Comparison(
    val idx1: Int,
    val idx2: Int,
    val pos1: Pos3D,
    val pos2: Pos3D,
    val distance: Double
)

data class Circuit(
    val list: MutableList<Pos3D> = mutableListOf()
) {
    fun hasPos(pos: Pos3D) = list.contains(pos)
    fun addPos(pos: Pos3D) = list.add(pos)
    fun addCircuit(circuit: Circuit) = this.list.addAll(circuit.list)
}

data class Network(
    val circuits: MutableList<Circuit> = mutableListOf()
) {
    fun add(t: Comparison) {
        val c1 = circuits.firstOrNull { it.hasPos(t.pos1) }
        val c2 = circuits.firstOrNull { it.hasPos(t.pos2) }

        when {
            c1 == null && c2 == null -> {
                circuits += Circuit(mutableListOf(t.pos1, t.pos2))
            }

            c1 != null && c2 != null -> {
                if (c1 === c2) return
                c1.addCircuit(c2)
                circuits.remove(c2)
            }

            c1 == null -> c2!!.addPos(t.pos1)
            else -> c1.addPos(t.pos2)
        }
    }
}

fun main() {
    val network = Network()

    val positions = getPositions()
    positions
        .withIndex()
        .flatMap { (idx, pos) ->
            positions
                .withIndex()
                .filter { it.index > idx }
                .map { Comparison(idx, it.index, pos, it.value, it.value.distance(pos)) }
        }
        .sortedBy { it.distance }
        .forEach { t -> network.add(t) }

    val sum = network
        .circuits
        .map { it.list.count() }
        .sortedDescending()
        .take(3)
        .reduce { acc, v -> acc * v }

    println(sum)
}