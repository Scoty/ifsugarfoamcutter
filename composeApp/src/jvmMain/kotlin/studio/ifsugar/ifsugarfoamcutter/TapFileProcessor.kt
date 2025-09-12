package studio.ifsugar.ifsugarfoamcutter

import java.io.File

class TapFileProcessor {

    /**
     * Processes the given .tap file:
     * - Removes the first 7 characters of each line,
     * - Keeps only lines containing "G1" (case-sensitive),
     * - Skips empty/blank lines,
     * - Appends feedRate and power information to each line.
     *
     * @param inputFile The original .tap file
     * @param feedRate  The feed rate value (from slider)
     * @param power     The power value (from slider)
     * @return The newly generated processed file
     */
    fun processFile(inputFile: File, feedRate: Int, power: Int): File {
        require(inputFile.exists() && inputFile.isFile) {
            "Input file does not exist: ${inputFile.path}"
        }

        val outputFile = File(
            inputFile.parentFile,
            inputFile.nameWithoutExtension + "_signed.gcode"
        )

        val processedLines = inputFile.useLines { lines ->
            lines.map { line ->
                if (line.length > 7) line.drop(7) else ""
            }
                .map { it.trimEnd() }
                .filter { it.isNotBlank() }
                .filter { it.contains("G1") }
                .toList()
        }

        val prefix = """
            M3 S$power
            G4 P5 (Wait 5 seconds)
            G21 (All units in mm)
            G1 F$feedRate
            (Start Cutting GCODE)
        """.trimIndent()

        outputFile.writeText("$prefix\n")
        outputFile.appendText(processedLines.joinToString("\n"))
        outputFile.appendText("\n$suffix\n")
        return outputFile
    }

    val suffix = """
        (End Cutting GCODE)
        G1 X0.00 Y0.00
        M5 (WIRE OFF)
        M2
        (EOF)
    """.trimIndent()
}