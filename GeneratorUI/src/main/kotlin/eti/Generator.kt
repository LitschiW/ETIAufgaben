package eti

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Paths
import kotlin.random.Random

class Generator {

    private val document = StringBuilder()
    private val tempfolder: File = createTempDir()
    private val setupFolder = File(tempfolder.absolutePath + "\\SetupData\\")
    private val exercisesFolder = File(tempfolder.absolutePath + "\\Aufgaben\\")
    private val repoRoot: File

    init {
        val root = File(Paths.get("").toAbsolutePath().toString())

        repoRoot =
                if (root.name == "GeneratorUI") {
                    root.parentFile
                } else root;
    }

    /**
     * @param topics Map that contains the list of topics (absolute paths) used as keys and references the used list of subtopics(names) as value to this key.
     * @param maxNumOfSubExercises maximum number of subtopics loads for each topic, use Int.MAX_VALUE for everything.
     * @param targetFile output File for the pdf
     * @param saveTex set this to true if the generated tex code should be saved into a folder.
     */
    fun generateDocument(topics: Map<String, String>, maxNumOfSubExercises: Int, targetFile: File, saveTex: Boolean = false) {
        //one may want to do this async and lock the main screen for the duration

        //begin main document
        startDocument(targetFile.name)
        for ((topic, subtopics) in topics) {
            //start new partial document for topic
            //add \aufgabenbereich(topic) to partial document
            //add \hinweis(topic)to partial document
            //generate exercises
            //add subtopics/exercises to partial document
            // store partial document into .tex file
            // \include tex file into mainDocument
        }
        //end main document
        endDocument()
        //save file (optional copy .tex's into folder)
        //call script to trigger latex interpreter.
        //wait for script to return and show result

        //notify finished (? look up kotlin async's)
    }

    private fun startDocument(fileName: String) {
        //copy SetupData to tempfolder
        var x = File(Paths.get("SetupData").toAbsolutePath().toString())
        if (!x.exists()) {//find folder if we are debugging
            x = x.parentFile.parentFile
            x = x.listFiles().find { file -> file.name == "SetupData" }!!
        }
        x.copyRecursively(setupFolder, true)

        document.append("""
\documentclass [12pt]{ article }
\input {SetupData/usepackage}
\begin {document}
    \setTitel { ${fileName} }
    \input {SetupData/pagesetup}
        """.trimIndent())
    }

    private fun endDocument() {
        document.append("""
\end{document}
%this document was automatically generated
        """.trimIndent())
    }


    private fun getTwoRandomExcercisesFromTopic(exerciseTopic: File): String {
        val result = StringBuilder()
        val count = exerciseTopic.listFiles().size
        if (count <= 0 || count == 1 && exerciseTopic.listFiles()[0].listFiles().size <= 1) throw Exception("There are not enough exercises for this Topic")

        val topic1Nb = Random.nextInt(0, count)
        var topic2Nb = topic1Nb

        while (topic1Nb == topic2Nb && count != 1) {
            topic2Nb = Random.nextInt(0, count)
        }
        val topic1File = exerciseTopic.listFiles()[topic1Nb]
        val topic2File = exerciseTopic.listFiles()[topic2Nb]

        if (topic1Nb != topic2Nb) {
            result.append(getRandomExercisesFromSubTopic(topic1File, 1))
            result.append(getRandomExercisesFromSubTopic(topic2File, 1))
        } else {
            result.append(getRandomExercisesFromSubTopic(topic1File, 2))
        }
        return result.toString()
    }

    private fun getRandomExercisesFromSubTopic(subTopic: File, count: Int = 0): String {
        val exercises = subTopic.listFiles()
        if (count < 0 || count > exercises.size) throw Exception("There are not enough exercises in this Topic")
        val result = StringBuilder()

        val used = mutableSetOf<Int>()

        for (i in 0..count) {
            var next = Random.nextInt(0, exercises.size)
            while (used.contains(next))
                next = Random.nextInt(0, exercises.size)
            val nextFile = exercises[next]
            result.append(getLinesOfFile(nextFile)).append("\n")
        }
        return result.toString()
    }

    private fun getLinesOfFile(file: File): String {
        if (!file.isFile) throw IOException("This is not a File!")
        return file.readText()
    }
}

