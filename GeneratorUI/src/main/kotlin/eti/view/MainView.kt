package eti.view

import eti.data.Options
import eti.dialogues.SaveDialog
import javafx.stage.Modality
import tornadofx.*

class MainView : View("ETI Generator"), OptionsObserver {
    private var currentOptions: Options = Options(false, false, false, 2, -1)

    override fun onOptionschanged(opt: Options) {
        currentOptions = opt
        outPut.onOptionschanged(opt) //foreword event to outPutSelector
    }

    val outPut = OutputSelector().apply {
        action {
            val selection = boxHolder.getSelection()
            val targetFile = this.getTarget()
            SaveDialog().run {
                initModality(Modality.NONE)
                startGeneration(selection, targetFile, currentOptions) { checkAndHandelPathExists() }
            }
        }
    }
    val boxHolder = BoxHolder(this)

    override val root =
            borderpane {
                top = boxHolder.root
                bottom = outPut.root
            }
}
