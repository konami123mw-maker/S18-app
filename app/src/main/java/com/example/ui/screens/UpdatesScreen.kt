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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Update
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ThemeFullItem
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen
import com.example.ui.ThemeViewModel
import com.example.ui.components.ThemeImage

enum class UpdateFilterTime {
    TODAY, THIS_WEEK, THIS_MONTH
}

@Composable
fun UpdatesScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf(UpdateFilterTime.THIS_WEEK) }

    val now = System.currentTimeMillis()
    val updatedThemes = remember(fullThemes, selectedFilter) {
        val maxAgeMs = when (selectedFilter) {
            UpdateFilterTime.TODAY -> 86400000L
            UpdateFilterTime.THIS_WEEK -> 7 * 86400000L
            UpdateFilterTime.THIS_MONTH -> 30 * 86400000L
        }
        fullThemes.filter { it.theme.published && (now - it.theme.updatedAt) <= maxAgeMs }
            .sortedByDescending { it.theme.updatedAt }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("updates_screen"),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        // Back Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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

                Text(
                    text = S18Strings.get("recently_updated", language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Time Filter Tabs (Today, This Week, This Month)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val todayLabel = if (language == AppLanguage.AR) "اليوم" else "Today"
                val weekLabel = if (language == AppLanguage.AR) "هذا الأسبوع" else "This Week"
                val monthLabel = if (language == AppLanguage.AR) "هذا الشهر" else "This Month"

                TimeFilterChip(
                    title = todayLabel,
                    isSelected = selectedFilter == UpdateFilterTime.TODAY,
                    onClick = { selectedFilter = UpdateFilterTime.TODAY },
                    modifier = Modifier.weight(1f)
                )
                TimeFilterChip(
                    title = weekLabel,
                    isSelected = selectedFilter == UpdateFilterTime.THIS_WEEK,
                    onClick = { selectedFilter = UpdateFilterTime.THIS_WEEK },
                    modifier = Modifier.weight(1f)
                )
                TimeFilterChip(
                    title = monthLabel,
                    isSelected = selectedFilter == UpdateFilterTime.THIS_MONTH,
                    onClick = { selectedFilter = UpdateFilterTime.THIS_MONTH },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Updates List
        if (updatedThemes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (language == AppLanguage.AR) "لا توجد تحديثات في هذه الفترة" else "No updates in this period",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(updatedThemes) { item ->
                UpdateTimelineCard(
                    item = item,
                    language = language,
                    onClick = { viewModel.navigateTo(Screen.ThemeDetails(item.theme.slug)) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TimeFilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(
                1.dp,
                if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun UpdateTimelineCard(
    item: ThemeFullItem,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0D1527))
                ) {
                    ThemeImage(
                        imageUrl = item.theme.coverImageUrl.ifEmpty { "theme_preview_liquid" },
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = item.theme.name
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.theme.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        item.latestVersion?.let { ver ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981)
                            ) {
                                Text(
                                    text = "v${ver.version}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${item.company.name} · ${item.designer.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF00E5FF)
                    )

                    item.latestVersion?.changelog?.let { cl ->
                        if (cl.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cl.lines().firstOrNull() ?: cl,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
