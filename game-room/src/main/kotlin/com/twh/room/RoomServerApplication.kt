package com.twh.room

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RoomServerApplication

fun main(args: Array<String>) {
    runApplication<RoomServerApplication>(*args)
}
