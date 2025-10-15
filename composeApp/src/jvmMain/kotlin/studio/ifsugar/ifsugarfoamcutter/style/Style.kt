package studio.ifsugar.ifsugarfoamcutter.style

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
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