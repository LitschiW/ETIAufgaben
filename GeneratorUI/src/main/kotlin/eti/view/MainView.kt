package eti.view

import eti.Generator
import eti.data.Options
import eti.data.OptionsObserver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tornadofx.*

class MainView : View("ETI Generator"), OptionsObserver {
    private var currentOptions: Options = Options(false, false, false, 2, -1)

    override fun onOptionschanged(opt: Options) {
        currentOptions = opt
        outPut.onOptionschanged(opt) //foreword event to outPutSelector
    }

    val outPut = OutputSelector().apply {
        action {
            val gen = Generator()
            val output = gen.inputStream.bufferedReader()
            val selection = boxHolder.getSelection()
            val targetFile = this.getTarget()

            GlobalScope.launch {
                try {
                    gen.generateDocument(selection,
                            targetFile,
                            currentOptions)
                } catch (e: Exception) {
                    println(e)
                }

            }

            do {
                val s = output.readLine()
                if (s != null) println(s)
            } while (s != null)
            checkAndHandelPathExists()
        }
    }
    val boxHolder = BoxHolder(this)

    override val root =
            borderpane {
                top = boxHolder.root
                bottom = outPut.root
            }
}
