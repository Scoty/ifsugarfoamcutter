package studio.ifsugar.ifsugarfoamcutter.style

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GradientButton(
    text: String,
    colors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion,
    fixedWidth: Dp = Dp.Unspecified // width wraps content if unspecified
) {
    val brush = Brush.horizontalGradient(colors)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = modifier
            .then(if (fixedWidth != Dp.Unspecified) Modifier.width(fixedWidth) else Modifier.Companion)
            .shadow(
                elevation = if (isHovered) 20.dp else 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(brush)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}

// Slider with subtle glow
@Composable
fun GlowingSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    modifier: Modifier = Modifier.Companion,
    thumbColor: Color,
    trackColor: Color
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        modifier = modifier.drawBehind {
            val glowColor = trackColor.copy(alpha = 0.2f)
            drawRoundRect(
                glowColor,
                topLeft = Offset(0f, size.height / 2 - 4.dp.toPx()),
                size = Size(size.width, 8.dp.toPx()),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        },
        colors = SliderDefaults.colors(
            thumbColor = thumbColor,
            activeTrackColor = trackColor,
            inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
        )
    )
}

val defaultTextStyle = TextStyle(
    color = Color.White,
    fontSize = 16.sp,
    shadow = Shadow(
        color = Color.Black.copy(alpha = 0.6f),
        offset = Offset(2f, 2f),
        blurRadius = 4f
    )
)

@Composable
fun ColorfulBackground(modifier: Modifier = Modifier.Companion) {
    // Animate gradient offset for subtle movement
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val gradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFF3F51B5),
                Color(0xFF2196F3),
                Color(0xFF00BCD4),
                Color(0xFF4CAF50),
                Color(0xFFFFC107),
                Color(0xFFF44336)
            ),
            start = Offset(offsetX, offsetY),
            end = Offset(size.width + offsetX, size.height + offsetY)
        )

        drawRect(brush = gradient, size = size)

        // Optional: subtle grid lines on top
        val step = 30f
        val lineColor = Color.White.copy(alpha = 0.05f)
        var x = 0f
        while (x < size.width) {
            drawLine(lineColor, Offset(x, 0f), Offset(x - size.width, size.height))
            x += step
        }
        var y = 0f
        while (y < size.height) {
            drawLine(lineColor, Offset(0f, y), Offset(size.width, y - size.height))
            y += step
        }
    }
}