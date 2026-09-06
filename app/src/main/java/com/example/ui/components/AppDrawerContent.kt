package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen

@Composable
fun AppDrawerContent(
    currentScreen: Screen,
    language: AppLanguage,
    onNavigate: (Screen) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current

    ModalDrawerSheet(
        modifier = Modifier.width(310.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 14.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Header: App Logo & Branding
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "S18",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    Column {
                        Text(
                            text = "S18_THEME",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (language == AppLanguage.AR) "بوابة الثيمات والخلفيات" else "Themes & Wallpapers Hub",
                            fontSize = 11.sp,
                            color = Color(0xFF00E5FF)
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            // Primary Navigation Items
            item {
                DrawerNavigationItem(
                    label = S18Strings.get("home", language),
                    icon = Icons.Default.Home,
                    selected = currentScreen is Screen.Home,
                    onClick = {
                        onCloseDrawer()
                        onNavigate(Screen.Home)
                    }
                )
            }

            item {
                DrawerNavigationItem(
                    label = S18Strings.get("favorites", language),
                    icon = Icons.Default.Favorite,
                    selected = currentScreen is Screen.Favorites,
                    onClick = {
                        onCloseDrawer()
                        onNavigate(Screen.Favorites)
                    }
                )
            }

            item {
                DrawerNavigationItem(
                    label = S18Strings.get("categories", language),
                    icon = Icons.Default.Category,
                    selected = currentScreen is Screen.Categories,
                    onClick = {
                        onCloseDrawer()
                        onNavigate(Screen.Categories)
                    }
                )
            }

            item {
                DrawerNavigationItem(
                    label = S18Strings.get("support", language),
                    icon = Icons.Default.ContactSupport,
                    selected = currentScreen is Screen.Support,
                    onClick = {
                        onCloseDrawer()
                        onNavigate(Screen.Support)
                    }
                )
            }

            item {
                DrawerNavigationItem(
                    label = S18Strings.get("installation_guides_all", language),
                    icon = Icons.Default.MenuBook,
                    selected = currentScreen is Screen.DeviceCompatibilityGuide,
                    onClick = {
                        onCloseDrawer()
                        onNavigate(Screen.DeviceCompatibilityGuide)
                    }
                )
            }

            item {
                DrawerNavigationItem(
                    label = S18Strings.get("about_us", language),
                    icon = Icons.Default.Info,
                    selected = currentScreen is Screen.AboutUs,
                    onClick = {
                        onCloseDrawer()
                        onNavigate(Screen.AboutUs)
                    }
                )
            }

            item {
                DrawerNavigationItem(
                    label = S18Strings.get("settings", language),
                    icon = Icons.Default.Settings,
                    selected = currentScreen is Screen.Settings,
                    onClick = {
                        onCloseDrawer()
                        onNavigate(Screen.Settings)
                    }
                )
            }

            item {
                DrawerNavigationItem(
                    label = S18Strings.get("privacy_policy", language),
                    icon = Icons.Default.PrivacyTip,
                    selected = currentScreen is Screen.PrivacyPolicy,
                    onClick = {
                        onCloseDrawer()
                        onNavigate(Screen.PrivacyPolicy)
                    }
                )
            }

            item {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            // Quick App Actions: Rate App & Share App
            item {
                DrawerNavigationItem(
                    label = S18Strings.get("rate_app", language),
                    icon = Icons.Default.Star,
                    selected = false,
                    onClick = {
                        onCloseDrawer()
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, if (language == AppLanguage.AR) "شكراً لتقييمك لتطبيق S18_THEME!" else "Thank you for rating S18_THEME!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            item {
                DrawerNavigationItem(
                    label = S18Strings.get("share_app", language),
                    icon = Icons.Default.Share,
                    selected = false,
                    onClick = {
                        onCloseDrawer()
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "S18_THEME")
                            putExtra(Intent.EXTRA_TEXT, "حمل أجمل الثيمات والخلفيات فائقة الجودة من تطبيق S18_THEME: https://s18theme.dev")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share S18_THEME"))
                    }
                )
            }
        }
    }
}

@Composable
private fun DrawerNavigationItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = Color(0xFF00E5FF).copy(alpha = 0.12f),
            unselectedContainerColor = Color.Transparent,
            selectedTextColor = Color(0xFF00E5FF),
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
