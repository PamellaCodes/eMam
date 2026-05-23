package com.example.emam2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.emam2.presentation.navigation.BottomNavBar
import com.example.emam2.presentation.navigation.NavGraph
import com.example.emam2.presentation.navigation.Screen
import com.example.emam2.ui.theme.EMam2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EMam2Theme {
                val navController = rememberNavController()
                val navBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStack?.destination?.route

                // Sembunyikan bottom nav di halaman onboarding/login/signup
                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Chat.route,
                    Screen.Scan.route,
                    Screen.Forum.route,
                    Screen.Profile.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}