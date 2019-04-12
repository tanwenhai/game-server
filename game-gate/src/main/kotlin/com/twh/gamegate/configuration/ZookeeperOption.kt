package com.twh.gamegate.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "zookeeper", ignoreUnknownFields = true)
class ZookeeperOption {
    lateinit var connection: String
    lateinit var rootPath: String
}