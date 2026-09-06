package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen
import com.example.ui.ThemeViewModel
import com.example.ui.components.ThemeCard

@Composable
fun CompanyScreen(
    companySlug: String,
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val companies by viewModel.publishedCompanies.collectAsStateWithLifecycle()
    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()

    val company = companies.find { it.slug == companySlug }

    if (company == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Company Not Found",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.navigateBack() }) {
                    Text(S18Strings.get("home", language))
                }
            }
        }
        return
    }

    var selectedSort by remember { mutableStateOf(SortOption.LATEST) }

    val themesForCompany = remember(fullThemes, company.id, selectedSort) {
        fullThemes.filter { it.theme.companyId == company.id && it.theme.published }
            .let { list ->
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
            .testTag("company_screen_${company.slug}"),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        // Back Button Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
            }
        }

        // Company Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = company.name.take(2).uppercase(),
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        }

                        Column {
                            Text(
                                text = if (language == AppLanguage.AR) "ثيمات ${company.name}" else "${company.name} THEMES",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (language == AppLanguage.AR) "${themesForCompany.size} ثيم متاح" else "${themesForCompany.size} Themes available",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }

                    if (company.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = company.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Sort Options
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
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
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Themes Grid
        if (themesForCompany.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = S18Strings.get("no_themes", language),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(themesForCompany.chunked(2)) { pair ->
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
