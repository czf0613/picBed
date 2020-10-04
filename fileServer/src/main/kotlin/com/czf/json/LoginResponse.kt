package com.czf.json

import java.sql.Timestamp

data class LoginResponse(val userId:Int, val token:String, val expireDate:Timestamp)