package com.matejuh.minimaldemo

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import reactor.netty.http.server.HttpServer

fun main(args: Array<String>) {
    val context: ApplicationContext = AnnotationConfigApplicationContext().also {
        it.scan("com.matejuh.minimaldemo")
        it.refresh()
    }
    val server = context.getBean(HttpServer::class.java)
    server.bindNow().onDispose().block()
}
