package com.czf.entity.user

import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user", schema = "pic_bed")
class User {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id = -1

    @Basic
    @Column(name = "user_name", nullable = false, unique = true, length = 50)
    var userName = UUID.randomUUID().toString().replace("-", "")

    @Basic
    @Column(name = "password", nullable = false, length = 0)
    var password = ""

    @Basic
    @Column(name = "icon_dir", nullable = true, length = 0, unique = true)
    var iconFile:String? = null

    @Basic
    @Column(name = "expire_date", nullable = false)
    var expireDate = Timestamp(System.currentTimeMillis() + 100*24*3600*1000L)

    @Basic
    @Column(name = "phone", nullable = false, length = 100, unique = true)
    var phone = ""
}