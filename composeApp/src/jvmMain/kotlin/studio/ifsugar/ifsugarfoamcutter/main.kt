package studio.ifsugar.ifsugarfoamcutter

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.formdev.flatlaf.FlatLightLaf
import ifsugarfoamcutter.composeapp.generated.resources.Res
import ifsugarfoamcutter.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    FlatLightLaf.setup()
    val windowState = rememberWindowState(width = 600.dp,
        height = 900.dp,
        position = WindowPosition.Aligned(
            alignment = androidx.compose.ui.Alignment.Center
        ))

    Window(
        onCloseRequest = ::exitApplication,
        title = "If Sugar Foam Cutter v2.3.0",
        state = windowState,
        resizable = false, // <--- prevent resizing
        icon = painterResource(Res.drawable.logo) // <-- Set your icon here
    ) {
        App()
    }
}