package com.aksh.currencyexchangeservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.core.env.get
import org.springframework.web.servlet.function.router
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@SpringBootApplication
class CurrencyExchangeServiceApplication

fun main(args: Array<String>) {
    runApplication<CurrencyExchangeServiceApplication>(*args) {
        val beansInitializer = beans {
            bean<ExchangeService>()
            bean {
                getRouter(ref(), env["server.port"] ?: "8080")
            }
        }
        addInitializers(beansInitializer)
    }
}

private fun getRouter(exchangeService: ExchangeService, port: String) = router {
    GET("/from/{from}/to/{to}") { request ->
        ok().headers { headers ->
            headers["Port"] = port
        }.body(request.run {
            exchangeService.getExchangeRate(pathVariable("from"), pathVariable("to"))
        })
    }
}

class ExchangeService {
    fun getExchangeRate(from: String, to: String) = ExchangeValue(from, to, BigDecimal.ONE)
}

@Entity
data class ExchangeValue(
        @Column(name="currency_from") val from: String,
        @Column(name="currency_to") val to: String,
        val conversionMultiple: BigDecimal,
        @Id val id: Long = 0L
)
