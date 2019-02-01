package eti.data

import java.io.File
import java.net.URI

class SubTopic : File {

    private val exercises: MutableList<Exercise> = mutableListOf()

    val Exercises get() = exercises.toList()

    constructor(pathname: String?) : super(pathname)
    constructor(parent: String?, child: String?) : super(parent, child)
    constructor(parent: File?, child: String?) : super(parent, child)
    constructor(uri: URI?) : super(uri)

    init {
        //load exercises
        for (file in listFiles()) {
            println(file.extension)
            if (file.extension == "tex") {
                exercises.add(Exercise(file.absolutePath))
            }
        }
    }

    fun getExercisesText(maxCountOfExercises: Int = Int.MAX_VALUE): String {
        if (!this.exists()) return ""

        val builder = StringBuilder()
        val subTopicExercisesCount = minOf(exercises.size, maxCountOfExercises)
        val numbs = List(exercises.size - 1) { i -> i }.shuffled()

        for (i in 0..(subTopicExercisesCount - 1)) {
            builder.append(exercises[numbs[i]].readText() + "\n")
        }
        return builder.toString()
    }
}