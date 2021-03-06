package com.aksh.currencyconversionservice

import brave.sampler.Sampler
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.ribbon.RibbonClient
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
@EnableDiscoveryClient
class CurrencyConversionServiceApplication

fun main(args: Array<String>) {
    runApplication<CurrencyConversionServiceApplication>(*args) {
        val beansInitializer = beans {
            bean<ConversionService>()
            bean {
                val port = env["server.port"]
                getRouter(ref(), ref(), port ?: "8080")
            }
            bean<Sampler> { Sampler.ALWAYS_SAMPLE }
        }
        addInitializers(beansInitializer)
    }
}

private fun getRouter(
        currencyExchangeServiceProxy: CurrencyExchangeServiceProxy,
        conversionService: ConversionService,
        myPort: String
) = router {
    val logger = LoggerFactory.getLogger("Router")
    GET("from/{from}/to/{to}/quantity/{quantity}") { request ->
        val (multiple, port) = conversionService.getConversionMultipleAndPort(
                request.pathVariable("from"),
                request.pathVariable("to"),
                currencyExchangeServiceProxy
        )
        logger.info("Exchange rate = {}", multiple)
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

@FeignClient(name = "netflix-zuul-api-gateway-server")
@RibbonClient(name = "currency-exchange-service")
interface CurrencyExchangeServiceProxy {
    @GetMapping("currency-exchange-service/from/{from}/to/{to}")
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