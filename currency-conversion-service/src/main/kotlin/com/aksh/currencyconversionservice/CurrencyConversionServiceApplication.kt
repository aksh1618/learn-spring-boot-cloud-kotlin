package com.aksh.currencyconversionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.core.env.get
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.math.BigDecimal

@SpringBootApplication
class CurrencyConversionServiceApplication

fun main(args: Array<String>) {
    runApplication<CurrencyConversionServiceApplication>(*args) {
        val beansInitializer = beans {
            bean<ConversionService>()
            bean {
                val port = env["server.port"]
                getRouter(ref(), port ?: "8080")
            }
        }
        addInitializers(beansInitializer)
    }
}

private fun getRouter(conversionService: ConversionService, myPort: String): RouterFunction<ServerResponse> = router {
    GET("from/{from}/to/{to}/quantity/{quantity}") { request ->
        val (multiple, port) = conversionService.getConversionMultipleAndPort(
                request.pathVariable("from"),
                request.pathVariable("to")
        )
        ok().headers { headers ->
            headers["Port"] = port ?: myPort
        }.body(request.run {
            CurrencyConversionBean(
                    pathVariable("from"),
                    pathVariable("to"),
                    multiple,
                    BigDecimal(pathVariable("quantity")),
                    BigDecimal(pathVariable("quantity")) * multiple
            )
        })
    }
}

class ConversionService {
    fun getConversionMultipleAndPort(fromCurrency: String, toCurrency: String) = RestTemplate()
            .getForEntity(
                    "http://localhost:8000/from/{from}/to/{to}",
                    CurrencyConversionBean::class.java,
                    mapOf("from" to fromCurrency, "to" to toCurrency)
            ).let {
                Pair(
                        it.body?.conversionMultiple ?: throw ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't connect to exchange service"
                        ),
                        it.headers["Port"]?.get(0)
                )
            }
}

data class CurrencyConversionBean(
        val from: String,
        val to: String,
        val conversionMultiple: BigDecimal,
        val quantity: BigDecimal?,
        val convertedQuantity: BigDecimal?,
        val id: Long = 0L
)