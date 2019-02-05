package eti.app

import eti.Generator
import eti.data.ExerciseDirectory
import eti.data.SubTopic
import eti.data.Topic
import eti.view.MainView
import javafx.stage.Stage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tornadofx.*
import java.io.File
import kotlin.collections.List
import kotlin.collections.mutableMapOf
import kotlin.collections.set

class MyApp : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.height = 500.0
        stage.width = 800.0
        super.start(stage)

        val map = mutableMapOf<Topic, List<SubTopic>>()
        for (topic in ExerciseDirectory.Topics) {
            map[topic] = topic.SubTopics
        }

        val gen = Generator()
        val out = gen.inputStream.bufferedReader()

 /*      GlobalScope.launch { gen.generateDocument(map, 1, File("D:\\ExerciseX.pdf"), saveTex = true) }

        do {
            val s = out.readLine()
            if(s!=null)println(s)
        } while (s != null)
        println("Done Main")*/
    }
}