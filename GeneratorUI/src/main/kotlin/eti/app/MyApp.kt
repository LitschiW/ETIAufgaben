package eti.app

import eti.view.MainView
import javafx.stage.Stage
import tornadofx.App

class MyApp: App(MainView::class, Styles::class)
{
    override fun start(stage: Stage) {
        stage.minHeight = 500.0
        stage.minWidth = 800.0
        super.start(stage)
    }
}