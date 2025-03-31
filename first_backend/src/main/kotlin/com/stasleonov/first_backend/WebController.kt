package com.stasleonov.first_backend

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WebController {

    @GetMapping("/home")  // Обрабатываем /home
    fun homePage(): String {
        return "forward:/index.html"  // Перенаправляем на index.html
    }

    @GetMapping("/")
    fun indexPage(): String {
        return "forward:/index.html"  // Перенаправляем на index.html
    }
}