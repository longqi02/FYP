package com.example.trafficsignapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    /**
     * Insert or update a user.
     * If the `id` already exists, REPLACE the row.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(user: User)

    /**
     * Load a single user by their primary key.
     */
    @Query("SELECT * FROM users WHERE id = :id")
    fun getById(id: String): User?
}