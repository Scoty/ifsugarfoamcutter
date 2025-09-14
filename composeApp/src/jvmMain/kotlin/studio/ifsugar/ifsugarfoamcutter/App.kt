package studio.ifsugar.ifsugarfoamcutter


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ifsugarfoamcutter.composeapp.generated.resources.Res
import ifsugarfoamcutter.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skiko.hostId
import studio.ifsugar.ifsugarfoamcutter.file.GCodePath
import studio.ifsugar.ifsugarfoamcutter.file.SortedFileSystemView
import studio.ifsugar.ifsugarfoamcutter.file.TapFileFilter
import studio.ifsugar.ifsugarfoamcutter.file.TapFileProcessor
import studio.ifsugar.ifsugarfoamcutter.style.ColorfulBackground
import studio.ifsugar.ifsugarfoamcutter.style.GlowingSlider
import studio.ifsugar.ifsugarfoamcutter.style.GradientButton
import studio.ifsugar.ifsugarfoamcutter.style.defaultTextStyle
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

@Composable
@Preview
fun App() {

    var selectedFile by remember { mutableStateOf<File?>(null) }
    var feedRate by remember { mutableStateOf(1000f) }
    var power by remember { mutableStateOf(1000f) }
    var lastDirectory by remember { mutableStateOf<File?>(null) }
    var statusMessage by remember { mutableStateOf("") }
    var gcodePreview by remember { mutableStateOf("") }
    var gcodePath by remember { mutableStateOf<GCodePath?>(null) }

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
                        statusMessage = ""
                        gcodePath = null
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
                                val (newFile, previewText) = processor.processFile(
                                    file,
                                    feedRate.toInt(),
                                    power.toInt()
                                )
                                gcodePreview = previewText
                                gcodePath = processor.extractPath(previewText)
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
                } else {
                    Text("")
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(12.dp)
                ) {
                    Text(
                        "G-code Preview (2D Path):",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (gcodePath != null) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val points = gcodePath!!.points

                            if (points.isNotEmpty()) {
                                // Normalize points to fit canvas
                                val minX = points.minOf { it.first }
                                val maxX = points.maxOf { it.first }
                                val minY = points.minOf { it.second }
                                val maxY = points.maxOf { it.second }

                                val scaleX = size.width / (maxX - minX).coerceAtLeast(1f)
                                val scaleY = size.height / (maxY - minY).coerceAtLeast(1f)
                                val scale = minOf(scaleX, scaleY)

                                var last: Offset? = null
                                points.forEach { (x, y) ->
                                    val px = (x - minX) * scale
                                    val py = size.height - (y - minY) * scale // flip Y
                                    val current = Offset(px, py)

                                    if (last != null) {
                                        drawLine(
                                            color = Color.Green,
                                            start = last!!,
                                            end = current,
                                            strokeWidth = 2f
                                        )
                                    }
                                    last = current
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

