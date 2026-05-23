package com.example.emam2.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emam2.ui.theme.AccentColor
import com.example.emam2.ui.theme.PrimaryColor

@Composable
fun HomeScreen(
    fullName: String,
    pregnancyWeek: Int,
    estimatedDueDate: String,
    trimester: Int,
    totalCalories: Double = 0.0,
    totalIronMg: Double = 0.0,
    totalFolateMcg: Double = 0.0,
    totalCalciumMg: Double = 0.0,
    totalZincMg: Double = 0.0,
    totalIodineMcg: Double = 0.0,
    totalVitaminDMcg: Double = 0.0,
    latestAiNote: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 60.dp)
    ) {
        // Greeting + foto profil
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .border(2.dp, Color.Black, RoundedCornerShape(60.dp))
                .clip(RoundedCornerShape(60.dp))
                .background(AccentColor)
                .padding(start = 12.dp, end = 28.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("Halo, bunda 👋", fontSize = 12.sp, color = Color.Black, lineHeight = 14.sp)
                Text(fullName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card kehamilan
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentColor),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Usia Kehamilan", fontSize = 12.sp, color = PrimaryColor)
                            Text("$pregnancyWeek Minggu", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentColor),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Perkiraan Lahir", fontSize = 12.sp, color = PrimaryColor)
                            Text(estimatedDueDate, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("T1 1-12 Minggu", "T2 13-26 Minggu", "T3 27-40 Minggu")
                        .forEachIndexed { index, label ->
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = if (index + 1 == trimester) Color.White else Color.White.copy(alpha = 0.5f),
                                fontWeight = if (index + 1 == trimester) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { pregnancyWeek / 40f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "Memasuki Minggu ke-$pregnancyWeek dari 40",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // AI Note
        if (latestAiNote.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3F3)),
                border = BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.EventNote,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Catatan AI Asisten Nutrisi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(latestAiNote, fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ringkasan Nutrisi
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Ringkasan Asupan Nutrisi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryColor)
                Text("Hari ini", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                NutrientProgressBar(Icons.Filled.LocalFireDepartment, "Kalori", totalCalories, 2300.0, "kkal")

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NutrientProgressBar(Icons.Filled.Bloodtype, "Zat Besi", totalIronMg, 35.0, "mg")
                        NutrientProgressBar(Icons.Filled.FitnessCenter, "Kalsium", totalCalciumMg, 1200.0, "mg")
                        NutrientProgressBar(Icons.Filled.Water, "Yodium", totalIodineMcg, 220.0, "mcg")
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NutrientProgressBar(Icons.Filled.Eco, "Folat", totalFolateMcg, 600.0, "mcg")
                        NutrientProgressBar(Icons.Filled.Medication, "Zinc", totalZincMg, 11.0, "mg")
                        NutrientProgressBar(Icons.Filled.WbSunny, "Vit D", totalVitaminDMcg, 15.0, "mcg")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun NutrientProgressBar(
    icon: ImageVector,
    label: String,
    current: Double,
    target: Double,
    unit: String
) {
    val progress = (current / target).toFloat().coerceIn(0f, 1f)
    val percent = (progress * 100).toInt()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, fontSize = 11.sp, color = Color.DarkGray)
            }
            Text("$percent%", fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = PrimaryColor,
            trackColor = PrimaryColor.copy(alpha = 0.15f)
        )
        Text(
            "${"%.1f".format(current)}/${"%.0f".format(target)} $unit",
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun HomeScreenWithViewModel(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryColor)
        }
        return
    }

    HomeScreen(
        fullName = uiState.fullName,
        pregnancyWeek = uiState.pregnancyWeek,
        estimatedDueDate = uiState.estimatedDueDate,
        trimester = uiState.trimester,
        totalCalories = uiState.totalCalories,
        totalIronMg = uiState.totalIronMg,
        totalFolateMcg = uiState.totalFolateMcg,
        totalCalciumMg = uiState.totalCalciumMg,
        totalZincMg = uiState.totalZincMg,
        totalIodineMcg = uiState.totalIodineMcg,
        totalVitaminDMcg = uiState.totalVitaminDMcg,
        latestAiNote = uiState.latestAiNote
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    MaterialTheme {
        HomeScreen(
            fullName = "Ratna Sari",
            pregnancyWeek = 18,
            estimatedDueDate = "28 Juli 2026",
            trimester = 2,
            totalCalories = 1530.0,
            totalIronMg = 20.0,
            totalFolateMcg = 430.0,
            totalCalciumMg = 1020.0,
            totalZincMg = 5.0,
            totalIodineMcg = 198.0,
            totalVitaminDMcg = 6.0,
            latestAiNote = "Vit D Kamu Kurang Hari Ini! Coba tambahkan hati ayam atau kacang merah di makan malam ya Bunda."
        )
    }
}