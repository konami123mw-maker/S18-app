package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen

@Composable
fun AppBottomNavigationBar(
    currentScreen: Screen,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(Screen.Home, S18Strings.get("home", language), Icons.Default.Home),
            Triple(Screen.Search, S18Strings.get("search", language), Icons.Default.Search),
            Triple(Screen.Favorites, S18Strings.get("favorites", language), Icons.Default.Favorite),
            Triple(Screen.Categories, S18Strings.get("categories", language), Icons.Default.Category),
            Triple(Screen.Collections, S18Strings.get("collections", language), Icons.Default.Collections)
        )

        navItems.forEach { (screen, title, icon) ->
            val isSelected = when (screen) {
                is Screen.Home -> currentScreen is Screen.Home
                is Screen.Search -> currentScreen is Screen.Search
                is Screen.Favorites -> currentScreen is Screen.Favorites
                is Screen.Categories -> currentScreen is Screen.Categories
                is Screen.Collections -> currentScreen is Screen.Collections || currentScreen is Screen.CollectionDetails
                else -> false
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00E5FF),
                    selectedTextColor = Color(0xFF00E5FF),
                    indicatorColor = Color(0xFF00E5FF).copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
