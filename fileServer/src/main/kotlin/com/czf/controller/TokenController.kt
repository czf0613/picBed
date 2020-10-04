package com.czf.controller

import com.alibaba.fastjson.JSON
import com.czf.entity.token.TokenDAO
import com.czf.service.TokenService
import com.czf.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/token")
class TokenController {
    @Autowired
    private lateinit var tokenDAO: TokenDAO

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var httpServletRequest: HttpServletRequest

    @Autowired
    private lateinit var tokenService: TokenService

    @RequestMapping(value = ["/{userId}/all"], method = [RequestMethod.GET])
    fun getAllToken(@PathVariable("userId")userId:Int):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        return ResponseEntity(JSON.toJSONString(tokenDAO.findAllByOwner(userId)), HttpStatus.OK)
    }

    @RequestMapping(value = ["/{userId}/generate"], method = [RequestMethod.POST])
    fun generate(@PathVariable("userId")userId:Int, @RequestParam("label",required = false, defaultValue = "新建token")label:String):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)
        val result = tokenService.generateToken(userId, label)
        return if(result == null)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        else
            ResponseEntity(JSON.toJSONString(result), HttpStatus.OK)
    }

    @RequestMapping(value = ["/delete/{tokenId}"], method = [RequestMethod.DELETE])
    fun deleteToken(@PathVariable("tokenId")tokenId:Int):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        tokenDAO.deleteById(tokenId)
        return ResponseEntity(HttpStatus.OK)
    }
}