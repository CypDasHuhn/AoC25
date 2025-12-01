package dev.cypdashuhn.aoc25.d1

import dev.cypdashuhn.aoc25.getLines

fun getSteps() = getLines(1, "input").map(Step::fromString)

val range = (0..99)
val start = range.first
val end = range.toList().size

data class CodeState(
    var currentNum: Int = 50,
    var amountOfPassing0s: Int = 0
) {
    fun roundPassed() { amountOfPassing0s += 1 }
    fun report() { println(this) }
}

fun main() {
    calc1()
    println("---")
    calc2()
}

fun calc1() {
    val state = CodeState()
    
    getSteps().forEach { step ->
        state.currentNum += step.modifier

        val overshoot = state.currentNum >= end
        val undershoot = state.currentNum < start

        if (overshoot) {
            state.currentNum -= end
        } else if (undershoot) {
            state.currentNum += end
        }

        if (state.currentNum == start) state.roundPassed()
    }

    state.report()
}

fun calc2() {
    val state = CodeState()

    getSteps().forEach { step ->
        val previousNum = state.currentNum
        state.currentNum += step.modifier

        val overshoot = state.currentNum >= end
        val undershoot = state.currentNum < start
        val landedAtStart = state.currentNum == start

        if (overshoot) {
            state.roundPassed()
            state.currentNum -= end
        } else if (undershoot) {
            if (previousNum != start) state.roundPassed()
            state.currentNum += end
        } else if (landedAtStart) {
            state.roundPassed()
        }

        state.amountOfPassing0s += step.passes
    }

    state.report()
}

data class Step(
    val modifier: Int,
    val passes: Int
) {
    companion object {
        fun fromString(s: String): Step {
            val directionStr = s.first()
            val isLeft = when (directionStr) {
                'L' -> true
                'R' -> false
                else -> throw IllegalArgumentException("L / R expected")
            }

            var num = s.substring(1).toInt()

            val overshoots = num / end
            if (num >= end) {
                num = num % overshoots
            }

            val modifier = if (isLeft) -num else num

            return Step(modifier, overshoots)
        }
    }
}