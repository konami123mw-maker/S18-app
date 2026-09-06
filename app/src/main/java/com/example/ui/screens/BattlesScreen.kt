package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.SportsKabaddi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.ThemeBattle
import com.example.data.ThemeFullItem
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.Screen
import com.example.ui.ThemeViewModel

@Composable
fun BattlesScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val battles by viewModel.activeBattles.collectAsStateWithLifecycle()
    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("battles_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
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

                Column {
                    Text(
                        text = S18Strings.get("theme_battles", language),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = S18Strings.get("active_battles", language),
                        fontSize = 12.sp,
                        color = Color(0xFF00E5FF)
                    )
                }
            }
        }

        if (battles.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد معارك نشطة حالياً",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(battles) { battle ->
                val themeA = fullThemes.find { it.theme.id == battle.themeAId }
                val themeB = fullThemes.find { it.theme.id == battle.themeBId }

                BattleCardItem(
                    battle = battle,
                    themeA = themeA,
                    themeB = themeB,
                    language = language,
                    onVoteA = { viewModel.voteInBattle(battle.id, battle.themeAId) },
                    onVoteB = { viewModel.voteInBattle(battle.id, battle.themeBId) },
                    onThemeClick = { slug -> viewModel.navigateTo(Screen.ThemeDetails(slug)) }
                )
            }
        }
    }
}

@Composable
fun BattleCardItem(
    battle: ThemeBattle,
    themeA: ThemeFullItem?,
    themeB: ThemeFullItem?,
    language: AppLanguage,
    onVoteA: () -> Unit,
    onVoteB: () -> Unit,
    onThemeClick: (String) -> Unit
) {
    val totalVotes = (battle.themeAVotes + battle.themeBVotes).coerceAtLeast(1)
    val ratioA = battle.themeAVotes.toFloat() / totalVotes
    val ratioB = battle.themeBVotes.toFloat() / totalVotes

    val animatedRatioA by animateFloatAsState(
        targetValue = ratioA,
        animationSpec = tween(600),
        label = "ratioA"
    )

    val percentA = (ratioA * 100).toInt()
    val percentB = (ratioB * 100).toInt()

    val battleTitle = if (language == AppLanguage.AR && battle.titleAr.isNotBlank()) battle.titleAr else battle.title

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(Color(0x6000E5FF), Color(0x60FF0055)))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Battle Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0x30FF6D00), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsKabaddi,
                            contentDescription = null,
                            tint = Color(0xFFFF6D00),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = battleTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x2000E5FF)
                ) {
                    Text(
                        text = "${battle.themeAVotes + battle.themeBVotes} ${S18Strings.get("votes", language)}",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Two Themes side-by-side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Theme A
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(9f / 14f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0x4000E5FF), RoundedCornerShape(16.dp))
                            .clickable { themeA?.let { onThemeClick(it.theme.slug) } }
                    ) {
                        AsyncImage(
                            model = themeA?.theme?.coverImageUrl ?: "",
                            contentDescription = themeA?.theme?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (battle.themeAVotes > battle.themeBVotes) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xEE10B981),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = S18Strings.get("winner", language),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = themeA?.theme?.name ?: "Theme A",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = themeA?.company?.name ?: "",
                        fontSize = 11.sp,
                        color = Color(0xFF00E5FF)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onVoteA,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF),
                            contentColor = Color(0xFF031024)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.HowToVote,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${S18Strings.get("vote_now", language)} ($percentA%)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // VS Divider
                Column(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF1E293B), CircleShape)
                            .border(1.dp, Color(0x40FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VS",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color(0xFFFF0055)
                        )
                    }
                }

                // Theme B
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(9f / 14f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0x40FF0055), RoundedCornerShape(16.dp))
                            .clickable { themeB?.let { onThemeClick(it.theme.slug) } }
                    ) {
                        AsyncImage(
                            model = themeB?.theme?.coverImageUrl ?: "",
                            contentDescription = themeB?.theme?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (battle.themeBVotes > battle.themeAVotes) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xEE10B981),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = S18Strings.get("winner", language),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = themeB?.theme?.name ?: "Theme B",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = themeB?.company?.name ?: "",
                        fontSize = 11.sp,
                        color = Color(0xFFFF0055)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onVoteB,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF0055),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.HowToVote,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${S18Strings.get("vote_now", language)} ($percentB%)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Percentage Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFF0055))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedRatioA)
                        .height(8.dp)
                        .background(Color(0xFF00E5FF))
                )
            }
        }
    }
}
