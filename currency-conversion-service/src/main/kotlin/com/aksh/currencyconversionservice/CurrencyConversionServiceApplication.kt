package com.aksh.currencyconversionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.support.beans
import org.springframework.core.env.get
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.function.router
import java.math.BigDecimal

@SpringBootApplication
@EnableFeignClients("com.aksh.currencyconversionservice")
class CurrencyConversionServiceApplication

fun main(args: Array<String>) {
    runApplication<CurrencyConversionServiceApplication>(*args) {
        val beansInitializer = beans {
            bean<ConversionService>()
            bean {
                val port = env["server.port"]
                getRouter(ref(), ref(), port ?: "8080")
            }
        }
        addInitializers(beansInitializer)
    }
}

private fun getRouter(
        currencyExchangeServiceProxy: CurrencyExchangeServiceProxy,
        conversionService: ConversionService,
        myPort: String
) = router {
    GET("from/{from}/to/{to}/quantity/{quantity}") { request ->
        val (multiple, port) = conversionService.getConversionMultipleAndPort(
                request.pathVariable("from"),
                request.pathVariable("to"),
                currencyExchangeServiceProxy
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

@FeignClient(name = "currency-exchange-service", url = "localhost:8000")
interface CurrencyExchangeServiceProxy {
    @GetMapping("/from/{from}/to/{to}")
    fun retrieveExchangeValue(
            @PathVariable from: String, @PathVariable to: String
    ): ResponseEntity<CurrencyConversionBean>
}

class ConversionService {
    fun getConversionMultipleAndPort(
            fromCurrency: String, toCurrency: String, currencyExchangeServiceProxy: CurrencyExchangeServiceProxy
    ) = currencyExchangeServiceProxy
            .retrieveExchangeValue(fromCurrency, toCurrency)
            .let {
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