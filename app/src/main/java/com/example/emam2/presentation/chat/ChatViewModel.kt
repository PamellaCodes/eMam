package com.example.emam2.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage as OpenAIChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val userInput: String = "",
    val pregnancyWeek: Int = 0,
    val trimester: Int = 1
)

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    private val openAI = OpenAI("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")

    init {
        loadUserData()
        addWelcomeMessage()
    }

    private fun loadUserData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val week = doc.getLong("pregnancyWeek")?.toInt() ?: 0
                val trimester = when {
                    week <= 12 -> 1
                    week <= 26 -> 2
                    else -> 3
                }
                _uiState.update { it.copy(pregnancyWeek = week, trimester = trimester) }
            }
    }

    private fun addWelcomeMessage() {
        val welcome = ChatMessage(
            content = "Halo Bunda! 👋 Aku MAX.AI, asisten nutrisi khusus ibu hamil. Tanyakan apa saja tentang nutrisi & makanan sehat selama kehamilan!",
            isFromUser = false
        )
        _uiState.update { it.copy(messages = listOf(welcome)) }
    }

    fun onInputChanged(text: String) = _uiState.update { it.copy(userInput = text) }

    fun sendMessage() {
        val userText = _uiState.value.userInput.trim()
        if (userText.isBlank() || _uiState.value.isLoading) return

        val userMessage = ChatMessage(content = userText, isFromUser = true)
        _uiState.update {
            it.copy(messages = it.messages + userMessage, userInput = "", isLoading = true)
        }

        viewModelScope.launch {
            try {
                val week = _uiState.value.pregnancyWeek
                val trimester = _uiState.value.trimester

                val systemMessage = OpenAIChatMessage(
                    role = ChatRole.System,
                    content = """
                        Kamu adalah MAX.AI, asisten nutrisi khusus untuk ibu hamil.
                        User sedang hamil minggu ke-$week (Trimester $trimester).
                        Berikan saran dalam Bahasa Indonesia yang ramah dan berbasis ilmu gizi.
                        Fokus pada makanan yang mudah ditemukan di Indonesia.
                        Jika ada pertanyaan budget, berikan rekomendasi yang realistis untuk Indonesia.
                        Jawab singkat dan jelas, maksimal 3-4 paragraf.
                    """.trimIndent()
                )

                val historyMessages = _uiState.value.messages.takeLast(6).map { msg ->
                    OpenAIChatMessage(
                        role = if (msg.isFromUser) ChatRole.User else ChatRole.Assistant,
                        content = msg.content
                    )
                }

                val request = ChatCompletionRequest(
                    model = ModelId("gpt-5.4-mini"),
                    messages = listOf(systemMessage) + historyMessages
                )

                val response = openAI.chatCompletion(request)
                val aiReply = response.choices.firstOrNull()?.message?.content
                    ?: "Maaf, aku tidak bisa memproses pesanmu sekarang."

                val aiMessage = ChatMessage(content = aiReply, isFromUser = false)
                _uiState.update {
                    it.copy(messages = it.messages + aiMessage, isLoading = false)
                }
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "Error: ${e.message}",
                    isFromUser = false
                )
                _uiState.update {
                    it.copy(messages = it.messages + errorMessage, isLoading = false)
                }
            }
        }
    }
}