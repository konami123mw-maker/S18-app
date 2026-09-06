package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import android.net.Uri
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CollectionEntity
import com.example.data.Comment
import com.example.data.Company
import com.example.data.Designer
import com.example.data.SubAdmin
import com.example.data.ThemeBattle
import com.example.data.ThemeEntity
import com.example.data.ThemeFullItem
import com.example.data.ThemePreview
import com.example.data.ThemeVersion
import com.example.ui.AdminTab
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen
import com.example.ui.ThemeViewModel
import com.example.ui.components.ThemeImage
import com.example.util.ThemeImageUtils

@Composable
fun AdminDashboardScreen(
    viewModel: ThemeViewModel,
    initialTab: AdminTab = AdminTab.DASHBOARD,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsStateWithLifecycle()
    val isAuthenticated by viewModel.isAdminAuthenticated.collectAsStateWithLifecycle()
    val isSuperAdmin by viewModel.isSuperAdmin.collectAsStateWithLifecycle()
    val currentSubAdmin by viewModel.currentSubAdmin.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(initialTab) }

    // If sub-admin is logged in, ensure restricted system tabs default to THEMES
    LaunchedEffect(isSuperAdmin, activeTab) {
        if (!isSuperAdmin && (activeTab == AdminTab.BACKUPS || activeTab == AdminTab.SETTINGS)) {
            activeTab = AdminTab.THEMES
        }
    }

    // If not logged in, display modern Admin & Sub-Admin credentials login prompt
    if (!isAuthenticated) {
        AdminLoginView(
            language = language,
            onLogin = { id, pass, onResult ->
                viewModel.loginAdminWithCredentials(id, pass, onResult)
            },
            onCancel = { viewModel.navigateBack() },
            modifier = modifier
        )
        return
    }

    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()
    val companies by viewModel.allCompanies.collectAsStateWithLifecycle()
    val designers by viewModel.allDesigners.collectAsStateWithLifecycle()
    val comments by viewModel.allComments.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen")
    ) {
        // Top Admin Header
        AdminTopHeader(
            activeTab = activeTab,
            language = language,
            isSuperAdmin = isSuperAdmin,
            currentSubAdmin = currentSubAdmin,
            onBack = { viewModel.navigateBack() },
            onLogout = { viewModel.logoutAdmin() }
        )

        // Horizontal Tabs Navigation (Dashboard, Themes, Companies, Designers, Comments, Analytics, Backups, Settings)
        AdminTabsRow(
            activeTab = activeTab,
            language = language,
            isSuperAdmin = isSuperAdmin,
            onSelectTab = { activeTab = it }
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // Active Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (activeTab) {
                AdminTab.DASHBOARD -> AdminDashboardOverview(
                    fullThemes = fullThemes,
                    companies = companies,
                    designers = designers,
                    comments = comments,
                    language = language,
                    onNavigateToTab = { activeTab = it }
                )
                AdminTab.THEMES -> AdminThemesManager(
                    fullThemes = fullThemes,
                    companies = companies,
                    designers = designers,
                    language = language,
                    viewModel = viewModel
                )
                AdminTab.WALLPAPERS -> AdminWallpapersManager(
                    fullThemes = fullThemes,
                    language = language,
                    viewModel = viewModel
                )
                AdminTab.COMPANIES -> AdminCompaniesManager(
                    companies = companies,
                    fullThemes = fullThemes,
                    language = language,
                    viewModel = viewModel
                )
                AdminTab.DESIGNERS -> AdminDesignersManager(
                    designers = designers,
                    language = language,
                    viewModel = viewModel
                )
                AdminTab.COMMENTS -> AdminCommentsManager(
                    comments = comments,
                    fullThemes = fullThemes,
                    language = language,
                    viewModel = viewModel
                )
                AdminTab.ANALYTICS -> AdminAnalyticsView(
                    fullThemes = fullThemes,
                    companies = companies,
                    designers = designers,
                    comments = comments,
                    language = language
                )
                AdminTab.UPDATES -> AdminUpdatesView(
                    fullThemes = fullThemes,
                    language = language
                )
                AdminTab.BACKUPS -> AdminBackupsView(
                    language = language,
                    viewModel = viewModel
                )
                AdminTab.SETTINGS -> AdminSettingsView(
                    language = language,
                    viewModel = viewModel
                )
                AdminTab.SUB_ADMINS -> AdminSubAdminsManager(
                    language = language,
                    viewModel = viewModel
                )
                AdminTab.BATTLES -> AdminBattlesManager(
                    viewModel = viewModel,
                    language = language
                )
                AdminTab.COLLECTIONS -> AdminCollectionsManager(
                    viewModel = viewModel,
                    language = language
                )
            }
        }
    }
}

@Composable
fun AdminLoginView(
    language: AppLanguage,
    onLogin: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(0) } // 0 = Super Admin, 1 = Sub-Admin
    var pin by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0x4000E5FF)
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0x2000E5FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (selectedMode == 0) Icons.Default.Lock else Icons.Default.SupervisorAccount,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Text(
                    text = if (language == AppLanguage.AR) "تسجيل الدخول للوحة التحكم" else "Admin Portal Access",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                // Segmented Tab for Login Type
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(4.dp)
                ) {
                    // Super Admin Tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedMode = 0
                                errorMessage = null
                            },
                        color = if (selectedMode == 0) Color(0xFF00E5FF) else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.AR) "الأدمن الأصلي" else "Super Admin",
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = if (selectedMode == 0) Color(0xFF031024) else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Sub-Admin Tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedMode = 1
                                errorMessage = null
                            },
                        color = if (selectedMode == 1) Color(0xFF00E5FF) else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.AR) "أدمن فرعي" else "Sub-Admin",
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = if (selectedMode == 1) Color(0xFF031024) else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (selectedMode == 0) {
                    Text(
                        text = if (language == AppLanguage.AR) "أدخل رمز PIN للأدمن الأصلي (الرمز الافتراضي: 1234)" else "Enter Super Admin PIN (Default: 1234)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    OutlinedTextField(
                        value = pin,
                        onValueChange = {
                            pin = it
                            errorMessage = null
                        },
                        placeholder = { Text(if (language == AppLanguage.AR) "رمز PIN (مثل: 1234)" else "PIN Code (1234)") },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = if (language == AppLanguage.AR) "سجّل الدخول بالإيميل الوهمي وكلمة المرور المسجلة لك من الأدمن الأصلي" else "Log in with your mock email and password to add themes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text(if (language == AppLanguage.AR) "البريد الإلكتروني الوهمي" else "Mock Email") },
                        placeholder = { Text("e.g. mod1@s18theme.fake") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text(if (language == AppLanguage.AR) "كلمة المرور / الرمز السري" else "Password / PIN") },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorMessage != null) {
                    Surface(
                        color = Color(0x30EF4444),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x60EF4444)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFFF6B6B),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (language == AppLanguage.AR) "إلغاء" else "Cancel")
                    }

                    Button(
                        onClick = {
                            if (selectedMode == 0) {
                                onLogin("admin", pin) { success, err ->
                                    if (!success) {
                                        errorMessage = err ?: if (language == AppLanguage.AR) "رمز PIN غير صحيح" else "Incorrect PIN"
                                    }
                                }
                            } else {
                                onLogin(email, password) { success, err ->
                                    if (!success) {
                                        errorMessage = err ?: if (language == AppLanguage.AR) "فشل تسجيل الدخول" else "Login failed"
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF),
                            contentColor = Color(0xFF031024)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (language == AppLanguage.AR) "تسجيل الدخول" else "Login",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminTopHeader(
    activeTab: AdminTab,
    language: AppLanguage,
    isSuperAdmin: Boolean = true,
    currentSubAdmin: SubAdmin? = null,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "S18_THEME",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = Color(0xFF00E5FF)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSuperAdmin) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isSuperAdmin) {
                                if (language == AppLanguage.AR) "الأدمن الأصلي" else "SUPER ADMIN"
                            } else {
                                if (language == AppLanguage.AR) "أدمن فرعي" else "SUB-ADMIN"
                            },
                            color = if (isSuperAdmin) Color(0xFF00E5FF) else Color(0xFF10B981),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = if (isSuperAdmin) {
                        getTabTitle(activeTab, language)
                    } else {
                        "${currentSubAdmin?.displayName ?: (if (language == AppLanguage.AR) "مشرف فرعي" else "Sub-Admin")} • ${getTabTitle(activeTab, language)}"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(
            onClick = onLogout,
            modifier = Modifier
                .size(38.dp)
                .background(Color(0x20EF4444), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = S18Strings.get("logout", language),
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AdminTabsRow(
    activeTab: AdminTab,
    language: AppLanguage,
    isSuperAdmin: Boolean = true,
    onSelectTab: (AdminTab) -> Unit
) {
    val displayedTabs = remember(isSuperAdmin) {
        if (isSuperAdmin) {
            AdminTab.values().toList()
        } else {
            listOf(
                AdminTab.THEMES,
                AdminTab.WALLPAPERS,
                AdminTab.COMPANIES,
                AdminTab.SUB_ADMINS,
                AdminTab.DASHBOARD,
                AdminTab.COMMENTS,
                AdminTab.BATTLES,
                AdminTab.COLLECTIONS,
                AdminTab.ANALYTICS,
                AdminTab.UPDATES
            )
        }
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(displayedTabs) { tab ->
            val isSelected = tab == activeTab
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectTab(tab) }
                    .border(
                        1.dp,
                        if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = getTabIcon(tab),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = getTabTitle(tab, language),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

fun getTabTitle(tab: AdminTab, language: AppLanguage): String {
    val key = when (tab) {
        AdminTab.DASHBOARD -> "dashboard"
        AdminTab.THEMES -> "themes"
        AdminTab.WALLPAPERS -> "wallpapers"
        AdminTab.COMPANIES -> "companies"
        AdminTab.DESIGNERS -> "designers"
        AdminTab.COMMENTS -> "comments"
        AdminTab.ANALYTICS -> "analytics"
        AdminTab.UPDATES -> "updates"
        AdminTab.BACKUPS -> "backups"
        AdminTab.SETTINGS -> "settings"
        AdminTab.SUB_ADMINS -> "sub_admins"
        AdminTab.BATTLES -> "theme_battles"
        AdminTab.COLLECTIONS -> "collections"
    }
    return S18Strings.get(key, language)
}

fun getTabIcon(tab: AdminTab): ImageVector {
    return when (tab) {
        AdminTab.DASHBOARD -> Icons.Default.Dashboard
        AdminTab.THEMES -> Icons.Default.Palette
        AdminTab.WALLPAPERS -> Icons.Default.PhotoLibrary
        AdminTab.COMPANIES -> Icons.Default.Settings
        AdminTab.DESIGNERS -> Icons.Default.Person
        AdminTab.COMMENTS -> Icons.Default.Comment
        AdminTab.ANALYTICS -> Icons.Default.Analytics
        AdminTab.UPDATES -> Icons.Default.Update
        AdminTab.BACKUPS -> Icons.Default.Backup
        AdminTab.SETTINGS -> Icons.Default.Settings
        AdminTab.SUB_ADMINS -> Icons.Default.SupervisorAccount
        AdminTab.BATTLES -> Icons.Default.SportsKabaddi
        AdminTab.COLLECTIONS -> Icons.Default.Collections
    }
}

// 1. Dashboard Overview
@Composable
fun AdminDashboardOverview(
    fullThemes: List<ThemeFullItem>,
    companies: List<Company>,
    designers: List<Designer>,
    comments: List<Comment>,
    language: AppLanguage,
    onNavigateToTab: (AdminTab) -> Unit
) {
    val totalViews = fullThemes.sumOf { it.theme.views }
    val totalDownloads = fullThemes.sumOf { it.theme.downloads }
    val pendingComments = comments.count { !it.approved }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Top Stats Grid (2x3)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminStatCard(
                        title = S18Strings.get("total_themes", language),
                        value = fullThemes.size.toString(),
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = S18Strings.get("total_companies", language),
                        value = companies.size.toString(),
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminStatCard(
                        title = S18Strings.get("total_views", language),
                        value = totalViews.toString(),
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = S18Strings.get("total_downloads", language),
                        value = totalDownloads.toString(),
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminStatCard(
                        title = S18Strings.get("total_designers", language),
                        value = designers.size.toString(),
                        color = Color(0xFFEC4899),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = S18Strings.get("pending_comments", language),
                        value = pendingComments.toString(),
                        color = if (pendingComments > 0) Color(0xFFEF4444) else Color(0xFF64748B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (language == AppLanguage.AR) "إجراءات سريعة" else "Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onNavigateToTab(AdminTab.THEMES) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(S18Strings.get("add_theme", language), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { onNavigateToTab(AdminTab.COMPANIES) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(S18Strings.get("add_company", language), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = color
            )
        }
    }
}

// 2. Themes Manager (List, Add, Edit, Delete, Duplicate)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminThemesManager(
    fullThemes: List<ThemeFullItem>,
    companies: List<Company>,
    designers: List<Designer>,
    language: AppLanguage,
    viewModel: ThemeViewModel
) {
    var editingThemeItem by remember { mutableStateOf<ThemeFullItem?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    var themeToDelete by remember { mutableStateOf<ThemeEntity?>(null) }

    // Dialog for Add / Edit
    if (isAddingNew || editingThemeItem != null) {
        AdminThemeEditDialog(
            item = editingThemeItem,
            companies = companies,
            designers = designers,
            language = language,
            onDismiss = {
                isAddingNew = false
                editingThemeItem = null
            },
            onSave = { theme, version, previews ->
                viewModel.saveTheme(theme, version, previews) {
                    isAddingNew = false
                    editingThemeItem = null
                }
            }
        )
    }

    // Safe Delete Confirmation Dialog
    if (themeToDelete != null) {
        AlertDialog(
            onDismissRequest = { themeToDelete = null },
            title = { Text(S18Strings.get("confirm_delete_theme", language)) },
            text = { Text(S18Strings.get("action_cannot_undone", language)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTheme(themeToDelete!!) {
                            themeToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(S18Strings.get("delete", language))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { themeToDelete = null }) {
                    Text(S18Strings.get("cancel", language))
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${fullThemes.size} ${S18Strings.get("themes", language)}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = { isAddingNew = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(S18Strings.get("add_theme", language), fontWeight = FontWeight.Bold)
                }
            }
        }

        items(fullThemes) { item ->
            val theme = item.theme
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0D1527))
                        ) {
                            ThemeImage(
                                imageUrl = theme.coverImageUrl.ifEmpty { "theme_preview_liquid" },
                                modifier = Modifier.fillMaxSize(),
                                contentDescription = theme.name
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = theme.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (theme.published) Color(0x2010B981) else Color(0x20F59E0B)
                                ) {
                                    Text(
                                        text = if (theme.published) S18Strings.get("published", language) else S18Strings.get("draft", language),
                                        color = if (theme.published) Color(0xFF10B981) else Color(0xFFF59E0B),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${item.company.name} · v${item.latestVersion?.version ?: "1.0"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }

                    // Action Icons: Edit, Duplicate, Delete
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { editingThemeItem = item },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = { viewModel.duplicateTheme(theme.id) {} },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = { themeToDelete = theme },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// Dialog for Add/Edit Theme
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminThemeEditDialog(
    item: ThemeFullItem?,
    companies: List<Company>,
    designers: List<Designer>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (ThemeEntity, ThemeVersion?, List<ThemePreview>?) -> Unit
) {
    var name by remember { mutableStateOf(item?.theme?.name ?: "") }
    var slug by remember { mutableStateOf(item?.theme?.slug ?: "") }
    var description by remember { mutableStateOf(item?.theme?.description ?: "") }
    var coverUrl by remember { mutableStateOf(item?.theme?.coverImageUrl ?: "theme_preview_liquid") }
    var previewUrls by remember {
        mutableStateOf(
            if (!item?.previews.isNullOrEmpty()) item!!.previews.map { it.imageUrl }
            else listOf(coverUrl, "theme_preview_liquid")
        )
    }
    var tags by remember { mutableStateOf(item?.theme?.tags ?: "AMOLED,Minimal") }
    var isFeatured by remember { mutableStateOf(item?.theme?.featured ?: false) }
    var isPublished by remember { mutableStateOf(item?.theme?.published ?: true) }

    var selectedCompanyId by remember { mutableStateOf(item?.theme?.companyId ?: companies.firstOrNull()?.id ?: 1L) }
    var selectedDesignerId by remember { mutableStateOf(item?.theme?.designerId ?: designers.firstOrNull()?.id ?: 1L) }

    var versionNum by remember { mutableStateOf(item?.latestVersion?.version ?: "1.0") }
    var downloadUrl by remember { mutableStateOf(item?.latestVersion?.downloadUrl ?: "https://t.me/s18theme") }
    var changelog by remember { mutableStateOf(item?.latestVersion?.changelog ?: "Initial release") }

    val context = LocalContext.current
    var customPreviewUrlInput by remember { mutableStateOf("") }
    var showAddUrlInput by remember { mutableStateOf(false) }

    // Upload mode: 0 = Upload file directly from device, 1 = Remote URL link
    var uploadMode by remember {
        mutableStateOf(if (downloadUrl.startsWith("/") || downloadUrl.startsWith("file://") || downloadUrl.startsWith("content://")) 0 else 1)
    }
    var localFileName by remember { mutableStateOf(if (uploadMode == 0 && downloadUrl.isNotBlank()) downloadUrl.substringAfterLast('/') else "") }
    var localFileSizeText by remember { mutableStateOf("") }

    // Direct Device File Picker for theme package (.zip, .mtz, .hwt, etc.)
    val themeFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val (persistedPath, sizeBytes) = ThemeImageUtils.persistThemeFile(context, it)
            val origName = ThemeImageUtils.getFileNameFromUri(context, it)
            downloadUrl = persistedPath
            localFileName = origName
            localFileSizeText = if (sizeBytes > 1024 * 1024) {
                String.format("%.1f MB", sizeBytes / (1024.0 * 1024.0))
            } else {
                String.format("%d KB", sizeBytes / 1024)
            }
        }
    }

    // Direct Device Gallery Pickers with persistent internal storage copying
    val coverGalleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val persistentUri = ThemeImageUtils.persistImageUri(context, it)
            coverUrl = persistentUri
            if (!previewUrls.contains(persistentUri)) {
                previewUrls = listOf(persistentUri) + previewUrls
            }
        }
    }

    val previewsGalleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val persistentUris = uris.map { ThemeImageUtils.persistImageUri(context, it) }
            previewUrls = (previewUrls + persistentUris).distinct()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (item == null) S18Strings.get("add_theme", language) else S18Strings.get("edit_theme", language))
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (item == null) {
                                slug = it.lowercase().replace(" ", "-").replace("[^a-z0-9-]".toRegex(), "")
                            }
                        },
                        label = { Text("Theme Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = slug,
                        onValueChange = { slug = it },
                        label = { Text("Slug") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    // Company Selection
                    Text("Company", style = MaterialTheme.typography.labelSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(companies) { comp ->
                            FilterChip(
                                title = comp.name,
                                isSelected = comp.id == selectedCompanyId,
                                onClick = { selectedCompanyId = comp.id }
                            )
                        }
                    }
                }
                item {
                    // Designer Selection
                    Text("Designer", style = MaterialTheme.typography.labelSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(designers) { des ->
                            FilterChip(
                                title = des.name,
                                isSelected = des.id == selectedDesignerId,
                                onClick = { selectedDesignerId = des.id }
                            )
                        }
                    }
                }
                item {
                    // Cover Image Section with Device Gallery Selection
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("غلاف الثيم (Cover Image - 2640×1200)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(55.dp)
                                    .aspectRatio(1200f / 2640f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0B1019))
                            ) {
                                ThemeImage(
                                    imageUrl = coverUrl,
                                    modifier = Modifier.fillMaxSize(),
                                    contentDescription = "Cover"
                                )
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = { coverGalleryPicker.launch("image/*") },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1E293B),
                                        contentColor = Color(0xFF00E5FF)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6000E5FF)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("اختيار من معرض الجهاز", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedTextField(
                                    value = coverUrl,
                                    onValueChange = { coverUrl = it },
                                    label = { Text("أو رابط / اسم الصورة", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
                item {
                    // Previews Gallery Section (2640x1200 Device Screenshots)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "صور المعاينة الاستعراضية (${previewUrls.size})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { showAddUrlInput = !showAddUrlInput },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6038BDF8)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ رابط URL", fontSize = 11.sp, color = Color(0xFF38BDF8))
                                }

                                OutlinedButton(
                                    onClick = { previewsGalleryPicker.launch("image/*") },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6038BDF8)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ صور من الجهاز", fontSize = 11.sp, color = Color(0xFF38BDF8))
                                }
                            }
                        }

                        if (showAddUrlInput) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = customPreviewUrlInput,
                                    onValueChange = { customPreviewUrlInput = it },
                                    placeholder = { Text("https://... أو اسم ملف المعاينة", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        val trimmed = customPreviewUrlInput.trim()
                                        if (trimmed.isNotEmpty()) {
                                            if (!previewUrls.contains(trimmed)) {
                                                previewUrls = previewUrls + trimmed
                                            }
                                            customPreviewUrlInput = ""
                                            showAddUrlInput = false
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
                                ) {
                                    Text("إضافة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (previewUrls.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(previewUrls) { pUrl ->
                                    Box(
                                        modifier = Modifier
                                            .width(60.dp)
                                            .aspectRatio(1200f / 2640f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0B1019))
                                    ) {
                                        ThemeImage(
                                            imageUrl = pUrl,
                                            modifier = Modifier.fillMaxSize(),
                                            contentDescription = "Preview"
                                        )

                                        IconButton(
                                            onClick = {
                                                if (previewUrls.size > 1) {
                                                    previewUrls = previewUrls.filter { it != pUrl }
                                                }
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(22.dp)
                                                .background(Color(0xCCEF4444), CircleShape)
                                        ) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(13.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    // Theme File Package Section: 2 clear options (Direct Device Upload vs External Link)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.AR) "ملف الثيم للتحميل" else "Theme Package File",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF00E5FF)
                        )

                        // Segmented Mode Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { uploadMode = 0 },
                                shape = RoundedCornerShape(10.dp),
                                color = if (uploadMode == 0) Color(0xFF00E5FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.AR) "📁 رفع من الجهاز" else "📁 Device Upload",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uploadMode == 0) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { uploadMode = 1 },
                                shape = RoundedCornerShape(10.dp),
                                color = if (uploadMode == 1) Color(0xFF00E5FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.AR) "🔗 رابط خارجي" else "🔗 Remote URL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uploadMode == 1) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                                )
                            }
                        }

                        if (uploadMode == 0) {
                            // Device File Picker Card
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4000E5FF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (downloadUrl.isNotBlank() && (downloadUrl.startsWith("/") || downloadUrl.startsWith("file://") || downloadUrl.startsWith("content://"))) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = localFileName.ifBlank { "تم اختيار ملف الثيم بنجاح" },
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = Color.White,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                if (localFileSizeText.isNotBlank()) {
                                                    Text(text = "الحجم: $localFileSizeText", fontSize = 11.sp, color = Color(0xFF00E5FF))
                                                }
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = if (language == AppLanguage.AR)
                                                "ارفع ملف الثيم (.zip, .mtz, .hwt, .theme) من ذاكرة جهازك مباشرة"
                                            else
                                                "Upload theme file (.zip, .mtz, etc.) directly from your device storage",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            themeFilePicker.launch(arrayOf("*/*"))
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (downloadUrl.isNotBlank() && (downloadUrl.startsWith("/") || downloadUrl.startsWith("file://")))
                                                (if (language == AppLanguage.AR) "تغيير الملف المحدد" else "Change Selected File")
                                            else
                                                (if (language == AppLanguage.AR) "اختيار ملف الثيم من الجهاز" else "Select File From Device"),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            // Remote URL field
                            OutlinedTextField(
                                value = downloadUrl,
                                onValueChange = { downloadUrl = it },
                                label = { Text(if (language == AppLanguage.AR) "رابط التحميل الخارجي للملف (External URL)" else "External Download URL") },
                                placeholder = { Text("https://example.com/theme.zip") },
                                supportingText = {
                                    Text(
                                        text = "يدعم روابط التليجرام، GitHub Releases، Google Drive أو روابط السيرفرات المباشرة",
                                        fontSize = 10.sp,
                                        color = Color(0xFF00E5FF)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = versionNum,
                        onValueChange = { versionNum = it },
                        label = { Text("Version") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = changelog,
                        onValueChange = { changelog = it },
                        label = { Text("What's New / Changelog") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Tags (comma-separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Featured")
                        Switch(
                            checked = isFeatured,
                            onCheckedChange = { isFeatured = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Published (Uncheck for Draft)")
                        Switch(
                            checked = isPublished,
                            onCheckedChange = { isPublished = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(context, if (language == AppLanguage.AR) "يرجى إدخال اسم الثيم أولاً" else "Please enter theme name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val validCompanyId = if (selectedCompanyId > 0 && companies.any { it.id == selectedCompanyId }) {
                        selectedCompanyId
                    } else {
                        companies.firstOrNull()?.id ?: 1L
                    }
                    val validDesignerId = if (selectedDesignerId > 0 && designers.any { it.id == selectedDesignerId }) {
                        selectedDesignerId
                    } else {
                        designers.firstOrNull()?.id ?: 1L
                    }
                    val safeSlug = slug.trim().ifBlank {
                        name.trim().lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifBlank { "theme-${System.currentTimeMillis() % 10000}" }
                    }
                    val themeEntity = ThemeEntity(
                        id = item?.theme?.id ?: 0L,
                        companyId = validCompanyId,
                        designerId = validDesignerId,
                        name = name.trim(),
                        slug = safeSlug,
                        description = description.trim().ifBlank { "High quality custom theme for your phone." },
                        coverImageUrl = coverUrl.trim().ifBlank { "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800" },
                        tags = tags.trim(),
                        featured = isFeatured,
                        published = isPublished,
                        views = item?.theme?.views ?: 0,
                        downloads = item?.theme?.downloads ?: 0
                    )
                    val versionEntity = ThemeVersion(
                        id = item?.latestVersion?.id ?: 0L,
                        themeId = item?.theme?.id ?: 0L,
                        version = versionNum.trim().ifBlank { "1.0" },
                        downloadUrl = downloadUrl.trim().ifBlank { "https://t.me/s18theme" },
                        changelog = changelog.trim().ifBlank { "Initial release" },
                        releaseDate = "September 2026",
                        published = isPublished
                    )
                    val previews = if (previewUrls.isNotEmpty()) {
                        previewUrls.mapIndexed { index, url ->
                            ThemePreview(
                                themeId = item?.theme?.id ?: 0L,
                                imageUrl = url.trim(),
                                sortOrder = index + 1
                            )
                        }
                    } else {
                        listOf(
                            ThemePreview(
                                themeId = item?.theme?.id ?: 0L,
                                imageUrl = coverUrl.trim().ifBlank { "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800" },
                                sortOrder = 1
                            )
                        )
                    }
                    try {
                        onSave(themeEntity, versionEntity, previews)
                    } catch (e: Exception) {
                        Toast.makeText(context, "فشل حفظ الثيم: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
            ) {
                Text(if (isPublished) S18Strings.get("publish_now", language) else S18Strings.get("save_draft", language), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(S18Strings.get("cancel", language))
            }
        }
    )
}

// 3. Companies Manager with Delete Protection (Specs #76)
@Composable
fun AdminCompaniesManager(
    companies: List<Company>,
    fullThemes: List<ThemeFullItem>,
    language: AppLanguage,
    viewModel: ThemeViewModel
) {
    val context = LocalContext.current
    var editingCompany by remember { mutableStateOf<Company?>(null) }
    var isAdding by remember { mutableStateOf(false) }

    if (isAdding || editingCompany != null) {
        var compName by remember { mutableStateOf(editingCompany?.name ?: "") }
        var compSlug by remember { mutableStateOf(editingCompany?.slug ?: "") }
        var compDesc by remember { mutableStateOf(editingCompany?.description ?: "") }
        var compLogoUrl by remember { mutableStateOf(editingCompany?.logoUrl ?: "") }

        val logoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                val persisted = ThemeImageUtils.persistCompanyLogo(context, uri)
                compLogoUrl = persisted
            }
        }

        AlertDialog(
            onDismissRequest = {
                isAdding = false
                editingCompany = null
            },
            title = {
                Text(
                    text = if (editingCompany == null)
                        (if (language == AppLanguage.AR) "إضافة شركة جديدة" else "Add New Company")
                    else
                        (if (language == AppLanguage.AR) "تعديل بيانات الشركة" else "Edit Company"),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        // Company Logo Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A))
                                    .border(2.dp, Color(0xFF00E5FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (compLogoUrl.isNotBlank()) {
                                    ThemeImage(
                                        imageUrl = compLogoUrl,
                                        contentDescription = "Logo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = compName.take(2).uppercase().ifBlank { "CO" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color(0xFF00E5FF)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        logoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (language == AppLanguage.AR) "اختيار لوجو من المعرض" else "Pick Logo", fontSize = 12.sp)
                                }

                                if (compLogoUrl.isNotBlank()) {
                                    IconButton(onClick = { compLogoUrl = "" }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove logo", tint = Color(0xFFEF4444))
                                    }
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = compName,
                            onValueChange = {
                                compName = it
                                if (editingCompany == null) {
                                    compSlug = it.lowercase().replace(" ", "-").replace(Regex("[^a-z0-9\\-]"), "")
                                }
                            },
                            label = { Text(if (language == AppLanguage.AR) "اسم الشركة" else "Company Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = compDesc,
                            onValueChange = { compDesc = it },
                            label = { Text(if (language == AppLanguage.AR) "وصف الشركة" else "Description") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = compLogoUrl,
                            onValueChange = { compLogoUrl = it },
                            label = { Text(if (language == AppLanguage.AR) "مسار / رابط اللوجو (اختياري)" else "Logo Path / URL") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = compSlug,
                            onValueChange = { compSlug = it },
                            label = { Text("Slug") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (compName.isNotBlank() && compSlug.isNotBlank()) {
                            val c = Company(
                                id = editingCompany?.id ?: 0L,
                                name = compName.trim(),
                                slug = compSlug.trim(),
                                description = compDesc.trim(),
                                logoUrl = compLogoUrl.trim()
                            )
                            viewModel.saveCompany(c) {
                                isAdding = false
                                editingCompany = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
                ) {
                    Text(if (language == AppLanguage.AR) "حفظ الشركة" else "Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    isAdding = false
                    editingCompany = null
                }) {
                    Text(S18Strings.get("cancel", language))
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.AR) "${companies.size} شركة مسجلة" else "${companies.size} Companies",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = { isAdding = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(S18Strings.get("add_company", language), fontWeight = FontWeight.Bold)
                }
            }
        }

        items(companies) { comp ->
            val themeCount = fullThemes.count { it.theme.companyId == comp.id }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Logo Thumbnail
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!comp.logoUrl.isNullOrBlank()) {
                                ThemeImage(
                                    imageUrl = comp.logoUrl,
                                    contentDescription = comp.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = comp.name.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF00E5FF)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = comp.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (comp.description.isNotBlank()) {
                                Text(
                                    text = comp.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "$themeCount Themes · /${comp.slug}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { editingCompany = comp }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF00E5FF))
                        }
                        IconButton(
                            onClick = {
                                viewModel.deleteCompany(comp) { ok, err ->
                                    if (!ok) {
                                        Toast.makeText(context, err ?: "Cannot delete company with themes", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}

// 4. Designers Manager
@Composable
fun AdminDesignersManager(
    designers: List<Designer>,
    language: AppLanguage,
    viewModel: ThemeViewModel
) {
    var editingDesigner by remember { mutableStateOf<Designer?>(null) }
    var isAdding by remember { mutableStateOf(false) }

    if (isAdding || editingDesigner != null) {
        var dName by remember { mutableStateOf(editingDesigner?.name ?: "") }
        var dBio by remember { mutableStateOf(editingDesigner?.bio ?: "") }
        var dTelegram by remember { mutableStateOf(editingDesigner?.telegramUrl ?: "") }
        var dTikTok by remember { mutableStateOf(editingDesigner?.tiktokUrl ?: "") }

        AlertDialog(
            onDismissRequest = {
                isAdding = false
                editingDesigner = null
            },
            title = { Text(if (editingDesigner == null) S18Strings.get("add_designer", language) else "Edit Designer") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = dName, onValueChange = { dName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dBio, onValueChange = { dBio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dTelegram, onValueChange = { dTelegram = it }, label = { Text("Telegram URL") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dTikTok, onValueChange = { dTikTok = it }, label = { Text("TikTok URL") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (dName.isNotBlank()) {
                            val slug = dName.lowercase().replace(" ", "-")
                            val des = Designer(
                                id = editingDesigner?.id ?: 0L,
                                name = dName.trim(),
                                slug = slug,
                                bio = dBio.trim(),
                                telegramUrl = dTelegram.trim(),
                                tiktokUrl = dTikTok.trim()
                            )
                            viewModel.saveDesigner(des) {
                                isAdding = false
                                editingDesigner = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    isAdding = false
                    editingDesigner = null
                }) {
                    Text(S18Strings.get("cancel", language))
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${designers.size} Designers",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = { isAdding = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(S18Strings.get("add_designer", language), fontWeight = FontWeight.Bold)
                }
            }
        }

        items(designers) { designer ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = designer.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = designer.telegramUrl.ifEmpty { "@${designer.slug}" },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF00E5FF)
                        )
                    }

                    Row {
                        IconButton(onClick = { editingDesigner = designer }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF00E5FF))
                        }
                        IconButton(onClick = { viewModel.deleteDesigner(designer) {} }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}

// 5. Comments Moderation Manager
@Composable
fun AdminCommentsManager(
    comments: List<Comment>,
    fullThemes: List<ThemeFullItem>,
    language: AppLanguage,
    viewModel: ThemeViewModel
) {
    var filterPendingOnly by remember { mutableStateOf(false) }

    val filtered = if (filterPendingOnly) comments.filter { !it.approved } else comments
    val themeMap = remember(fullThemes) { fullThemes.associateBy { it.theme.id } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filtered.size} Comments",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                FilterChip(
                    title = S18Strings.get("pending_comments", language),
                    isSelected = filterPendingOnly,
                    onClick = { filterPendingOnly = !filterPendingOnly }
                )
            }
        }

        if (filtered.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No comments to moderate", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(filtered) { comment ->
                val themeName = themeMap[comment.themeId]?.theme?.name ?: "Theme #${comment.themeId}"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = comment.nickname,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF00E5FF)
                                )
                                Text(
                                    text = "on $themeName",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (comment.approved) Color(0x2010B981) else Color(0x20F59E0B)
                            ) {
                                Text(
                                    text = if (comment.approved) "Approved" else "Pending",
                                    color = if (comment.approved) Color(0xFF10B981) else Color(0xFFF59E0B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = comment.comment,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!comment.approved) {
                                Button(
                                    onClick = { viewModel.approveComment(comment) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(S18Strings.get("approve", language), fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.hideComment(comment) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(S18Strings.get("hide", language), fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            IconButton(
                                onClick = { viewModel.deleteComment(comment) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// 6. Analytics (Strictly Admin only)
@Composable
fun AdminAnalyticsView(
    fullThemes: List<ThemeFullItem>,
    companies: List<Company>,
    designers: List<Designer>,
    comments: List<Comment>,
    language: AppLanguage
) {
    val totalViews = fullThemes.sumOf { it.theme.views }
    val totalDownloads = fullThemes.sumOf { it.theme.downloads }
    val publishedCount = fullThemes.count { it.theme.published }
    val draftCount = fullThemes.count { !it.theme.published }

    val topViewed = remember(fullThemes) { fullThemes.sortedByDescending { it.theme.views }.take(5) }
    val topDownloaded = remember(fullThemes) { fullThemes.sortedByDescending { it.theme.downloads }.take(5) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = S18Strings.get("analytics", language),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Strictly Admin Protected Metrics",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF10B981)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(title = "Published Themes", value = publishedCount.toString(), color = Color(0xFF10B981), modifier = Modifier.weight(1f))
                AdminStatCard(title = "Draft Themes", value = draftCount.toString(), color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(title = "Total Views", value = totalViews.toString(), color = Color(0xFF00E5FF), modifier = Modifier.weight(1f))
                AdminStatCard(title = "Total Downloads", value = totalDownloads.toString(), color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
            }
        }

        // Top 5 Most Viewed Themes
        item {
            Text(
                text = "Most Viewed Themes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    topViewed.forEachIndexed { idx, t ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${idx + 1}. ${t.theme.name}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                            Text(text = "${t.theme.views} views", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Top 5 Most Downloaded Themes
        item {
            Text(
                text = "Most Downloaded Themes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    topDownloaded.forEachIndexed { idx, t ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${idx + 1}. ${t.theme.name}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                            Text(text = "${t.theme.downloads} downloads", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// 7. Admin Updates View
@Composable
fun AdminUpdatesView(
    fullThemes: List<ThemeFullItem>,
    language: AppLanguage
) {
    val updated = remember(fullThemes) { fullThemes.sortedByDescending { it.theme.updatedAt } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Admin Update Center",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tracking all modified themes and version deployments",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(updated) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = item.theme.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Latest Version: ${item.latestVersion?.version ?: "1.0"}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF00E5FF))
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0x2010B981)) {
                        Text(text = "Active", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }
    }
}

// 8. Backups & JSON Export
@Composable
fun AdminBackupsView(
    language: AppLanguage,
    viewModel: ThemeViewModel
) {
    val context = LocalContext.current
    var exportedJson by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = S18Strings.get("backups", language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Create secure exports of all database entities and records.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Button(
                onClick = {
                    viewModel.exportBackup { json ->
                        exportedJson = json
                        Toast.makeText(context, S18Strings.get("backup_created", language), Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
            ) {
                Icon(imageVector = Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(S18Strings.get("export_backup", language), fontWeight = FontWeight.Bold)
            }
        }

        if (exportedJson != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Exported JSON Preview", fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                            IconButton(
                                onClick = {
                                    val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clip.setPrimaryClip(ClipData.newPlainText("Backup JSON", exportedJson))
                                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = exportedJson!!.take(500) + if (exportedJson!!.length > 500) "\n... [Remaining data truncated in preview]" else "",
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// 8.5 Admin Wallpapers Manager
@Composable
fun AdminWallpapersManager(
    fullThemes: List<ThemeFullItem>,
    language: AppLanguage,
    viewModel: ThemeViewModel
) {
    val context = LocalContext.current
    var showAddWallpaperDialog by remember { mutableStateOf(false) }
    var wallpaperName by remember { mutableStateOf("") }
    var wallpaperUrl by remember { mutableStateOf("") }
    var wallpaperTags by remember { mutableStateOf("4K, Minimal, Cyber") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val localPath = ThemeImageUtils.persistImageUri(context, uri)
            wallpaperUrl = localPath
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = S18Strings.get("wallpapers", language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = S18Strings.get("publish_wallpaper", language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showAddWallpaperDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(S18Strings.get("publish_wallpaper", language), fontWeight = FontWeight.Bold)
            }
        }

        // List of themes serving as wallpapers or dedicated wallpaper cards
        val wallpaperThemes = fullThemes.filter { it.theme.tags.contains("wallpaper", ignoreCase = true) || it.theme.tags.contains("خلفية", ignoreCase = true) }

        if (wallpaperThemes.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "لا توجد خلفيات منشورة حالياً",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اضغط على زر نشر خلفية جديدة لرفع خلفيات فائقة الجودة لشاشات الهواتف.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(wallpaperThemes) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF151B28))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp, 80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0B1019))
                            ) {
                                ThemeImage(
                                    imageUrl = item.theme.coverImageUrl,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    contentDescription = item.theme.name
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.theme.name,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = item.theme.tags,
                                    fontSize = 12.sp,
                                    color = Color(0xFF00E5FF)
                                )
                                Text(
                                    text = "Downloads: ${item.theme.downloads}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = {
                                    viewModel.deleteTheme(item.theme.id)
                                    Toast.makeText(context, "Deleted wallpaper", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Wallpaper Dialog
        if (showAddWallpaperDialog) {
            AlertDialog(
                onDismissRequest = { showAddWallpaperDialog = false },
                title = { Text(S18Strings.get("publish_wallpaper", language), fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = wallpaperName,
                            onValueChange = { wallpaperName = it },
                            label = { Text("Wallpaper Title / اسم الخلفية") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = wallpaperTags,
                            onValueChange = { wallpaperTags = it },
                            label = { Text("Tags (e.g. Wallpaper, 4K, Cyber)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = wallpaperUrl,
                            onValueChange = { wallpaperUrl = it },
                            label = { Text("Image URL or Upload from device") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("اختر صورة من المعرض (Photo Picker)")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (wallpaperName.isNotBlank() && wallpaperUrl.isNotBlank()) {
                                try {
                                    val slug = "wallpaper-${System.currentTimeMillis()}"
                                    val newTheme = ThemeEntity(
                                        name = wallpaperName.trim(),
                                        slug = slug,
                                        description = "High quality smartphone wallpaper published via S18 Admin.",
                                        companyId = 1L,
                                        designerId = 1L,
                                        coverImageUrl = wallpaperUrl.trim(),
                                        tags = if (wallpaperTags.contains("wallpaper", ignoreCase = true)) wallpaperTags.trim() else "$wallpaperTags, Wallpaper, خلفية".trim(),
                                        published = true,
                                        featured = true
                                    )
                                    viewModel.saveTheme(theme = newTheme) {
                                        Toast.makeText(context, "تم نشر الخلفية بنجاح!", Toast.LENGTH_SHORT).show()
                                        showAddWallpaperDialog = false
                                        wallpaperName = ""
                                        wallpaperUrl = ""
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "فشل نشر الخلفية: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "يرجى كتابة الاسم وتحديد الصورة أولاً", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
                    ) {
                        Text("نشر الآن", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showAddWallpaperDialog = false }) {
                        Text(S18Strings.get("cancel", language))
                    }
                }
            )
        }
    }
}

// 9. Admin Settings
@Composable
fun AdminSettingsView(
    language: AppLanguage,
    viewModel: ThemeViewModel
) {
    val context = LocalContext.current
    var siteName by remember { mutableStateOf("S18_THEME") }
    var tagline by remember { mutableStateOf("Beautiful Themes. One Place.") }
    var badgeDays by remember { mutableStateOf("7") }
    var telegramUrl by remember { mutableStateOf("https://t.me/s18theme") }
    var tiktokUrl by remember { mutableStateOf("https://tiktok.com/@s18theme") }

    val currentAccentHex by viewModel.accentColorHex.collectAsStateWithLifecycle()
    val currentCardStyle by viewModel.uiCardStyle.collectAsStateWithLifecycle()
    var selectedAccent by remember(currentAccentHex) { mutableStateOf(currentAccentHex) }
    var selectedCardStyle by remember(currentCardStyle) { mutableStateOf(currentCardStyle) }

    val availableAccents = listOf(
        "#00E5FF" to "Cyan Cyber",
        "#7C4DFF" to "Deep Violet",
        "#00E676" to "Matrix Green",
        "#FF9100" to "Solar Amber",
        "#FF1744" to "Crimson Flame"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = S18Strings.get("settings", language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Appearance Customization Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = S18Strings.get("appearance_settings", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF00E5FF)
                    )

                    Text(
                        text = S18Strings.get("accent_theme_color", language),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        availableAccents.forEach { (hex, name) ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            val isChosen = selectedAccent.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isChosen) 3.dp else 1.dp,
                                        color = if (isChosen) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAccent = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChosen) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = name,
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = S18Strings.get("app_design_style", language),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("cyber", "minimal", "glass").forEach { styleKey ->
                            val label = when (styleKey) {
                                "minimal" -> S18Strings.get("style_minimal", language)
                                "glass" -> S18Strings.get("style_glass", language)
                                else -> S18Strings.get("style_cyber", language)
                            }
                            val isSelected = selectedCardStyle == styleKey
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedCardStyle = styleKey },
                                color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = siteName,
                onValueChange = { siteName = it },
                label = { Text("Site Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = tagline,
                onValueChange = { tagline = it },
                label = { Text("Site Tagline") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = badgeDays,
                onValueChange = { badgeDays = it },
                label = { Text("NEW Badge Duration (Days)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = telegramUrl,
                onValueChange = { telegramUrl = it },
                label = { Text("Telegram Link") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = tiktokUrl,
                onValueChange = { tiktokUrl = it },
                label = { Text("TikTok Link") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    viewModel.updateAppAppearance(selectedAccent, selectedCardStyle)
                    Toast.makeText(context, "Settings & Appearance saved successfully!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
            ) {
                Text("Save Settings & Appearance", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 10. Super Admin - Sub-Admins Manager
@Composable
fun AdminSubAdminsManager(
    language: AppLanguage,
    viewModel: ThemeViewModel
) {
    val context = LocalContext.current
    val subAdmins by viewModel.allSubAdmins.collectAsStateWithLifecycle()

    var editingSubAdmin by remember { mutableStateOf<SubAdmin?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    var subAdminToDelete by remember { mutableStateOf<SubAdmin?>(null) }

    // Dialog for Add / Edit Sub-Admin
    if (isAddingNew || editingSubAdmin != null) {
        AdminSubAdminEditDialog(
            subAdmin = editingSubAdmin,
            language = language,
            onDismiss = {
                isAddingNew = false
                editingSubAdmin = null
            },
            onSave = { updated ->
                viewModel.saveSubAdmin(updated) {
                    isAddingNew = false
                    editingSubAdmin = null
                    Toast.makeText(context, if (language == AppLanguage.AR) "تم حفظ بيانات المشرف بنجاح" else "Sub-admin saved successfully", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (subAdminToDelete != null) {
        AlertDialog(
            onDismissRequest = { subAdminToDelete = null },
            title = {
                Text(
                    text = if (language == AppLanguage.AR) "حذف حساب المشرف" else "Delete Sub-Admin",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == AppLanguage.AR)
                        "هل أنت متأكد من حذف حساب المشرف (${subAdminToDelete?.displayName})؟ لن يتمكن من تسجيل الدخول بعد الآن."
                    else
                        "Are you sure you want to delete sub-admin (${subAdminToDelete?.displayName})? They will no longer be able to log in."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        subAdminToDelete?.let { target ->
                            viewModel.deleteSubAdmin(target) {
                                subAdminToDelete = null
                                Toast.makeText(context, if (language == AppLanguage.AR) "تم حذف المشرف" else "Sub-admin deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(S18Strings.get("delete", language), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { subAdminToDelete = null }) {
                    Text(S18Strings.get("cancel", language))
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Super Admin Portal Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F172A)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color(0xFF00E5FF), Color(0xFF38BDF8), Color(0xFF10B981))
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupervisorAccount,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (language == AppLanguage.AR) "إدارة المشرفين والأدمن" else "Admins Management",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = Color.White
                                )
                                Text(
                                    text = if (language == AppLanguage.AR) "إضافة وإدارة المشرفين والصلاحيات وحسابات الدخول" else "Manage sub-admins, roles & credentials",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "ADMIN MANAGEMENT",
                                color = Color(0xFF00E5FF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Stat Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val activeCount = subAdmins.count { it.isActive }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (language == AppLanguage.AR) "إجمالي المشرفين" else "Total Sub-Admins",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "${subAdmins.size}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF00E5FF)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (language == AppLanguage.AR) "الحسابات النشطة" else "Active Accounts",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "$activeCount",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.AR) "قائمة المشرفين الفرعيين" else "Sub-Admins List",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = { isAddingNew = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        contentColor = Color(0xFF031024)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.AR) "إضافة مشرف" else "Add Sub-Admin",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Sub-Admins List
        if (subAdmins.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupervisorAccount,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = if (language == AppLanguage.AR) "لا يوجد مشرفون فرعيون حالياً" else "No sub-admins configured yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(subAdmins) { subAdmin ->
                var showPin by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (subAdmin.isActive) Color(0xFF00E5FF).copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (subAdmin.isActive) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF334155)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = subAdmin.displayName.take(2).uppercase(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = if (subAdmin.isActive) Color(0xFF00E5FF) else Color(0xFF94A3B8)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = subAdmin.displayName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF00E5FF).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = subAdmin.role,
                                                color = Color(0xFF00E5FF),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = subAdmin.email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF38BDF8).copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = if (language == AppLanguage.AR) "حساب داخلي" else "Internal",
                                                color = Color(0xFF38BDF8),
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Active Switch
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (subAdmin.isActive) (if (language == AppLanguage.AR) "نشط" else "Active")
                                    else (if (language == AppLanguage.AR) "معطّل" else "Disabled"),
                                    fontSize = 11.sp,
                                    color = if (subAdmin.isActive) Color(0xFF10B981) else Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold
                                )
                                Switch(
                                    checked = subAdmin.isActive,
                                    onCheckedChange = { viewModel.toggleSubAdminActive(subAdmin) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF10B981),
                                        checkedTrackColor = Color(0xFF10B981).copy(alpha = 0.3f)
                                    )
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                        // Bottom Action Row (PIN display + Copy + Edit + Delete)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.clickable { showPin = !showPin }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (showPin) "كلمة المرور: ${subAdmin.pin}" else "كلمة المرور: ••••",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                                Text(
                                    text = if (showPin) (if (language == AppLanguage.AR) "(إخفاء)" else "(hide)") else (if (language == AppLanguage.AR) "(إظهار)" else "(show)"),
                                    fontSize = 10.sp,
                                    color = Color(0xFF00E5FF)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // Copy credentials button
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        val clip = ClipData.newPlainText(
                                            "SubAdmin Credentials",
                                            "البريد الوهمي: ${subAdmin.email}\nكلمة المرور: ${subAdmin.pin}"
                                        )
                                        clipboard?.setPrimaryClip(clip)
                                        Toast.makeText(
                                            context,
                                            if (language == AppLanguage.AR) "تم نسخ بيانات الدخول (الإيميل والباسورد)" else "Credentials copied",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Credentials",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { editingSubAdmin = subAdmin },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { subAdminToDelete = subAdmin },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
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

// Dialog for Add / Edit Sub-Admin
@Composable
fun AdminSubAdminEditDialog(
    subAdmin: SubAdmin?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (SubAdmin) -> Unit
) {
    var name by remember { mutableStateOf(subAdmin?.displayName ?: "") }
    var email by remember { mutableStateOf(subAdmin?.email ?: "mod1@s18theme.fake") }
    var role by remember { mutableStateOf(subAdmin?.role ?: "Theme Manager (إضافة وإدارة ثيمات)") }
    var pin by remember { mutableStateOf(subAdmin?.pin ?: "2026") }
    var isActive by remember { mutableStateOf(subAdmin?.isActive ?: true) }

    val roleOptions = listOf(
        "Theme Manager (إضافة وإدارة ثيمات)",
        "Content Moderator",
        "Designer Coordinator",
        "Support Specialist"
    )

    val fakeDomainSuggestions = listOf(
        "@s18theme.fake",
        "@s18.fake",
        "@themes.mock",
        "@creator.internal"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (subAdmin == null)
                    (if (language == AppLanguage.AR) "إضافة أدمن فرعي جديد" else "Add New Sub-Admin")
                else
                    (if (language == AppLanguage.AR) "تعديل بيانات المشرف الفرعي" else "Edit Sub-Admin"),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Info banner
                Surface(
                    color = Color(0xFF00E5FF).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
                ) {
                    Text(
                        text = if (language == AppLanguage.AR)
                            "✓ يحصل هذا الأدمن الفرعي على صلاحية إضافة ونشر ثيمات جديدة فور تسجيل دخوله بالحساب الوهمي."
                        else
                            "✓ This sub-admin will be authorized to add and publish new themes upon login.",
                        fontSize = 11.sp,
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (language == AppLanguage.AR) "اسم الأدمن الفرعي" else "Sub-Admin Name") },
                    placeholder = { Text("e.g. أحمد - مصمم الثيمات") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(if (language == AppLanguage.AR) "البريد الإلكتروني الوهمي (Mock Email)" else "Mock Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = {
                            Text(
                                text = if (language == AppLanguage.AR) "إيميل غير حقيقي يُستخدم فقط لتسجيل الدخول للتطبيق" else "Fake email used strictly for app login",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    )

                    // Quick Domain Appenders
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(fakeDomainSuggestions) { domain ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        val prefix = email.substringBefore("@").ifEmpty { "subadmin" }
                                        email = "$prefix$domain"
                                    },
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1E293B)
                            ) {
                                Text(
                                    text = domain,
                                    fontSize = 10.sp,
                                    color = Color(0xFF38BDF8),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text(if (language == AppLanguage.AR) "كلمة المرور الوهمية (Mock Password)" else "Mock Password / PIN") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text(if (language == AppLanguage.AR) "الدور والمسؤولية" else "Role / Responsibility") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Quick role suggestions
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(roleOptions) { r ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { role = r }
                                .border(
                                    1.dp,
                                    if (role == r) Color(0xFF00E5FF) else Color(0x30FFFFFF),
                                    RoundedCornerShape(8.dp)
                                ),
                            shape = RoundedCornerShape(8.dp),
                            color = if (role == r) Color(0xFF00E5FF).copy(alpha = 0.15f) else Color(0xFF1E293B)
                        ) {
                            Text(
                                text = r,
                                fontSize = 11.sp,
                                color = if (role == r) Color(0xFF00E5FF) else Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.AR) "الحساب نشط ومفعل" else "Account is Active",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        val toSave = SubAdmin(
                            id = subAdmin?.id ?: 0L,
                            email = email.trim(),
                            displayName = name.trim(),
                            role = role.trim(),
                            pin = pin.trim().ifEmpty { "2026" },
                            canManageThemes = true,
                            canManageCompanies = false,
                            canModerateComments = true,
                            canManageDesigners = false,
                            isActive = isActive
                        )
                        onSave(toSave)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF031024)
                )
            ) {
                Text(
                    text = if (language == AppLanguage.AR) "حفظ الحساب" else "Save Sub-Admin",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(S18Strings.get("cancel", language))
            }
        }
    )
}

// ----------------------------------------------------
// BATTLES MANAGER (Section 53)
// ----------------------------------------------------
@Composable
fun AdminBattlesManager(
    viewModel: ThemeViewModel,
    language: AppLanguage
) {
    val battles by viewModel.allBattles.collectAsStateWithLifecycle()
    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()
    var editingBattle by remember { mutableStateOf<ThemeBattle?>(null) }
    var isCreating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = S18Strings.get("theme_battles", language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${battles.size} ${if (language == AppLanguage.AR) "معارك مسجلة" else "registered battles"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { isCreating = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D00),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(S18Strings.get("add_battle", language))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (battles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (language == AppLanguage.AR) "لا توجد معارك حالياً" else "No battles found",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(battles) { battle ->
                    val themeA = fullThemes.find { it.theme.id == battle.themeAId }
                    val themeB = fullThemes.find { it.theme.id == battle.themeBId }
                    val title = if (language == AppLanguage.AR && battle.titleAr.isNotBlank()) battle.titleAr else battle.title

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x30FFFFFF))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (battle.active) Color(0x2010B981) else Color(0x20EF4444)
                                    ) {
                                        Text(
                                            text = if (battle.active) "Active" else "Ended",
                                            color = if (battle.active) Color(0xFF10B981) else Color(0xFFEF4444),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "${themeA?.theme?.name ?: "ID:${battle.themeAId}"} (${battle.themeAVotes} votes) VS ${themeB?.theme?.name ?: "ID:${battle.themeBId}"} (${battle.themeBVotes} votes)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF00E5FF)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { editingBattle = battle }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteBattle(battle) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isCreating) {
        AdminBattleEditDialog(
            battle = null,
            fullThemes = fullThemes,
            language = language,
            onDismiss = { isCreating = false },
            onSave = { newBattle ->
                viewModel.saveBattle(newBattle) {
                    isCreating = false
                }
            }
        )
    }

    editingBattle?.let { battle ->
        AdminBattleEditDialog(
            battle = battle,
            fullThemes = fullThemes,
            language = language,
            onDismiss = { editingBattle = null },
            onSave = { updated ->
                viewModel.saveBattle(updated) {
                    editingBattle = null
                }
            }
        )
    }
}

@Composable
fun AdminBattleEditDialog(
    battle: ThemeBattle?,
    fullThemes: List<ThemeFullItem>,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (ThemeBattle) -> Unit
) {
    var title by remember { mutableStateOf(battle?.title ?: "") }
    var titleAr by remember { mutableStateOf(battle?.titleAr ?: "") }
    var themeAId by remember { mutableStateOf(battle?.themeAId ?: fullThemes.firstOrNull()?.theme?.id ?: 1L) }
    var themeBId by remember { mutableStateOf(battle?.themeBId ?: fullThemes.getOrNull(1)?.theme?.id ?: 2L) }
    var isActive by remember { mutableStateOf(battle?.active ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (battle == null) S18Strings.get("add_battle", language) else S18Strings.get("edit_battle", language),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (EN)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = titleAr,
                    onValueChange = { titleAr = it },
                    label = { Text("Title (AR)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Select Theme A ID
                Text(
                    text = "Theme A: ${fullThemes.find { it.theme.id == themeAId }?.theme?.name ?: "ID: $themeAId"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF00E5FF)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(fullThemes) { item ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (themeAId == item.theme.id) Color(0xFF00E5FF) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { themeAId = item.theme.id }
                        ) {
                            Text(
                                text = item.theme.name,
                                color = if (themeAId == item.theme.id) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Select Theme B ID
                Text(
                    text = "Theme B: ${fullThemes.find { it.theme.id == themeBId }?.theme?.name ?: "ID: $themeBId"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF0055)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(fullThemes) { item ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (themeBId == item.theme.id) Color(0xFFFF0055) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { themeBId = item.theme.id }
                        ) {
                            Text(
                                text = item.theme.name,
                                color = if (themeBId == item.theme.id) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Is Active Battle")
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val toSave = battle?.copy(
                            title = title.trim(),
                            titleAr = titleAr.trim(),
                            themeAId = themeAId,
                            themeBId = themeBId,
                            active = isActive
                        ) ?: ThemeBattle(
                            title = title.trim(),
                            titleAr = titleAr.trim(),
                            themeAId = themeAId,
                            themeBId = themeBId,
                            active = isActive,
                            startDate = System.currentTimeMillis(),
                            endDate = System.currentTimeMillis() + 7 * 86400000L
                        )
                        onSave(toSave)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D00),
                    contentColor = Color.White
                )
            ) {
                Text(if (language == AppLanguage.AR) "حفظ المعركة" else "Save Battle", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(S18Strings.get("cancel", language))
            }
        }
    )
}

// ----------------------------------------------------
// COLLECTIONS MANAGER (Section 69)
// ----------------------------------------------------
@Composable
fun AdminCollectionsManager(
    viewModel: ThemeViewModel,
    language: AppLanguage
) {
    val collections by viewModel.allCollections.collectAsStateWithLifecycle()
    var editingCollection by remember { mutableStateOf<CollectionEntity?>(null) }
    var isCreating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = S18Strings.get("collections", language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${collections.size} ${if (language == AppLanguage.AR) "مجموعات" else "collections"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { isCreating = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF031024)
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(S18Strings.get("add_collection", language))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (collections.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (language == AppLanguage.AR) "لا توجد مجموعات مسجلة" else "No collections found",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(collections) { col ->
                    val name = if (language == AppLanguage.AR && col.nameAr.isNotBlank()) col.nameAr else col.name

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x30FFFFFF))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (col.isPublic) Color(0x2010B981) else Color(0x20EF4444)
                                    ) {
                                        Text(
                                            text = if (col.isPublic) "Public" else "Private",
                                            color = if (col.isPublic) Color(0xFF10B981) else Color(0xFFEF4444),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (col.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = col.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { editingCollection = col }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteCollection(col) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isCreating) {
        AdminCollectionEditDialog(
            collection = null,
            language = language,
            onDismiss = { isCreating = false },
            onSave = { newCol ->
                viewModel.saveCollection(newCol) {
                    isCreating = false
                }
            }
        )
    }

    editingCollection?.let { col ->
        AdminCollectionEditDialog(
            collection = col,
            language = language,
            onDismiss = { editingCollection = null },
            onSave = { updated ->
                viewModel.saveCollection(updated) {
                    editingCollection = null
                }
            }
        )
    }
}

@Composable
fun AdminCollectionEditDialog(
    collection: CollectionEntity?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (CollectionEntity) -> Unit
) {
    var name by remember { mutableStateOf(collection?.name ?: "") }
    var nameAr by remember { mutableStateOf(collection?.nameAr ?: "") }
    var slug by remember { mutableStateOf(collection?.slug ?: "") }
    var description by remember { mutableStateOf(collection?.description ?: "") }
    var coverImageUrl by remember { mutableStateOf(collection?.coverImageUrl ?: "") }
    var isPublic by remember { mutableStateOf(collection?.isPublic ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (collection == null) S18Strings.get("add_collection", language) else S18Strings.get("edit_collection", language),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (collection == null) {
                            slug = it.lowercase().replace(" ", "-").replace(Regex("[^a-z0-9-]"), "")
                        }
                    },
                    label = { Text("Collection Name (EN)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nameAr,
                    onValueChange = { nameAr = it },
                    label = { Text("Collection Name (AR)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = slug,
                    onValueChange = { slug = it },
                    label = { Text("Slug") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = coverImageUrl,
                    onValueChange = { coverImageUrl = it },
                    label = { Text("Cover Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Is Public")
                    Switch(checked = isPublic, onCheckedChange = { isPublic = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val toSave = collection?.copy(
                            name = name.trim(),
                            nameAr = nameAr.trim(),
                            slug = slug.trim().ifEmpty { name.lowercase().replace(" ", "-") },
                            description = description.trim(),
                            coverImageUrl = coverImageUrl.trim(),
                            isPublic = isPublic
                        ) ?: CollectionEntity(
                            name = name.trim(),
                            nameAr = nameAr.trim(),
                            slug = slug.trim().ifEmpty { name.lowercase().replace(" ", "-") },
                            description = description.trim(),
                            coverImageUrl = coverImageUrl.trim(),
                            isPublic = isPublic
                        )
                        onSave(toSave)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF031024)
                )
            ) {
                Text(if (language == AppLanguage.AR) "حفظ المجموعة" else "Save Collection", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(S18Strings.get("cancel", language))
            }
        }
    )
}
