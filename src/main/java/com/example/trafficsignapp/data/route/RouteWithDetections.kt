package com.example.trafficsignapp.data.route

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.trafficsignapp.data.detection.DetectedSignEntity

/**
 * Wrapper class for the @Relation query, bringing in the DetectedSignEntity objects.
 */
data class RouteWithDetections(
    @Embedded
    val route: Route,

    @Relation(
        parentColumn  = "routeId",
        entityColumn  = "detectionId",
        associateBy   = Junction(RouteDetectionCrossRef::class)
    )
    val detections: List<DetectedSignEntity>
)