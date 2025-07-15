package com.example.trafficsignapp.data.route


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trafficsignapp.data.detection.DetectedSignEntity

@Database(
    entities = [Route::class, RouteDetectionCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class RouteDatabase : RoomDatabase() {
    abstract fun routeDao(): RouteDao

    companion object {
        @Volatile private var INSTANCE: RouteDatabase? = null

        fun getInstance(ctx: Context): RouteDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    RouteDatabase::class.java,
                    "route.db"
                ).build().also { INSTANCE = it }
            }
    }
}