package com.czf.entity.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*
import javax.transaction.Transactional

@Transactional
interface UserDAO: JpaRepository<User, Int> {
    @Query("select u.password from User u where u.userName = :userName")
    fun findPasswordByUserName(@Param("userName") userName:String):String

    @Query("select u.id from User u where u.userName = :userName")
    fun findIdWithUserName(@Param("userName") userName:String):Int

    @Query("select u.expireDate from User u where u.id = :id")
    fun getValidDate(@Param("id") id:Int): Date

    @Query("select u.userName from User u where u.id = :id")
    fun getUserNameWithId(@Param("id") id:Int): String

    fun existsByUserName(userName:String):Boolean
    fun existsByPhone(phone:String):Boolean
    fun findByUserName(userName:String):User
}