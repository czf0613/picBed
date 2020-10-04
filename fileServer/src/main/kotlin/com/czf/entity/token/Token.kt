package com.czf.entity.token

import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "token", schema = "pic_bed")
class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    var id = -1

    @Basic
    @Column(name = "owner", nullable = false)
    var owner = -1

    @Basic
    @Column(name = "value", nullable = false, unique = true, length = 0)
    var value = UUID.randomUUID().toString().replace("-", "")

    @Basic
    @Column(name = "label", nullable = true, length = 100)
    var label:String = RandomStringUtils.randomAlphanumeric(64)

    @Basic
    @Column(name = "generate_date", nullable = false)
    var generateDate = Timestamp(System.currentTimeMillis())

    @Basic
    @Column(name = "recent_usage", nullable = false)
    var recentUsage = Timestamp(System.currentTimeMillis())
}