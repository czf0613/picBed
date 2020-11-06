package com.czf.entity.fileRecord

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

@Transactional
interface FileRecordDAO: JpaRepository<FileRecord, Int> {
    @Query("select f.path from FileRecord f where f.id =:id")
    fun findFilePathById(@Param("id")id:Int):String

    @Query("select f.path from FileRecord f where f.owner =:id")
    fun findAllPathsWithUserId(@Param("id")id:Int):List<String>
}