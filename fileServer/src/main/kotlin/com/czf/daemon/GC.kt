package com.czf.daemon

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class GC {
    @Scheduled(cron = "0 0 1 1 * *")
    fun deleteFile() {

    }

    @Scheduled(cron = "0 1 * * * *")
    fun cleanMissingFiles() {

    }

    @Scheduled(cron = "0 1 * * * *")
    fun cleanInvalidUser() {

    }
}