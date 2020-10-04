package com.czf.json

import java.sql.Timestamp

data class UserInformation(val userId:Int, val userName:String, val expireDate:Timestamp, val phone:String)