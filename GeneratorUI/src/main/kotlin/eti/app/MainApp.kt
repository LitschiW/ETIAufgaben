package eti.app

import eti.view.MainView
import javafx.stage.Stage
import tornadofx.*

class MainApp : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.height = 500.0
        stage.width = 850.0
        stage.resizableProperty().set(false)
        super.start(stage)
    }
}