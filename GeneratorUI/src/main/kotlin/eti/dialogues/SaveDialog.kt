package eti.dialogues

import eti.Generator
import eti.data.Options
import eti.data.SubTopic
import eti.data.Topic
import javafx.application.Platform
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.util.Callback
import tornadofx.*
import java.io.File
import java.io.PipedInputStream
import kotlin.concurrent.thread

class SaveDialog : Dialog<Boolean>() {
    val textArea = textarea {
        isWrapText = true
        isEditable = false
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
                        options: Options,
                        onFinished: (Boolean) -> Unit = {}) {
        show()

        val outPutStream = PipedInputStream()
        val generationCanceled = false
        //start generator async
        Generator.generateDocument(
                selection,
                targetFile,
                options,
                outPutStream) {
            //@it holds whether the generation was successful
            Platform.runLater { dialogPane.lookupButton(ButtonType.FINISH).isDisable = false }
            outPutStream.close()
            onFinished(it)
            generationCanceled != it
        }

        //start output listener thread
        thread {
            val time = System.currentTimeMillis()
            var connected = true
            while (outPutStream.available() == 0) {//waiting for the pipe to connect
                if (System.currentTimeMillis() - time > 1000 || generationCanceled) { //timeout after 1s or if the generating was canceled before the pipe was connected (e.g. for argument errors)
                    connected = false
                    Platform.runLater {
                        if (!generationCanceled) textArea.appendText("Output Pipe did not connect in Time!\n No output will be available")
                        dialogPane.lookupButton(ButtonType.FINISH).isDisable = false
                    }
                    break
                }
            }
            if (connected) {
                val reader = outPutStream.bufferedReader()
                try {
                    do {
                        val s = reader.readLine()
                        if (s != null) {
                            Platform.runLater { textArea.appendText(s + "\n") }
                        }
                    } while (s != null)
                } catch (e: Throwable) {
                }
            }
        }
    }
}