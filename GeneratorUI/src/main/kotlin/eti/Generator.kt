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

            writeOutput("generating Text for topic \"${topic.nameWithoutExtension}\"...\n")
            //let the topic generate its text
            val topicText = topic.generateText(subtopics, maxNumOfExercisesPerSubtopic)

            writeOutput("writing Text to file ${topicDoc.absolutePath}\n")
            //add subtopics/exercise text to partial document
            topicDoc.appendText(topicText)

            writeOutput("appending text to document\n")
            // \include tex file into mainDocument
            document.appendln("""      \include{Aufgaben/${topicDoc.nameWithoutExtension}}""")
            writeOutput("Done..\n")
        }
        //end main document
        endDocument()
        //generate pdf file and copies it to target (also copies tex if necessary )
        generatePDF(targetFile, saveTex)
        //cleanup
        tempDir.deleteRecursively()
//notify finished (? look up kotlin event system)

        out.close()
    }

    private fun generatePDF(targetFile: File, saveTex: Boolean) {
        //save file
        val latexFile = File(tempDir.absolutePath + "\\${targetFile.nameWithoutExtension}.tex")
        writeOutput("saving generated text to ${latexFile.absolutePath}\n")
        latexFile.writeText(document.toString())
        writeOutput("Done..\n")

        //if saveTex: copy result's tex structure into the targetFile's folder
        if (saveTex) {
            writeOutput("save tex files is enabled.\nCopying project files to target location $targetFile.absolutePath.removeSuffix(\".pdf\"))\n")
            tempDir.copyRecursively(File(targetFile.absolutePath.removeSuffix(".pdf")), true)
            writeOutput("Done..\n")
        }

        writeOutput("starting latex compiler...\n")
        //call command to trigger latex interpreter
        if (compileLatex(latexFile)) //command will be run synchronously
            writeOutput("\n\nPDF erfolgreich erstellt.\n")
        else {
            writeOutput("\n\nEin Fehler ist aufgetreten.\n Es wurde keine keine pdf erstellt\n")
            tempDir.deleteRecursively()
            return
        }

        //if saveTex:  copy pdf in targetFile's folder
        val pdfFile = File(tempDir.absolutePath + "\\${targetFile.name}")
        writeOutput("moving pdf-file from output to target file/folder\n")
        if (saveTex) {
            pdfFile.copyTo(File(targetFile.absolutePath.removeSuffix(".pdf") + "\\${targetFile.name}"))
        } else {
            //else copy pdf into targetFile
            pdfFile.copyTo(targetFile)
        }
    }

    private fun compileLatex(latexFile: File): Boolean {
        //create command...
        val command = "pdflatex.exe -shell-escape -synctex=1 -interaction=nonstopmode ${latexFile.absolutePath}"

        writeOutput("Starting latex compiler with:\n")
        writeOutput("$command\n")

        val p = ProcessBuilder(command.split(' '))
                .directory(tempDir)
                .start() //..and run it

        // read the output from the command (input into this program)
        val stdInput = p.inputStream.bufferedReader()

        do {
            val s = stdInput.readLine()
//detect error here
            if (s != null) writer.apply { write(s + "\n"); flush() }
        } while ((s) != null)

        return true
    }

    private fun startDocument(fileName: String) {
        writeOutput("Starting...\n")
        //copy SetupData to tempDir
        var x = File(Paths.get("SetupData").toAbsolutePath().toString())
        if (!x.exists()) {//find folder if we are debugging
            x = x.parentFile.parentFile
            x = x.listFiles().find { file -> file.name == "SetupData" }!!
        }
        writeOutput("fond SetupData at ${x.absolutePath}\n")
        writeOutput("copy SetupData to ${setupDir.absolutePath}\n")
        x.copyRecursively(setupDir, true)
        writeOutput("Done..\n")

        document.appendln("""
\documentclass[12pt]{article}
\input{SetupData/usepackage}
\begin{document}
    \setTitel{${fileName}}
    \input{SetupData/pagesetup}
    """.trimIndent())

        writeOutput("Created document start\n")
        exercisesDir.mkdir()
        writeOutput("created directory for Exercises\n")
    }

    private fun endDocument() {
        document.appendln("""
\end{document}
%this document was automatically generated
        """.trimIndent())
        writeOutput("finished creating of the document text\n")
    }

    private fun writeOutput(text: String) {
        writer.apply { write(text);flush() }
    }
}