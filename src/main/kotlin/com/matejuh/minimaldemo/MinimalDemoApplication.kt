package com.matejuh.minimaldemo

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.micrometer.jmx.JmxConfig
import io.micrometer.jmx.JmxMeterRegistry
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import reactor.netty.http.server.HttpServer

val registry = CompositeMeterRegistry()
fun server(port: Int = 9001): HttpServer =
    HttpServer
        .create()
        .port(port)
        .handle(ReactorHttpHandlerAdapter(RouterFunctions.toHttpHandler(routes())))
        .accessLog(true)

var dbUserName = "user"
var dbPassword = "pass"
var url = "jdbc:tc:postgresql:12.4:///spring-demo?TC_TMPFS=/testtmpfs:rw"

val hikariConfig = HikariConfig().apply {
    jdbcUrl = url
    username = dbUserName
    password = dbPassword
}
val datasource = HikariDataSource(hikariConfig).apply { metricRegistry = registry }

private val jooq: DSLContext = DSL.using(datasource, SQLDialect.POSTGRES)
val liquibase = Liquibase(
    "db.changelog/db.changelog-master.sql",
    ClassLoaderResourceAccessor(),
    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(datasource.connection))
).update()

private val bookRepository = BookRepository(jooq)
private val bookService = BookService(bookRepository)
private val bookController = BookController(bookService)

fun routes(): RouterFunction<out ServerResponse> = coRouter {
    POST(BOOK_URI, bookController::createBook)
    GET(BOOK_ITEM_URI, bookController::getBook)
}

fun main(args: Array<String>) {
    val jmxRegistry = JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM)
    registry.add(jmxRegistry)
    ClassLoaderMetrics().bindTo(registry)
    JvmMemoryMetrics().bindTo(registry)
    JvmGcMetrics().bindTo(registry)
    ProcessorMetrics().bindTo(registry)
    JvmThreadMetrics().bindTo(registry)

    server().bindNow().onDispose().block()
}
