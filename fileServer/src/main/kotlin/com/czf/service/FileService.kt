package com.czf.service

import com.czf.entity.fileRecord.FileRecord
import com.czf.entity.fileRecord.FileRecordDAO
import com.czf.entity.user.UserDAO
import com.czf.itemPath
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.servlet.http.HttpServletResponse


@Service
class FileService {
    @Autowired
    private lateinit var userDAO: UserDAO

    @Autowired
    private lateinit var fileRecordDAO: FileRecordDAO

    suspend fun writeFileTo(place: String, multipartFile: MultipartFile) {
        withContext(Dispatchers.IO) {
            val directory = place.substringBeforeLast("/")
            val dir=File(directory)
            if(!dir.exists())
                dir.mkdirs()

            val file = File(place)
            multipartFile.transferTo(file)
        }
    }

    fun writeFileToStream(path: String, httpServletResponse: HttpServletResponse) = runBlocking {
        if(!checkExist(path))
            return@runBlocking

        val file = File(path)
        val outputStream = httpServletResponse.outputStream
        val buff = ByteArray(1024)
        val bufferedInputStream = BufferedInputStream(FileInputStream(file))
        var i = bufferedInputStream.read(buff)
        while (i != -1) {
            outputStream.write(buff, 0, buff.size)
            outputStream.flush()
            i = bufferedInputStream.read(buff)
        }
    }

    suspend fun deleteFileAt(path: String) {
        withContext(Dispatchers.IO) {
            val file = File(path)
            if(file.exists()) file.delete()
        }
    }

    suspend fun checkExist(path: String):Boolean {
        return withContext(Dispatchers.IO) {
            val file = File(path)
            file.exists()
        }
    }

    suspend fun loadUserDir(userId: Int): List<String> {
        return withContext(Dispatchers.IO) {
            val userName = userDAO.getUserNameWithId(userId)
            val path = "$itemPath/$userName"
            val file = File(path)
            file.list()?.asList() ?: emptyList()
        }
    }

    fun saveFile(userId: Int, multipartFile: MultipartFile): Int {
        val userName = userDAO.getUserNameWithId(userId)
        //本来应该检查用户是否过期，这里没有写
        val suffix = multipartFile.originalFilename!!.substringAfterLast(".", "png")
        var path = "$itemPath/$userName/${UUID.randomUUID()}"
        var fileRecord = FileRecord().apply {
            this.owner = userId
            this.path = path
        }
        fileRecord = fileRecordDAO.save(fileRecord)
        path = "$itemPath/$userName/${fileRecord.id}.$suffix"

        GlobalScope.launch {
            writeFileTo(path, multipartFile)
        }

        fileRecord.path=path
        fileRecordDAO.save(fileRecord)

        return fileRecord.id
    }

    fun editLabel(label: String, fileId: Int):Boolean {
        return try {
            val file = fileRecordDAO.findById(fileId).get().apply {
                this.label = label
            }
            fileRecordDAO.save(file)
            true
        } catch (e: Exception) {
            false
        }
    }
}