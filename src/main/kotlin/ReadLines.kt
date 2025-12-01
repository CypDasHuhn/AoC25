package dev.cypdashuhn.aoc25

import java.io.BufferedReader
import java.io.File

fun getBufferedReader(day: Int, file: String): BufferedReader {
    return File("src/main/kotlin/d$day/$file.txt").bufferedReader()
}

fun getLinesStream(day: Int, file: String) = getBufferedReader(day, file).lines()
fun getLines(day: Int, file: String) = getLinesStream(day, file).toList()