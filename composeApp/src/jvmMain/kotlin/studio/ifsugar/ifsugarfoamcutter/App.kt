package studio.ifsugar.ifsugarfoamcutter


import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ifsugarfoamcutter.composeapp.generated.resources.Res
import ifsugarfoamcutter.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

@Composable
fun GradientButton(
    text: String,
    colors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fixedWidth: Dp = Dp.Unspecified // width wraps content if unspecified
) {
    val brush = Brush.horizontalGradient(colors)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = modifier
            .then(if (fixedWidth != Dp.Unspecified) Modifier.width(fixedWidth) else Modifier)
            .shadow(
                elevation = if (isHovered) 20.dp else 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
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
    modifier: Modifier = Modifier,
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
@Preview
fun App() {
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var feedRate by remember { mutableStateOf(1000f) }
    var power by remember { mutableStateOf(1000f) }
    var lastDirectory by remember { mutableStateOf<File?>(null) }
    var statusMessage by remember { mutableStateOf("") }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
        ) {
            ColorfulBackground(modifier = Modifier.fillMaxSize())

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp), // spacing between elements
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    contentScale = ContentScale.Fit,
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(140.dp)
                        .background(Color.Transparent)
                )

                Text(
                    selectedFile?.name ?: "No file selected",
                    color = Color.White,
                    style = defaultTextStyle
                )

                GradientButton(
                    text = "Choose .tap File",
                    colors = listOf(Color(0xFF4A90E2), Color(0xFF357ABD)),
                    onClick = {
                        val chooser = JFileChooser(
                            lastDirectory ?: File(System.getProperty("user.home")),
                            SortedFileSystemView(FileSystemView.getFileSystemView())
                        ).apply {
                            fileFilter = TapFileFilter()
                            isAcceptAllFileFilterUsed = false
                        }
                        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            selectedFile = chooser.selectedFile
                            lastDirectory = chooser.selectedFile.parentFile
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Feed Rate (Speed): ${feedRate.toInt()}", color = Color.White, style = defaultTextStyle)
                    GlowingSlider(
                        value = feedRate,
                        onValueChange = { feedRate = it },
                        valueRange = 100f..1000f,
                        steps = 8,
                        thumbColor = Color.Cyan,
                        trackColor = Color.Cyan,
                        modifier = Modifier.width(300.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Power (PWM): ${power.toInt()}", color = Color.White, style = defaultTextStyle)
                    GlowingSlider(
                        value = power,
                        onValueChange = { power = it },
                        valueRange = 200f..1500f,
                        steps = 12,
                        thumbColor = Color.Magenta,
                        trackColor = Color.Magenta,
                        modifier = Modifier.width(300.dp)
                    )
                }

                GradientButton(
                    text = "Save",
                    colors = listOf(Color(0xFF43A047), Color(0xFF2E7D32)),
                    onClick = {
                        selectedFile?.let { file ->
                            try {
                                val processor = TapFileProcessor()
                                val newFile = processor.processFile(
                                    file,
                                    feedRate.toInt(),
                                    power.toInt()
                                )
                                statusMessage = "Processed file saved: ${newFile.name}"
                            } catch (e: Exception) {
                                statusMessage = "Error: ${e.message}"
                            }
                        } ?: run {
                            statusMessage = "No file selected!"
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (statusMessage.isNotEmpty()) {
                    val color =
                        if (statusMessage.startsWith("No") or statusMessage.startsWith("Error"))
                            Color.Yellow
                        else Color.Green
                    Text(statusMessage, color = color, style = defaultTextStyle)
                }
            }
        }
    }
}

@Composable
fun ColorfulBackground(modifier: Modifier = Modifier) {
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
