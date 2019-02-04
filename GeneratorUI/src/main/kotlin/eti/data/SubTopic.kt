package eti.data

import java.io.File
import java.net.URI

class SubTopic : File {

    private val exercises: MutableList<Exercise> = mutableListOf()

    val Exercises get() = exercises.toList()

    val parentTopic: Topic

    constructor(parentTopic: Topic, pathname: String?) : super(pathname) {
        this.parentTopic = parentTopic
    }

    constructor(parentTopic: Topic, parent: String?, child: String?) : super(parent, child) {
        this.parentTopic = parentTopic
    }

    constructor(parentTopic: Topic, parent: File?, child: String?) : super(parent, child) {
        this.parentTopic = parentTopic
    }

    constructor(parentTopic: Topic, uri: URI?) : super(uri) {
        this.parentTopic = parentTopic
    }

    init {
        //load exercises
        for (file in listFiles()) {
            if (file.extension == "tex") {
                exercises.add(Exercise(file.absolutePath))
            }
        }
    }

    fun getExercisesText(maxCountOfExercises: Int = Int.MAX_VALUE): String {
        if (!this.exists()) return ""

        val builder = StringBuilder()
        val subTopicExercisesCount = minOf(exercises.size, maxCountOfExercises)
        val numbs = List(exercises.size) { i -> i }.shuffled()

        for (i in 0..(subTopicExercisesCount - 1)) {
            for (readLine in exercises[numbs[i]].readLines()) {
                //filtering out comments
                if (readLine.trimStart().startsWith('%') || readLine.isEmpty()) continue
                val indexOfPercentage = readLine.indexOf('%', ignoreCase = true)
                builder.appendln(readLine.substring(0, if (indexOfPercentage == -1) readLine.length else indexOfPercentage - 1))
            }
        }
        return builder.toString()
    }
}