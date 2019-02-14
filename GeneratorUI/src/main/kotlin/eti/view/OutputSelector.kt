package eti.view

import eti.data.Options
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
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
    var targetFile: File = File(Paths.get(Paths.get("").toAbsolutePath().toString(), File.separator, getNextFileName()).toUri())
    var saveAction = {}

    fun action(op: () -> Unit) {
        saveAction = op
    }

    val hMargin = 3.0

    val pathField = textfield {
        hboxConstraints {
            marginLeftRight(hMargin)
            hGrow = Priority.ALWAYS
        }
        maxWidth = 500.0
        text = targetFile.parentFile.absolutePath + File.separator //sperator is optional, just for the looks
        textProperty().addListener(this@OutputSelector)
    }
    val fileField = textfield {
        text = targetFile.name
        vboxConstraints { marginLeftRight(hMargin) }
        textProperty().addListener(this@OutputSelector)
    }
    val existsLabel = label("Am angegebenem Ort existiert bereits eine entsprechende Pdf.") {
        vboxConstraints {
            marginLeft = 20.0
        }
        visibleProperty().set(false)
        textFill = Color.RED
        font = Font("Arial", 12.0)
    }
    var botpane: BorderPane? = null

    override val root = vbox {
        hbox {
            add(pathField)
            vbox {
                label("Projekt/Datei Name")
                add(fileField)
            }
            button("  ...  ") {
                action {
                    var target = if (currentOptions.saveLatex) {
                        val dirChooser = DirectoryChooser()
                        dirChooser.title = "Ordner wählen, in dem der Projektordner gespeichert werden soll:"
                        if (targetFile.parentFile.isDirectory && targetFile.parentFile.exists()) dirChooser.initialDirectory = targetFile.parentFile
                        else dirChooser.initialDirectory = File(Paths.get("").toString())
                        dirChooser.showDialog(currentStage)
                    } else {
                        val pdfExtension = FileChooser.ExtensionFilter("PDF Datei", "*.pdf")
                        val fileChooser = FileChooser()
                        fileChooser.title = "Speichern unter..."
                        fileChooser.initialDirectory = File(pathField.text)
                        fileChooser.initialFileName = fileField.text
                        fileChooser.extensionFilters.addAll(List(1) { pdfExtension })
                        fileChooser.selectedExtensionFilter = pdfExtension
                        fileChooser.showSaveDialog(currentStage)
                    }
                    if (target != null) {
                        if (target.extension == "" && !currentOptions.saveLatex) target = File(target.absolutePath + ".pdf")//append .pdf extension if its missing

                        //copy path sections into textFields
                        if (currentOptions.saveLatex) {
                            pathField.text = target.absolutePath
                            targetFile = File(Paths.get(target.absolutePath, fileField.text).toUri())//copy over to local var
                        } else {
                            targetFile = target
                            pathField.text = target.absolutePath.removeSuffix(target.name)
                            fileField.text = target.name
                        }
                    }
                }
                vboxConstraints { marginLeftRight(hMargin) }
            }
            alignment = Pos.BOTTOM_CENTER
        }
        add(existsLabel)
        botpane = borderpane {
            left = button("Quit") {
                action { exitProcess(0) }
            }
            right = button("Save") {
                action {
                    saveAction()
                }
            }
            val pad = 10
            padding = insets(0, pad, pad, pad)
        }
        alignment = Pos.CENTER
    }

    init {
        checkAndHandelPathExists()
    }

    private fun getNextFileName(): String {
        val currDir = File(Paths.get("").toAbsolutePath().toUri())
        var nb = 0
        for (file in currDir.listFiles()) {
            if (file.isFile && file.name == "AB$nb")
                nb++
        }
        return "AB$nb.pdf"
    }

    override fun onOptionschanged(opt: Options) {
        currentOptions = opt //update options
        //handle switch from folder to file (save latex project or not)
        val text = fileField.text
        if (opt.saveLatex) {
            fileField.text = text.removeSuffix(".pdf")
            targetFile = File(targetFile.absolutePath.removeSuffix(".pdf"))
        } else {
            fileField.text =
                    if (!text.endsWith(".pdf")) {
                        targetFile = File((targetFile.absolutePath + ".pdf"))
                        "$text.pdf"
                    } else text
        }
    }

    override fun changed(observable: ObservableValue<out String>?, oldValue: String?, newValue: String?) {
        checkAndHandelPathExists()
    }

    fun checkAndHandelPathExists() {
        val target = File(Paths.get(pathField.text, fileField.text).toUri())
        existsLabel.isVisible =
                ((target.isFile && target.exists() && !currentOptions.saveLatex)
                        || (currentOptions.saveLatex && target.isDirectory && target.listFiles().any { file -> file.name == target.name + ".pdf" }))
        botpane?.right?.isDisable = existsLabel.isVisible //disable save button
    }

    fun getTarget(): File {
        return targetFile
    }
}