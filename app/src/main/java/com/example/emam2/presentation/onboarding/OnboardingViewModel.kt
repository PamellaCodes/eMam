package com.example.emam2.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emam2.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class OnboardingUiState(
    val step: Int = 0,
    val name: String = "",
    val email: String = "",
    val hphtDate: LocalDate? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)

class OnboardingViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, error = "") }
    fun onEmailChanged(value: String) = _uiState.update { it.copy(email = value, error = "") }
    fun onHphtSelected(date: LocalDate) = _uiState.update { it.copy(hphtDate = date, error = "") }
    fun nextStep() = _uiState.update { it.copy(step = it.step + 1) }
    fun prevStep() = _uiState.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }

    fun submit(onComplete: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Nama tidak boleh kosong") }
            return
        }
        if (state.hphtDate == null) {
            _uiState.update { it.copy(error = "Pilih tanggal HPHT terlebih dahulu") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.saveUserProfile(state.name, state.email, state.hphtDate)
            _uiState.update { it.copy(isLoading = false) }
            onComplete()
        }
    }
}