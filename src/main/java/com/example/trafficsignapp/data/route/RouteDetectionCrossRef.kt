package com.example.trafficsignapp.data.route

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "route_detection_ref",
    primaryKeys = ["routeId","detectionId"],
    indices = [
        Index(value = ["detectionId"]),  // <-- add this
        Index(value = ["routeId"])     // optional—PK columns are usually indexed already
    ]
)
data class RouteDetectionCrossRef(
    val routeId: Long,
    val detectionId: Long
)