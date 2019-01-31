package eti

import java.io.File
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
    fun generateDocument(topics: Map<String, List<String>>,
                         maxNumOfExercisesPerSubtopic: Int,
                         targetFile: File,
                         randomizeSubTopics: Boolean = false,
                         saveTex: Boolean = false) {
        //one may want to do this async and lock the main screen for the duration

        //check Arguments
        if (targetFile.extension != "pdf") throw IllegalArgumentException("targetFile extention should be '.pdf'")
        if (maxNumOfExercisesPerSubtopic <= 0) throw IllegalArgumentException("maxNumOfExercisesPerSubtopic has to be greater than 0")


        //begin main document
        startDocument(targetFile.nameWithoutExtension)
        for ((topic, subtopics) in topics) {
            //start new partial document for topic
            val topicDoc = File(exercisesFolder.absolutePath + "\\$topic.tex")
            //add \aufgabenbereich(topic) to partial document
            topicDoc.appendText("""
                \aufgabenbereich{$topic}
            """.trimIndent())
            //add \hinweis(topic)to partial document
            //generate exercises
            val exerciseText = getExcersizeTextForTopic(topic, subtopics, maxNumOfExercisesPerSubtopic, randomizeSubTopics)
            //add subtopics/exercises to partial document
            topicDoc.appendText(exerciseText)
            // \include tex file into mainDocument
            document.append("""
                \include{Aufgaben/$topic.tex}
            """.trimIndent())
        }
        //end main document
        endDocument()
        //save file (optional copy .tex's into a folder)
        val latexFile = File(tempfolder.absolutePath + "\\${targetFile.nameWithoutExtension}.tex")
        latexFile.writeText(document.toString())
        //call script to trigger latex interpreter.
        //wait for script to return and show result

        //notify finished (? look up kotlin async's)
    }

    private fun getExcersizeTextForTopic(topic: String, subtopics: List<String>, maxNumOfExercisesPerSubtopic: Int, randomizeSubTopics: Boolean): String {
        val builder = StringBuilder()
        val topicFolderPath = (repoRoot.absolutePath + "\\Aufgaben\\$topic\\")
        for (subtopic in subtopics) {
            builder.append(getSubExercisesFromSubTopic(topicFolderPath + subtopic, maxNumOfExercisesPerSubtopic))
                    .append("\n")
        }
        return builder.toString()
    }

    private fun getSubExercisesFromSubTopic(subTopicPath: String, maxNumOfExercisesPerSubtopic: Int): String {
        val builder = StringBuilder()
        val subTopicFolder = File(subTopicPath)
        val subTopicExercises = subTopicFolder.listFiles()
        val subTopicExercisesCount = Math.min(subTopicExercises.size, maxNumOfExercisesPerSubtopic);
        val numbs = List(subTopicExercises.size - 1) { i -> i }.shuffled()

        for (i in 0..subTopicExercisesCount) {
            builder.append(subTopicExercises[numbs[i]].readText() + "\n")
        }
        return builder.toString();
    }

    private fun startDocument(fileName: String) {
        //copy SetupData to tempfolder
        var x = File(Paths.get("SetupData").toAbsolutePath().toString())
        if (!x.exists()) {//find folder if we are debugging
            x = x.parentFile.parentFile
            x = x.listFiles().find { file -> file.name == "SetupData" }!!
        }
        x.copyRecursively(setupFolder, true)

        document.appendln("""
\documentclass[12pt]{ article }
\input{SetupData/usepackage}
\begin{document}
    \setTitel{${fileName}}
    \input{SetupData/pagesetup}
    """.trimIndent())
    }

    private fun endDocument() {
        document.appendln("""
\end{document}
%this document was automatically generated
        """.trimIndent())
    }
}

