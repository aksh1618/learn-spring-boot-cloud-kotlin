package com.aksh.currencyexchangeservice

import org.springframework.boot.WebApplicationType
import org.springframework.core.env.get
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webmvc.webMvc
import java.math.BigDecimal

val app = application(WebApplicationType.SERVLET) {
    beans { bean<ExchangeService>() }
    webMvc {
        port = Integer.parseInt(env["server.port"])
        router {
            val exchangeService = ref<ExchangeService>()
            GET("/from/{from}/to/{to}") { request ->
                ok().body(
                        exchangeService.getExchangeRate(
                                request.pathVariable("from"), request.pathVariable("to")
                        )
                )
            }
        }
        converters { jackson() }
    }
}

class ExchangeService {
    fun getExchangeRate(from: String, to: String) = ExchangeValue(from, to, BigDecimal.ONE)
}

data class ExchangeValue(val from: String, val to: String, val conversionMultiple: BigDecimal, val id: Long = 0L)

fun main() {
    app.run()
}