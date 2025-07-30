package com.pucetec.auth.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ExampleController {

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello, World!"
    }

    @GetMapping("/health")
    fun health(): String {
        return "OK"
    }

    @PostMapping("/admin/action")
    fun action(): String {
        return "Admin action performed successfully!"
    }

    @PostMapping("/everybody")
    fun everybody(): String {
        return "Everybody can call this endpoint!"
    }

    @GetMapping("/secure-data")
    fun secureData(): String {
        return "Secure Data: Only ADMINS and SUPERUSERS can see this."
    }
}