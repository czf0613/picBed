package com.czf.service

import com.czf.entity.token.Token
import com.czf.entity.token.TokenDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class TokenService {
    @Autowired
    private lateinit var tokenDAO: TokenDAO

    fun generateToken(userId:Int, label:String):Token? {
        if(tokenDAO.countAllByOwner(userId) >= 10)
            return null

        val token = Token().apply {
            this.label = label
            this.owner = userId
        }

        return tokenDAO.save(token)
    }

    private fun updateUsage(token:String) {
        val tokenObj = tokenDAO.findByValue(token).apply {
            this.recentUsage = Timestamp(System.currentTimeMillis())
        }
        tokenDAO.save(tokenObj)
    }

    fun checkToken(token:String):Boolean {
        if(!tokenDAO.existsByValue(token))
            return false

        updateUsage(token)
        return true
    }
}