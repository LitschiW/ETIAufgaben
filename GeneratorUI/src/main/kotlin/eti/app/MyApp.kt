package eti.app

import eti.ExerciseDirectory
import eti.Generator
import eti.data.*
import eti.view.MainView
import javafx.stage.Stage
import tornadofx.App
import java.io.File

class MyApp : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.minHeight = 500.0
        stage.minWidth = 800.0
        super.start(stage)

        val map = mutableMapOf<Topic, List<SubTopic>>()
        for (topic in ExerciseDirectory.Topics) {
            map[topic] = topic.SubTopics
        }
        Generator().generateDocument(map, 1, File("D:\\ExerciseX.pdf"), saveTex = true)
    }
}