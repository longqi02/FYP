package com.example.trafficsignapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.trafficsignapp.data.User
//import com.example.trafficsignapp.data.TrafficSignMaster
//import com.example.trafficsignapp.data.Route
//import com.example.trafficsignapp.data.RecordedSign

// TODO: replace MyEntity with your actual @Entity classes
@Database(
    entities = [
        User::class,
       // TrafficSignMaster::class,
       // Route::class,
       // RecordedSign::class ],
        ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
  //  abstract fun signDao(): TrafficSignDao
  //  abstract fun routeDao(): RouteDao
  //  abstract fun recordedSignDao(): RecordedSignDao

    // Define your DAOs here, for example:
    // abstract fun userDao(): UserDao
    // abstract fun routeDao(): RouteDao

    companion object {
        // your singleton getInstance() or initialization from TrafficSignApp
    }
}

