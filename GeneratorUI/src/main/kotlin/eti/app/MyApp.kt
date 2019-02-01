package eti.app

import eti.ExerciseDirectory
import eti.Generator
import eti.view.MainView
import javafx.stage.Stage
import tornadofx.App
import java.io.File

class MyApp : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.minHeight = 500.0
        stage.minWidth = 800.0
        super.start(stage)

        val x = Generator()
        x.generateDocument(mapOf(), 2, File("D:\\result.pdf"), saveTex = true)
    }
}