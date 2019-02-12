package eti.view

import eti.data.Options
import eti.data.OptionsObserver
import tornadofx.*

class MainView : View("ETI Generator"), OptionsObserver {
    private var currentOptions: Options = Options(false, false, false, 2, -1)

    override fun onOptionschanged(opt: Options) {
        currentOptions = opt
        outPut.onOptionschanged(opt) //foreword event to outPutSelector
    }

    val outPut = OutputSelector()
    val boxHolder = BoxHolder(this)

    override val root =
            borderpane {
                center = boxHolder.root
                bottom = outPut.root
            }
}
