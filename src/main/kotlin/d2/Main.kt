package dev.cypdashuhn.aoc25.d2

import dev.cypdashuhn.aoc25.getLines
import dev.cypdashuhn.aoc25.getLinesStream

fun main() {
    println(Id.doesIdRepeat("12"))
    println(Id.doesIdRepeat("101"))
    println(Id.doesIdRepeat("302"))
    println(Id.doesIdRepeat("330"))
    println(Id.doesIdRepeat("1213"))

    println("---")

    println(Id.doesIdRepeat("12"))
    println(Id.doesIdRepeat("11"))
    println(Id.doesIdRepeat("1212"))
    println(Id.doesIdRepeat("123123"))
    println(Id.doesIdRepeat("12341234"))
}

fun getIds(): List<Id> {
    val lines = getLines(2, "input")
    assert(lines.size == 1) { "should be just one" }
    val first = lines[0]
    val ids = first.split(",").map(Id::fromString)
    return ids
}

// ([0-9])\1+
data class Id(
    val first: String,
    val second: String
) {
    companion object {
        fun fromString(s: String): Id {
            val parts = s.split("-")
            assert(parts.size == 2) { "Must be size of 2!" }

            return Id(parts[0], parts[1])
        }

        fun doesIdRepeat(id: String): Boolean {
            var pattern = ""
            var localMatch = ""
            var matches = 1

            for ((idx, char) in id.withIndex()) {
                if (idx == 0) {
                    pattern = char.toString()
                    continue
                }

                var currentTake = localMatch + char
                if (currentTake == pattern.take(localMatch.length + 1)) {
                    localMatch += char
                } else {
                    if (char == pattern[0]) {
                        localMatch = ""
                        localMatch += char
                        matches += 1
                    } else if (matches == 1){
                         pattern += char
                         localMatch += char
                    } else {
                        return false
                    }
                }
            }
            return matches > 1
        }
    }

}
