package org.contourgara

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringS3KotlinApplication

fun main(args: Array<String>) {
    runApplication<SpringS3KotlinApplication>(*args)
}
