package eti.app

import eti.view.MainView
import javafx.stage.Stage
import tornadofx.*

class MyApp : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.height = 500.0
        stage.width = 850.0
        super.start(stage)
    }
}