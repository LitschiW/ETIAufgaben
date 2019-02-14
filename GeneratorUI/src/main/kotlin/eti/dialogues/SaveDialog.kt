package eti.dialogues

import eti.Generator
import eti.data.Options
import eti.data.SubTopic
import eti.data.Topic
import javafx.application.Platform
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.util.Callback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tornadofx.*
import java.io.File
import kotlin.concurrent.thread

class SaveDialog : Dialog<Boolean>() {
    val textArea = textarea {
        isWrapText = true
    }

    init {
        title = "Saving..."
        graphic = null
        headerText = "generiere Pdf..."
        dialogPane.content = gridpane { add(textArea) }
        dialogPane.buttonTypes.addAll(ButtonType.FINISH)
        dialogPane.lookupButton(ButtonType.FINISH).isDisable = true
        resultConverter = Callback { button -> button == ButtonType.FINISH }
    }

    fun startGeneration(selection: Map<Topic, List<SubTopic>>,
                        targetFile: File,
                        options: Options) {
        show()
        val gen = Generator()
        //start generator async
        GlobalScope.launch {
            try {
                gen.generateDocument(
                        selection,
                        targetFile,
                        options)
            } catch (e: Exception) {
                println(e)
            }
        }
        //start output listener thread
        thread {
            val reader = gen.inputStream.bufferedReader()
            do {
                val s = reader.readLine()
                if (s != null) {
                    Platform.runLater { textArea.appendText(s + "\n") }
                }
            } while (s != null)
            Platform.runLater { dialogPane.lookupButton(ButtonType.FINISH).isDisable = false }
        }
    }
}