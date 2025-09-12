package studio.ifsugar.ifsugarfoamcutter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ifsugarfoamcutter.composeapp.generated.resources.Res
import ifsugarfoamcutter.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import java.awt.Toolkit

fun main() = application {
    val windowState = rememberWindowState(width = 600.dp,
        height = 550.dp,
        position = WindowPosition.Aligned(
            alignment = androidx.compose.ui.Alignment.Center
        ))

    Window(
        onCloseRequest = ::exitApplication,
        title = "IfSugar Foam Cutter",
        state = windowState,
        resizable = false, // <--- prevent resizing
        icon = painterResource(Res.drawable.logo) // <-- Set your icon here
    ) {
        App()
    }
}