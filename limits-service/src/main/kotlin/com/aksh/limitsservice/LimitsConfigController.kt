package com.aksh.limitsservice

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LimitsConfigController {

    @GetMapping("/limits")
    fun retrieveLimitsFromConfiguration() = LimitConfig(1000, 1)

}