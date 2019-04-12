package com.twh.room

import com.twh.core.GameServerApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RoomServerApplication : GameServerApplication()

fun main(args: Array<String>) {
    runApplication<RoomServerApplication>(*args)
}
