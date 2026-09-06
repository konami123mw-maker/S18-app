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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Designer
import com.example.data.ThemeFullItem
import com.example.ui.AdminTab
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen
import com.example.ui.ThemeViewModel
import com.example.ui.components.CompanyCard
import com.example.ui.components.ThemeCard
import com.example.ui.components.ThemeImage
import com.example.ui.components.ThemeSwitch

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
    val designers by viewModel.allDesigners.collectAsStateWithLifecycle()
    val activeBattles by viewModel.activeBattles.collectAsStateWithLifecycle()
    val publicCollections by viewModel.publicCollections.collectAsStateWithLifecycle()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val selectedMood by viewModel.selectedMood.collectAsStateWithLifecycle()

    val publishedThemes = fullThemes.filter { it.theme.published }.filter { item ->
        if (selectedMood == null) true
        else item.theme.tags.contains(selectedMood!!, ignoreCase = true) || item.theme.name.contains(selectedMood!!, ignoreCase = true)
    }
    val featuredThemes = publishedThemes.filter { it.theme.featured }
    val trendingThemes = publishedThemes.sortedByDescending { it.theme.views * 2 + it.theme.downloads * 5 }
    val latestThemes = publishedThemes.sortedByDescending { it.theme.createdAt }
    val recentlyUpdatedThemes = publishedThemes.sortedByDescending { it.theme.updatedAt }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // 1. Header Bar
        item {
            HeaderBar(
                language = language,
                isDarkMode = isDarkMode,
                unreadNotificationsCount = unreadNotificationsCount,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                onSearchClick = { viewModel.navigateTo(Screen.Search) },
                onNotificationsClick = { viewModel.toggleNotificationsSheet(true) },
                onAdminClick = { viewModel.navigateTo(Screen.Admin(AdminTab.DASHBOARD)) },
                onMenuClick = onMenuClick
            )
        }

        // 2. Hero Section
        item {
            HeroSection(
                language = language,
                onExploreThemes = { viewModel.navigateTo(Screen.Search) },
                onBrowseCompanies = {
                    companies.firstOrNull()?.let {
                        viewModel.navigateTo(Screen.CompanyDetails(it.slug))
                    }
                },
                onSearchClick = { viewModel.navigateTo(Screen.Search) }
            )
        }

        // Feature Shortcuts: Battles & Collections (Section 25, 53, 69)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Battles Button Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(Screen.Battles) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x50FF6D00))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0x25FF6D00), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsKabaddi,
                                contentDescription = null,
                                tint = Color(0xFFFF6D00),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = S18Strings.get("theme_battles", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${activeBattles.size} ${if (language == AppLanguage.AR) "نشطة" else "Active"}",
                                fontSize = 10.sp,
                                color = Color(0xFFFF6D00)
                            )
                        }
                    }
                }

                // Collections Button Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(Screen.Collections) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5000E5FF))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0x2500E5FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = S18Strings.get("collections", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${publicCollections.size} ${if (language == AppLanguage.AR) "مجموعة" else "Suites"}",
                                fontSize = 10.sp,
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }
                }
            }

            // Additional Quick Navigation (Categories & Wallpapers)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Categories Shortcut Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(Screen.Categories) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x507C4DFF))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0x257C4DFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = Color(0xFF7C4DFF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = S18Strings.get("categories", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Text(
                                text = if (language == AppLanguage.AR) "الأقسام والأنماط" else "Browse styles",
                                fontSize = 10.sp,
                                color = Color(0xFF7C4DFF)
                            )
                        }
                    }
                }

                // Wallpapers Shortcut Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(Screen.Search) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5000E676))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0x2500E676), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = S18Strings.get("wallpapers", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Text(
                                text = if (language == AppLanguage.AR) "خلفيات فائقة 4K" else "4K Wallpapers",
                                fontSize = 10.sp,
                                color = Color(0xFF00E676)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Mood-Based Discovery (Section 57)
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = S18Strings.get("moods", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (selectedMood != null) {
                        Text(
                            text = if (language == AppLanguage.AR) "إعادة تعيين" else "Reset",
                            color = Color(0xFF00E5FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.selectMood(null) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val moods = listOf(
                    "Cyberpunk" to "⚡ Cyberpunk",
                    "Minimalist" to "⚪ Minimalist",
                    "AMOLED" to "⬛ AMOLED Dark",
                    "Neon" to "🟣 Neon Glow",
                    "Pastel" to "🌸 Pastel",
                    "Retro" to "🕹️ Retro 90s",
                    "Nature" to "🌿 Nature"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedMood == null) Color(0xFF00E5FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.clickable { viewModel.selectMood(null) }
                        ) {
                            Text(
                                text = S18Strings.get("filter_all", language),
                                color = if (selectedMood == null) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    items(moods) { (key, label) ->
                        val isSelected = selectedMood == key
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0x30FFFFFF)),
                            modifier = Modifier.clickable {
                                viewModel.selectMood(if (isSelected) null else key)
                            }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color(0xFF031024) else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Live Battle Spotlight (Section 53)
        if (activeBattles.isNotEmpty()) {
            val spotlightBattle = activeBattles.first()
            val themeA = fullThemes.find { it.theme.id == spotlightBattle.themeAId }
            val themeB = fullThemes.find { it.theme.id == spotlightBattle.themeBId }

            item {
                SectionHeader(
                    title = S18Strings.get("theme_battles", language),
                    icon = Icons.Default.SportsKabaddi,
                    iconTint = Color(0xFFFF6D00),
                    onSeeAll = { viewModel.navigateTo(Screen.Battles) }
                )
                com.example.ui.screens.BattleCardItem(
                    battle = spotlightBattle,
                    themeA = themeA,
                    themeB = themeB,
                    language = language,
                    onVoteA = { viewModel.voteInBattle(spotlightBattle.id, spotlightBattle.themeAId) },
                    onVoteB = { viewModel.voteInBattle(spotlightBattle.id, spotlightBattle.themeBId) },
                    onThemeClick = { slug -> viewModel.navigateTo(Screen.ThemeDetails(slug)) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // 3. Companies Section
        item {
            SectionHeader(
                title = S18Strings.get("companies", language),
                onSeeAll = null
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(companies) { company ->
                    val themeCount = fullThemes.count { it.theme.companyId == company.id && it.theme.published }
                    CompanyCard(
                        company = company,
                        themeCount = themeCount,
                        language = language,
                        onClick = { viewModel.navigateTo(Screen.CompanyDetails(company.slug)) },
                        modifier = Modifier.width(220.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // 4. Featured Themes (Horizontal Banner / Cards)
        if (featuredThemes.isNotEmpty()) {
            item {
                SectionHeader(
                    title = S18Strings.get("featured_themes", language),
                    icon = Icons.Default.AutoAwesome,
                    iconTint = Color(0xFF8B5CF6)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(featuredThemes) { item ->
                        ThemeCard(
                            item = item,
                            language = language,
                            onCardClick = { viewModel.navigateTo(Screen.ThemeDetails(item.theme.slug)) },
                            onQuickPreviewClick = { viewModel.showQuickPreview(item) },
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        // 5. Trending Themes (Horizontal Scroll)
        if (trendingThemes.isNotEmpty()) {
            item {
                SectionHeader(
                    title = S18Strings.get("trending_themes", language),
                    icon = Icons.Default.Whatshot,
                    iconTint = Color(0xFFFF6D00)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(trendingThemes.take(6)) { item ->
                        ThemeCard(
                            item = item,
                            language = language,
                            onCardClick = { viewModel.navigateTo(Screen.ThemeDetails(item.theme.slug)) },
                            onQuickPreviewClick = { viewModel.showQuickPreview(item) },
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        // 6. Recently Updated Themes
        if (recentlyUpdatedThemes.isNotEmpty()) {
            item {
                SectionHeader(
                    title = S18Strings.get("recently_updated", language),
                    onSeeAll = { viewModel.navigateTo(Screen.Updates) }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(recentlyUpdatedThemes.take(6)) { item ->
                        ThemeCard(
                            item = item,
                            language = language,
                            onCardClick = { viewModel.navigateTo(Screen.ThemeDetails(item.theme.slug)) },
                            onQuickPreviewClick = { viewModel.showQuickPreview(item) },
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        // 7. Latest Themes (2-Column Grid)
        item {
            SectionHeader(
                title = S18Strings.get("latest_themes", language),
                onSeeAll = { viewModel.navigateTo(Screen.Search) }
            )
        }

        if (latestThemes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(42.dp)
                        )
                        Text(
                            text = S18Strings.get("empty_themes_title", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = S18Strings.get("empty_themes_desc", language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { viewModel.navigateTo(Screen.Admin(AdminTab.THEMES)) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF031024))
                        ) {
                            Text(S18Strings.get("open_admin", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(latestThemes.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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

        // 8. Popular Designers
        if (designers.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(28.dp))
                SectionHeader(
                    title = S18Strings.get("popular_designers", language)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(designers) { designer ->
                        val count = fullThemes.count { it.theme.designerId == designer.id && it.theme.published }
                        DesignerChip(
                            designer = designer,
                            themeCount = count,
                            onClick = { viewModel.navigateTo(Screen.DesignerDetails(designer.slug)) }
                        )
                    }
                }
            }
        }

        // 9. Footer
        item {
            Spacer(modifier = Modifier.height(48.dp))
            FooterSection(language = language)
        }
    }
}

@Composable
fun HeaderBar(
    language: AppLanguage,
    isDarkMode: Boolean,
    unreadNotificationsCount: Int,
    onToggleLanguage: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAdminClick: () -> Unit,
    onMenuClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // S18 Logo & Brand Title
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
                            listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S18",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "S18_THEME",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Action controls: Notifications, Search, Language, Custom Switch, Admin, Menu (3 dots)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Notifications Button with badge
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(36.dp)
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

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = S18Strings.get("search", language),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Language Switch button (AR / EN)
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleLanguage),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
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

            // Custom Uiverse-inspired Theme Switch
            ThemeSwitch(
                isDarkMode = isDarkMode,
                onToggle = onToggleDarkMode,
                contentDescriptionText = S18Strings.get("toggle_dark_mode", language)
            )

            // Admin Portal Access Button
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = S18Strings.get("admin", language),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Three dots Side Menu (Drawer) trigger
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = S18Strings.get("menu", language),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun HeroSection(
    language: AppLanguage,
    onExploreThemes: () -> Unit,
    onBrowseCompanies: () -> Unit,
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D1829),
                        Color(0xFF131D32),
                        Color(0xFF090D17)
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(Color(0x5000E5FF), Color(0x307C4DFF), Color(0x10FFFFFF))
                ),
                RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0x2000E5FF),
                modifier = Modifier.border(1.dp, Color(0x4000E5FF), RoundedCornerShape(20.dp))
            ) {
                Text(
                    text = "S18_THEME PLATFORM",
                    color = Color(0xFF00E5FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = S18Strings.get("app_name", language),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = S18Strings.get("tagline", language),
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onExploreThemes,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        contentColor = Color(0xFF031024)
                    )
                ) {
                    Text(
                        text = S18Strings.get("explore_themes", language),
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onBrowseCompanies,
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(Color(0x80FFFFFF), Color(0x40FFFFFF)))
                    )
                ) {
                    Text(
                        text = S18Strings.get("browse_companies", language),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Large Search Bar trigger
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onSearchClick),
                color = Color(0x30FFFFFF),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = S18Strings.get("search_placeholder", language),
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    onSeeAll: (() -> Unit)? = null
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
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (onSeeAll != null) {
            Text(
                text = "→",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onSeeAll)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun DesignerChip(
    designer: Designer,
    themeCount: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                ThemeImage(
                    imageUrl = designer.avatarUrl,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = designer.name
                )
            }

            Column {
                Text(
                    text = designer.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$themeCount Themes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FooterSection(language: AppLanguage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "S18_THEME",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = Color(0xFF00E5FF)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (language == AppLanguage.AR) "اكتشف أجمل الثيمات لجهازك." else "Discover beautiful themes for your device.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Telegram: @s18theme",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF38BDF8)
            )
            Text(
                text = "TikTok: @s18theme",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFE2E8F0)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "© 2026 S18_THEME",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}
