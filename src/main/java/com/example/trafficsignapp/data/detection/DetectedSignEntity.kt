package com.example.trafficsignapp.data.detection


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detected_signs")
data class DetectedSignEntity(
    @PrimaryKey(autoGenerate = true)
    val detectionId: Long = 0L,
    val classId: Int,
    val confidence: Float,
    val timestamp: Long
)