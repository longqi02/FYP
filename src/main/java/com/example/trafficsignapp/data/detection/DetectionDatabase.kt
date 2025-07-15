package com.example.trafficsignapp.data.detection
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DetectedSignEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DetectionDatabase : RoomDatabase() {
    abstract fun detectedSignDao(): DetectedSignDao

    companion object {
        @Volatile private var INSTANCE: DetectionDatabase? = null

        fun getInstance(ctx: Context): DetectionDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    DetectionDatabase::class.java,
                    "detection.db"
                ).build().also { INSTANCE = it }
            }
    }
}