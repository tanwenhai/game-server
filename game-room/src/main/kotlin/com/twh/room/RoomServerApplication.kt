package com.twh.room

import com.twh.core.configuration.GameServerBootstarp
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
        scanBasePackages = ["com.twh"]
)
class RoomServerApplication : GameServerBootstarp()

fun main(args: Array<String>) {
    runApplication<RoomServerApplication>(*args)
}
