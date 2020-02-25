package com.aksh.limitsservice

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LimitsConfigController(
        val config: Config
) {

    @GetMapping("/limits")
    fun retrieveLimitsFromConfiguration() = LimitConfig(config.maximum, config.minimum)

}