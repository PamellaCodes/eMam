package com.example.emam2.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val pregnancyWeek: Int = 0,
    val trimester: Int = 1,
    val estimatedDueDate: String = "",
    val isLoading: Boolean = true
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid ?: ""

    init {
        loadUserData()
    }

    private fun loadUserData() {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val week = doc.getLong("pregnancyWeek")?.toInt() ?: 0
                val trimester = when {
                    week <= 12 -> 1
                    week <= 26 -> 2
                    else -> 3
                }
                val estimatedDueDate = calculateDueDate(week)
                _uiState.update {
                    it.copy(
                        fullName = doc.getString("fullName") ?: "",
                        email = doc.getString("email") ?: "",
                        pregnancyWeek = week,
                        trimester = trimester,
                        estimatedDueDate = estimatedDueDate,
                        isLoading = false
                    )
                }
            }
    }

    private fun calculateDueDate(pregnancyWeek: Int): String {
        val remainingWeeks = 40 - pregnancyWeek
        val dueDate = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.WEEK_OF_YEAR, remainingWeeks)
        }
        val months = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember")
        val day = dueDate.get(java.util.Calendar.DAY_OF_MONTH)
        val month = months[dueDate.get(java.util.Calendar.MONTH)]
        val year = dueDate.get(java.util.Calendar.YEAR)
        return "$day $month $year"
    }

    fun signOut(onSignOut: () -> Unit) {
        auth.signOut()
        onSignOut()
    }
}