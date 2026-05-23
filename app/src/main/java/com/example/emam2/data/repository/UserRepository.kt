package com.example.emam2.data.repository

import com.example.emam2.data.local.AppDatabase
import com.example.emam2.data.local.UserProfileEntity
import com.example.emam2.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class UserRepository(private val db: AppDatabase) {

    fun observeUserProfile(): Flow<UserProfile?> =
        db.userProfileDao().observeProfile().map { it?.toDomain() }

    suspend fun getUserProfile(): UserProfile? =
        db.userProfileDao().getProfile()?.toDomain()

    suspend fun saveUserProfile(name: String, email: String, hphtDate: LocalDate) {
        db.userProfileDao().upsert(
            UserProfileEntity(
                name = name,
                email = email,
                pregnancyStartDateEpoch = hphtDate.toEpochDay()
            )
        )
    }

    private fun UserProfileEntity.toDomain(): UserProfile {
        val hpht = LocalDate.ofEpochDay(pregnancyStartDateEpoch)
        val estimatedDueDate = hpht.plusDays(280)
        val today = LocalDate.now()
        val daysPregnant = ChronoUnit.DAYS.between(hpht, today).toInt().coerceAtLeast(0)
        val currentWeek = ((daysPregnant / 7) + 1).coerceAtMost(40)
        val trimester = when {
            currentWeek <= 12 -> 1
            currentWeek <= 26 -> 2
            else -> 3
        }
        return UserProfile(
            name = name,
            email = email,
            pregnancyStartDate = hpht,
            estimatedDueDate = estimatedDueDate,
            currentWeek = currentWeek,
            trimester = trimester
        )
    }
}