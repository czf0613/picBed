package com.czf.service

import com.czf.entity.login.LoginDAO
import com.czf.entity.login.LoginState
import com.czf.entity.user.User
import com.czf.entity.user.UserDAO
import com.czf.iconPath
import com.czf.json.LoginResponse
import com.czf.json.UserInformation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp

@Service
class UserService {
    @Autowired
    private lateinit var userDAO: UserDAO

    @Autowired
    private lateinit var loginDao:LoginDAO

    @Autowired
    private lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var fileService: FileService

    enum class AbleToLogin {
        OK, NO_SUCH_USER, WRONG_PASSWORD, TOO_MANY_DEVICE
    }

    enum class RegisterAccountResult {
        SUCCESS, USERNAME_DUPLICATE, PHONE_USED, NAME_TOO_LONG
    }

    enum class TimeUnit {
        DAY, MONTH, YEAR
    }

    fun checkPassword(userName:String, rawPassword:String): AbleToLogin {
        if(!userDAO.existsByUserName(userName))
            return AbleToLogin.NO_SUCH_USER

        val encodedPassword = userDAO.findPasswordByUserName(userName)
        if(!bCryptPasswordEncoder.matches(rawPassword.toLowerCase(), encodedPassword))
            return AbleToLogin.WRONG_PASSWORD

        val userId = userDAO.findIdWithUserName(userName)
        if(loginDao.countAllByOwner(userId) >= 5)
            return AbleToLogin.TOO_MANY_DEVICE

        return AbleToLogin.OK
    }

    fun login(userName:String, rawPassword:String): LoginResponse? {
        return when(checkPassword(userName, rawPassword)) {
            AbleToLogin.OK -> {
                val id = userDAO.findIdWithUserName(userName)
                val login = LoginState().apply {
                    this.owner = id
                }
                loginDao.save(login)
                LoginResponse(id, login.token, login.expireDate)
            }
            else -> null
        }
    }

    fun refresh(oldToken:String):LoginResponse? {
        return if(!checkStatus(oldToken))
            null
        else {
            val login = loginDao.findByToken(oldToken).apply {
                this.expireDate = Timestamp(System.currentTimeMillis() + 15*24*3600*1000)
            }
            loginDao.save(login)
            LoginResponse(login.owner, oldToken, login.expireDate)
        }
    }

    fun checkStatus(token:String):Boolean {
        return loginDao.existsByToken(token)
    }

    fun registerUser(userName:String, rawPassword:String, phone:String): RegisterAccountResult {
        if(userName.length > 50)
            return RegisterAccountResult.NAME_TOO_LONG

        val user = User().apply {
            this.userName = userName
            this.password = bCryptPasswordEncoder.encode(rawPassword.toLowerCase())
            this.phone = phone
        }

        return try {
            userDAO.save(user)
            RegisterAccountResult.SUCCESS
        } catch (e:Exception) {
            if(userDAO.existsByPhone(phone)) RegisterAccountResult.PHONE_USED else RegisterAccountResult.USERNAME_DUPLICATE
        }
    }

    fun modifyPassword(userName:String, oldPassword:String, newPassword:String): Boolean {
        if(checkPassword(userName, oldPassword) != AbleToLogin.OK)
            return false

        val user = userDAO.findByUserName(userName).apply {
            this.password = newPassword
        }

        userDAO.save(user)
        return true
    }

    fun modifyUserInformation(info:UserInformation):Boolean {
        val user = userDAO.findByUserName(info.userName).apply {
            this.phone = info.phone
        }

        return try {
            userDAO.save(user)
            true
        } catch (e:Exception) {
            false
        }
    }

    fun uploadIcon(userId:Int, multipartFile: MultipartFile) {
        val suffix = multipartFile.originalFilename!!.substringAfterLast(".", "png")
        val filePath= "$iconPath/$userId.$suffix"
        val user = userDAO.findById(userId).get().apply {
            this.iconFile=filePath
        }
        userDAO.save(user)
        GlobalScope.launch {
            fileService.writeFileTo(filePath, multipartFile)
        }
    }

    //need to implement
    fun buyService(duration:Int, unit:TimeUnit): Boolean {
        return false
    }

    //need to implement
    fun checkService(userId:Int): Boolean {
        val expireDate = userDAO.getValidDate(userId)
        //return expireDate.before(Date())
        return true
    }
}