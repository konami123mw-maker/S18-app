package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Company
import com.example.data.Designer
import com.example.data.ThemeEntity
import com.example.data.ThemeFullItem
import com.example.data.ThemePreview
import com.example.data.ThemeVersion
import com.example.ui.AppLanguage
import com.example.ui.ThemeViewModel
import com.example.ui.components.ThemeImage
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CyberElectricBlue
import com.example.ui.theme.CyberMint
import com.example.util.ThemeImageUtils
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Theme Publishing Studio
 * A comprehensive, professional publishing studio with Liquid Glass aesthetic,
 * mobile-first layout (1200x2640 ratio), real gallery image selection,
 * multi-preview management, persistent internal storage copy, and real theme package file upload.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThemePublishingStudio(
    viewModel: ThemeViewModel,
    themeId: Long? = null,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsStateWithLifecycle()
    val companies by viewModel.allCompanies.collectAsStateWithLifecycle()
    val designers by viewModel.allDesigners.collectAsStateWithLifecycle()
    val fullThemes by viewModel.fullThemes.collectAsStateWithLifecycle()

    val isAr = language == AppLanguage.AR

    // If themeId is provided, find existing theme for editing
    val existingItem = remember(themeId, fullThemes) {
        if (themeId != null) fullThemes.find { it.theme.id == themeId } else null
    }

    // State Fields
    var themeName by remember { mutableStateOf(existingItem?.theme?.name ?: "") }
    var slug by remember { mutableStateOf(existingItem?.theme?.slug ?: "") }
    var selectedCompanyId by remember { mutableStateOf(existingItem?.theme?.companyId ?: (companies.firstOrNull()?.id ?: 1L)) }
    var selectedDesignerId by remember { mutableStateOf(existingItem?.theme?.designerId ?: (designers.firstOrNull()?.id ?: 1L)) }
    var versionStr by remember { mutableStateOf(existingItem?.latestVersion?.version ?: "1.0") }

    // Cover Image State (1200 x 2640 aspect ratio)
    var coverImageUrl by remember { mutableStateOf(existingItem?.theme?.coverImageUrl ?: "") }

    // Preview Images State
    val previewUrls = remember {
        mutableStateListOf<String>().apply {
            if (existingItem != null) {
                addAll(existingItem.previews.sortedBy { it.sortOrder }.map { it.imageUrl })
            }
        }
    }

    // Descriptions
    var descriptionAr by remember {
        mutableStateOf(
            existingItem?.theme?.descriptionAr?.ifEmpty { null }
                ?: existingItem?.theme?.description ?: ""
        )
    }
    var descriptionEn by remember {
        mutableStateOf(
            existingItem?.theme?.descriptionEn?.ifEmpty { null }
                ?: existingItem?.theme?.description ?: ""
        )
    }

    // Package / Download File
    var packageUrl by remember { mutableStateOf(existingItem?.latestVersion?.downloadUrl ?: "") }
    var packageFileName by remember { mutableStateOf(existingItem?.theme?.packageFileName ?: "") }
    var packageFileSize by remember { mutableStateOf(existingItem?.theme?.packageFileSize ?: 0L) }

    // Tags / Categories
    var selectedTags by remember {
        mutableStateOf(
            existingItem?.theme?.tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet()
                ?: setOf("Cyber", "AMOLED")
        )
    }
    var customTagInput by remember { mutableStateOf("") }

    // Advanced Flags
    var isFeatured by remember { mutableStateOf(existingItem?.theme?.featured ?: false) }
    var isPublished by remember { mutableStateOf(existingItem?.theme?.published ?: true) }
    var isDraft by remember { mutableStateOf(existingItem?.theme?.isDraft ?: false) }
    var isPrivatePreview by remember { mutableStateOf(existingItem?.theme?.isPrivatePreview ?: false) }
    var selectedAccentColor by remember { mutableStateOf(existingItem?.theme?.accentColor ?: "#00E5FF") }
    var changelog by remember { mutableStateOf(existingItem?.latestVersion?.changelog ?: "") }
    var previewLayoutTemplate by remember { mutableStateOf(existingItem?.theme?.previewLayoutTemplate ?: "VERTICAL_GRID") }

    // UI Dialog States
    var showLivePreview by remember { mutableStateOf(false) }
    var showAddCompanyDialog by remember { mutableStateOf(false) }
    var showAddDesignerDialog by remember { mutableStateOf(false) }
    var showUrlInputDialog by remember { mutableStateOf<String?>(null) } // "cover" or "preview"

    // Auto slug generator
    LaunchedEffect(themeName) {
        if (existingItem == null && slug.isBlank() && themeName.isNotBlank()) {
            slug = themeName.trim().lowercase(Locale.ENGLISH)
                .replace(Regex("[^a-z0-9\\-_]"), "-")
                .replace(Regex("-+"), "-")
                .trim('-')
        }
    }

    // Gallery Picker for Cover Image
    val coverImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val persistedPath = ThemeImageUtils.persistImageUri(context, it)
            coverImageUrl = persistedPath
            viewModel.addMediaItem(
                fileName = ThemeImageUtils.getFileNameFromUri(context, it),
                fileUri = persistedPath,
                fileType = "COVER_IMAGE",
                fileSize = 0L,
                title = themeName.ifBlank { "Cover Image" }
            )
            Toast.makeText(context, if (isAr) "تم حفظ صورة الغلاف بنجاح ✓" else "Cover image saved ✓", Toast.LENGTH_SHORT).show()
        }
    }

    // Multiple Gallery Picker for Preview Images
    val multiPreviewPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                val persistedPath = ThemeImageUtils.persistImageUri(context, uri)
                previewUrls.add(persistedPath)
                viewModel.addMediaItem(
                    fileName = ThemeImageUtils.getFileNameFromUri(context, uri),
                    fileUri = persistedPath,
                    fileType = "PREVIEW_IMAGE",
                    fileSize = 0L,
                    title = themeName.ifBlank { "Preview" }
                )
            }
            Toast.makeText(context, if (isAr) "تمت إضافة ${uris.size} صور معاينة بنجاح" else "Added ${uris.size} preview images", Toast.LENGTH_SHORT).show()
        }
    }

    // Theme Package File Picker
    val packageFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = ThemeImageUtils.getFileNameFromUri(context, it)
            val (persistedPath, sizeBytes) = ThemeImageUtils.persistThemeFile(context, it)
            packageUrl = persistedPath
            packageFileName = fileName
            packageFileSize = sizeBytes
            viewModel.addMediaItem(
                fileName = fileName,
                fileUri = persistedPath,
                fileType = "THEME_PACKAGE",
                fileSize = sizeBytes,
                title = themeName.ifBlank { "Theme Package" }
            )
            Toast.makeText(context, if (isAr) "تم رفع ملف الثيم بنجاح ($fileName)" else "Theme package loaded ($fileName)", Toast.LENGTH_SHORT).show()
        }
    }

    // Save/Publish Function
    fun saveTheme(asDraftMode: Boolean = false) {
        val cleanName = themeName.trim()
        if (cleanName.isBlank()) {
            Toast.makeText(context, if (isAr) "يرجى كتابة اسم الثيم" else "Please enter theme name", Toast.LENGTH_SHORT).show()
            return
        }
        val cleanSlug = if (slug.isNotBlank()) slug.trim() else cleanName.lowercase().replace(" ", "-")

        val effectiveDescription = if (isAr) {
            descriptionAr.ifBlank { descriptionEn }
        } else {
            descriptionEn.ifBlank { descriptionAr }
        }

        val themeEntity = ThemeEntity(
            id = existingItem?.theme?.id ?: 0L,
            companyId = selectedCompanyId,
            designerId = selectedDesignerId,
            name = cleanName,
            slug = cleanSlug,
            description = effectiveDescription,
            descriptionAr = descriptionAr,
            descriptionEn = descriptionEn,
            coverImageUrl = coverImageUrl.ifBlank { previewUrls.firstOrNull() ?: "" },
            tags = selectedTags.joinToString(","),
            featured = isFeatured,
            published = if (asDraftMode) false else isPublished,
            accentColor = selectedAccentColor,
            packageFileName = packageFileName,
            packageFileSize = packageFileSize,
            isDraft = asDraftMode || isDraft,
            isPrivatePreview = isPrivatePreview,
            previewLayoutTemplate = previewLayoutTemplate,
            updatedAt = System.currentTimeMillis(),
            createdAt = existingItem?.theme?.createdAt ?: System.currentTimeMillis()
        )

        val version = ThemeVersion(
            id = existingItem?.latestVersion?.id ?: 0L,
            themeId = themeEntity.id,
            version = versionStr.ifBlank { "1.0" },
            downloadUrl = packageUrl,
            changelog = changelog,
            published = !asDraftMode && isPublished,
            updatedAt = System.currentTimeMillis()
        )

        val previews = previewUrls.mapIndexed { index, url ->
            ThemePreview(
                id = 0L,
                themeId = themeEntity.id,
                imageUrl = url,
                sortOrder = index
            )
        }

        viewModel.saveFullTheme(
            theme = themeEntity,
            version = version,
            previews = previews,
            onComplete = {
                viewModel.logAdminAction(
                    if (existingItem != null) "Edit Theme" else "Publish Theme",
                    "Saved theme '${themeEntity.name}' (${if (asDraftMode) "Draft" else "Published"})"
                )
                Toast.makeText(
                    context,
                    if (isAr) {
                        if (asDraftMode) "تم حفظ الثيم كمسودة بنجاح ✓" else "تم نشر الثيم بنجاح في المتجر! ✓"
                    } else {
                        if (asDraftMode) "Theme saved as draft ✓" else "Theme published successfully! ✓"
                    },
                    Toast.LENGTH_LONG
                ).show()
                onDismiss()
            }
        )
    }

    // Main Studio Container with Liquid Glass Styling
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("theme_publishing_studio")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Studio Header (Liquid Glass)
            StudioTopHeader(
                isAr = isAr,
                isEditing = existingItem != null,
                onClose = onDismiss,
                onLivePreview = { showLivePreview = true }
            )

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Basic Information Glass Card
                item {
                    BasicInfoGlassCard(
                        isAr = isAr,
                        themeName = themeName,
                        onThemeNameChange = { themeName = it },
                        slug = slug,
                        onSlugChange = { slug = it },
                        companies = companies,
                        selectedCompanyId = selectedCompanyId,
                        onSelectCompany = { selectedCompanyId = it },
                        onAddNewCompany = { showAddCompanyDialog = true },
                        designers = designers,
                        selectedDesignerId = selectedDesignerId,
                        onSelectDesigner = { selectedDesignerId = it },
                        onAddNewDesigner = { showAddDesignerDialog = true },
                        versionStr = versionStr,
                        onVersionChange = { versionStr = it },
                        selectedTags = selectedTags,
                        onToggleTag = { tag ->
                            selectedTags = if (selectedTags.contains(tag)) {
                                selectedTags - tag
                            } else {
                                selectedTags + tag
                            }
                        },
                        customTagInput = customTagInput,
                        onCustomTagChange = { customTagInput = it },
                        onAddCustomTag = {
                            if (customTagInput.isNotBlank()) {
                                selectedTags = selectedTags + customTagInput.trim()
                                customTagInput = ""
                            }
                        }
                    )
                }

                // Section 2: Cover Image (1200 x 2640 Ratio)
                item {
                    CoverImageGlassCard(
                        isAr = isAr,
                        coverImageUrl = coverImageUrl,
                        onPickFromGallery = { coverImagePicker.launch("image/*") },
                        onEnterUrl = { showUrlInputDialog = "cover" },
                        onRemoveCover = { coverImageUrl = "" }
                    )
                }

                // Section 3: Previews Gallery (1200 x 2640 Ratio Multiple)
                item {
                    PreviewsGalleryGlassCard(
                        isAr = isAr,
                        previewUrls = previewUrls,
                        onPickMultiGallery = { multiPreviewPicker.launch("image/*") },
                        onEnterUrl = { showUrlInputDialog = "preview" },
                        onSetAsCover = { url ->
                            coverImageUrl = url
                            Toast.makeText(context, if (isAr) "تم تعيين الصورة كغلاف رئيسي" else "Set as cover image", Toast.LENGTH_SHORT).show()
                        },
                        onMoveUp = { idx ->
                            if (idx > 0) {
                                val item = previewUrls.removeAt(idx)
                                previewUrls.add(idx - 1, item)
                            }
                        },
                        onMoveDown = { idx ->
                            if (idx < previewUrls.size - 1) {
                                val item = previewUrls.removeAt(idx)
                                previewUrls.add(idx + 1, item)
                            }
                        },
                        onRemove = { idx ->
                            previewUrls.removeAt(idx)
                        }
                    )
                }

                // Section 4: Arabic Description
                item {
                    DescriptionGlassCard(
                        title = if (isAr) "بطاقة الوصف باللغة العربية" else "Arabic Description",
                        subtitle = if (isAr) "اكتب وصفاً مفصلاً يبرز مميزات الثيم للمستخدمين باللغة العربية" else "Detailed description in Arabic",
                        value = descriptionAr,
                        onValueChange = { descriptionAr = it },
                        placeholder = if (isAr) "مثال: ثيم نيون مستقبلي متكامل لشاشات الأموليد، يتميز بأيقونات زجاجية وخلفيات تفاعلية عالية الدقة..." else "Write Arabic description...",
                        quickSnippets = listOf(
                            "✨ مميزات الثيم الرئيسية:" to "\n\n✨ مميزات الثيم:\n• أيقونات نيون ثلاثية الأبعاد بدقة فائقة\n• دعم كامل لشاشات AMOLED مع توفير استهلاك البطارية\n• ودجات تفاعلية للساعة والطقس والبطارية",
                            "📱 الأجهزة المتوافقة:" to "\n\n📱 الأجهزة المتوافقة:\n• متوافق مع كافة الهواتف الذكية بنظام أندرويد 12 فأحدث\n• يدعم واجهات HyperOS, OneUI, ColorOS",
                            "⚙️ طريقة التثبيت:" to "\n\n⚙️ طريقة التثبيت:\n1. قم بتحميل ملف الثيم بالضغط على زر التحميل\n2. افتح تطبيق الثيمات على هاتفك واستورد الملف\n3. اضغط تطبيق وأعد تشغيل جهازك"
                        )
                    )
                }

                // Section 5: English Description
                item {
                    DescriptionGlassCard(
                        title = if (isAr) "بطاقة الوصف باللغة الإنجليزية" else "English Description",
                        subtitle = if (isAr) "الوصف بالإنجليزية للظهور للمستخدمين العالميين" else "Global English description",
                        value = descriptionEn,
                        onValueChange = { descriptionEn = it },
                        placeholder = "Example: Futuristic Cyberpunk theme with deep AMOLED blacks, glowing neon accents, and custom glass icons...",
                        quickSnippets = listOf(
                            "✨ Key Features:" to "\n\n✨ Key Features:\n• Custom 3D neon icons\n• Pure AMOLED black battery-saving backgrounds\n• Dynamic clock & weather widgets",
                            "📱 Compatibility:" to "\n\n📱 Compatibility:\n• Fully compatible with Android 12+\n• Supports OneUI, HyperOS, MIUI, OxygenOS"
                        )
                    )
                }

                // Section 6: Theme Download Package
                item {
                    ThemePackageGlassCard(
                        isAr = isAr,
                        packageUrl = packageUrl,
                        packageFileName = packageFileName,
                        packageFileSize = packageFileSize,
                        onPickFile = { packageFilePicker.launch(arrayOf("*/*")) },
                        onPackageUrlChange = {
                            packageUrl = it
                            if (it.isNotBlank() && packageFileName.isBlank()) {
                                packageFileName = it.substringAfterLast("/")
                            }
                        }
                    )
                }

                // Section 7: Advanced Studio Settings
                item {
                    AdvancedSettingsGlassCard(
                        isAr = isAr,
                        isFeatured = isFeatured,
                        onToggleFeatured = { isFeatured = it },
                        isPublished = isPublished,
                        onTogglePublished = { isPublished = it },
                        isDraft = isDraft,
                        onToggleDraft = { isDraft = it },
                        isPrivatePreview = isPrivatePreview,
                        onTogglePrivatePreview = { isPrivatePreview = it },
                        selectedAccentColor = selectedAccentColor,
                        onSelectAccentColor = { selectedAccentColor = it },
                        changelog = changelog,
                        onChangelogChange = { changelog = it },
                        previewLayoutTemplate = previewLayoutTemplate,
                        onSelectPreviewLayout = { previewLayoutTemplate = it }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Fixed Liquid Glass Bottom Bar
            StudioBottomControls(
                isAr = isAr,
                onCancel = onDismiss,
                onSaveDraft = { saveTheme(asDraftMode = true) },
                onLivePreview = { showLivePreview = true },
                onPublish = { saveTheme(asDraftMode = false) }
            )
        }

        // Live Preview Dialog
        if (showLivePreview) {
            StudioLivePreviewDialog(
                isAr = isAr,
                themeName = themeName.ifBlank { "S18 Theme Preview" },
                company = companies.find { it.id == selectedCompanyId },
                designer = designers.find { it.id == selectedDesignerId },
                coverImageUrl = coverImageUrl.ifBlank { previewUrls.firstOrNull() ?: "" },
                previewUrls = previewUrls,
                description = if (isAr) descriptionAr.ifBlank { descriptionEn } else descriptionEn.ifBlank { descriptionAr },
                tags = selectedTags.toList(),
                version = versionStr,
                accentColor = selectedAccentColor,
                onDismiss = { showLivePreview = false }
            )
        }

        // URL Input Dialog
        showUrlInputDialog?.let { target ->
            var inputUrl by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showUrlInputDialog = null },
                title = { Text(if (isAr) "إدخال رابط الصورة" else "Enter Image URL") },
                text = {
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        placeholder = { Text("https://example.com/image.jpg") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (inputUrl.isNotBlank()) {
                                if (target == "cover") {
                                    coverImageUrl = inputUrl.trim()
                                } else {
                                    previewUrls.add(inputUrl.trim())
                                }
                                showUrlInputDialog = null
                            }
                        }
                    ) {
                        Text(if (isAr) "إضافة" else "Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUrlInputDialog = null }) {
                        Text(if (isAr) "إلغاء" else "Cancel")
                    }
                }
            )
        }

        // Quick Add Company Dialog
        if (showAddCompanyDialog) {
            QuickAddCompanyDialog(
                viewModel = viewModel,
                isAr = isAr,
                onCompanyAdded = { newId ->
                    selectedCompanyId = newId
                    showAddCompanyDialog = false
                },
                onDismiss = { showAddCompanyDialog = false }
            )
        }

        // Quick Add Designer Dialog
        if (showAddDesignerDialog) {
            QuickAddDesignerDialog(
                viewModel = viewModel,
                isAr = isAr,
                onDesignerAdded = { newId ->
                    selectedDesignerId = newId
                    showAddDesignerDialog = false
                },
                onDismiss = { showAddDesignerDialog = false }
            )
        }
    }
}

// -------------------------------------------------------------
// Sub-Components & Glass Cards
// -------------------------------------------------------------

@Composable
private fun StudioTopHeader(
    isAr: Boolean,
    isEditing: Boolean,
    onClose: () -> Unit,
    onLivePreview: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, CyanNeon.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isAr) {
                                if (isEditing) "تعديل الثيم • Theme Studio" else "Theme Publishing Studio"
                            } else {
                                if (isEditing) "Edit Theme • Theme Studio" else "Theme Publishing Studio"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = CyanNeon
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyanNeon.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, CyanNeon)
                        ) {
                            Text(
                                text = "PRO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanNeon,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isAr) "نظام نشر متكامل • صور من المعرض • أبعاد 1200x2640" else "Mobile-First Publishing • Direct Gallery Picker",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedButton(
                onClick = onLivePreview,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, CyberElectricBlue.copy(alpha = 0.7f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = CyberElectricBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAr) "معاينة" else "Preview",
                    fontSize = 12.sp,
                    color = CyberElectricBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Section 1: Basic Information Glass Card
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BasicInfoGlassCard(
    isAr: Boolean,
    themeName: String,
    onThemeNameChange: (String) -> Unit,
    slug: String,
    onSlugChange: (String) -> Unit,
    companies: List<Company>,
    selectedCompanyId: Long,
    onSelectCompany: (Long) -> Unit,
    onAddNewCompany: () -> Unit,
    designers: List<Designer>,
    selectedDesignerId: Long,
    onSelectDesigner: (Long) -> Unit,
    onAddNewDesigner: () -> Unit,
    versionStr: String,
    onVersionChange: (String) -> Unit,
    selectedTags: Set<String>,
    onToggleTag: (String) -> Unit,
    customTagInput: String,
    onCustomTagChange: (String) -> Unit,
    onAddCustomTag: () -> Unit
) {
    LiquidGlassContainer(
        title = if (isAr) "1. بطاقة المعلومات الأساسية" else "1. Basic Information",
        icon = Icons.Default.Info
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Theme Name
            OutlinedTextField(
                value = themeName,
                onValueChange = onThemeNameChange,
                label = { Text(if (isAr) "اسم الثيم *" else "Theme Name *") },
                placeholder = { Text(if (isAr) "مثال: Cyber Neon AMOLED" else "e.g. Cyber Neon AMOLED") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Slug
            OutlinedTextField(
                value = slug,
                onValueChange = onSlugChange,
                label = { Text(if (isAr) "الرابط الفريد (Slug)" else "Slug (Unique ID)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Brand / Company Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "الشركة / البراند:" else "Brand / Company:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    TextButton(onClick = onAddNewCompany, contentPadding = PaddingValues(horizontal = 4.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isAr) "إضافة شركة جديدة" else "New Brand", fontSize = 12.sp)
                    }
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(companies) { company ->
                        val isSelected = company.id == selectedCompanyId
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CyanNeon.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (isSelected) CyanNeon else Color.Transparent),
                            modifier = Modifier.clickable { onSelectCompany(company.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                                }
                                Text(
                                    text = company.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CyanNeon else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Designer Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "المصمم المعتمد:" else "Designer:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    TextButton(onClick = onAddNewDesigner, contentPadding = PaddingValues(horizontal = 4.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isAr) "إضافة مصمم جديد" else "New Designer", fontSize = 12.sp)
                    }
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(designers) { designer ->
                        val isSelected = designer.id == selectedDesignerId
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CyberElectricBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (isSelected) CyberElectricBlue else Color.Transparent),
                            modifier = Modifier.clickable { onSelectDesigner(designer.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = CyberElectricBlue, modifier = Modifier.size(14.dp))
                                }
                                Text(
                                    text = designer.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CyberElectricBlue else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Version number
            OutlinedTextField(
                value = versionStr,
                onValueChange = onVersionChange,
                label = { Text(if (isAr) "رقم الإصدار (Version)" else "Version (e.g. 1.0)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Categories / Tags
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isAr) "التصنيفات والوسوم (اختر من القائمة أو أضف جديداً):" else "Categories & Tags:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                val standardTags = listOf(
                    "AMOLED", "Minimal", "Cyber", "Dark", "iOS", "HyperOS", "OneUI",
                    "Neon", "Nature", "Abstract", "Gaming", "Anime", "Futuristic", "Retro"
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    standardTags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CyanNeon.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, if (isSelected) CyanNeon else Color.Transparent),
                            modifier = Modifier.clickable { onToggleTag(tag) }
                        ) {
                            Text(
                                text = "#$tag",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Add custom tag input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customTagInput,
                        onValueChange = onCustomTagChange,
                        placeholder = { Text(if (isAr) "أضف وسم مخصص..." else "Custom tag...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Button(
                        onClick = onAddCustomTag,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color(0xFF031024))
                    ) {
                        Text(if (isAr) "إضافة" else "Add")
                    }
                }
            }
        }
    }
}

/**
 * Section 2: Cover Image Glass Card (1200 x 2640 Ratio)
 */
@Composable
private fun CoverImageGlassCard(
    isAr: Boolean,
    coverImageUrl: String,
    onPickFromGallery: () -> Unit,
    onEnterUrl: () -> Unit,
    onRemoveCover: () -> Unit
) {
    LiquidGlassContainer(
        title = if (isAr) "2. بطاقة صورة الغلاف (1200 × 2640)" else "2. Cover Image (1200x2640)",
        icon = Icons.Default.Image
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = if (isAr) {
                    "يتم عرض صورة الغلاف في واجهة المتجر وشاشة المعاينة بأبعاد شاشة الهاتف الكاملة (1200 × 2640 بكسل). اختر صورة مباشرة من معرض جهازك:"
                } else {
                    "Cover image is displayed in mobile aspect ratio (1200x2640). Pick directly from your phone gallery:"
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // Image Preview Mockup Frame
            if (coverImageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ThemeImage(
                        imageUrl = coverImageUrl,
                        contentDescription = "Cover Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1200f / 2640f),
                        contentScale = ContentScale.Crop
                    )

                    // Badge top right
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "1200 × 2640 • الغلاف",
                            fontSize = 10.sp,
                            color = CyanNeon,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    // Bottom action overlay
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onPickFromGallery) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isAr) "استبدال الصورة" else "Replace", color = CyanNeon, fontSize = 12.sp)
                        }
                        TextButton(onClick = onRemoveCover) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isAr) "حذف" else "Remove", color = Color(0xFFFF5252), fontSize = 12.sp)
                        }
                    }
                }
            } else {
                // Empty Picker Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                        .border(1.dp, CyanNeon.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .clickable { onPickFromGallery() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = if (isAr) "اضغط هنا لاختيار صورة الغلاف من المعرض" else "Tap here to pick cover image from Gallery",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = CyanNeon
                        )
                        Text(
                            text = if (isAr) "النسبة المثالية: 1200 × 2640 بكسل" else "Target Ratio: 1200x2640 px",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Direct Picker Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPickFromGallery,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color(0xFF031024)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isAr) "فتح معرض الصور" else "Device Gallery", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onEnterUrl,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isAr) "إدخال رابط URL" else "Paste Image URL", fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * Section 3: Previews Gallery Glass Card (Multi-select, reorder, delete, set as cover)
 */
@Composable
private fun PreviewsGalleryGlassCard(
    isAr: Boolean,
    previewUrls: List<String>,
    onPickMultiGallery: () -> Unit,
    onEnterUrl: () -> Unit,
    onSetAsCover: (String) -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onRemove: (Int) -> Unit
) {
    LiquidGlassContainer(
        title = if (isAr) "3. بطاقة صور المعاينة (${previewUrls.size} صور)" else "3. Preview Gallery (${previewUrls.size})",
        icon = Icons.Default.PhotoLibrary
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = if (isAr) {
                    "يمكنك اختيار عدة صور معاً من معرض الهاتف. تظهر للمستخدمين كمعرض صور هاتفية (1200 × 2640). يمكنك تغيير الترتيب أو اختيار أي صورة كغلاف:"
                } else {
                    "Pick multiple images from device gallery. Reorder, set as cover, or remove:"
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // Horizontal Preview Cards
            if (previewUrls.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    itemsIndexed(previewUrls) { index, url ->
                        PreviewPhoneItem(
                            isAr = isAr,
                            index = index,
                            total = previewUrls.size,
                            imageUrl = url,
                            onSetAsCover = { onSetAsCover(url) },
                            onMoveLeft = { onMoveUp(index) },
                            onMoveRight = { onMoveDown(index) },
                            onRemove = { onRemove(index) }
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = if (isAr) "لم تتم إضافة أي صور معاينة بعد. اضغط على الزر بالأسفل لاختيار صور من المعرض." else "No preview images added yet.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPickMultiGallery,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberElectricBlue, contentColor = Color(0xFF031024)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isAr) "إضافة صور من المعرض" else "Pick from Gallery", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onEnterUrl,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isAr) "إضافة برابط URL" else "Add via URL", fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * Single Phone Mockup Item in Previews Gallery
 */
@Composable
private fun PreviewPhoneItem(
    isAr: Boolean,
    index: Int,
    total: Int,
    imageUrl: String,
    onSetAsCover: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(260.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, CyanNeon.copy(alpha = 0.35f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image
            ThemeImage(
                imageUrl = imageUrl,
                contentDescription = "Preview #$index",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Top overlay badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
            ) {
                Text(
                    text = "#${index + 1}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanNeon,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Remove button top end
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(28.dp)
                    .background(Color.Black.copy(alpha = 0.65f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(14.dp)
                )
            }

            // Bottom controls overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Set as cover
                Text(
                    text = if (isAr) "★ تعيين كغلاف" else "★ Set as Cover",
                    fontSize = 10.sp,
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onSetAsCover() }
                        .padding(vertical = 2.dp)
                )

                // Move left/right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (index > 0) {
                        Text(
                            text = "◀",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .clickable { onMoveLeft() }
                                .padding(2.dp)
                        )
                    }
                    if (index < total - 1) {
                        Text(
                            text = "▶",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .clickable { onMoveRight() }
                                .padding(2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section 4 & 5: Description Glass Card
 */
@Composable
private fun DescriptionGlassCard(
    title: String,
    subtitle: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    quickSnippets: List<Pair<String, String>>
) {
    LiquidGlassContainer(title = title, icon = Icons.Default.Description) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Character count & quick snippets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${value.length} حرف",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickSnippets.forEach { (label, snippet) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyanNeon.copy(alpha = 0.12f),
                            border = BorderStroke(0.5.dp, CyanNeon.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable {
                                onValueChange(value + snippet)
                            }
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = CyanNeon,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section 6: Theme Download Package Glass Card
 */
@Composable
private fun ThemePackageGlassCard(
    isAr: Boolean,
    packageUrl: String,
    packageFileName: String,
    packageFileSize: Long,
    onPickFile: () -> Unit,
    onPackageUrlChange: (String) -> Unit
) {
    LiquidGlassContainer(
        title = if (isAr) "6. بطاقة ملف الثيم للتحميل" else "6. Download Package",
        icon = Icons.Default.AttachFile
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = if (isAr) {
                    "يمكنك اختيار ملف الثيم (.zip, .mtz, .hwt, .theme) مباشرة من ذاكرة جهازك، أو إدخال رابط تحميل مباشر:"
                } else {
                    "Select theme file (.zip, .mtz, .hwt, .theme) directly from device storage or enter direct URL:"
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // File Loaded State
            if (packageFileName.isNotBlank() || packageUrl.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyberMint.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, CyberMint.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberMint, modifier = Modifier.size(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = packageFileName.ifBlank { "theme_package.zip" },
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = CyberMint,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (packageFileSize > 0) {
                                val sizeMb = String.format(Locale.US, "%.1f MB", packageFileSize / (1024f * 1024f))
                                Text(text = "حجم الملف: $sizeMb", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        TextButton(onClick = onPickFile) {
                            Text(if (isAr) "تغيير" else "Change", fontSize = 12.sp, color = CyberMint)
                        }
                    }
                }
            }

            // Pick File from Device Button
            Button(
                onClick = onPickFile,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color(0xFF031024)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAr) "اختيار ملف الثيم من ذاكرة الهاتف" else "Select Theme File from Storage",
                    fontWeight = FontWeight.Bold
                )
            }

            // Or direct URL input
            OutlinedTextField(
                value = packageUrl,
                onValueChange = onPackageUrlChange,
                label = { Text(if (isAr) "أو أدخل رابط تحميل خارجي مباشر (URL)" else "Or External Download URL") },
                placeholder = { Text("https://example.com/themes/cyber.zip") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
        }
    }
}

/**
 * Section 7: Advanced Studio Settings Glass Card
 */
@Composable
private fun AdvancedSettingsGlassCard(
    isAr: Boolean,
    isFeatured: Boolean,
    onToggleFeatured: (Boolean) -> Unit,
    isPublished: Boolean,
    onTogglePublished: (Boolean) -> Unit,
    isDraft: Boolean,
    onToggleDraft: (Boolean) -> Unit,
    isPrivatePreview: Boolean,
    onTogglePrivatePreview: (Boolean) -> Unit,
    selectedAccentColor: String,
    onSelectAccentColor: (String) -> Unit,
    changelog: String,
    onChangelogChange: (String) -> Unit,
    previewLayoutTemplate: String,
    onSelectPreviewLayout: (String) -> Unit
) {
    LiquidGlassContainer(
        title = if (isAr) "7. الإعدادات المتقدمة ونمط العرض" else "7. Advanced Settings & Layout",
        icon = Icons.Default.Tune
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = if (isAr) "ثيم مميز (Featured)" else "Featured Theme", fontWeight = FontWeight.SemiBold)
                    Text(text = if (isAr) "يظهر في شريط الهيرو الرئيسي بالصفحة الأولى" else "Hero banner display", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isFeatured,
                    onCheckedChange = onToggleFeatured,
                    colors = SwitchDefaults.colors(checkedThumbColor = CyanNeon, checkedTrackColor = CyanNeon.copy(alpha = 0.5f))
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = if (isAr) "نشر عام فوري (Published)" else "Published (Public)", fontWeight = FontWeight.SemiBold)
                    Text(text = if (isAr) "يظهر للجميع فوراً في التطبيق" else "Visible to all users", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isPublished && !isDraft,
                    onCheckedChange = onTogglePublished,
                    colors = SwitchDefaults.colors(checkedThumbColor = CyberMint, checkedTrackColor = CyberMint.copy(alpha = 0.5f))
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Accent Color Selector for Theme Card
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isAr) "لون الثيم الأساسي (Theme Card Accent):" else "Theme Card Accent Color:",
                    fontWeight = FontWeight.SemiBold
                )
                val palette = listOf(
                    "#00E5FF", "#38BDF8", "#00F5A0", "#10B981", "#F59E0B",
                    "#EF4444", "#EC4899", "#8B5CF6", "#6366F1", "#F97316"
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    palette.forEach { hex ->
                        val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { CyanNeon }
                        val isSelected = selectedAccentColor.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    if (isSelected) 3.dp else 1.dp,
                                    if (isSelected) Color.White else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { onSelectAccentColor(hex) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Preview Layout Template Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isAr) "قالب عرض صور المعاينة:" else "Preview Layout Template:",
                    fontWeight = FontWeight.SemiBold
                )
                val templates = listOf(
                    "VERTICAL_GRID" to (if (isAr) "شبكة عمودية" else "Vertical Grid"),
                    "CINEMATIC" to (if (isAr) "سينمائي واسع" else "Cinematic"),
                    "HORIZONTAL_STORY" to (if (isAr) "قصة أفقية" else "Horizontal Story"),
                    "PHONE_MOCKUP" to (if (isAr) "إطار الهاتف" else "Phone Mockup")
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    templates.forEach { (key, label) ->
                        val isSelected = previewLayoutTemplate == key
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CyanNeon.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, if (isSelected) CyanNeon else Color.Transparent),
                            modifier = Modifier.clickable { onSelectPreviewLayout(key) }
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyanNeon else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Changelog
            OutlinedTextField(
                value = changelog,
                onValueChange = onChangelogChange,
                label = { Text(if (isAr) "سجل التغييرات (Changelog)" else "Changelog") },
                placeholder = { Text(if (isAr) "مثال: تحسين استهلاك البطارية ودعم أيقونات إضافية" else "Battery improvements, extra icons...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

/**
 * Fixed Bottom Control Bar (Liquid Glass)
 */
@Composable
private fun StudioBottomControls(
    isAr: Boolean,
    onCancel: () -> Unit,
    onSaveDraft: () -> Unit,
    onLivePreview: () -> Unit,
    onPublish: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, CyanNeon.copy(alpha = 0.25f)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cancel Button
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(if (isAr) "إلغاء" else "Cancel", fontSize = 12.sp)
            }

            // Save Draft Button
            OutlinedButton(
                onClick = onSaveDraft,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, CyberElectricBlue.copy(alpha = 0.6f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyberElectricBlue)
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isAr) "حفظ مسودة" else "Draft", fontSize = 12.sp, color = CyberElectricBlue)
            }

            // Publish Now Button
            Button(
                onClick = onPublish,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color(0xFF031024)),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAr) "نشر الثيم الآن" else "Publish Theme",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Liquid Glass Container Card Wrapper
 */
@Composable
private fun LiquidGlassContainer(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    CyanNeon.copy(alpha = 0.45f),
                    Color.Transparent,
                    CyberElectricBlue.copy(alpha = 0.35f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanNeon.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            content()
        }
    }
}

/**
 * Live Preview Modal Dialog
 */
@Composable
private fun StudioLivePreviewDialog(
    isAr: Boolean,
    themeName: String,
    company: Company?,
    designer: Designer?,
    coverImageUrl: String,
    previewUrls: List<String>,
    description: String,
    tags: List<String>,
    version: String,
    accentColor: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "معاينة حية للثيم • كما سيظهر للمستخدمين" else "Live Preview • How it looks to users",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyanNeon
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Phone Screen Showcase
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Cover
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f))
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                ThemeImage(
                                    imageUrl = coverImageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .fillMaxWidth()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                            )
                                        )
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = themeName,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${company?.name ?: "Brand"} • ${designer?.name ?: "Designer"} • v$version",
                                        fontSize = 12.sp,
                                        color = CyanNeon
                                    )
                                }
                            }
                        }
                    }

                    // Previews Carousel
                    if (previewUrls.isNotEmpty()) {
                        item {
                            Text(
                                text = if (isAr) "معرض المعاينة الكامل:" else "Full Previews:",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(previewUrls) { url ->
                                    Card(
                                        modifier = Modifier
                                            .width(140.dp)
                                            .height(280.dp),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        ThemeImage(
                                            imageUrl = url,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Description
                    item {
                        Text(
                            text = if (isAr) "الوصف:" else "Description:",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description.ifBlank { "No description provided." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color(0xFF031024))
                ) {
                    Text(if (isAr) "العودة إلى استوديو النشر" else "Back to Studio", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Quick Add Company Dialog
 */
@Composable
private fun QuickAddCompanyDialog(
    viewModel: ThemeViewModel,
    isAr: Boolean,
    onCompanyAdded: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var slug by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isAr) "إضافة شركة / براند جديد" else "Add New Brand") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (slug.isBlank()) slug = it.lowercase().replace(" ", "-")
                    },
                    label = { Text(if (isAr) "اسم الشركة" else "Brand Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = slug,
                    onValueChange = { slug = it },
                    label = { Text(if (isAr) "الرابط الفريد (Slug)" else "Slug") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val company = Company(name = name.trim(), slug = slug.trim().ifBlank { name.trim().lowercase() })
                        viewModel.saveCompany(company) {
                            onCompanyAdded(company.id)
                        }
                    }
                }
            ) {
                Text(if (isAr) "إضافة" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isAr) "إلغاء" else "Cancel")
            }
        }
    )
}

/**
 * Quick Add Designer Dialog
 */
@Composable
private fun QuickAddDesignerDialog(
    viewModel: ThemeViewModel,
    isAr: Boolean,
    onDesignerAdded: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var slug by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isAr) "إضافة مصمم جديد" else "Add New Designer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (slug.isBlank()) slug = it.lowercase().replace(" ", "-")
                    },
                    label = { Text(if (isAr) "اسم المصمم" else "Designer Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = slug,
                    onValueChange = { slug = it },
                    label = { Text(if (isAr) "الرابط الفريد (Slug)" else "Slug") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val designer = Designer(name = name.trim(), slug = slug.trim().ifBlank { name.trim().lowercase() })
                        viewModel.saveDesigner(designer) {
                            onDesignerAdded(designer.id)
                        }
                    }
                }
            ) {
                Text(if (isAr) "إضافة" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isAr) "إلغاء" else "Cancel")
            }
        }
    )
}
