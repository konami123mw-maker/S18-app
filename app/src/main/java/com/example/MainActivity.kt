package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.Screen
import com.example.ui.ThemeViewModel
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.AppDrawerContent
import com.example.ui.components.NotificationsSheet
import com.example.ui.components.QuickPreviewModal
import com.example.ui.screens.AboutUsScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.BattlesScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CollectionsScreen
import com.example.ui.screens.CompanyScreen
import com.example.ui.screens.DesignerScreen
import com.example.ui.screens.DeviceCompatibilityScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SupportScreen
import com.example.ui.screens.ThemeDetailsScreen
import com.example.ui.screens.UpdatesScreen
import com.example.ui.theme.S18Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ThemeViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val language by viewModel.language.collectAsStateWithLifecycle()
            val currentScreen by viewModel.screen.collectAsStateWithLifecycle()
            val quickPreviewTheme by viewModel.quickPreviewTheme.collectAsStateWithLifecycle()
            val showNotificationsSheet by viewModel.showNotificationsSheet.collectAsStateWithLifecycle()
            val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()
            val context = LocalContext.current

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val coroutineScope = rememberCoroutineScope()

            // Support Arabic RTL / English LTR
            val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                S18Theme(darkTheme = isDarkMode) {
                    BackHandler(enabled = drawerState.isOpen || currentScreen !is Screen.Home) {
                        if (drawerState.isOpen) {
                            coroutineScope.launch { drawerState.close() }
                        } else {
                            viewModel.navigateBack()
                        }
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            AppDrawerContent(
                                currentScreen = currentScreen,
                                language = language,
                                onNavigate = { screen ->
                                    viewModel.navigateTo(screen)
                                },
                                onCloseDrawer = {
                                    coroutineScope.launch { drawerState.close() }
                                }
                            )
                        }
                    ) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = MaterialTheme.colorScheme.background,
                            bottomBar = {
                                // Bottom Navigation Bar is displayed for non-admin full-screen destinations
                                if (currentScreen !is Screen.Admin) {
                                    AppBottomNavigationBar(
                                        currentScreen = currentScreen,
                                        language = language,
                                        onNavigate = { screen ->
                                            viewModel.navigateTo(screen)
                                        }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                Crossfade(
                                    targetState = currentScreen,
                                    label = "screen_crossfade"
                                ) { screen ->
                                    when (screen) {
                                        is Screen.Home -> HomeScreen(
                                            viewModel = viewModel,
                                            onMenuClick = {
                                                coroutineScope.launch {
                                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                                }
                                            }
                                        )
                                        is Screen.Search -> SearchScreen(viewModel = viewModel)
                                        is Screen.Favorites -> FavoritesScreen(viewModel = viewModel)
                                        is Screen.Categories -> CategoriesScreen(viewModel = viewModel)
                                        is Screen.Support, is Screen.SupportContact -> SupportScreen(viewModel = viewModel)
                                        is Screen.AboutUs -> AboutUsScreen(viewModel = viewModel)
                                        is Screen.Settings -> SettingsScreen(viewModel = viewModel)
                                        is Screen.PrivacyPolicy -> PrivacyPolicyScreen(viewModel = viewModel)
                                        is Screen.RateApp -> HomeScreen(viewModel = viewModel)
                                        is Screen.Profile -> HomeScreen(viewModel = viewModel)
                                        is Screen.CompanyDetails -> CompanyScreen(
                                            companySlug = screen.slug,
                                            viewModel = viewModel
                                        )
                                        is Screen.ThemeDetails -> ThemeDetailsScreen(
                                            themeSlug = screen.slug,
                                            viewModel = viewModel
                                        )
                                        is Screen.DesignerDetails -> DesignerScreen(
                                            designerSlug = screen.slug,
                                            viewModel = viewModel
                                        )
                                        is Screen.Updates -> UpdatesScreen(viewModel = viewModel)
                                        is Screen.Battles -> BattlesScreen(viewModel = viewModel)
                                        is Screen.Collections -> CollectionsScreen(viewModel = viewModel)
                                        is Screen.CollectionDetails -> CollectionsScreen(viewModel = viewModel)
                                        is Screen.Admin -> AdminDashboardScreen(
                                            viewModel = viewModel,
                                            initialTab = screen.tab
                                        )
                                        is Screen.DeviceCompatibilityGuide -> DeviceCompatibilityScreen(viewModel = viewModel)
                                    }
                                }

                                // Notifications Bottom Sheet (Section 66)
                                if (showNotificationsSheet) {
                                    NotificationsSheet(
                                        notifications = allNotifications,
                                        language = language,
                                        onDismiss = { viewModel.toggleNotificationsSheet(false) },
                                        onNotificationClick = { notification ->
                                            viewModel.markNotificationAsRead(notification.id)
                                            viewModel.toggleNotificationsSheet(false)
                                            if (notification.relatedThemeSlug.isNotBlank()) {
                                                viewModel.navigateTo(Screen.ThemeDetails(notification.relatedThemeSlug))
                                            } else if (notification.type == "battle") {
                                                viewModel.navigateTo(Screen.Battles)
                                            }
                                        },
                                        onMarkAllRead = { viewModel.markAllNotificationsAsRead() }
                                    )
                                }

                                // Quick Preview Modal
                                quickPreviewTheme?.let { item ->
                                    QuickPreviewModal(
                                        item = item,
                                        language = language,
                                        onDismiss = { viewModel.dismissQuickPreview() },
                                        onViewDetails = {
                                            viewModel.dismissQuickPreview()
                                            viewModel.navigateTo(Screen.ThemeDetails(item.theme.slug))
                                        },
                                        onDownload = {
                                            viewModel.downloadTheme(context, item)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
