package com.example.emam2.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val fullName: String = "",
    val email: String = "",
    val pregnancyWeek: Int = 0,
    val estimatedDueDate: String = "",
    val trimester: Int = 1,
    val isLoading: Boolean = true,
    val error: String = "",
    // Nutrisi hari ini
    val totalCalories: Double = 0.0,
    val totalIronMg: Double = 0.0,
    val totalFolateMcg: Double = 0.0,
    val totalCalciumMg: Double = 0.0,
    val totalZincMg: Double = 0.0,
    val totalIodineMcg: Double = 0.0,
    val totalVitaminDMcg: Double = 0.0,
    val latestAiNote: String = ""
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    // Kebutuhan nutrisi harian ibu hamil (AKG 2019 Kemenkes RI)
    val dailyNeeds = mapOf(
        "calories" to 2300.0,
        "ironMg" to 35.0,
        "folateMcg" to 600.0,
        "calciumMg" to 1200.0,
        "zincMg" to 11.0,
        "iodineMcg" to 220.0,
        "vitaminDMcg" to 15.0
    )

    init {
        fetchUserData()
    }

    fun fetchUserData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Ambil data user
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val fullName = document.getString("fullName") ?: ""
                    val email = document.getString("email") ?: ""
                    val pregnancyWeek = document.getLong("pregnancyWeek")?.toInt() ?: 0
                    val trimester = when {
                        pregnancyWeek <= 12 -> 1
                        pregnancyWeek <= 26 -> 2
                        else -> 3
                    }
                    val estimatedDueDate = calculateDueDate(pregnancyWeek)
                    _uiState.update {
                        it.copy(
                            fullName = fullName,
                            email = email,
                            pregnancyWeek = pregnancyWeek,
                            trimester = trimester,
                            estimatedDueDate = estimatedDueDate,
                            isLoading = false
                        )
                    }
                }
                // Ambil data nutrisi hari ini
                fetchTodayNutrition(uid)
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(error = e.message ?: "Gagal mengambil data", isLoading = false) }
            }
    }

    private fun fetchTodayNutrition(uid: String) {
        val startOfDay = getStartOfDay()
        val endOfDay = startOfDay + 86400000L

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("nutrition")
            .whereGreaterThan("timestamp", startOfDay)
            .whereLessThan("timestamp", endOfDay)
            .get()
            .addOnSuccessListener { documents ->
                var calories = 0.0
                var iron = 0.0
                var folate = 0.0
                var calcium = 0.0
                var zinc = 0.0
                var iodine = 0.0
                var vitaminD = 0.0
                var latestNote = ""

                for (doc in documents) {
                    calories += doc.getDouble("calories") ?: 0.0
                    iron += doc.getDouble("ironMg") ?: 0.0
                    folate += doc.getDouble("folateMcg") ?: 0.0
                    calcium += doc.getDouble("calciumMg") ?: 0.0
                    zinc += doc.getDouble("zincMg") ?: 0.0
                    iodine += doc.getDouble("iodineMcg") ?: 0.0
                    vitaminD += doc.getDouble("vitaminDMcg") ?: 0.0
                    if (latestNote.isEmpty()) latestNote = doc.getString("aiNote") ?: ""
                }

                _uiState.update {
                    it.copy(
                        totalCalories = calories,
                        totalIronMg = iron,
                        totalFolateMcg = folate,
                        totalCalciumMg = calcium,
                        totalZincMg = zinc,
                        totalIodineMcg = iodine,
                        totalVitaminDMcg = vitaminD,
                        latestAiNote = latestNote
                    )
                }
            }
    }

    private fun getStartOfDay(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
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
}