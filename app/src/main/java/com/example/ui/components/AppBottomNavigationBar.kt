package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AdminTab
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen

/**
 * Modern Mobile-First Floating Dock Bottom Navigation Bar
 * Rounded pill shape, semi-transparent background, subtle cyan glow border
 * Strictly includes: Home, Categories, Favorites, Notifications, Account / Admin
 */
@Composable
fun AppBottomNavigationBar(
    currentScreen: Screen,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit
) {
    val navItems = listOf(
        NavigationTab(
            screen = Screen.Home,
            title = S18Strings.get("home", language),
            icon = Icons.Default.Home,
            isSelected = currentScreen is Screen.Home
        ),
        NavigationTab(
            screen = Screen.Categories,
            title = S18Strings.get("categories", language),
            icon = Icons.Default.Category,
            isSelected = currentScreen is Screen.Categories
        ),
        NavigationTab(
            screen = Screen.Favorites,
            title = S18Strings.get("favorites", language),
            icon = Icons.Default.Favorite,
            isSelected = currentScreen is Screen.Favorites
        ),
        NavigationTab(
            screen = Screen.Updates,
            title = S18Strings.get("notifications", language),
            icon = Icons.Default.Notifications,
            isSelected = currentScreen is Screen.Updates
        ),
        NavigationTab(
            screen = Screen.Admin(AdminTab.DASHBOARD),
            title = if (language == AppLanguage.AR) "الحساب" else "Account",
            icon = Icons.Default.AdminPanelSettings,
            isSelected = currentScreen is Screen.Admin
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color(0x5500E5FF),
                    ambientColor = Color(0x33000000)
                ),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            border = BorderStroke(1.dp, Color(0x3300E5FF))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    FloatingNavItem(
                        tab = item,
                        onClick = { onNavigate(item.screen) }
                    )
                }
            }
        }
    }
}

private data class NavigationTab(
    val screen: Screen,
    val title: String,
    val icon: ImageVector,
    val isSelected: Boolean
)

@Composable
private fun FloatingNavItem(
    tab: NavigationTab,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = if (tab.isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    if (tab.isSelected) Color(0x2400E5FF) else Color.Transparent
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.title,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = tab.title,
            fontSize = 10.sp,
            fontWeight = if (tab.isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
            maxLines = 1
        )
    }
}
