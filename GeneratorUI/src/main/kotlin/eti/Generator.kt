package eti

import eti.data.Options
import eti.data.SubTopic
import eti.data.Topic
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import java.io.File
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.nio.file.Paths

class Generator {

    //Directories used to store latex docs
    private val document = StringBuilder()
    private val tempDir: File = createTempDir()
    private val setupDir = File(Paths.get(tempDir.absolutePath, "SetupData").toUri())
    private val exercisesDir = File(Paths.get(tempDir.absolutePath, "Aufgaben").toUri())
    private val repoRoot: File

    private val notUsableCharsForLatex = "[ $\"{}]"

    //Settings
    private var targetFile: File? = null
    private var currentOptions: Options = Options()


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
     * @param targetFile output File for the pdf
     * @param options provide options for the compiling here
     */
    @Synchronized
    fun generateDocument(topics: Map<Topic, List<SubTopic>>,
                         targetFile: File,
                         options: Options) {
        try {
            //check Arguments for errors
            checkArguments(topics, targetFile, options)


            //begin main document
            startDocument()

            //iterate over selected Topics and Subtopics
            for ((topic, subtopics) in topics) {
                //start new partial document for topic and
                //remove not LaTeX conform symbols from .tex File's name
                var topicDoc = File(exercisesDir.absolutePath + "${File.separator}${topic.nameWithoutExtension.replace(Regex(notUsableCharsForLatex), "")}.tex")
                var count = 1
                while (!topicDoc.createNewFile()) {
                    topicDoc = File(topicDoc.absolutePath.removeSuffix(".tex") + "${count++}.tex")
                }

                writeOutput("generating Text for topic \"${topic.nameWithoutExtension}\"...\n")
                //let the topic generate its text depending on the generation style
                val topicText =
                        if (currentOptions.randomSubTopics) topic.generateText(currentOptions.subTopicsCount, currentOptions.subTopicExerciseCount)
                        else topic.generateText(subtopics, currentOptions.subTopicExerciseCount)
//TODO: answer generation
                writeOutput("writing Text to file ${topicDoc.absolutePath}\n")
                //add subtopics/exercise text to partial document
                topicDoc.appendText(topicText)

                writeOutput("appending text to document\n")
                // \include tex file into mainDocument
                document.appendln("""    \include{Aufgaben/${topicDoc.nameWithoutExtension}}""")
                writeOutput("Done..\n")
            }
            //end main document
            endDocument()
            //generate pdf file and copies it to target (also copies tex if necessary )
            generatePDF()
            //cleanup
            tempDir.deleteRecursively()

//TODO: may notify finished (if caller listens to the outputStream, it will automatically pause), may provide optional callback or something...

        } catch (e: Exception) {
            throw e
        } finally {
            out.close()
        }
    }

    private fun checkArguments(topics: Map<Topic, List<SubTopic>>, targetFile: File, options: Options) {
        writeOutput("starting generation...\n")
        writeOutput("checking Arguments...\n")

        if (targetFile.exists() && (!options.saveLatex && targetFile.isFile || targetFile.isDirectory && targetFile.listFiles().any { file -> file.name == targetFile.name })) {
            handelTargetFileExisting(targetFile) //either deletes the existing file or throws an exception
        }

        this.targetFile =
                if (targetFile.extension != "pdf" && !options.saveLatex) File(targetFile.absolutePath + ".pdf")
                else targetFile


        if (options.subTopicExerciseCount <= 0) throw IllegalArgumentException("SubtopicExerciseCount has to be greater than 0\n")

        currentOptions = options

        if (targetFile.isDirectory && options.saveLatex) writeOutput("Warning!: selected to save latex project, but target folder name is not a Directory!\n")
    }

    private fun handelTargetFileExisting(targetFile: File) {
        val alert = Alert(Alert.AlertType.WARNING)
        alert.title = ("Datei existiert bereits!")
        alert.headerText = "Error whilst starting generation"
        alert.contentText = "Die angegebene Ziel Datei existiert bereits oder der Zielordner enthält bereits eine Datei mit passendem Namen.\nSoll die vorhandene Datei gelöscht werden?"
        alert.buttonTypes.addAll(ButtonType.YES, ButtonType.CANCEL)
        val res = alert.showAndWait()
        if (res.isPresent && res.get() == ButtonType.YES) {
            if (targetFile.isFile) targetFile.delete()
            else if (targetFile.isDirectory) targetFile.listFiles().find { file -> file.name == targetFile.name }?.delete()
        } else throw IllegalArgumentException("Die angegebene Ziel Datei existiert bereits oder der Zielordner enthält bereits eine Datei mit passendem Namen.")
    }

    private fun generatePDF() {
        val targetFile = this.targetFile as File
        //save latex file
        val latexFile = File(Paths.get(tempDir.absolutePath, "${targetFile.nameWithoutExtension}.tex").toUri())
        writeOutput("saving generated text to ${latexFile.absolutePath}\n")
        latexFile.writeText(document.toString())
        writeOutput("Done..\n")

        //if saveTex: copy result's tex structure into the targetFile's folder
        if (currentOptions.saveLatex) {
            writeOutput("save tex files is enabled.\nCopying project files to target location ${targetFile.absolutePath}\n")
            tempDir.copyRecursively(File(targetFile.absolutePath), true)
            writeOutput("Done..\n")
        }

        writeOutput("starting latex compiler...\n")
        //call command to trigger latex interpreter
        if (compileLatex(latexFile)) //command will be run synchronously
            writeOutput("\n\nPDF erfolgreich erstellt!\n")
        else {
            writeOutput("\n\nEin Fehler ist aufgetreten.\n Es wurde keine keine pdf erstellt\n")
            tempDir.deleteRecursively()
            throw Exception("Something went wrong while compiling the LaTeX code. See the output for more information.")
        }

        //if saveTex:  copy pdf in targetFile's folder
        val pdfFile = File(latexFile.absolutePath.removeSuffix(".tex") + ".pdf")
        writeOutput("moving pdf-file from output to target file/folder ${targetFile.absolutePath}\n")
        if (currentOptions.saveLatex) {
            pdfFile.copyTo(File(Paths.get(targetFile.absolutePath, pdfFile.name).toUri()))
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

        var detectedError = false
        do {
            val s = stdInput.readLine()
            if (s != null) {
                writer.apply { write(s + "\n"); flush() }
                //really simple error detection
                detectedError = detectedError || (s.contains("error"))
            }
        } while ((s) != null)

        return !detectedError
    }

    private fun startDocument() {
        writeOutput("Starting Document...\n")
        //copy SetupData to tempDir
        var x = File(Paths.get("SetupData").toAbsolutePath().toString())
        if (!x.exists()) {//find folder if we are debugging
            x = x.parentFile.parentFile
            x = x.listFiles().find { file -> file.name == "SetupData" }!!
        }
        writeOutput("found SetupData at ${x.absolutePath}\n")
        writeOutput("copy SetupData to ${setupDir.absolutePath}\n")
        x.copyRecursively(setupDir, true)
        writeOutput("Done..\n")

        document.appendln("""
\documentclass[12pt]{article}
\input{SetupData/usepackage}
\begin{document}
    \setTitel{${targetFile?.nameWithoutExtension}}
    \input{SetupData/pagesetup}
    """.trimIndent())

        writeOutput("Created document start\n")
        exercisesDir.mkdir()
        writeOutput("Created directory for Exercises\n")
    }

    private fun endDocument() {
        document.appendln("""
\end{document}
%this document was automatically generated
        """.trimIndent())
        writeOutput("finished creating of the document text\n")
    }

    private fun writeOutput(text: String) {
        writer.apply { write(text);flush(); }
    }
}