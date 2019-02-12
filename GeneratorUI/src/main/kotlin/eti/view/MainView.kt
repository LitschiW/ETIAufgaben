package eti.view

import tornadofx.*

class MainView : View("ETI Generator") {
    val outPut = OutputSelector()
    val boxHolder = BoxHolder()

    override val root =
            borderpane {
                center = boxHolder.root
                bottom = outPut.root

            }
}
