package dev.cypdashuhn.aoc25.d5

import dev.cypdashuhn.aoc25.getLinesStream

fun LongRange.longCount(): Long {
    return this.endInclusive - this.start + 1
}
fun LongRange.intersects(long: Long) = this.start <= long && this.endInclusive >= long

fun main() {
    var range = 3..5
    var range2 = 3.toLong()..5

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

data class Comparison(
    val range: LongRange,
    val leftIntersected: Boolean,
    val rightIntersected: Boolean,
    val completeOverlap: Boolean
) {
    companion object {
        fun compare(r: LongRange, r2: LongRange): Comparison {
            val completeOverlap = r.start <= r2.start && r.endInclusive >= r2.endInclusive
            val leftIntersected = r2.intersects(r.endInclusive)
            val rightIntersected = r2.intersects(r.start)
            return Comparison(r, leftIntersected, rightIntersected, completeOverlap)
        }
    }
}

fun calc3() {
    val data = getStuff()

    val sortedRanges = data.freshRanges.sortedWith(compareBy({it.first }, { it.last })).toSet()

    val bruteforce = sortedRanges.flatMap { it.toList() }.toSet()

    val cutRanges = mutableListOf<LongRange>()
    sortedRanges.withIndex().forEach { (idx, range) ->
        val overlapping = cutRanges.map { Comparison.compare(it, range) }
        if (overlapping.any { it.completeOverlap }) return@forEach
        val highestEnd = overlapping.filter { it.leftIntersected }.maxOfOrNull { it.range.endInclusive }
        val lowestStart = overlapping.filter { it.rightIntersected }.maxOfOrNull { it.range.start }

        if (highestEnd != null && lowestStart != null && lowestStart >= highestEnd) return@forEach

        var start = range.start
        var end = range.endInclusive
        if (highestEnd != null) {
            if (highestEnd == range.endInclusive) { return@forEach }
            if (highestEnd >= range.start) start = highestEnd + 1
        } else if (lowestStart != null) {
            if (lowestStart == range.start) { return@forEach }
            if (lowestStart <= range.endInclusive) end = lowestStart - 1
        }

        if (start > end) return@forEach
        cutRanges += start .. end
    }

    var s = cutRanges.map { (it.start to it.hashCode()) to true } + cutRanges.map { (it.endInclusive to it.hashCode()) to false}
    var duplicates = s.filter { o -> s.count { it.first == o.first } == 2 }.map { o -> s.filter { o.first == it.first } }
    var y = duplicates.filter { it[0].first.second != it[1].first.second }
    var e = s.toSet()

    var count: Long = 0
    cutRanges.forEach {
        count += it.longCount()
    }

    val ordered = cutRanges.sortedWith(compareBy({it.start}, {it.endInclusive}))

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
    getLinesStream(5, "test").forEach { s ->
        if (s.trim() == "") isSecond = true;
        else if (!isSecond) {
            val (first, second) = s.split("-")
            val firstLong = first.toLong()
            val secondLong = second.toLong()
            var s = if (secondLong > firstLong) firstLong..secondLong else secondLong..firstLong
            freshRanges += s
        } else {
            items += s.toLong()
        }
    }
    return Data(freshRanges, items)
}