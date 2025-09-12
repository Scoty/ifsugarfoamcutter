package studio.ifsugar.ifsugarfoamcutter

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val windowState = rememberWindowState(width = 600.dp, height = 550.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "IfSugar Foam Cutter",
        state = windowState,
        resizable = false // <--- prevent resizing
    ) {
        App() // your main Composable
    }
}