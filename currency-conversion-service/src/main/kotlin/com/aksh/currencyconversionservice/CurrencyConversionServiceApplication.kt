package com.aksh.currencyconversionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.core.env.get
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.math.BigDecimal

@SpringBootApplication
class CurrencyConversionServiceApplication

fun main(args: Array<String>) {
    runApplication<CurrencyConversionServiceApplication>(*args) {
        val beansInitializer = beans {
            bean {
                val port = env["server.port"]
                getRouter(port ?: "8080")
            }
        }
        addInitializers(beansInitializer)
    }
}

private fun getRouter(port: String): RouterFunction<ServerResponse> = router {
    GET("from/{from}/to/{to}/quantity/{quantity}") { request ->
        ok().headers { headers ->
            headers["Port"] = port
        }.body(request.run {
            CurrencyConversionBean(
                    pathVariable("from"),
                    pathVariable("to"),
                    BigDecimal.ONE,
                    BigDecimal(pathVariable("quantity")),
                    BigDecimal.ONE
            )
        })
    }
}

data class CurrencyConversionBean(
        val from: String,
        val to: String,
        val conversionMultiple: BigDecimal,
        val quantity: BigDecimal,
        val convertedQuantity: BigDecimal,
        val id: Long = 0L
)