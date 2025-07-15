package com.example.trafficsignapp


import android.app.Application
import com.google.firebase.FirebaseApp
import androidx.room.Room
import com.example.trafficsignapp.data.AppDatabase
import com.google.android.libraries.places.api.Places

class TrafficSignApp : Application() {

    companion object {
        /** Global access to the Room database */
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        // 1️⃣ Initialize Firebase (so FirebaseAuth, Firestore, etc. work app-wide)
        FirebaseApp.initializeApp(this)

        // 2️⃣ Build your Room database once
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "traffic_app.db"
        )
            // .addCallback(...)   // ← optional: prepopulate your master sign table from assets
            .build()
    }
}