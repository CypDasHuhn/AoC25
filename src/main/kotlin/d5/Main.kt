package dev.cypdashuhn.aoc25.d5

import dev.cypdashuhn.aoc25.getLinesStream

fun LongRange.longCount() = this.endInclusive - this.start + 1

fun main() {
    calc3()
}

fun calc1() {
    val data = getStuff()

    val count = data.items
    println(count)
}

fun calc2() {
    val data = getStuff()

    var count = 0.toLong()
    val sortedRanges = data.freshRanges.sortedBy { it.first }
    val queuedRanges = mutableListOf<LongRange>()

    println(sortedRanges.sumOf { it.longCount() })

    sortedRanges.withIndex().forEach { (idx, range) ->
        val toDelete = mutableListOf<Int>()
        queuedRanges.withIndex().forEach { (idx, queueItem) ->
            if (queueItem.endInclusive < range.start) {
                count += queueItem.longCount()
                toDelete += idx
            }
        }
        toDelete.sortDescending()
        for (td in toDelete) {
            queuedRanges.removeAt(td)
        }

        var cutRange = range
        queuedRanges.forEach { range ->
            cutRange =
                (if (range.endInclusive > cutRange.start) range.endInclusive else cutRange.start)..cutRange.endInclusive
        }

        queuedRanges += cutRange
    }

    count += queuedRanges.sumOf { it.longCount() }

    println(count)
}

fun calc3() {
    val data = getStuff()

    val sortedRanges = data.freshRanges.sortedWith(compareBy({it.start}, { it.endInclusive })).toSet()

    val cutRanges = mutableListOf<LongRange>()
    sortedRanges.withIndex().forEach { (idx, range) ->
        var localRange = range
        if (idx > 165) {
            println("-")
        }
        val overlaps = cutRanges.map { cr ->
            cr to ((cr.start <= localRange.endInclusive && cr.start >= localRange.start ) to (cr.endInclusive >= localRange.start && cr.endInclusive <= localRange.endInclusive))
        }.filter { it.second.first || it.second.second }
        val highestEnd = overlaps.filter { it.second.second }.maxOfOrNull { it.first.endInclusive }
        val lowestStart = overlaps.filter { it.second.first }.minOfOrNull { it.first.start }
        if (highestEnd != null && lowestStart != null && highestEnd >= lowestStart) return@forEach
        if (highestEnd != null &&highestEnd >= range.start) return@forEach
        if (lowestStart != null && lowestStart <= range.endInclusive) return@forEach

        var start = range.start
        var end = range.endInclusive
        if (highestEnd != null && highestEnd >= range.start) {
            start = highestEnd
        }
        if (lowestStart != null && lowestStart <= range.endInclusive) {
            end = lowestStart
        }
        cutRanges += end..start
    }

    var s = cutRanges.map { it.start } + cutRanges.map { it.endInclusive }
    var duplicates = s.filter { o -> s.count { it == o } == 2 }
    var e = s.toSet()

    var count: Long = 0
    cutRanges.forEach {
        count += it.longCount()
    }
    println(count)
}

data class Data(
    val freshRanges: MutableList<LongRange> = mutableListOf(),
    val items: MutableList<Long> = mutableListOf()
)

fun getStuff(): Data {
    val freshRanges = mutableListOf<LongRange>()
    val items = mutableListOf<Long>()
    var isSecond = false;
    getLinesStream(5, "input").forEach { s ->
        if (s.trim() == "") isSecond = true;
        else if (!isSecond) {
            val (first, second) = s.split("-")
            val firstLong = first.toLong()
            val secondLong = second.toLong()
            freshRanges += if (secondLong > firstLong) firstLong..secondLong else secondLong..firstLong
        } else {
            items += s.toLong()
        }
    }
    return Data(freshRanges, items)
}