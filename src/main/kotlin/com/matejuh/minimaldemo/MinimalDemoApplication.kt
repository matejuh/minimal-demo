package com.matejuh.minimaldemo

import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder

class MinimalDemoApplication

val appBuilder: SpringApplicationBuilder = SpringApplicationBuilder(MinimalDemoApplication::class.java)
    .sources(// server
        ReactiveWebServerFactoryAutoConfiguration::class.java,
        // web
        HttpHandlerAutoConfiguration::class.java,
        WebFluxAutoConfiguration::class.java,
        ErrorWebFluxAutoConfiguration::class.java,
        // metrics
        MetricsAutoConfiguration::class.java,
        // db
        DataSourceAutoConfiguration::class.java,
        // jooq
        JooqAutoConfiguration::class.java,
        // liquibase
        LiquibaseAutoConfiguration::class.java,
        // app
        AppConfig::class.java)

fun main(args: Array<String>) {
    appBuilder.build().run(*args)
}
