package com.aksh.limitsservice

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "limit-service")
data class Config(
        val minimum: Int,
        val maximum: Int
)