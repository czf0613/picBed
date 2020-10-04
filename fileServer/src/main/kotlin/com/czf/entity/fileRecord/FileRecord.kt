package com.czf.entity.fileRecord

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "file_record", schema = "pic_bed")
class FileRecord {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id = -1

    @Basic
    @Column(name = "owner", nullable = false)
    var owner = -1

    @Basic
    @Column(name = "label", nullable = true, length = 100)
    var label = ""

    @Basic
    @Column(name = "path", nullable = false, unique = true, length = 0)
    var path = ""

    @Basic
    @Column(name = "generate_date", nullable = false)
    var generateDate = Timestamp(System.currentTimeMillis())
}