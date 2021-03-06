package com.twh.core.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "zookeeper")
open class ZookeeperOption {
    lateinit var connection: String
    lateinit var rootPath: String
}