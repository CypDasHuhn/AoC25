package dev.cypdashuhn.aoc25.d12

typealias ShapeIndex = Byte

data class Shape(
    val index: ShapeIndex,
    val data: Array<Array<Boolean>>
) {
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
            fun flush() { shapes += fromStrings(buffer) }

            list.withIndex().forEach { (idx, s) ->
                if(s.isEmpty()) { flush(); return@forEach }

                if (buffer.isEmpty()) {
                    val isShapeStart = s.contains(":")
                    if (isShapeStart) buffer += s
                    else return ShapeList(shapes, list.drop(idx))
                } else buffer += s
            }

            throw IllegalStateException("not here")
        }
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
    }
}