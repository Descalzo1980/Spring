package com.stasleonov.first_backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FirstBackendApplication

fun main(args: Array<String>) {
	runApplication<FirstBackendApplication>(*args)
}
