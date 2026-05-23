package com.example.emam2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val email: String = "",
    val pregnancyStartDateEpoch: Long,
    val language: String = "id"
)