package com.matejuh.minimaldemo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
@ComponentScan(basePackages = ["com.matejuh.minimaldemo"])
class AppConfig {

    @Bean
    fun routes(bookController: BookController): RouterFunction<out ServerResponse> = coRouter {
        POST(BOOK_URI, bookController::createBook)
        GET(BOOK_ITEM_URI, bookController::getBook)
    }
}
