package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.AppLanguage
import com.example.ui.S18Strings

@Composable
fun BeforeAfterSlider(
    beforeImageUrl: String,
    afterImageUrl: String,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var sliderRatio by remember { mutableFloatStateOf(0.5f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color(0x3000E5FF), RoundedCornerShape(20.dp))
            .background(Color(0xFF0A0F1D))
    ) {
        val widthPx = constraints.maxWidth.toFloat()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
        ) {
            // Layer 1: "After" image (Full width background)
            AsyncImage(
                model = afterImageUrl,
                contentDescription = S18Strings.get("after", language),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Layer 2: "Before" image clipped to slider position
            val beforeWidth = (widthPx * sliderRatio).coerceIn(0f, widthPx)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(with(androidx.compose.ui.platform.LocalDensity.current) { beforeWidth.toDp() })
                    .clip(RectangleShape)
            ) {
                AsyncImage(
                    model = beforeImageUrl,
                    contentDescription = S18Strings.get("before", language),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(with(androidx.compose.ui.platform.LocalDensity.current) { widthPx.toDp() })
                )
            }

            // Draggable Divider Line & Handle
            val dividerOffset = with(androidx.compose.ui.platform.LocalDensity.current) {
                (widthPx * sliderRatio).toDp()
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .offset(x = dividerOffset - 1.5.dp)
                    .width(3.dp)
                    .background(Color(0xFF00E5FF))
            )

            // Center Circular Handle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = dividerOffset - 22.dp)
                    .background(Color(0xFF0A0F1D), CircleShape)
                    .border(2.dp, Color(0xFF00E5FF), CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newRatio = (sliderRatio + dragAmount.x / widthPx).coerceIn(0.05f, 0.95f)
                            sliderRatio = newRatio
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Badge: Before (Left top)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xAA000000),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = S18Strings.get("before", language),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            // Badge: After (Right top)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xCC00E5FF),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Text(
                    text = S18Strings.get("after", language),
                    color = Color(0xFF031024),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
