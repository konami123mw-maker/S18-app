package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Custom Uiverse-inspired Day/Night Theme Switch with transforming Sun/Moon and Stars
 */
@Composable
fun ThemeSwitch(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescriptionText: String = "Toggle dark mode"
) {
    val interactionSource = remember { MutableInteractionSource() }

    val trackWidth = 64.dp
    val trackHeight = 32.dp
    val thumbSize = 26.dp
    val thumbOffsetTarget = if (isDarkMode) 34.dp else 4.dp

    val thumbOffset by animateDpAsState(
        targetValue = thumbOffsetTarget,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "thumbOffset"
    )

    val moonCraterAlpha by animateFloatAsState(
        targetValue = if (isDarkMode) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "craterAlpha"
    )

    val trackBrush = if (isDarkMode) {
        Brush.horizontalGradient(
            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0D1527))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color(0xFF38BDF8), Color(0xFF60A5FA), Color(0xFF93C5FD))
        )
    }

    val thumbColor by animateColorAsState(
        targetValue = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFFFBBF24),
        animationSpec = tween(durationMillis = 300),
        label = "thumbColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isDarkMode) Color(0x40FFFFFF) else Color(0x30000000),
        animationSpec = tween(durationMillis = 300),
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .semantics {
                role = Role.Switch
                contentDescription = contentDescriptionText
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle
            )
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(trackBrush)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .drawBehind {
                if (isDarkMode) {
                    // Decorative Stars in night sky
                    drawCircle(Color(0xCCFFFFFF), radius = 1.5.dp.toPx(), center = Offset(14.dp.toPx(), 9.dp.toPx()))
                    drawCircle(Color(0x99FFFFFF), radius = 1.2.dp.toPx(), center = Offset(24.dp.toPx(), 22.dp.toPx()))
                    drawCircle(Color(0xDDFFFFFF), radius = 1.8.dp.toPx(), center = Offset(18.dp.toPx(), 18.dp.toPx()))
                    drawCircle(Color(0x88FFFFFF), radius = 1.0.dp.toPx(), center = Offset(28.dp.toPx(), 11.dp.toPx()))
                } else {
                    // Decorative Clouds in day sky
                    drawCircle(Color(0x66FFFFFF), radius = 6.dp.toPx(), center = Offset(44.dp.toPx(), 20.dp.toPx()))
                    drawCircle(Color(0x77FFFFFF), radius = 8.dp.toPx(), center = Offset(52.dp.toPx(), 18.dp.toPx()))
                    drawCircle(Color(0x66FFFFFF), radius = 5.dp.toPx(), center = Offset(58.dp.toPx(), 22.dp.toPx()))
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // Toggle Thumb (Sun / Moon)
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .shadow(
                    elevation = if (isDarkMode) 2.dp else 6.dp,
                    shape = CircleShape,
                    spotColor = if (isDarkMode) Color.Black else Color(0xFFF59E0B)
                )
                .clip(CircleShape)
                .background(thumbColor)
                .drawBehind {
                    if (isDarkMode && moonCraterAlpha > 0.05f) {
                        // Moon Craters
                        val craterColor = Color(0x4064748B)
                        drawCircle(
                            color = craterColor,
                            radius = 3.dp.toPx(),
                            center = Offset(size.width * 0.35f, size.height * 0.38f)
                        )
                        drawCircle(
                            color = craterColor,
                            radius = 2.2.dp.toPx(),
                            center = Offset(size.width * 0.65f, size.height * 0.30f)
                        )
                        drawCircle(
                            color = craterColor,
                            radius = 2.8.dp.toPx(),
                            center = Offset(size.width * 0.55f, size.height * 0.68f)
                        )
                    }
                }
        )
    }
}
