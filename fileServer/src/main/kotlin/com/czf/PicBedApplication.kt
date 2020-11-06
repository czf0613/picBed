package com.czf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableWebSecurity
class PicBedApplication

const val itemPath = "/picBed/userFiles"
const val iconPath = "/picBed/icons"
const val domain = "https://domain/api"
const val defaultImage = "/picBed/icons/default.png"

fun main(args: Array<String>) {
    runApplication<PicBedApplication>(*args)
}

fun String.asMediaType(): String {
    return when(this.toLowerCase()) {
        "png" -> MediaType.IMAGE_PNG_VALUE
        "jpg" -> MediaType.IMAGE_JPEG_VALUE
        "jpeg" -> MediaType.IMAGE_JPEG_VALUE
        "gif" -> MediaType.IMAGE_GIF_VALUE
        else -> MediaType.ALL_VALUE
    }
}