package com.aksh.currencyexchangeservice

import brave.sampler.Sampler
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.support.beans
import org.springframework.core.env.get
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.web.servlet.function.router
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@SpringBootApplication
@EnableDiscoveryClient
class CurrencyExchangeServiceApplication

fun main(args: Array<String>) {
    runApplication<CurrencyExchangeServiceApplication>(*args) {
        val beansInitializer = beans {
            bean {
                getRouter(ref(), env["server.port"] ?: "8080")
            }
            bean<Sampler> { Sampler.ALWAYS_SAMPLE }
        }
        addInitializers(beansInitializer)
    }
}

private fun getRouter(exchangeService: ExchangeValueRepository, port: String) = router {
    val logger = LoggerFactory.getLogger("Router")
    GET("/from/{from}/to/{to}") { request ->
        ok().headers { headers ->
            headers["Port"] = port
        }.body(request.run {
            exchangeService.findByFromAndTo(pathVariable("from"), pathVariable("to")).also {
                logger.info("Exchange rate = {}", it)
            }
        })
    }
}

interface ExchangeValueRepository : JpaRepository<ExchangeValue, Long> {
    fun findByFromAndTo(from: String, to: String): ExchangeValue
}

@Entity
data class ExchangeValue(
        @Column(name = "currency_from") val from: String,
        @Column(name = "currency_to") val to: String,
        val conversionMultiple: BigDecimal,
        @Id val id: Long = 0L
)
