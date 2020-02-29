package com.aksh.limitsservice

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
class LimitsConfigController(
        val config: Config
) {

    @GetMapping("/limits")
    @HystrixCommand(fallbackMethod = "limitsFallback")
    fun retrieveLimitsFromConfiguration() = when (Random.nextInt(2)) {
        0 -> LimitConfig(config.maximum, config.minimum)
        else -> throw RuntimeException("Uh oh!")
    }

    fun limitsFallback() = LimitConfig(100, 1)

}