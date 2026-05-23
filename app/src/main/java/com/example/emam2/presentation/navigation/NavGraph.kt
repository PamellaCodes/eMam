package com.example.emam2.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.emam2.presentation.chat.ChatScreen
import com.example.emam2.presentation.forum.ForumScreen
import com.example.emam2.presentation.home.HomeScreen
import com.example.emam2.presentation.home.HomeScreenWithViewModel
import com.example.emam2.presentation.onboarding.IntroSliderScreen
import com.example.emam2.presentation.onboarding.LoginScreen
import com.example.emam2.presentation.onboarding.SignUpScreen
import com.example.emam2.presentation.profile.ProfileScreen
import com.example.emam2.presentation.scan.ScanScreen


sealed class Screen(val route: String) {
    object Intro : Screen("intro")
    object Login : Screen("login") 
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Chat : Screen("chat")
    object Scan : Screen("scan")
    object Forum : Screen("forum")
    object Profile : Screen("profile")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Intro.route,
        modifier = modifier
    ) {
        composable(Screen.Intro.route) {
            IntroSliderScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Intro.route) { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Intro.route) { inclusive = true }
                    }
                },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Home.route) {
            HomeScreenWithViewModel()

        }
        composable(Screen.Scan.route) {
            ScanScreen()
        }
        composable(Screen.Chat.route) {
            ChatScreen()
        }

        composable(Screen.Forum.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Forum - Coming Soon!")
            }
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onSignOut = {
                    navController.navigate(Screen.Intro.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Forum.route) {
            ForumScreen()
        }
    }
}