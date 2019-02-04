package eti.app

import eti.data.ExerciseDirectory
import eti.Generator
import eti.data.*
import eti.view.MainView
import javafx.stage.Stage
import tornadofx.App
import java.io.File
import kotlinx.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyApp : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.minHeight = 500.0
        stage.minWidth = 800.0
        super.start(stage)

        val map = mutableMapOf<Topic, List<SubTopic>>()
        for (topic in ExerciseDirectory.Topics) {
            map[topic] = topic.SubTopics
        }

        val gen = Generator()
        val out = gen.inputStream.bufferedReader()

        GlobalScope.launch { gen.generateDocument(map, 1, File("D:\\ExerciseX.pdf"), saveTex = true) }

        do {
            val s = out.readLine()
            if(s!=null)println(s)
        } while (s != null)
        println("Done Main")
    }
}