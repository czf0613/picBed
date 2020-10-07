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

/**
 * 负责处理图片上传token管理的接口。注意不是用户登录的token，是文件上传的token
 *
 * @property 注入对象如下
 */
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

    /**
     * 获取该用户名下所有的活动的token
     * 使用get方法
     *
     * @param userId 用户id
     *
     * @return 返回值为所有Token组成的list，token类的定义下见
     * @see com.czf.entity.token.Token
     */
    @RequestMapping(value = ["/{userId}/all"], method = [RequestMethod.GET])
    fun getAllToken(@PathVariable("userId")userId:Int):ResponseEntity<String> {
        //检查用户有没有登录token，注意此token非彼token
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        return ResponseEntity(JSON.toJSONString(tokenDAO.findAllByOwner(userId)), HttpStatus.OK)
    }

    /**
     * 为该用户生成一个新的token
     *
     * @param userId 用户id
     * @param label 新令牌的标签，用于区分，用户可以选择不输入该参数，有默认值完成
     *
     * @return 返回一个token对象，如果token生成失败就返回400操作码
     * @see com.czf.entity.token.Token
     */
    @RequestMapping(value = ["/{userId}/generate"], method = [RequestMethod.POST])
    fun generate(@PathVariable("userId")userId:Int, @RequestParam("label",required = false, defaultValue = "新建token")label:String):ResponseEntity<String> {
        //检查用户有没有登录token
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)
        val result = tokenService.generateToken(userId, label)
        return if(result == null)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        else
            ResponseEntity(JSON.toJSONString(result), HttpStatus.OK)
    }

    /**
     * 按照token编号删除一个token
     * 使用http delete方法
     *
     * @param tokenId Token的id，在前面返回的实体类中有该信息
     * @see com.czf.controller.TokenController.getAllToken
     */
    @RequestMapping(value = ["/delete/{tokenId}"], method = [RequestMethod.DELETE])
    fun deleteToken(@PathVariable("tokenId")tokenId:Int):ResponseEntity<String> {
        if(!userService.checkStatus(httpServletRequest.getHeader("token")))
            return ResponseEntity(HttpStatus.FORBIDDEN)

        tokenDAO.deleteById(tokenId)
        return ResponseEntity(HttpStatus.OK)
    }
}