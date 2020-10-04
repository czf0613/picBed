package com.czf.entity.login

import org.apache.commons.lang.RandomStringUtils
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "login", schema = "pic_bed")
class LoginState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    var id = -1

    @Basic
    @Column(name = "token", unique = true, nullable = false, length = 0)
    var token:String = RandomStringUtils.randomAlphanumeric(64)

    @Basic
    @Column(name = "owner", nullable = false)
    var owner = -1

    @Basic
    @Column(name = "expire_date", nullable = false)
    var expireDate = Timestamp(System.currentTimeMillis() + 15*24*3600*1000)
}