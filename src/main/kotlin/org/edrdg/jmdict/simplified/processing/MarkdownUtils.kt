package org.edrdg.jmdict.simplified.processing

object MarkdownUtils {
    fun heading(text: String, level: Int): String {
        require(level in 1..6) {
            "level must be in 1-6 range"
        }
        return "${"#".repeat(level)} $text"
    }

    fun table(
        columns: List<String>,
        rows: List<List<Any>>,
        alignRight: List<Int> = emptyList(), // list of indexes
    ): String {
        val sb = StringBuilder()
        val colWidths = columns.mapIndexed { i, column ->
            val maxValueLength = rows.maxOfOrNull { it[i].toString().length } ?: 0
            maxOf(column.length, maxValueLength)
        }
        sb.append(
            "| ",
            columns.mapIndexed { i, column ->
                column.padEnd(colWidths[i], ' ')
            }.joinToString(" | "),
            " |\n",
        )
        sb.append(
            "| ",
            colWidths.mapIndexed { i, columnWidth ->
                "${"-".repeat(columnWidth)}${if (alignRight.contains(i)) ":" else " "}"
            }.joinToString("| "), // note no leading space
            "|\n", // note no leading space
        )
        rows.forEachIndexed { rowIndex, row ->
            sb.append(
                "| ",
                row.mapIndexed { i, value ->
                    val s = value.toString()
                    if (alignRight.contains(i)) {
                        s.padStart(colWidths[i], ' ')
                    } else {
                        s.padEnd(colWidths[i], ' ')
                    }
                }.joinToString(" | "),
                " |${if (rowIndex < rows.size - 1) "\n" else ""}",
            )
        }
        return sb.toString()
    }
}
