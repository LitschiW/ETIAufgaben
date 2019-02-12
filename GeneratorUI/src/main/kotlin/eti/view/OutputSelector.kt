package eti.view

import javafx.geometry.Insets
import javafx.scene.layout.Priority
import tornadofx.*
import java.io.File
import java.nio.file.Paths

class OutputSelector : View() {
    override val root = vbox {
        hbox {
            textfield {
                hgrow = Priority.ALWAYS
                hboxConstraints {
                    marginLeft = 5.0
                }
                apply {
                    text = getNextFilePath()
                }
            }
            button(" ... ") {
                hboxConstraints {
                    marginLeftRight(5.0)
                }
            }
        }
        borderpane {
            left = button("Quit") {
                borderpaneConstraints {
                    margin = Insets(5.0)
                }
            }
            right = button("Save") {
                borderpaneConstraints {
                    margin = Insets(5.0)
                }
            }
        }
        borderpaneConstraints {
            margin = Insets(5.0)
        }
        minHeight = 50.0
    }

    private fun getNextFilePath(): String {
        val currDir = File(Paths.get("").toAbsolutePath().toUri())
        var nb = 0
        for (file in currDir.listFiles()) {
            if (file.isFile && file.name == "AB_$nb")
                nb++
        }
        return "$currDir${File.separator}AB_$nb.pdf"
    }
}