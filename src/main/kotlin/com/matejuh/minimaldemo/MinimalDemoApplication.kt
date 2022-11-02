package com.matejuh.minimaldemo

import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(AppConfig::class)
class MinimalDemoApplication

fun main(args: Array<String>) {
    runApplication<MinimalDemoApplication>(*args)
}
