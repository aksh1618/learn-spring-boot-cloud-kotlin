package com.aksh.netflixzuulapigatewayserver

import brave.sampler.Sampler
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.support.beans
import org.springframework.stereotype.Component

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
class NetflixZuulApiGatewayServerApplication

fun main(args: Array<String>) {
    runApplication<NetflixZuulApiGatewayServerApplication>(*args) {
        addInitializers(
                beans {
                    bean<Sampler> { Sampler.ALWAYS_SAMPLE }
                }
        )
    }
}

@Component
class ZuulLoggingServer : ZuulFilter() {
    private val logger = LoggerFactory.getLogger(ZuulFilter::class.java)
    override fun shouldFilter() = true
    override fun filterOrder() = 1
    override fun filterType() = "pre"
    override fun run(): Any = RequestContext.getCurrentContext().request.apply {
        logger.info("request {} at uri {}", this, requestURI)
    }
}