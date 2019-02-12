package eti.view

import javafx.scene.layout.Priority
import tornadofx.*
import java.io.File
import java.nio.file.Paths

class OutputSelector : View() {
    override val root = gridpane {
        textfield {
            apply {
                text = getNextFilePath()
            }
            gridpaneConstraints {
                columnIndex = 0
                rowIndex = 0
                columnSpan = 2
                hgrow = Priority.ALWAYS
                fillWidth =true
            }
        }
        button(" ... ") {
            gridpaneConstraints {
                columnIndex = 3
                rowIndex = 0
                fillWidth =true
            }
            hgrow = Priority.ALWAYS
        }
        button("Quit") {
            gridpaneConstraints {
                columnIndex = 0
                rowIndex = 1
            }
        }
        button("Save") {
            gridpaneConstraints {
                columnIndex = 3
                rowIndex = 1
            }
        }
        hgrow = Priority.ALWAYS
        paddingAll=5
        minHeight = 50.0
    }

    private fun getNextFilePath(): String {
        val currDir = File(Paths.get("").toAbsolutePath().toUri())
        var nb = 0
        for (file in currDir.listFiles()) {
            if (file.isFile && file.name == "AB_$nb")
                nb++
        }
        return "$currDir\\AB_$nb.pdf"
    }
}
