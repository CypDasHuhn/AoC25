package dev.cypdashuhn.aoc25.d11

import dev.cypdashuhn.aoc25.getLines

val devices = getDeviceList() + Device("out", listOf())
fun getDeviceList() = getLines(11, "input").map(Device::fromString)
data class Device(
    val name: String,
    val otherDeviceNames: List<String>
) {
    companion object {
        fun fromString(s: String): Device {
            val name = s.substring(0..2)
            val otherDeviceNames = s.substring(5).split(" ")
            return Device(name, otherDeviceNames)
        }
    }

    val isYou by lazy { name == "you" }
    val isOut by lazy { name == "out" }
    val otherDevices by lazy { otherDeviceNames.map { d -> devices.first { d == it.name }} }
}

fun main() {
    val first = devices.first { it.isYou }
    walk(first, listOf())
    println(endings)
}

var endings = 0
fun walk(device: Device, devicesWalked: List<Device>) {
    val interesting = device.otherDevices.filter { !devicesWalked.contains(it) }.toMutableList()
    if (interesting.any { it.isOut }) {
        endings += 1
        interesting.removeIf { it.isOut }
    }
    val newWalked = devicesWalked + device
    interesting.forEach { walk(it, newWalked) }
}