package com.czf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableWebSecurity
class PicBedApplication

/**
 * 存放一些可能全局会用到的常量
 * @param 存放用户文件的地址，这个是根目录，根据用户名还会创建文件夹
 * @param 存放用户头像的地址
 * @param 域名以及端口，到时候存放图片后返回的URL会有这个值
 * @param 存放默认用户头像的文件地址
 */
const val itemPath = "/picBed/userFiles"
const val iconPath = "/picBed/icons"
const val domain = "https://domain.xxx:23456"
const val defaultImage = "/picBed/icons/default.png"

/**
 * 顶层函数，不需要任何类包裹就可以直接启动
 *
 * @param 参数就是 system args
 *
 * @see 可以参考Springboot的经典启动方法 SpringBootApplication.run(xxx.class, args)
 */
fun main(args: Array<String>) {
    runApplication<PicBedApplication>(*args)
}

/**
 * 扩展函数，用于判断媒体类型
 * @param 后缀名 不含.
 * @return 对应MediaType，如果没有就默认/oetec/stream
 */
fun String.asMediaType(): String {
    return when(this.toLowerCase()) {
        "png" -> MediaType.IMAGE_PNG_VALUE
        "jpg" -> MediaType.IMAGE_JPEG_VALUE
        "jpeg" -> MediaType.IMAGE_JPEG_VALUE
        "gif" -> MediaType.IMAGE_GIF_VALUE
        else -> MediaType.ALL_VALUE
    }
}