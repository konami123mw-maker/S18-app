package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Company
import com.example.data.ThemeFullItem
import com.example.ui.AdminTab
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen
import com.example.ui.ThemeViewModel
import com.example.ui.components.BrandPillCard
import com.example.ui.components.ThemeCard
import com.example.ui.components.ThemeImage
import com.example.ui.components.ThemeSwitch

/**
 * Mobile-First HomeScreen: Designed strictly for smartphone screens with
 * proportional cards, clean hierarchy, touch-friendly targets, and seamless brand discovery.
 */
@Composable
fun HomeScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {}
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()
    val companies by viewModel.publishedCompanies.collectAsStateWithLifecycle()
    val activeBattles by viewModel.activeBattles.collectAsStateWithLifecycle()
    val publicCollections by viewModel.publicCollections.collectAsStateWithLifecycle()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val selectedMood by viewModel.selectedMood.collectAsStateWithLifecycle()

    var selectedCompanyFilterId by remember { mutableStateOf<Long?>(null) }

    // Filter published themes based on mood and optional company filter
    val publishedThemes = fullThemes.filter { it.theme.published }
        .filter { item ->
            val matchesCompany = selectedCompanyFilterId == null || item.theme.companyId == selectedCompanyFilterId
            val matchesMood = selectedMood == null ||
                item.theme.tags.contains(selectedMood!!, ignoreCase = true) ||
                item.theme.name.contains(selectedMood!!, ignoreCase = true)
            matchesCompany && matchesMood
        }

    val featuredThemes = publishedThemes.filter { it.theme.featured }
    val heroTheme = featuredThemes.firstOrNull() ?: publishedThemes.firstOrNull()
    val trendingThemes = publishedThemes.sortedByDescending { it.theme.views * 2 + it.theme.downloads * 5 }
    val latestThemes = publishedThemes.sortedByDescending { it.theme.createdAt }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Mobile Top Bar
        item {
            MobileTopBar(
                language = language,
                isDarkMode = isDarkMode,
                unreadNotificationsCount = unreadNotificationsCount,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                onNotificationsClick = { viewModel.toggleNotificationsSheet(true) },
                onAdminClick = { viewModel.navigateTo(Screen.Admin(AdminTab.DASHBOARD)) },
                onMenuClick = onMenuClick
            )
        }

        // 2. Mobile Quick Search Pill
        item {
            MobileSearchBar(
                language = language,
                onClick = { viewModel.navigateTo(Screen.Search) }
            )
        }

        // 3. Mobile Shortcuts (4 Quick Actions)
        item {
            MobileFeatureShortcuts(
                language = language,
                activeBattlesCount = activeBattles.size,
                collectionsCount = publicCollections.size,
                onWallpapersClick = { viewModel.navigateTo(Screen.Search) },
                onBattlesClick = { viewModel.navigateTo(Screen.Battles) },
                onCollectionsClick = { viewModel.navigateTo(Screen.Collections) },
                onCategoriesClick = { viewModel.navigateTo(Screen.Categories) }
            )
        }

        // 4. Mobile Featured Hero Card (Compact, 16:9 ratio)
        if (heroTheme != null) {
            item {
                MobileHeroCard(
                    item = heroTheme,
                    language = language,
                    onCardClick = { viewModel.navigateTo(Screen.ThemeDetails(heroTheme.theme.slug)) },
                    onQuickPreviewClick = { viewModel.showQuickPreview(heroTheme) }
                )
            }
        }

        // 5. Brands Rail (الشركات والماركات)
        if (companies.isNotEmpty()) {
            item {
                MobileSectionHeader(
                    title = if (language == AppLanguage.AR) "الشركات والماركات" else "Official Brands",
                    icon = Icons.Default.AutoAwesome,
                    actionText = if (selectedCompanyFilterId != null) {
                        (if (language == AppLanguage.AR) "إلغاء التصفية" else "Reset")
                    } else null,
                    onActionClick = { selectedCompanyFilterId = null }
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(companies) { company ->
                        val themeCount = fullThemes.count { it.theme.companyId == company.id && it.theme.published }
                        val isSelected = selectedCompanyFilterId == company.id
                        BrandPillCard(
                            company = company,
                            themeCount = themeCount,
                            language = language,
                            isSelected = isSelected,
                            onClick = {
                                selectedCompanyFilterId = if (isSelected) null else company.id
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // 6. Mood / Style Filter Chips
        item {
            MobileMoodChips(
                selectedMood = selectedMood,
                language = language,
                onSelectMood = { viewModel.selectMood(it) }
            )
        }

        // 7. Trending Themes (Horizontal Scroll)
        if (trendingThemes.isNotEmpty() && selectedCompanyFilterId == null && selectedMood == null) {
            item {
                MobileSectionHeader(
                    title = S18Strings.get("trending_themes", language),
                    icon = Icons.Default.Whatshot,
                    iconTint = Color(0xFFFF6D00),
                    actionText = if (language == AppLanguage.AR) "عرض الكل" else "See All",
                    onActionClick = { viewModel.navigateTo(Screen.Search) }
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trendingThemes.take(6)) { item ->
                        ThemeCard(
                            item = item,
                            language = language,
                            onCardClick = { viewModel.navigateTo(Screen.ThemeDetails(item.theme.slug)) },
                            onQuickPreviewClick = { viewModel.showQuickPreview(item) },
                            modifier = Modifier.width(165.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // 8. Main 2-Column Mobile Themes Grid
        item {
            val titleText = if (selectedCompanyFilterId != null) {
                val compName = companies.firstOrNull { it.id == selectedCompanyFilterId }?.name ?: ""
                if (language == AppLanguage.AR) "ثيمات $compName (${publishedThemes.size})" else "$compName Themes (${publishedThemes.size})"
            } else {
                if (language == AppLanguage.AR) "أحدث الثيمات المتاحة (${publishedThemes.size})" else "Latest Themes (${publishedThemes.size})"
            }

            MobileSectionHeader(
                title = titleText,
                actionText = if (language == AppLanguage.AR) "بحث متقدم" else "Search",
                onActionClick = { viewModel.navigateTo(Screen.Search) }
            )
        }

        if (latestThemes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if (language == AppLanguage.AR) "لا توجد ثيمات مطابقة" else "No Themes Found",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (language == AppLanguage.AR) "جرب إلغاء الفلاتر أو تغيير كلمة البحث" else "Try clearing filters or changing search keywords",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = {
                                selectedCompanyFilterId = null
                                viewModel.selectMood(null)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
                        ) {
                            Text(if (language == AppLanguage.AR) "إعادة ضبط الكل" else "Reset All", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Render 2 items per row strictly matching mobile phone proportions
            items(latestThemes.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach { item ->
                        Box(modifier = Modifier.weight(1f)) {
                            ThemeCard(
                                item = item,
                                language = language,
                                onCardClick = { viewModel.navigateTo(Screen.ThemeDetails(item.theme.slug)) },
                                onQuickPreviewClick = { viewModel.showQuickPreview(item) }
                            )
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 9. Mobile Footer
        item {
            Spacer(modifier = Modifier.height(28.dp))
            MobileFooter(language = language)
        }
    }
}

/**
 * Clean Top Bar designed for mobile portrait screens (never overflows).
 */
@Composable
private fun MobileTopBar(
    language: AppLanguage,
    isDarkMode: Boolean,
    unreadNotificationsCount: Int,
    onToggleLanguage: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAdminClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Logo & Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF00E5FF), Color(0xFF0284C7))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S18",
                    color = Color(0xFF031024),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }

            Column {
                Text(
                    text = "S18_THEME",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (language == AppLanguage.AR) "متجر ثيمات الهاتف" else "Phone Themes Store",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action Icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Notifications with badge
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = S18Strings.get("notifications", language),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                    if (unreadNotificationsCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF00E5FF), CircleShape)
                        )
                    }
                }
            }

            // Language Switch
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onToggleLanguage),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFF00E5FF)
                    )
                    Text(
                        text = if (language == AppLanguage.EN) "AR" else "EN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Theme Switch
            ThemeSwitch(
                isDarkMode = isDarkMode,
                onToggle = onToggleDarkMode,
                contentDescriptionText = S18Strings.get("toggle_dark_mode", language)
            )

            // Admin Portal
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = S18Strings.get("admin", language),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Side Menu
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = S18Strings.get("menu", language),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Mobile-First Search Bar Pill
 */
@Composable
private fun MobileSearchBar(
    language: AppLanguage,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = if (language == AppLanguage.AR) "ابحث عن الثيمات، الشركات، المصممين..." else "Search themes, brands, designers...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * 4 Sleek Mobile Feature Shortcuts (Icons with clean mobile proportions)
 */
@Composable
private fun MobileFeatureShortcuts(
    language: AppLanguage,
    activeBattlesCount: Int,
    collectionsCount: Int,
    onWallpapersClick: () -> Unit,
    onBattlesClick: () -> Unit,
    onCollectionsClick: () -> Unit,
    onCategoriesClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ShortcutItem(
            icon = Icons.Default.PhotoLibrary,
            label = if (language == AppLanguage.AR) "الخلفيات" else "Wallpapers",
            badge = "4K",
            tint = Color(0xFF00E676),
            onClick = onWallpapersClick
        )
        ShortcutItem(
            icon = Icons.Default.SportsKabaddi,
            label = if (language == AppLanguage.AR) "المعارك" else "Battles",
            badge = if (activeBattlesCount > 0) "$activeBattlesCount" else null,
            tint = Color(0xFFFF6D00),
            onClick = onBattlesClick
        )
        ShortcutItem(
            icon = Icons.Default.Collections,
            label = if (language == AppLanguage.AR) "المجموعات" else "Suites",
            badge = if (collectionsCount > 0) "$collectionsCount" else null,
            tint = Color(0xFF00E5FF),
            onClick = onCollectionsClick
        )
        ShortcutItem(
            icon = Icons.Default.Category,
            label = if (language == AppLanguage.AR) "الأقسام" else "Categories",
            badge = null,
            tint = Color(0xFF9333EA),
            onClick = onCategoriesClick
        )
    }
}

@Composable
private fun ShortcutItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    badge: String?,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(tint.copy(alpha = 0.12f))
                    .border(1.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (badge != null) {
                Surface(
                    shape = CircleShape,
                    color = tint,
                    modifier = Modifier.padding(top = 2.dp, end = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color(0xFF031024),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

/**
 * Mobile-First Featured Hero Card (16:9 aspect ratio, dark gradient, prominent actions)
 */
@Composable
private fun MobileHeroCard(
    item: ThemeFullItem,
    language: AppLanguage,
    onCardClick: () -> Unit,
    onQuickPreviewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color(0x3300E5FF))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            ThemeImage(
                imageUrl = item.theme.coverImageUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = item.theme.name
            )

            // Dynamic cyber gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x40000000),
                                Color(0x20000000),
                                Color(0xE6080E1A)
                            )
                        )
                    )
            )

            // Top Badges
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF00E5FF)
                ) {
                    Text(
                        text = if (language == AppLanguage.AR) "★ ثيم الأسبوع" else "★ Featured",
                        color = Color(0xFF031024),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xD0080E1A),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0x6000E5FF))
                ) {
                    Text(
                        text = item.company.name,
                        color = Color(0xFF38BDF8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Bottom Info & Action
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.theme.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${item.company.name} · ${item.designer.name} · ${item.theme.downloads} downloads",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        maxLines = 1
                    )
                }

                Button(
                    onClick = onCardClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        contentColor = Color(0xFF031024)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.AR) "استعراض" else "View",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * Mobile Section Header
 */
@Composable
private fun MobileSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (actionText != null && onActionClick != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = actionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Mobile Mood / Style Filter Chips
 */
@Composable
private fun MobileMoodChips(
    selectedMood: String?,
    language: AppLanguage,
    onSelectMood: (String?) -> Unit
) {
    val moods = listOf(
        "Cyberpunk" to "⚡ Cyberpunk",
        "AMOLED" to "⬛ AMOLED Dark",
        "Minimalist" to "⚪ Minimalist",
        "Neon" to "🟣 Neon",
        "Pastel" to "🌸 Pastel",
        "Nature" to "🌿 Nature",
        "Retro" to "🕹️ Retro 90s"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (selectedMood == null) Color(0xFF00E5FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.clickable { onSelectMood(null) }
            ) {
                Text(
                    text = if (language == AppLanguage.AR) "جميع الأنماط" else "All Styles",
                    color = if (selectedMood == null) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }

        items(moods) { (key, label) ->
            val isSelected = selectedMood == key
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.clickable {
                    onSelectMood(if (isSelected) null else key)
                }
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(14.dp))
}

/**
 * Mobile-First Footer Card
 */
@Composable
private fun MobileFooter(language: AppLanguage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "S18_THEME PLATFORM",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
            color = Color(0xFF00E5FF)
        )
        Text(
            text = if (language == AppLanguage.AR) "أفضل الثيمات الرسمية والمعدلة لأجهزة أندرويد." else "Best official and custom Android themes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Telegram: @s18theme", fontSize = 11.sp, color = Color(0xFF38BDF8))
            Text("TikTok: @s18theme", fontSize = 11.sp, color = Color(0xFFCBD5E1))
        }
        Text(
            text = "© 2026 S18_THEME · Mobile Edition",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}
