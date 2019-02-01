package eti.data

import java.io.File
import java.net.URI
import kotlin.math.abs

class Topic : File {

    private val subTopics: MutableList<SubTopic>
    private val looseExercises: MutableList<Exercise>
    private val hint: File?

    val SubTopics get() = subTopics.toList()
    val LooseExercises get() = looseExercises.toList()

    constructor(pathname: String?) : super(pathname)
    constructor(parent: String?, child: String?) : super(parent, child)
    constructor(parent: File?, child: String?) : super(parent, child)
    constructor(uri: URI?) : super(uri)

    init {
        //load hint file
        val hintFile = File("$absolutePath\\hint.tex")
        hint = if (hintFile.exists()) hintFile else null

        //load subtopics
        subTopics = mutableListOf()
        looseExercises = mutableListOf()
        for (file in listFiles()) {
            if (file.isDirectory) subTopics.add(SubTopic(file.absolutePath))
            else if (file.isFile && file.extension == "tex") looseExercises.add(Exercise(file.absolutePath))
        }
        var x = 5
    }

    fun getHint(): String {
        return if (hint != null) """
            \hint{${hint.readText()}}
        """.trimIndent()
        else ""
    }

    private fun genBuilder() = StringBuilder("\\aufgabenbereich{$name}\n${getHint()}\n")

    fun generateText(subtopics: Collection<String>, maxNumOfExercisesPerSubtopic: Int = Int.MAX_VALUE): String {
        val builder = genBuilder()

        for (subTopic in this.subTopics) {
            if (subtopics.contains(subTopic.name)) {
                builder.appendln(subTopic.getExercisesText(maxNumOfExercisesPerSubtopic))
            }
        }
        return builder.toString()
    }

    fun generateText(NumberOfSubTopics: Int = Int.MAX_VALUE, maxNumOfExercisesPerSubtopic: Int = Int.MAX_VALUE): String {
        val builder = genBuilder()
        val targetCount = minOf(subTopics.size, NumberOfSubTopics)
        val indexes = List(subTopics.size - 1) { i -> i }.shuffled()

        for (i in (0..(targetCount - 1))) {
            builder.appendln(subTopics[indexes[i]].getExercisesText(maxNumOfExercisesPerSubtopic))
        }

        return builder.toString()
    }
}