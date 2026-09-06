package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.ThemePreview

/**
 * High-fidelity Smartphone Device Mockup
 * Strictly matches the 2640 x 1200 px screen aspect ratio (1200:2640 ~ 1:2.2)
 */
@Composable
fun PhoneMockup(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String = "Theme Preview",
    showResolutionBadge: Boolean = true,
    onFullscreenClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(36.dp),
                spotColor = Color(0x6000E5FF)
            )
            .border(
                width = 2.5.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF38BDF8), Color(0xFF1E293B), Color(0xFF0F172A))
                ),
                shape = RoundedCornerShape(36.dp)
            )
            .background(Color(0xFF0D121D), RoundedCornerShape(36.dp))
            .padding(5.dp)
            .clip(RoundedCornerShape(32.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Phone Screen Container with exact 1200 x 2640 Aspect Ratio
        Box(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1200f / 2640f)
                .background(Color(0xFF030712))
        ) {
            ThemeImage(
                imageUrl = imageUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (onFullscreenClick != null) Modifier.clickable { onFullscreenClick() }
                        else Modifier
                    ),
                contentScale = ContentScale.Crop,
                contentDescription = contentDescription
            )

            // Top Camera Punch-hole Notch
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.TopCenter)
                    .size(width = 14.dp, height = 14.dp)
                    .background(Color(0xFF000000), CircleShape)
                    .border(0.5.dp, Color(0x40FFFFFF), CircleShape)
            )

            // Cyber Resolution Badge (2640 × 1200 px)
            if (showResolutionBadge) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xBF080E1A),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0x4000E5FF)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "2640 × 1200",
                        color = Color(0xFF00E5FF),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            // Subtle Screen Glass Shine Reflection
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0x18FFFFFF),
                                Color(0x05FFFFFF),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Fullscreen badge button in corner
            if (onFullscreenClick != null) {
                IconButton(
                    onClick = onFullscreenClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .background(Color(0x99000000), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Preview Gallery displaying PhoneMockup + Swipe Navigation + Thumbnails + Counter
 */
@Composable
fun PreviewGallery(
    previews: List<ThemePreview>,
    fallbackCoverUrl: String,
    modifier: Modifier = Modifier,
    themeTitle: String = "Theme"
) {
    val items = remember(previews, fallbackCoverUrl) {
        if (previews.isNotEmpty()) previews.map { it.imageUrl }
        else listOf(fallbackCoverUrl)
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    var showLightbox by remember { mutableStateOf(false) }

    val safeIndex = selectedIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    val currentImage = items.getOrElse(safeIndex) { fallbackCoverUrl }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Phone Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            PhoneMockup(
                imageUrl = currentImage,
                modifier = Modifier
                    .width(235.dp)
                    .aspectRatio(1200f / 2640f),
                contentDescription = "$themeTitle Preview ${safeIndex + 1}",
                onFullscreenClick = { showLightbox = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Controls & Counter: "< Prev   3 / 8   Next >"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (selectedIndex > 0) selectedIndex--
                    else selectedIndex = items.size - 1
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0x33FFFFFF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp)
                )
            ) {
                Text(
                    text = "${safeIndex + 1} / ${items.size}",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = {
                    if (selectedIndex < items.size - 1) selectedIndex++
                    else selectedIndex = 0
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0x33FFFFFF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Thumbnails Strip
        if (items.size > 1) {
            Spacer(modifier = Modifier.height(14.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(items) { idx, url ->
                    val isSelected = idx == safeIndex
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(width = 46.dp, height = 76.dp)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF00E5FF) else Color(0x30FFFFFF),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedIndex = idx },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black)
                    ) {
                        ThemeImage(
                            imageUrl = url,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            contentDescription = "Thumbnail ${idx + 1}"
                        )
                    }
                }
            }
        }
    }

    // Fullscreen Lightbox Modal
    if (showLightbox) {
        LightboxModal(
            images = items,
            initialIndex = safeIndex,
            onDismiss = { showLightbox = false }
        )
    }
}

/**
 * Fullscreen Lightbox with Zoom, Swipe, and Keyboard/Touch navigation
 */
@Composable
fun LightboxModal(
    images: List<String>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(initialIndex) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

    val safeIndex = currentIndex.coerceIn(0, (images.size - 1).coerceAtLeast(0))
    val currentUrl = images.getOrElse(safeIndex) { "" }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF0000000))
        ) {
            // Lightbox Image with Pinch-to-zoom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 4f)
                            offset = if (scale > 1f) offset + pan else androidx.compose.ui.geometry.Offset.Zero
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                ThemeImage(
                    imageUrl = currentUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    contentScale = ContentScale.Fit,
                    contentDescription = "Fullscreen Preview"
                )
            }

            // Top Header (Close, Zoom reset, Counter)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .background(Color(0x80000000), CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x80000000)
                ) {
                    Text(
                        text = "${safeIndex + 1} / ${images.size}",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                IconButton(
                    onClick = {
                        scale = 1f
                        offset = androidx.compose.ui.geometry.Offset.Zero
                    },
                    modifier = Modifier
                        .background(Color(0x80000000), CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Reset Zoom",
                        tint = Color.White
                    )
                }
            }

            // Prev & Next Buttons on edges
            if (images.size > 1) {
                IconButton(
                    onClick = {
                        scale = 1f
                        offset = androidx.compose.ui.geometry.Offset.Zero
                        if (currentIndex > 0) currentIndex--
                        else currentIndex = images.size - 1
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                        .size(50.dp)
                        .background(Color(0x80000000), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Image",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        scale = 1f
                        offset = androidx.compose.ui.geometry.Offset.Zero
                        if (currentIndex < images.size - 1) currentIndex++
                        else currentIndex = 0
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp)
                        .size(50.dp)
                        .background(Color(0x80000000), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Image",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Universal Image Loader supporting local drawable resources (e.g. "theme_preview_liquid", "theme_preview_dark")
 * and web URLs via Coil.
 */
@Composable
fun ThemeImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    val context = LocalContext.current
    when {
        imageUrl == "theme_preview_liquid" -> {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.theme_preview_liquid),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageUrl == "theme_preview_dark" -> {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.theme_preview_dark),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageUrl.startsWith("drawable:") -> {
            val resName = imageUrl.removePrefix("drawable:")
            val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
            if (resId != 0) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = resId),
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = contentScale
                )
            } else {
                FallbackImage(modifier)
            }
        }
        imageUrl.startsWith("http://") || imageUrl.startsWith("https://") ||
        imageUrl.startsWith("content://") || imageUrl.startsWith("file://") ||
        imageUrl.startsWith("/") -> {
            val modelData = when {
                imageUrl.startsWith("/") -> java.io.File(imageUrl)
                imageUrl.startsWith("file://") -> java.io.File(imageUrl.removePrefix("file://"))
                imageUrl.startsWith("content://") -> android.net.Uri.parse(imageUrl)
                else -> imageUrl
            }
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(modelData)
                    .crossfade(true)
                    .placeholder(R.drawable.theme_preview_liquid)
                    .error(R.drawable.theme_preview_liquid)
                    .build(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        imageUrl.isNotEmpty() -> {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.theme_preview_liquid)
                    .error(R.drawable.theme_preview_liquid)
                    .build(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        else -> {
            FallbackImage(modifier)
        }
    }
}

@Composable
private fun FallbackImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0284C7))
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "S18_THEME",
            color = Color(0x80FFFFFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
