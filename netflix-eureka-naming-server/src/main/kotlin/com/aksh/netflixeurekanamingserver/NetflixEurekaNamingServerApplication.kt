package com.aksh.netflixeurekanamingserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class NetflixEurekaNamingServerApplication

fun main(args: Array<String>) {
    runApplication<NetflixEurekaNamingServerApplication>(*args)
}
