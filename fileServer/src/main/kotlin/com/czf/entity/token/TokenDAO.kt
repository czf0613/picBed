package com.czf.entity.token

import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional
interface TokenDAO: JpaRepository<Token, Int> {
    fun countAllByOwner(owner:Int):Int
    fun findByValue(value:String):Token
    fun existsByValue(value:String):Boolean
    fun findAllByOwner(owner: Int):List<Token>
}