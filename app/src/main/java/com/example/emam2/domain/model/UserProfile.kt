package com.example.emam2.domain.model

import java.time.LocalDate

data class UserProfile(
    val id: Int = 1,
    val name: String,
    val email: String = "",
    val pregnancyStartDate: LocalDate,
    val estimatedDueDate: LocalDate = pregnancyStartDate.plusDays(280),
    val currentWeek: Int = 1,
    val trimester: Int = 1
)