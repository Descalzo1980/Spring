package com.stasleonov.first_backend

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ApiController {

    @GetMapping("/info")
    fun getInfo(): Map<String, String> {
        return mapOf("message" to "Это информация из API", "status" to "success")
    }

    @GetMapping("/greet/{name}")
    fun greetUser(@PathVariable name: String): Map<String, String> {
        return mapOf("message" to "Привет, $name!", "status" to "success")
    }
}