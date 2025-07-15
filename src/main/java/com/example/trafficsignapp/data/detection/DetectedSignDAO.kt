package com.example.trafficsignapp.data.detection


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DetectedSignDao {
    @Insert fun insert(sign: DetectedSignEntity): Long

    @Query("SELECT * FROM detected_signs ORDER BY timestamp DESC")
    fun getAll(): List<DetectedSignEntity>

    /**  New: grab every detection in descending timestamp order  */
    @Query("SELECT * FROM detected_signs ORDER BY timestamp DESC")
    fun getAllDetections(): List<DetectedSignEntity>
}