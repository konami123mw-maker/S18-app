package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.ThemeRating
import com.example.util.DeviceCompatibility
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Comment
import com.example.data.ThemeFullItem
import com.example.data.ThemeVersion
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen
import com.example.ui.ThemeViewModel
import com.example.ui.components.BadgeTag
import com.example.ui.components.BeforeAfterSlider
import com.example.ui.components.PreviewGallery
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThemeDetailsScreen(
    themeSlug: String,
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsStateWithLifecycle()
    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()

    val item = fullThemes.find { it.theme.slug == themeSlug }

    LaunchedEffect(item) {
        if (item != null) {
            viewModel.viewTheme(item.theme.id)
        }
    }

    if (item == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = S18Strings.get("theme_not_found", language),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = S18Strings.get("theme_not_found_desc", language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.navigateBack() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = S18Strings.get("back_to_themes", language))
                }
            }
        }
        return
    }

    val theme = item.theme
    val company = item.company
    val designer = item.designer
    val versionsFlow = remember(theme.id) { viewModel.repository.getVersionsForTheme(theme.id) }
    val versions by versionsFlow.collectAsStateWithLifecycle(emptyList())
    val commentsFlow = remember(theme.id) { viewModel.repository.getApprovedCommentsForTheme(theme.id) }
    val approvedComments by commentsFlow.collectAsStateWithLifecycle(emptyList())

    val ratingsFlow = remember(theme.id) { viewModel.getRatingsForTheme(theme.id) }
    val ratings: List<ThemeRating> by ratingsFlow.collectAsStateWithLifecycle(emptyList())
    val avgRatingFlow = remember(theme.id) { viewModel.getAverageRatingForTheme(theme.id) }
    val avgRating: Double? by avgRatingFlow.collectAsStateWithLifecycle(null)
    val ratingsCountFlow = remember(theme.id) { viewModel.getRatingsCountForTheme(theme.id) }
    val ratingsCount: Int by ratingsCountFlow.collectAsStateWithLifecycle(0)

    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedRatingStars by remember { mutableIntStateOf(5) }
    var ratingNickname by remember { mutableStateOf("") }
    var ratingReviewText by remember { mutableStateOf("") }

    val dateFormat = remember(language) {
        val locale = if (language == AppLanguage.AR) Locale("ar") else Locale.ENGLISH
        SimpleDateFormat("MMMM d, yyyy", locale)
    }
    val updatedDateStr = remember(theme.updatedAt) { dateFormat.format(Date(theme.updatedAt)) }

    val favoriteIds by viewModel.favoriteThemeIds.collectAsStateWithLifecycle()
    val isFav = favoriteIds.contains(theme.id)

    val accentColor = remember(theme.accentColor) {
        try {
            if (theme.accentColor.isNotBlank()) Color(android.graphics.Color.parseColor(theme.accentColor))
            else Color(0xFF00E5FF)
        } catch (_: Exception) {
            Color(0xFF00E5FF)
        }
    }

    var nicknameInput by remember { mutableStateOf("") }
    var commentInput by remember { mutableStateOf("") }
    var commentSuccessMessage by remember { mutableStateOf<String?>(null) }
    var userReaction by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("theme_details_screen"),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        // Top Back & Share Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Favorite Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleFavoriteTheme(theme.id) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = S18Strings.get("favorite", language),
                            tint = if (isFav) Color(0xFFFF0055) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Copy Link Button
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Theme URL", "https://s18theme.dev/theme/${theme.slug}")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Link",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Native Share Button
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "${theme.name} - S18_THEME")
                                putExtra(Intent.EXTRA_TEXT, "Check out ${theme.name} for ${company.name} on S18_THEME: https://s18theme.dev/theme/${theme.slug}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Theme"))
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = S18Strings.get("share", language),
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Phone Mockup Preview Gallery
        item {
            PreviewGallery(
                previews = item.previews,
                fallbackCoverUrl = theme.coverImageUrl,
                themeTitle = theme.name
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Theme Title & Badges
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${company.name} · S18_THEME",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF00E5FF)
                    )
                    Text(
                        text = "·",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable {
                            viewModel.navigateTo(Screen.DesignerDetails(designer.slug))
                        }
                    ) {
                        Text(
                            text = designer.name,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (designer.isVerified) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = S18Strings.get("verified_creator", language),
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${if (language == AppLanguage.AR) "تم التحديث في" else "Updated"} $updatedDateStr",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Badges & Tags Flow
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Rating Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFB300).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.3f)),
                        modifier = Modifier.clickable { showRatingDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(14.dp)
                            )
                            val currentAvg = avgRating
                            val ratingDisplay = if (currentAvg != null && currentAvg > 0.0) String.format(Locale.US, "%.1f", currentAvg) else "5.0"
                            Text(
                                text = "$ratingDisplay ($ratingsCount)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFFB300)
                            )
                        }
                    }

                    item.latestVersion?.let { ver ->
                        BadgeTag(
                            text = "v${ver.version}",
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            textColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (theme.featured) {
                        BadgeTag(
                            text = S18Strings.get("featured_badge", language),
                            backgroundColor = Color(0xFFF59E0B),
                            textColor = Color(0xFF031024)
                        )
                    }
                    val tagsList = theme.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    tagsList.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Smart Device Compatibility Banner (Feature 2)
                val compat = remember(theme, company, language) {
                    DeviceCompatibility.checkThemeCompatibility(
                        companySlug = company.slug,
                        companyName = company.name,
                        themeTags = theme.tags,
                        language = language
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(1.dp, compat.badgeColor.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(compat.badgeColor.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Smartphone,
                                    contentDescription = null,
                                    tint = compat.badgeColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = compat.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = compat.description,
                                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.navigateTo(Screen.DeviceCompatibilityGuide)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = compat.badgeColor
                            ),
                            border = BorderStroke(1.dp, compat.badgeColor.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = S18Strings.get("view_install_guide", language),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Coming Soon Countdown Banner (if applicable)
                if (theme.comingSoon) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x25FF6D00)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x80FF6D00))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFFFF6D00),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = S18Strings.get("coming_soon", language),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color(0xFFFF6D00)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val targetLaunch = if (theme.launchDate > 0) theme.launchDate else System.currentTimeMillis() + 3 * 86400000L + 12 * 3600000L
                            val diff = (targetLaunch - System.currentTimeMillis()).coerceAtLeast(0L)
                            val days = diff / 86400000L
                            val hours = (diff % 86400000L) / 3600000L
                            val mins = (diff % 3600000L) / 60000L

                            Text(
                                text = "${S18Strings.get("launching_in", language)}: ${days}d ${hours}h ${mins}m",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "You will receive an in-app notification when released!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6D00))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(S18Strings.get("notify_me", language), fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Primary Action: Download Latest Version Button with Dynamic Accent Color
                Button(
                    onClick = { viewModel.downloadTheme(context, item) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("download_theme_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = if (accentColor.luminance() > 0.5f) Color(0xFF031024) else Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = S18Strings.get("download_latest_version", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Reactions Bar (Section 68)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val reactions = listOf(
                        "❤️" to (theme.likes + (if (userReaction == "❤️") 1 else 0)),
                        "🔥" to (94 + (if (userReaction == "🔥") 1 else 0)),
                        "⚡" to (67 + (if (userReaction == "⚡") 1 else 0)),
                        "✨" to (52 + (if (userReaction == "✨") 1 else 0)),
                        "😍" to (43 + (if (userReaction == "😍") 1 else 0))
                    )

                    reactions.forEach { (emoji, count) ->
                        val isSelected = userReaction == emoji
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, accentColor) else null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    userReaction = if (userReaction == emoji) null else emoji
                                    Toast.makeText(context, if (userReaction != null) "Reaction added: $emoji" else "Reaction removed", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = emoji, fontSize = 16.sp)
                                Text(
                                    text = "$count",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Description
                Text(
                    text = if (language == AppLanguage.AR) "الوصف" else "Description",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = theme.description.ifEmpty { "No description provided." },
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Before / After Comparison Slider (Section 54)
                if (theme.beforeImageUrl.isNotBlank() && theme.afterImageUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = S18Strings.get("before_after", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BeforeAfterSlider(
                        beforeImageUrl = theme.beforeImageUrl,
                        afterImageUrl = theme.afterImageUrl,
                        language = language
                    )
                }

                // Step-by-Step Installation Guide (Section 56)
                if (theme.installationGuide.isNotBlank()) {
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = S18Strings.get("installation_guide", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val steps = theme.installationGuide.split("\n").map { it.trim() }.filter { it.isNotBlank() }
                    steps.forEachIndexed { index, stepText ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(accentColor.copy(alpha = 0.2f), CircleShape)
                                        .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = accentColor,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                }
                                Text(
                                    text = stepText,
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // What's New (Changelog for latest version)
                item.latestVersion?.changelog?.let { cl ->
                    if (cl.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = S18Strings.get("whats_new", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            )
                        ) {
                            Text(
                                text = cl,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                // Version History
                if (versions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = S18Strings.get("version_history", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    versions.forEach { ver ->
                        VersionItemRow(
                            version = ver,
                            language = language,
                            onDownload = { viewModel.downloadTheme(context, item, ver) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Ratings & Reviews Section (Feature 4)
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${S18Strings.get("ratings_and_reviews", language)} ($ratingsCount)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(
                        onClick = { showRatingDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = S18Strings.get("rate_this_theme", language),
                            color = Color(0xFFFFB300),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Rating Summary Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val currentScore = avgRating
                            val avgScore = if (currentScore != null && currentScore > 0.0) String.format(Locale.US, "%.1f", currentScore) else "5.0"
                            Text(
                                text = avgScore,
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                                color = Color(0xFFFFB300)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                val currentStars = (avgRating ?: 5.0).toInt().coerceIn(1, 5)
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = if (i <= currentStars) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$ratingsCount ${S18Strings.get("reviews_count", language)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = S18Strings.get("tap_to_rate", language),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (star in 1..5) {
                                    IconButton(
                                        onClick = {
                                            selectedRatingStars = star
                                            showRatingDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Rate $star stars",
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reviews List
                if (ratings.isEmpty()) {
                    Text(
                        text = S18Strings.get("no_reviews_yet", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                    )
                } else {
                    for (r in ratings.take(5)) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = r.nickname,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        for (i in 1..5) {
                                            Icon(
                                                imageVector = if (i <= r.stars) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = null,
                                                tint = Color(0xFFFFB300),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                if (r.review.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = r.review,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Comments Section
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "${S18Strings.get("comments", language)} (${approvedComments.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Post Comment Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = S18Strings.get("be_first_comment", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = nicknameInput,
                            onValueChange = { nicknameInput = it.take(40) },
                            placeholder = { Text(S18Strings.get("nickname", language)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E5FF)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it.take(300) },
                            placeholder = { Text(S18Strings.get("write_comment", language)) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E5FF)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (nicknameInput.isNotBlank() && commentInput.isNotBlank()) {
                                    viewModel.postComment(theme.id, nicknameInput, commentInput) { ok ->
                                        if (ok) {
                                            commentSuccessMessage = S18Strings.get("comment_submitted_notice", language)
                                            nicknameInput = ""
                                            commentInput = ""
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00E5FF),
                                contentColor = Color(0xFF031024)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = S18Strings.get("post_comment", language),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (commentSuccessMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = commentSuccessMessage!!,
                                color = Color(0xFF10B981),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // List of Approved Comments
                Spacer(modifier = Modifier.height(16.dp))
                if (approvedComments.isEmpty()) {
                    Text(
                        text = S18Strings.get("no_comments", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    approvedComments.forEach { comment ->
                        CommentCard(comment = comment)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }

    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = S18Strings.get("rate_this_theme", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = S18Strings.get("tap_to_rate", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (star in 1..5) {
                            IconButton(
                                onClick = { selectedRatingStars = star },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = if (star <= selectedRatingStars) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "$star stars",
                                    tint = if (star <= selectedRatingStars) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                    val starLabel = when (selectedRatingStars) {
                        5 -> if (language == AppLanguage.AR) "5 نجوم - ممتاز جداً ★★★★★" else "5 Stars - Excellent!"
                        4 -> if (language == AppLanguage.AR) "4 نجوم - رائع ومميز ★★★★" else "4 Stars - Very Good!"
                        3 -> if (language == AppLanguage.AR) "3 نجوم - جيد ★★★" else "3 Stars - Good"
                        2 -> if (language == AppLanguage.AR) "نجمتان - يحتاج تحسين ★★" else "2 Stars - Needs Work"
                        else -> if (language == AppLanguage.AR) "نجمة واحدة - ضعيف ★" else "1 Star - Poor"
                    }
                    Text(
                        text = starLabel,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFFB300)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = ratingNickname,
                        onValueChange = { ratingNickname = it.take(30) },
                        placeholder = { Text(S18Strings.get("nickname", language)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ratingReviewText,
                        onValueChange = { ratingReviewText = it.take(250) },
                        placeholder = { Text(S18Strings.get("write_review_optional", language)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitThemeRating(
                            themeId = theme.id,
                            stars = selectedRatingStars,
                            nickname = ratingNickname,
                            review = ratingReviewText
                        ) { ok ->
                            if (ok) {
                                showRatingDialog = false
                                ratingNickname = ""
                                ratingReviewText = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB300),
                        contentColor = Color(0xFF1F1200)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = S18Strings.get("submit_rating", language),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text(text = S18Strings.get("cancel", language))
                }
            }
        )
    }
}

@Composable
fun VersionItemRow(
    version: ThemeVersion,
    language: AppLanguage,
    onDownload: () -> Unit
) {
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
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "v${version.version}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF00E5FF)
                    )
                    if (version.releaseDate.isNotEmpty()) {
                        Text(
                            text = "· ${version.releaseDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (version.changelog.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = version.changelog,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            IconButton(
                onClick = onDownload,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0x2000E5FF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download v${version.version}",
                    tint = Color(0xFF00E5FF)
                )
            }
        }
    }
}

@Composable
fun CommentCard(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.nickname,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF00E5FF)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
