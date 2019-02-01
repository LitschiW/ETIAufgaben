package eti

import eti.data.Exercise
import eti.data.SubTopic
import eti.data.Topic
import java.io.File
import java.nio.file.Paths

object ExerciseDirectory {

    private val rootFile: File

    private val topics: MutableList<Topic> = mutableListOf()
    private val subtopics: MutableList<SubTopic> = mutableListOf()
    private val exercises: MutableList<Exercise> = mutableListOf()

    init {
        //looking for the directory root
        var rootFile = File(Paths.get("Aufgaben").toAbsolutePath().toString())
        while (!(rootFile.exists() && rootFile.name == "Aufgaben")) {//find correct root if we are debugging
            rootFile = rootFile.parentFile
            val files = rootFile.listFiles()
            if (files.any { file -> file.name == "Aufgaben" })
                rootFile = files.find { file -> file.name == "Aufgaben" }!!
        }

        this.rootFile = rootFile
        for (file in this.rootFile.listFiles()) {
            val newTopic = Topic(file.absolutePath)
            topics.add(newTopic)
            subtopics.addAll(newTopic.getSubTopics())
            exercises.addAll(newTopic.getLooseExercises())
            for (subTopic in newTopic.getSubTopics()) {
                exercises.addAll(subTopic.exercises)
            }
        }
    }

    fun getTopicsFromNames(vararg names: String): List<Topic> {
        return topics.filter { topic -> names.contains(topic.name) }
    }

    fun getTopicsFromNames(names: Collection<String>): List<Topic> {
        return topics.filter { topic -> names.contains(topic.name) }
    }
}


