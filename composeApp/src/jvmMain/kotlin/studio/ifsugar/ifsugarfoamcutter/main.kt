package studio.ifsugar.ifsugarfoamcutter

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "If Sugar Foam Cutter",
    ) {
        App()
    }
}