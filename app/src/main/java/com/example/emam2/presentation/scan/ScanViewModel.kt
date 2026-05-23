package com.example.emam2.presentation.scan

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.ImagePart
import com.aallam.openai.api.chat.ListContent
import com.aallam.openai.api.chat.TextPart
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.ByteArrayOutputStream

data class NutritionResult(
    val foodName: String = "",
    val calories: Double = 0.0,
    val ironMg: Double = 0.0,
    val folateMcg: Double = 0.0,
    val calciumMg: Double = 0.0,
    val zincMg: Double = 0.0,
    val iodineMcg: Double = 0.0,
    val vitaminDMcg: Double = 0.0,
    val servingGrams: Double = 0.0,
    val aiNote: String = ""
)

data class ScanUiState(
    val isAnalyzing: Boolean = false,
    val capturedBitmap: Bitmap? = null,
    val result: NutritionResult? = null,
    val error: String = "",
    val isSaved: Boolean = false
)

class ScanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState

    private val openAI = OpenAI("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")

    fun analyzeFood(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true, capturedBitmap = bitmap, result = null, error = "") }
            try {
                // Convert bitmap to base64
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

                val prompt = """
                    Analisis gambar makanan ini dan berikan estimasi kandungan nutrisinya.
                    Ini untuk ibu hamil di Indonesia.
                    
                    Jawab HANYA dengan JSON valid berikut (tanpa teks lain):
                    {
                      "foodName": "nama makanan dalam bahasa Indonesia",
                      "servingGrams": 150,
                      "calories": 250.0,
                      "ironMg": 2.5,
                      "folateMcg": 45.0,
                      "calciumMg": 120.0,
                      "zincMg": 1.8,
                      "iodineMcg": 15.0,
                      "vitaminDMcg": 0.5,
                      "aiNote": "catatan singkat manfaat makanan ini untuk ibu hamil"
                    }
                """.trimIndent()

                val request = ChatCompletionRequest(
                    model = ModelId("gpt-4o"),
                    messages = listOf(
                        ChatMessage(
                            role = ChatRole.User,
                            content = listOf(
                                ImagePart("data:image/jpeg;base64,$base64Image"),
                                TextPart(prompt)
                            )
                        )
                    )
                )

                val response = openAI.chatCompletion(request)
                val jsonText = response.choices.firstOrNull()?.message?.content
                    ?.trim()
                    ?.removePrefix("```json")
                    ?.removePrefix("```")
                    ?.removeSuffix("```")
                    ?.trim() ?: throw Exception("Empty response")

                val json = JSONObject(jsonText)
                val result = NutritionResult(
                    foodName = json.getString("foodName"),
                    calories = json.getDouble("calories"),
                    ironMg = json.getDouble("ironMg"),
                    folateMcg = json.getDouble("folateMcg"),
                    calciumMg = json.getDouble("calciumMg"),
                    zincMg = json.getDouble("zincMg"),
                    iodineMcg = json.getDouble("iodineMcg"),
                    vitaminDMcg = json.getDouble("vitaminDMcg"),
                    servingGrams = json.getDouble("servingGrams"),
                    aiNote = json.optString("aiNote", "")
                )
                _uiState.update { it.copy(isAnalyzing = false, result = result) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAnalyzing = false, error = "Gagal menganalisis: ${e.message}") }
            }
        }
    }

    fun saveResult() {
        val result = _uiState.value.result ?: return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val data = hashMapOf(
            "foodName" to result.foodName,
            "calories" to result.calories,
            "ironMg" to result.ironMg,
            "folateMcg" to result.folateMcg,
            "calciumMg" to result.calciumMg,
            "zincMg" to result.zincMg,
            "iodineMcg" to result.iodineMcg,
            "vitaminDMcg" to result.vitaminDMcg,
            "servingGrams" to result.servingGrams,
            "aiNote" to result.aiNote,
            "timestamp" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("nutrition")
            .add(data)
            .addOnSuccessListener {
                _uiState.update { it.copy(isSaved = true) }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(error = "Gagal menyimpan: ${e.message}") }
            }
    }

    fun reset() {
        _uiState.update { ScanUiState() }
    }
}