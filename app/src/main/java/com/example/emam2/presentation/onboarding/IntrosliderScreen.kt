package com.example.emam2.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emam2.R
import com.example.emam2.ui.theme.PrimaryColor
import com.example.emam2.ui.theme.SecondaryColor

@Composable
fun IntroSliderScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(90.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Gambar onboarding
        Image(
            painter = painterResource(id = R.drawable.onboardingpict),
            contentDescription = "Onboarding",
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Text
        Text(
            text = "Selamat Datang, Bunda!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )

        //Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Pendamping nutrisi ibu hamil, untuk\nindonesian anti stunting!",
            fontSize = 14.sp,
            color = SecondaryColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Tombol Login
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Login", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tombol SignUp
        OutlinedButton(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryColor),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Sign Up", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}