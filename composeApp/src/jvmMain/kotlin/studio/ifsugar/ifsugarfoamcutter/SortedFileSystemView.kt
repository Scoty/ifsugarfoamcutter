package studio.ifsugar.ifsugarfoamcutter

import java.io.File
import javax.swing.filechooser.FileSystemView

// FileSystemView that filters to directories + .tap files and sorts by lastModified desc
class SortedFileSystemView(private val delegate: FileSystemView) : FileSystemView() {
    override fun createNewFolder(containingDir: File?): File = delegate.createNewFolder(containingDir)
    override fun getHomeDirectory(): File = delegate.homeDirectory
    override fun getDefaultDirectory(): File = delegate.defaultDirectory
    override fun isRoot(f: File?): Boolean = delegate.isRoot(f)
    override fun getParentDirectory(dir: File?): File? = delegate.getParentDirectory(dir)

    // IMPORTANT: filter here so the chooser only *shows* .tap files + directories
    override fun getFiles(dir: File?, useFileHiding: Boolean): Array<File> {
        val files = delegate.getFiles(dir, useFileHiding)
        val filtered = files.filter { it.isDirectory || it.extension.equals("tap", ignoreCase = true) }
        return filtered.sortedWith(
            compareBy<File> { !it.isDirectory }      // directories first
                .thenByDescending { it.lastModified() } // then newest files first
        ).toTypedArray()
    }
}