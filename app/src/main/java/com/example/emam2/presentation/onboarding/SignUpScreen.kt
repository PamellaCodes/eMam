package com.example.emam2.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emam2.R
import com.example.emam2.ui.theme.PrimaryColor

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var pregnancyWeek by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.offset(x = (-12).dp)
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryColor)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = "Logo",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hello mom!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Text(
            text = "Create account to get started",
            fontSize = 14.sp,
            color = PrimaryColor.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Full name
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it; errorMessage = "" },
            placeholder = { Text("Full name", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = PrimaryColor.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Pregnancy age
        OutlinedTextField(
            value = pregnancyWeek,
            onValueChange = { pregnancyWeek = it; errorMessage = "" },
            placeholder = { Text("Masukan usia kehamilan dalam minggu", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = PrimaryColor.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = "" },
            placeholder = { Text("Enter your email", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = PrimaryColor.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = "" },
            placeholder = { Text("Password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = PrimaryColor.copy(alpha = 0.5f)
            )
        )

        // Forgot password
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = {}) {
                Text("Forgot password?", color = PrimaryColor, fontSize = 13.sp)
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sign in button
        Button(
            onClick = {
                if (fullName.isBlank() || email.isBlank() || password.isBlank() || pregnancyWeek.isBlank()) {
                    errorMessage = "Semua box harus diisi"
                    return@Button
                }
                isLoading = true
                com.google.firebase.auth.FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = task.result.user?.uid ?: ""
                            val userData = hashMapOf(
                                "fullName" to fullName,
                                "email" to email,
                                "pregnancyWeek" to (pregnancyWeek.toIntOrNull() ?: 0),
                                "createdAt" to System.currentTimeMillis()
                            )
                            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid)
                                .set(userData)
                                .addOnCompleteListener { firestoreTask ->
                                    isLoading = false
                                    if (firestoreTask.isSuccessful) {
                                        onSignUpSuccess()
                                    } else {
                                        errorMessage = "Gagal menyimpan data, coba lagi"
                                    }
                                }
                        } else {
                            isLoading = false
                            errorMessage = task.exception?.message ?: "Registrasi gagal, coba lagi"
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            shape = RoundedCornerShape(30.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Sign Up", fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Login link
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account? ", color = Color.Gray, fontSize = 14.sp)
            TextButton(
                onClick = onLoginClick,
                contentPadding = PaddingValues(0.dp)
            ){
                Text("Sign In", color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}