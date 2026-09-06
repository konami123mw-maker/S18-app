package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.S18Strings
import com.example.ui.ThemeViewModel
import com.example.util.DeviceCompatibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceCompatibilityScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
    initialBrandSlug: String? = null
) {
    val language by viewModel.language.collectAsState()
    val accentColorHex by viewModel.accentColorHex.collectAsState()
    val accentColor = remember(accentColorHex) {
        try { Color(android.graphics.Color.parseColor(accentColorHex)) }
        catch (e: Exception) { Color(0xFF00E5FF) }
    }
    val context = LocalContext.current
    val deviceInfo = remember { DeviceCompatibility.getDeviceInfo() }

    val brands = listOf(
        "honor" to "HONOR (MagicOS)",
        "xiaomi" to "Xiaomi / POCO (HyperOS / MIUI)",
        "huawei" to "HUAWEI (EMUI / HarmonyOS)",
        "generic" to if (language == AppLanguage.AR) "أجهزة أندرويد أخرى" else "Other Android Brands"
    )

    // Select initial brand matching either requested slug or detected device
    val defaultBrandSlug = remember {
        if (!initialBrandSlug.isNullOrBlank()) initialBrandSlug
        else when {
            deviceInfo.manufacturer.contains("honor", ignoreCase = true) -> "honor"
            deviceInfo.manufacturer.contains("xiaomi", ignoreCase = true) ||
                    deviceInfo.manufacturer.contains("poco", ignoreCase = true) ||
                    deviceInfo.manufacturer.contains("redmi", ignoreCase = true) -> "xiaomi"
            deviceInfo.manufacturer.contains("huawei", ignoreCase = true) -> "huawei"
            else -> "honor"
        }
    }

    var selectedBrand by remember { mutableStateOf(defaultBrandSlug) }
    val guide = remember(selectedBrand, language) {
        DeviceCompatibility.getInstallationGuide(selectedBrand, language)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = S18Strings.get("installation_guides_all", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Detected Device Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(accentColor.copy(alpha = 0.6f), Color(0xFF00E676).copy(alpha = 0.4f))
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Smartphone,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = S18Strings.get("detected_device", language),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${deviceInfo.manufacturer} ${deviceInfo.model}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF00E676).copy(alpha = 0.15f),
                                border = null
                            ) {
                                Text(
                                    text = deviceInfo.detectedOs,
                                    color = Color(0xFF00E676),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Android ${deviceInfo.androidVersion} (API ${deviceInfo.sdkInt})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (language == AppLanguage.AR) "جاهز لتطبيق الثيمات ✓" else "Ready for Themes ✓",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF00E676)
                            )
                        }
                    }
                }
            }

            // Brand Selector Chips
            item {
                Text(
                    text = if (language == AppLanguage.AR) "اختر واجهة جهازك لعرض الدليل:" else "Select your device interface:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(brands) { (slug, label) ->
                        val isSelected = selectedBrand == slug
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedBrand = slug },
                            label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = accentColor.copy(alpha = 0.2f),
                                selectedLabelColor = accentColor
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }

            // Target Folder & Extension Quick Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.AR) "امتداد الملف المطلوب:" else "Required File Extension:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = guide.fileExtension,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = accentColor
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (language == AppLanguage.AR) "مسار المجلد بالجهاز:" else "Target Folder Path:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = guide.targetFolder,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Path",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Folder Path", guide.targetFolder))
                                            Toast.makeText(context, "Path copied!", Toast.LENGTH_SHORT).show()
                                        }
                                )
                            }
                        }
                    }
                }
            }

            // Guide Steps Header
            item {
                Text(
                    text = guide.brandTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Steps
            items(guide.steps) { step ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f))
                                .border(1.dp, accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${step.stepNumber}",
                                color = accentColor,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = step.description,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Tips Section
            if (guide.tips.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF8F00)))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (language == AppLanguage.AR) "نصائح مهمة لنجاح التثبيت" else "Important Installation Tips",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            guide.tips.forEach { tip ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("•", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                                    Text(
                                        text = tip,
                                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
