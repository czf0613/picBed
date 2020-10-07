package com.czf.controller

import com.czf.asMediaType
import com.czf.domain
import com.czf.entity.fileRecord.FileRecordDAO
import com.czf.entity.user.UserDAO
import com.czf.service.FileService
import com.czf.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 上传文件的接口
 *
 * @property 需要注入多个对象 已经展示在class开头部分了
 *
 * @author czf
 */
@RestController
class FileController {
    @Autowired
    private lateinit var httpServletRequest: HttpServletRequest

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var userDAO: UserDAO

    @Autowired
    private lateinit var fileService: FileService

    @Autowired
    private lateinit var fileDAO: FileRecordDAO

    /**
     * 文件上传接口
     *
     * @param userId Int类型的用户id
     * @param file multipart file
     * 潜在的一个参数为token 从header中获取
     *
     * @return post方法，返回值为对应文件的url
     */
    @RequestMapping(value = ["/upload"], method = [RequestMethod.POST])
    fun upload(@RequestParam("userId") userId: Int, @RequestParam("file") file: MultipartFile):ResponseEntity<String> {
        //校验token
        val token = httpServletRequest.getHeader("token")
        if(!tokenService.checkToken(token))
            return ResponseEntity("非法用户", HttpStatus.FORBIDDEN)

        //检验用户是否存在
        if(!userDAO.existsById(userId))
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        //交给一个后台service去存文件并得到文件id。存文件和获取文件id为异步的，所以这个方法不会很花时间，不阻塞
        val fileId = fileService.saveFile(userId, file)

        //拼接一个url返回，会用到域名和端口
        return ResponseEntity("$domain/download/$fileId", HttpStatus.OK)
    }

    /**
     * get方法，用于文件下载
     *
     * @param fileId 文件id
     * 会将文件写入至输出流中，但是流传输是会阻塞的，所以要考虑系统的并发问题和性能优化，但是此处没有做太多的优化
     */
    @RequestMapping(value = ["/download/{fileId}"], method = [RequestMethod.GET])
    fun download(@PathVariable("fileId") fileId: Int, httpServletResponse: HttpServletResponse):ResponseEntity<String> {
        val filePath = fileDAO.findFilePathById(fileId)

        //从完整的文件路径中获取文件名
        val fileName = filePath.substringAfterLast("/")
        //获取文件类型，即.之后的字符
        val type = filePath.substringAfterLast(".")
        httpServletResponse.setHeader("Content-type", type.asMediaType())
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=$fileName")

        fileService.writeFileToStream(filePath, httpServletResponse)

        return ResponseEntity(HttpStatus.OK)
    }
}