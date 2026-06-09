package com.example.emam2.presentation.onboarding

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.CalendarMonth
import com.example.emam2.ui.theme.AccentColor
import com.example.emam2.ui.theme.GreyColor
import com.example.emam2.ui.theme.PrimaryColor
import com.example.emam2.ui.theme.SecondaryColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@Preview(showBackground = true)
@Composable
fun PreviewStepWelcome() {
    StepWelcome(onNext = {})
}
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    listOf(PrimaryColor, SecondaryColor, AccentColor, GreyColor)
//                )
//            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "eMam",
                style = MaterialTheme.typography.displayMedium,
                color = PrimaryColor,
                fontWeight = FontWeight.Medium,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            //Logo


            // Step indicator
            StepIndicator(currentStep = uiState.step, totalSteps = 3)

            Spacer(modifier = Modifier.height(24.dp))

            // Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (uiState.step) {
                        0 -> StepWelcome(onNext = viewModel::nextStep)
                        1 -> StepPersonalInfo(
                            name = uiState.name,
                            email = uiState.email,
                            onNameChange = viewModel::onNameChanged,
                            onEmailChange = viewModel::onEmailChanged,
                            onNext = {
                                if (uiState.name.isNotBlank()) viewModel.nextStep()
                                else viewModel.onNameChanged("")
                            },
                            onBack = viewModel::prevStep,
                            error = uiState.error
                        )
                        2 -> StepHpht(
                            selectedDate = uiState.hphtDate,
                            onDateSelected = viewModel::onHphtSelected,
                            onSubmit = { viewModel.submit(onOnboardingComplete) },
                            onBack = viewModel::prevStep,
                            isLoading = uiState.isLoading,
                            error = uiState.error
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun StepWelcome(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Selamat Datang, Bunda!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF8B1A1A),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Pendamping nutrisi ibu hamil,untuk indonesia anti stunting!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Mulai", modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(totalSteps) { index ->
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(if (index == currentStep) 32.dp else 16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (index <= currentStep) PrimaryColor
                        else GreyColor.copy(alpha = 0.4f)
                    )
            )
        }
    }
}

@Composable
fun StepPersonalInfo(
    name: String,
    email: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    error: String
) {
    Column {
        Text(
            text = "Data Diri",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF8B1A1A),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Isi nama untuk personalisasi",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nama Lengkap") },
            leadingIcon = { Icon(Icons.Filled.Person, null, tint = Color(0xFF8B1A1A)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = error.isNotEmpty() && name.isBlank(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8B1A1A),
                focusedLabelColor = Color(0xFF8B1A1A)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, null, tint = Color(0xFF8B1A1A)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8B1A1A),
                focusedLabelColor = Color(0xFF8B1A1A)
            )
        )

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error, color = Color.Red, style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF8B1A1A))
            ) { Text("Kembali") }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B1A1A)),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Lanjut") }
        }
    }
}

@Composable
fun StepHpht(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean,
    error: String
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id"))

    val previewWeek = selectedDate?.let {
        val days = ChronoUnit.DAYS.between(it, LocalDate.now()).toInt()
        ((days / 7) + 1).coerceAtLeast(1).coerceAtMost(40)
    }
    val previewDue = selectedDate?.plusDays(280)

    Column {
        Text(
            text = "Usia Kehamilan",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF8B1A1A),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Masukkan Hari Pertama Haid Terakhir (HPHT)",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = {
                val cal = Calendar.getInstance()
                selectedDate?.let { cal.set(it.year, it.monthValue - 1, it.dayOfMonth) }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        onDateSelected(LocalDate.of(year, month + 1, day))
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    datePicker.maxDate = System.currentTimeMillis()
                    show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF8B1A1A))
        ) {
            Icon(Icons.Filled.CalendarMonth, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(selectedDate?.format(dateFormatter) ?: "Pilih tanggal HPHT")
        }

        if (previewWeek != null && previewDue != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF4E8E8)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Usia Kehamilan", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(
                            "$previewWeek Minggu",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF8B1A1A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Perkiraan Lahir", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(
                            previewDue.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id"))),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF8B1A1A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error, color = Color.Red, style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF8B1A1A))
            ) { Text("Kembali") }

            Button(
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                enabled = selectedDate != null && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B1A1A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Mulai")
                }
            }
        }
    }
}