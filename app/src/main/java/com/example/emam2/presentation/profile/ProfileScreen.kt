package com.example.emam2.presentation.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emam2.ui.theme.AccentColor
import com.example.emam2.ui.theme.PrimaryColor

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onSignOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Keluar?") },
            text = { Text("Kamu akan keluar dari akun ini.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.signOut(onSignOut) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("Keluar") }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) { Text("Batal") }
            }
        )
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryColor)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PrimaryColor
            )
        }

        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(24.dp))

        // Avatar + nama
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AccentColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.fullName.firstOrNull()?.toString() ?: "B",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                uiState.fullName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                uiState.email,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PrimaryColor
            ) {
                Text(
                    "Minggu ${uiState.pregnancyWeek} · Trimester ${uiState.trimester}",
                    fontSize = 13.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info kehamilan
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Data Kehamilan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrimaryColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileInfoRow(
                    icon = Icons.Outlined.PregnantWoman,
                    label = "Usia Kehamilan",
                    value = "${uiState.pregnancyWeek} Minggu"
                )
                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                ProfileInfoRow(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Perkiraan Lahir",
                    value = uiState.estimatedDueDate
                )
                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                ProfileInfoRow(
                    icon = Icons.Outlined.Timeline,
                    label = "Trimester",
                    value = "Trimester ${uiState.trimester}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pengaturan
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Pengaturan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrimaryColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileMenuItem(
                    icon = Icons.Outlined.AccountCircle,
                    title = "Pengaturan Profil",
                    subtitle = "Nama, foto profil",
                    onClick = {}
                )
                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                ProfileMenuItem(
                    icon = Icons.Outlined.Lock,
                    title = "Ubah Kata Sandi",
                    subtitle = "Kelola keamanan akun",
                    onClick = {}
                )
                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                ProfileMenuItem(
                    icon = Icons.Outlined.Language,
                    title = "Bahasa",
                    subtitle = "Bahasa Indonesia",
                    onClick = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Keluar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            ProfileMenuItem(
                icon = Icons.Outlined.Logout,
                title = "Keluar",
                subtitle = "Logout dari akun",
                onClick = { showSignOutDialog = true },
                tint = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: Color = PrimaryColor
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (tint == Color.Red) Color.Red else Color.Black)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}