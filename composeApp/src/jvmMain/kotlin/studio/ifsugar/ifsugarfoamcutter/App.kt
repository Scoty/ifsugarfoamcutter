package studio.ifsugar.ifsugarfoamcutter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ifsugarfoamcutter.composeapp.generated.resources.Res
import ifsugarfoamcutter.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
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

    MaterialTheme(colorScheme = darkColorScheme()) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color(0xFF121212))
                .requiredHeight(750.dp)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val imageModifier = Modifier
                    .fillMaxWidth()
                    .size(140.dp)
                    .background(Color.Cyan)
                Image(
                    contentScale = ContentScale.Fit,
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo",
                    modifier = imageModifier
                )

                Text(
                    selectedFile?.name ?: "No file selected",
                    color = Color.LightGray
                )

                Button(
                    onClick = {
                        val chooser = JFileChooser(
                            lastDirectory ?: File(System.getProperty("user.home")),
                            SortedFileSystemView(FileSystemView.getFileSystemView())
                        ).apply {
                            fileFilter = TapFileFilter()
                            isAcceptAllFileFilterUsed = false // hide "All files" option
                        }
                        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            selectedFile = chooser.selectedFile
                            lastDirectory = chooser.selectedFile.parentFile
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text("Choose .tap File")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Feed Rate (Speed): ${feedRate.toInt()}", color = Color.White)
                    Slider(
                        value = feedRate,
                        onValueChange = { feedRate = it },
                        valueRange = 100f..1000f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Cyan,
                            activeTrackColor = Color.Cyan
                        ),
                        modifier = Modifier.width(300.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Power (PWM): ${power.toInt()}", color = Color.White)
                    Slider(
                        value = power,
                        onValueChange = { power = it },
                        valueRange = 200f..1500f,
                        steps = 12,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Magenta,
                            activeTrackColor = Color.Magenta
                        ),
                        modifier = Modifier.width(300.dp)
                    )
                }

                Button(
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Text("Save")
                }

                if (statusMessage.isNotEmpty()) {
                    Text(statusMessage, color = Color.Yellow)
                }
            }
        }
    }
}