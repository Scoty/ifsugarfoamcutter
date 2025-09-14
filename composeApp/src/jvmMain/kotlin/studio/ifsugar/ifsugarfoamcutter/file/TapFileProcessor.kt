package studio.ifsugar.ifsugarfoamcutter.file

import java.io.File

data class GCodePath(val points: List<Pair<Float, Float>>)

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
     * @return The newly generated processed file and a text representation of it
     */
    fun processFile(inputFile: File, feedRate: Int, power: Int): Pair<File, String> {
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

        val prefix = $$"""
            G4 P1 (Wait 1 seconds)
            $H
            M3 S$$power
            G4 P5 (Wait 5 seconds)
            G21 (All units in mm)
            G1 F$$feedRate
            (Start Cutting GCODE)
        """.trimIndent()

        outputFile.writeText("$prefix\n")
        outputFile.appendText(processedLines.joinToString("\n"))
        outputFile.appendText("\n$suffix\n")
        return outputFile to processedLines.joinToString("\n")
    }

    val suffix = """
        (End Cutting GCODE)
        G1 X0.00 Y0.00
        M5 (WIRE OFF)
        M2
        (EOF)
    """.trimIndent()

    fun extractPath(gcodeText: String): GCodePath {
        val points = mutableListOf<Pair<Float, Float>>()
        val regex = Regex("""X([-]?\d+\.?\d*)\s*Y([-]?\d+\.?\d*)""")

        gcodeText.lines().forEach { line ->
            val match = regex.find(line)
            if (match != null) {
                val x = match.groupValues[1].toFloat()
                val y = match.groupValues[2].toFloat()
                points.add(x to y)
            }
        }
        return GCodePath(points)
    }
}