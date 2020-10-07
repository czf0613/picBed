package com.czf.controller

import com.alibaba.fastjson.JSON
import com.czf.asMediaType
import com.czf.defaultImage
import com.czf.entity.user.UserDAO
import com.czf.iconPath
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

/**
 * 用户登录以及管理页面
 * 东西很杂但是基本上功能一看就懂，日后考虑使用websocket改写这里面的方法
 *
 * @since 1.0.0
 */
@RestController
class UserController {
    @Autowired
    private lateinit var fileService: FileService

    @Autowired
    private lateinit var httpServletRequest: HttpServletRequest

    @Autowired
    private lateinit var userDAO: UserDAO

    @Autowired
    private lateinit var userService: UserService

    /**
     * 获取该用户的所有的文件的文件名，本质上，文件名，除去文件后缀名，就是文件的id
     *
     * @see com.czf.service.FileService.saveFile
     */
    @RequestMapping(value = ["/files/{userId}/all"], method = [RequestMethod.GET])
    fun allContents(@PathVariable("userId")userId:Int): ResponseEntity<String> = runBlocking {
        //检查用户有没有登陆
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return@runBlocking ResponseEntity<String>(HttpStatus.FORBIDDEN)

        ResponseEntity(JSON.toJSONString(fileService.loadUserDir(userId)), HttpStatus.OK)
    }

    //登录
    @RequestMapping(value = ["/login"], method = [RequestMethod.POST])
    fun login(@RequestParam("userName")userName:String, @RequestParam("password")password:String):ResponseEntity<String> {
        val response = userService.login(userName, password)
        return if(response == null)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        else
            ResponseEntity(JSON.toJSONString(response), HttpStatus.OK)
    }

    //注册用户
    @RequestMapping(value = ["/register"], method = [RequestMethod.POST])
    fun register(@RequestParam("userName")userName:String, @RequestParam("password")password:String, @RequestParam("phone")phone:String):ResponseEntity<String> {
        val result = userService.registerUser(userName, password, phone)
        val status = when(result) {
            UserService.RegisterAccountResult.SUCCESS -> HttpStatus.OK
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity(JSON.toJSONString(result), status)
    }

    //刷新登录token
    @RequestMapping(value = ["/refreshLogin"], method = [RequestMethod.POST])
    fun refresh():ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        val response = userService.refresh(httpServletRequest.getHeader("token"))
        return if(response == null)
            ResponseEntity(HttpStatus.FORBIDDEN)
        else
            ResponseEntity(JSON.toJSONString(response), HttpStatus.OK)
    }

    //改密码
    @RequestMapping(value = ["/modifyPassword"], method = [RequestMethod.POST])
    fun modifyPassword(@RequestParam("userName")userName:String, @RequestParam("oldPassword")old:String, @RequestParam("newPassword")new:String):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        return if(userService.modifyPassword(userName, old, new))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    //修改个人信息
    @RequestMapping(value = ["/modifySelfInformation"], method = [RequestMethod.POST])
    fun modifySelf(@RequestParam("information")information:String):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        println(information)
        val info = JSON.parseObject(information, UserInformation::class.java)
        return if (userService.modifyUserInformation(info))
            ResponseEntity(HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    //上传个人用户头像
    @RequestMapping(value = ["/icon/{userId}"], method = [RequestMethod.POST])
    fun uploadIcon(@PathVariable("userId")userId:Int, @RequestParam("file")file: MultipartFile):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        userService.uploadIcon(userId, file)
        return ResponseEntity(HttpStatus.OK)
    }

    //获取个人信息
    @RequestMapping(value = ["/self/{userId}"], method = [RequestMethod.GET])
    fun self(@PathVariable("userId")userId:Int):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        val user=userDAO.findById(userId).get()
        val userInformation=UserInformation(user.id, user.userName, user.expireDate, user.phone)
        return ResponseEntity(JSON.toJSONString(userInformation), HttpStatus.OK)
    }

    //下载个人头像
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

    /**
     * 由于头像文件名就是用户id，但是不确定文件类型是什么，可能是jpg或者png或者其他的
     * 但是如果靠搜索匹配的话效率可能不高，所以干脆直接一个个尝试可能更快
     *
     * @return 如果有匹配的就返回，如果没有，就返回缺省值
     */
    private fun findPossiblePath(name:String):String {
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

        val five=File("$name.gif")
        if(five.exists())
            return five.absolutePath

        return defaultImage
    }
}