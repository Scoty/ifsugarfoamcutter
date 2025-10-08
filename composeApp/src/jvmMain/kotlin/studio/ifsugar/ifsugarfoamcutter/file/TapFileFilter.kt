package studio.ifsugar.ifsugarfoamcutter.file

import java.io.File
import javax.swing.filechooser.FileFilter

// Custom filter: only directories + .tap files
// TO BE REMOVED - used by JFileChooser, but not used by SystemFileChooser
class TapFileFilter : FileFilter() {
    override fun accept(f: File): Boolean {
        return f.isDirectory || f.extension.equals("tap", ignoreCase = true)
    }

    override fun getDescription(): String = "Tap files"
}