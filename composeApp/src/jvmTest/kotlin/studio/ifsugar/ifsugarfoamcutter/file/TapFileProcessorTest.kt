package studio.ifsugar.ifsugarfoamcutter.file

import java.io.File
import kotlin.test.Test

class TapFileProcessorTest {
    val inputFile = File(TapFileProcessorTest::class.java.getResource("/file/input.tap")!!.file)
    val outputFile = File(TapFileProcessorTest::class.java.getResource("/file/output.gcode")!!.file)

    @Test
    fun processFile() {
        val processor = TapFileProcessor()
        val output = processor.processFile(inputFile, 1000, 1000)

        assert(output.first.readText() == outputFile.readText())
        assert(output.second.isNotBlank())
    }

}