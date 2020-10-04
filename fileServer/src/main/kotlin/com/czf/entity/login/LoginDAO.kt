package com.czf.entity.login

import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface LoginDAO: JpaRepository<LoginState, Int> {
    fun countAllByOwner(owner:Int):Int
    fun existsByToken(token:String):Boolean
    fun findByToken(token:String):LoginState
}