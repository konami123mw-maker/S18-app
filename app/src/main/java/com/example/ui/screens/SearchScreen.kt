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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.S18Strings
import com.example.ui.Screen
import com.example.ui.ThemeViewModel
import com.example.ui.components.ThemeCard

enum class SortOption {
    LATEST, MOST_VIEWED, MOST_DOWNLOADED, RECENTLY_UPDATED
}

@Composable
fun SearchScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()
    val companies by viewModel.publishedCompanies.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCompanyId by remember { mutableStateOf<Long?>(null) }
    var selectedSort by remember { mutableStateOf(SortOption.LATEST) }

    val filteredThemes = remember(fullThemes, searchQuery, selectedCompanyId, selectedSort) {
        val query = searchQuery.trim().lowercase()
        fullThemes.filter { item ->
            val matchPub = item.theme.published
            val matchCompany = selectedCompanyId == null || item.theme.companyId == selectedCompanyId
            val matchQuery = query.isEmpty() ||
                item.theme.name.lowercase().contains(query) ||
                item.company.name.lowercase().contains(query) ||
                item.designer.name.lowercase().contains(query) ||
                item.theme.tags.lowercase().contains(query)
            matchPub && matchCompany && matchQuery
        }.let { list ->
            when (selectedSort) {
                SortOption.LATEST -> list.sortedByDescending { it.theme.createdAt }
                SortOption.MOST_VIEWED -> list.sortedByDescending { it.theme.views }
                SortOption.MOST_DOWNLOADED -> list.sortedByDescending { it.theme.downloads }
                SortOption.RECENTLY_UPDATED -> list.sortedByDescending { it.theme.updatedAt }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen"),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        // Top Search Bar with Back Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = S18Strings.get("search_placeholder", language),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                )
            }
        }

        // Company Filters Horizontal Row
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        title = S18Strings.get("filter_all", language),
                        isSelected = selectedCompanyId == null,
                        onClick = { selectedCompanyId = null }
                    )
                }
                items(companies) { comp ->
                    FilterChip(
                        title = comp.name,
                        isSelected = selectedCompanyId == comp.id,
                        onClick = {
                            selectedCompanyId = if (selectedCompanyId == comp.id) null else comp.id
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Sort Row: Latest, Most Viewed, Most Downloaded, Recently Updated
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SortChip(
                        title = S18Strings.get("sort_latest", language),
                        isSelected = selectedSort == SortOption.LATEST,
                        onClick = { selectedSort = SortOption.LATEST }
                    )
                }
                item {
                    SortChip(
                        title = S18Strings.get("sort_most_viewed", language),
                        isSelected = selectedSort == SortOption.MOST_VIEWED,
                        onClick = { selectedSort = SortOption.MOST_VIEWED }
                    )
                }
                item {
                    SortChip(
                        title = S18Strings.get("sort_most_downloaded", language),
                        isSelected = selectedSort == SortOption.MOST_DOWNLOADED,
                        onClick = { selectedSort = SortOption.MOST_DOWNLOADED }
                    )
                }
                item {
                    SortChip(
                        title = S18Strings.get("sort_recently_updated", language),
                        isSelected = selectedSort == SortOption.RECENTLY_UPDATED,
                        onClick = { selectedSort = SortOption.RECENTLY_UPDATED }
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Results Count
        item {
            Text(
                text = "${filteredThemes.size} ${if (language.isRtl) "ثيم متوفر" else "Themes found"}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Grid of filtered themes
        if (filteredThemes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = S18Strings.get("no_themes", language),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            items(filteredThemes.chunked(2)) { pair ->
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
    }
}

@Composable
fun FilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SortChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
