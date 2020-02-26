package com.aksh.currencyexchangeservice

import org.springframework.boot.WebApplicationType
import org.springframework.core.env.get
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webmvc.webMvc

val app = application(WebApplicationType.SERVLET) {
    webMvc {
        port = Integer.parseInt(env["server.port"])
    }
}

fun main() {
    app.run()
}