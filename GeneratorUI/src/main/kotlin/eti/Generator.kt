package eti

import eti.data.SubTopic
import eti.data.Topic
import java.io.*
import java.nio.file.Paths

class Generator {

    //Directories used to store latex docs
    private val document = StringBuilder()
    private val tempDir: File = createTempDir()
    private val setupDir = File(tempDir.absolutePath + "\\SetupData\\")
    private val exercisesDir = File(tempDir.absolutePath + "\\Aufgaben\\")
    private val repoRoot: File

    //Streams (Output)
    private var out = PipedOutputStream()
    private var writer = out.writer()
    var inputStream = PipedInputStream(out)

    init {
        val root = File(Paths.get("").toAbsolutePath().toString())

        repoRoot = if (root.name == "GeneratorUI") root.parentFile else root
    }

    /**
     * @param topics Map that contains the list of topics (absolute paths) used as keys and references the used list of subtopics(names) as value to this key.
     * @param maxNumOfSubExercises maximum number of subtopics loads for each topic, use Int.MAX_VALUE for everything.
     * @param targetFile output File for the pdf
     * @param saveTex set this to true if the generated tex code should be saved into a folder.
     */
    fun generateDocument(topics: Map<Topic, List<SubTopic>>,
                         maxNumOfExercisesPerSubtopic: Int,
                         targetFile: File,
                         randomizeSubTopics: Boolean = false,
                         saveTex: Boolean = false) {
//one may want to do this async and lock the main screen for the duration

        //check Arguments
        if (targetFile.extension != "pdf") throw IllegalArgumentException("targetFile extension should be '.pdf'")
        if (maxNumOfExercisesPerSubtopic <= 0) throw IllegalArgumentException("maxNumOfExercisesPerSubtopic has to be greater than 0")

        //begin main document
        startDocument(targetFile.nameWithoutExtension)

        //iterate over selected Topics and Subtopics
        for ((topic, subtopics) in topics) {
            //start new partial document for topic
            val topicDoc = File(exercisesDir.absolutePath + "\\${topic.nameWithoutExtension.replace(Regex("[ $\"{}]"), "")}.tex")
//            if(!topicDoc.createNewFile()) {
// //may handle temp file already existing
//            }

            //let the topic generate its text
            val topicText = topic.generateText(subtopics, maxNumOfExercisesPerSubtopic)

            //add subtopics/exercise text to partial document
            topicDoc.appendText(topicText)

            // \include tex file into mainDocument
            document.appendln("""      \include{Aufgaben/${topicDoc.nameWithoutExtension}}""")
        }
        //end main document
        endDocument()
        //generate pdf file and copies it to target (also copies tex if necessary )
        generatePDF(targetFile, saveTex)
        //cleanup
        tempDir.deleteRecursively()
//notify finished (? look up kotlin event system)

    }

    private fun generatePDF(targetFile: File, saveTex: Boolean) {
        //save file
        val latexFile = File(tempDir.absolutePath + "\\${targetFile.nameWithoutExtension}.tex")
        latexFile.writeText(document.toString())

        //if saveTex: copy result's tex structure into the targetFile's folder
        if (saveTex) {
            tempDir.copyRecursively(File(targetFile.absolutePath.removeSuffix(".pdf")), true)
        }

        //call command to trigger latex interpreter
        interpretLatex(latexFile) //command will be run synchronously

        //if saveTex:  copy pdf in targetFile's folder
        val pdfFile = File(tempDir.absolutePath + "\\${targetFile.name}")
        if (saveTex) {
            pdfFile.copyTo(File(targetFile.absolutePath.removeSuffix(".pdf") + "\\${targetFile.name}"))
        } else {
            //else copy pdf into targetFile
            pdfFile.copyTo(targetFile)
        }
    }

    private fun interpretLatex(latexFile: File) {
        //create command...
        val command = "pdflatex.exe -shell-escape -synctex=1 -interaction=nonstopmode ${latexFile.absolutePath}"

        writer.write("Starting latex compiler with:\n")
        writer.write("$command\n")

        val p = ProcessBuilder(command.split(' '))
                .directory(tempDir)
                .start() //..and run it

        // read the output from the command (input into this program)
        val stdInput = p.inputStream.bufferedReader()

        do {
            val s = stdInput.readLine()
            if (s != null) writer.apply { write(s); flush() }
        } while ((s) != null)
    }

    private fun startDocument(fileName: String) {
        //copy SetupData to tempDir
        var x = File(Paths.get("SetupData").toAbsolutePath().toString())
        if (!x.exists()) {//find folder if we are debugging
            x = x.parentFile.parentFile
            x = x.listFiles().find { file -> file.name == "SetupData" }!!
        }
        x.copyRecursively(setupDir, true)

        document.appendln("""
\documentclass[12pt]{article}
\input{SetupData/usepackage}
\begin{document}
    \setTitel{${fileName}}
    \input{SetupData/pagesetup}
    """.trimIndent())

        exercisesDir.mkdir()
    }

    private fun endDocument() {
        document.appendln("""
\end{document}
%this document was automatically generated
        """.trimIndent())
    }
}