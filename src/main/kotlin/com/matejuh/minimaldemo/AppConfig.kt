package com.matejuh.minimaldemo

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.jmx.JmxConfig
import io.micrometer.jmx.JmxMeterRegistry
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import javax.sql.DataSource


@Configuration
@ComponentScan(basePackages = ["com.matejuh.minimaldemo"])
@PropertySource("classpath:application.properties")
class AppConfig {

    @Bean
    fun metricsRegistry(): MeterRegistry {
        val registry = CompositeMeterRegistry()
        val jmxRegistry = JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM)
        registry.add(jmxRegistry)

        ClassLoaderMetrics().bindTo(registry)
        JvmMemoryMetrics().bindTo(registry)
        JvmGcMetrics().bindTo(registry)
        ProcessorMetrics().bindTo(registry)
        JvmThreadMetrics().bindTo(registry)

        return registry
    }

    @Bean
    fun datasource(
        @Value("\${db.username}") dbUserName: String,
        @Value("\${db.username}") dbPassword: String,
        @Value("\${db.url}") dbUrl: String,
        registry: MeterRegistry
    ): DataSource {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbUrl
            username = dbUserName
            password = dbPassword
        }
        return HikariDataSource(hikariConfig).apply { metricRegistry = registry }
    }

    @Bean
    fun jooq(datasource: DataSource): DSLContext = DSL.using(datasource, SQLDialect.POSTGRES)

    @Bean
    fun liquibase(datasource: DataSource): Liquibase {
        val liquibase = Liquibase(
            "db.changelog/db.changelog-master.sql",
            ClassLoaderResourceAccessor(),
            DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(datasource.connection))
        )
        liquibase.update()
        return liquibase
    }

    @Bean
    fun routes(bookController: BookController): RouterFunction<out ServerResponse> = coRouter {
        POST(BOOK_URI, bookController::createBook)
        GET(BOOK_ITEM_URI, bookController::getBook)
    }

    @Bean
    fun nettyReactiveWebServerFactory(): ReactiveWebServerFactory = NettyReactiveWebServerFactory(9001)

    @Bean
    fun webHandler(routes: RouterFunction<out ServerResponse>): HttpHandler = RouterFunctions.toHttpHandler(routes)
}
