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

    @RequestMapping(value = ["/upload"], method = [RequestMethod.POST])
    fun upload(@RequestParam("userId") userId: Int, @RequestParam("file") file: MultipartFile):ResponseEntity<String> {
        val token = httpServletRequest.getHeader("token")
        if(!tokenService.checkToken(token))
            return ResponseEntity("非法用户", HttpStatus.FORBIDDEN)

        if(!userDAO.existsById(userId))
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        val fileId = fileService.saveFile(userId, file)

        return ResponseEntity("$domain/download/$fileId", HttpStatus.OK)
    }

    @RequestMapping(value = ["/download/{fileId}"], method = [RequestMethod.GET])
    fun download(@PathVariable("fileId") fileId: Int, httpServletResponse: HttpServletResponse):ResponseEntity<String> {
        val filePath = fileDAO.findFilePathById(fileId)

        val fileName = filePath.substringAfterLast("/")
        val type = filePath.substringAfterLast(".")
        httpServletResponse.setHeader("Content-type", type.asMediaType())
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=$fileName")

        fileService.writeFileToStream(filePath, httpServletResponse)

        return ResponseEntity(HttpStatus.OK)
    }
}