package eti.view

import eti.data.Options
import eti.data.OptionsObserver
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

class OutputSelector : View(), OptionsObserver, ChangeListener<String> {
    var currentOptions = Options()

    val textField = textfield {
        hgrow = Priority.ALWAYS
        hboxConstraints {
            marginLeft = 5.0
        }
        apply {
            text = getNextFilePath()
        }
    }.apply {
        textProperty().addListener(this@OutputSelector)
    }
    val existsLabel = label("Am angegebenem Ort existiert bereits eine entsprechende Pdf.") {
        vboxConstraints {
            marginLeft = 20.0
        }
    }.apply {
        visibleProperty().set(false)
        textFill = Color.RED
        font = Font("Arial", 12.0)
    }
    var botpane: BorderPane? = null
    override val root = vbox {
        hbox {
            add(textField)
            button(" ... ") {
                hboxConstraints {
                    marginLeftRight(5.0)
                }
            }
                    .apply {
                        action {
                            val target = if (currentOptions.saveLatex) {
                                DirectoryChooser().showDialog(currentStage)
                            } else {
                                FileChooser().showSaveDialog(currentStage)
                            }
                        }
                    }
        }
        add(existsLabel)
        botpane = borderpane {
            left = button("Quit") {
                borderpaneConstraints {
                    margin = Insets(5.0)
                }
            }.apply { action { exitProcess(0) } }
            right = button("Save") {
                borderpaneConstraints {
                    margin = Insets(5.0)
                }.apply {
                    action {
                        TODO("fire save called event")
                    }
                }
            }
        }
        borderpaneConstraints {
            margin = Insets(5.0)
        }
        minHeight = 50.0
    }

    init {
        checkAndHandelPathExists()
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

    override fun onOptionschanged(opt: Options) {
        currentOptions = opt

        //handle switch from folder to file (save latex project or not)
        val text = textField.text
        if (opt.saveLatex) {
            textField.text = text.removeSuffix(".pdf")
        } else {
            textField.text =
                    if (!text.endsWith(".pdf")) "$text.pdf"
                    else text
        }
    }

    override fun changed(observable: ObservableValue<out String>?, oldValue: String?, newValue: String?) {
        checkAndHandelPathExists()
    }

    private fun checkAndHandelPathExists() {
        val target = File(textField.text)
        existsLabel.isVisible =
                ((target.isFile && target.exists() && !currentOptions.saveLatex)
                        || (currentOptions.saveLatex && target.isDirectory && target.listFiles().any { file -> file.name == target.name + ".pdf" }))
        botpane?.right?.isDisable = existsLabel.isVisible
    }
}