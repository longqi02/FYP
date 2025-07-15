package com.example.trafficsignapp.data.route

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey(autoGenerate = true)
    val routeId: Long = 0L,
    // ← this name has to match your DAO query!
    val routeKey: String,

    val userId: String,
    val startTime: Long,
    val endTime: Long? = null
)