package com.aksh.limitsservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(Config::class)
class LimitsServiceApplication

fun main(args: Array<String>) {
    runApplication<LimitsServiceApplication>(*args)
}
