package com.czf.controller

import com.alibaba.fastjson.JSON
import com.czf.asMediaType
import com.czf.defaultImage
import com.czf.entity.login.LoginDAO
import com.czf.entity.user.UserDAO
import com.czf.iconPath
import com.czf.json.LoginResponse
import com.czf.json.UserInformation
import com.czf.service.FileService
import com.czf.service.UserService
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class UserController {
    @Autowired
    private lateinit var fileService: FileService

    @Autowired
    private lateinit var httpServletRequest: HttpServletRequest

    @Autowired
    private lateinit var userDAO: UserDAO

    @Autowired
    private lateinit var loginDAO: LoginDAO

    @Autowired
    private lateinit var userService: UserService

    @RequestMapping(value = ["/files/{userId}/all"], method = [RequestMethod.GET])
    fun allContents(@PathVariable("userId")userId:Int): ResponseEntity<List<String>> = runBlocking {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return@runBlocking ResponseEntity(HttpStatus.FORBIDDEN)

        ResponseEntity(fileService.loadUserDir(userId), HttpStatus.OK)
    }

    @RequestMapping(value = ["/login"], method = [RequestMethod.POST])
    fun login(@RequestParam("userName")userName:String, @RequestParam("password")password:String):ResponseEntity<LoginResponse> {
        val response = userService.login(userName, password)
        return if(response == null)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        else
            ResponseEntity(response, HttpStatus.OK)
    }

    @RequestMapping(value = ["/logOut"], method = [RequestMethod.POST])
    fun logOut():ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        loginDAO.deleteAllByToken(httpServletRequest.getHeader("token"))
        return ResponseEntity(HttpStatus.OK)
    }

    @RequestMapping(value = ["/checkLogin"], method = [RequestMethod.GET])
    fun checkLogin():ResponseEntity<String> {
        return if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        else
            ResponseEntity(HttpStatus.OK)
    }

    @RequestMapping(value = ["/register"], method = [RequestMethod.POST])
    fun register(@RequestParam("userName")userName:String, @RequestParam("password")password:String, @RequestParam("phone")phone:String):ResponseEntity<String> {
        val result = userService.registerUser(userName, password, phone)
        val status = when(result) {
            UserService.RegisterAccountResult.SUCCESS -> HttpStatus.OK
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity(JSON.toJSONString(result), status)
    }

    @RequestMapping(value = ["/refreshLogin"], method = [RequestMethod.POST])
    fun refresh():ResponseEntity<LoginResponse> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        val response = userService.refresh(httpServletRequest.getHeader("token"))
        return if(response == null)
            ResponseEntity(HttpStatus.FORBIDDEN)
        else
            ResponseEntity(response, HttpStatus.OK)
    }

    @RequestMapping(value = ["/modifyPassword"], method = [RequestMethod.POST])
    fun modifyPassword(@RequestParam("userName")userName:String, @RequestParam("oldPassword")old:String, @RequestParam("newPassword")new:String):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        return if(userService.modifyPassword(userName, old, new))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @RequestMapping(value = ["/modifySelfInformation"], method = [RequestMethod.POST])
    fun modifySelf(@RequestBody information: UserInformation):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        return if (userService.modifyUserInformation(information))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @RequestMapping(value = ["/icon/{userId}"], method = [RequestMethod.POST])
    fun uploadIcon(@PathVariable("userId")userId:Int, @RequestParam("file")file: MultipartFile):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        userService.uploadIcon(userId, file)
        return ResponseEntity(HttpStatus.OK)
    }

    @RequestMapping(value = ["/self/{userId}"], method = [RequestMethod.GET])
    fun self(@PathVariable("userId")userId:Int):ResponseEntity<UserInformation> {
        val user=userDAO.findById(userId).get()
        val userInformation=UserInformation(user.id, user.userName, user.expireDate, user.phone)
        return ResponseEntity(userInformation, HttpStatus.OK)
    }

    @RequestMapping(value = ["/icon/{userId}"], method = [RequestMethod.GET])
    fun downloadIcon(@PathVariable("userId")userId:Int, httpServletResponse: HttpServletResponse):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        val path=findPossiblePath("$iconPath/$userId")
        val fileName = path.substringAfterLast("/")
        val type = path.substringAfterLast(".")
        httpServletResponse.setHeader("Content-type", type.asMediaType())
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=$fileName")

        fileService.writeFileToStream(path, httpServletResponse)

        return ResponseEntity(HttpStatus.OK)
    }

    private fun findPossiblePath(name: String):String {
        val one=File("$name.jpg")
        if(one.exists())
            return one.absolutePath

        val two=File("$name.jpeg")
        if(two.exists())
            return two.absolutePath

        val three=File("$name.png")
        if(three.exists())
            return three.absolutePath

        val four=File("$name.heif")
        if(four.exists())
            return four.absolutePath

        return defaultImage
    }
}