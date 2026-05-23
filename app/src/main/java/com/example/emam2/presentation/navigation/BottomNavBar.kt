package com.example.emam2.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emam2.ui.theme.PrimaryColor

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("Tanya AI", Screen.Chat.route, Icons.Filled.Chat, Icons.Outlined.Chat),
    BottomNavItem("Scan", Screen.Scan.route, Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner),
    BottomNavItem("Forum", Screen.Forum.route, Icons.Filled.Forum, Icons.Outlined.Forum),
    BottomNavItem("User", Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    NavigationBar(
        containerColor = androidx.compose.ui.graphics.Color.White
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryColor,
                    selectedTextColor = PrimaryColor,
                    unselectedIconColor = androidx.compose.ui.graphics.Color.Gray,
                    indicatorColor = PrimaryColor.copy(alpha = 0.1f)
                )
            )
        }
    }
}