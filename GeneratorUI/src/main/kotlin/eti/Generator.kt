package eti

import eti.data.Options
import eti.data.SubTopic
import eti.data.Topic
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import java.io.File
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.nio.file.Paths
import java.util.Collections.synchronizedSet
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


const val notUsableCharsForLatex = "[ $\"{}]"

class Generator {

    companion object {

        private val TargetFilesInUse = synchronizedSet(mutableSetOf<File>())

        /**
         * Generates a latex based pdf document in the target file based on the topics provided int topics.<br/>
         * This Method spins up a thread so it can be called multiple times safely.
         *
         * <b>It is not possible to use the same target file for parallel running generators.</b>
         * In case you start generating whilst your target file is the target of a running generator the generating will fail.
         *
         *
         * @param topics Map that contains the list of topics (absolute paths) used as keys and references the used list of subtopics(names) as value to this key.
         * @param targetFile output File for the pdf
         * @param options provide options for the compiling here
         * @param outputStream this PipedInputStream will be connected to the output of the generator and the latex interpreter.
         * @param onFinished A callback that will be called once the generator stopped running. The Boolean parameter will indicate whether the generating was successful.
         */
        fun generateDocument(topics: Map<Topic, List<SubTopic>>,
                             targetFile: File,
                             options: Options,
                             outputStream: PipedInputStream? = null,
                             onFinished: (Boolean) -> Unit = { _: Boolean -> }) {

            thread {
                //generating folders
                //Directories used to store latex docs
                val document = StringBuilder()
                val tempDir: File = createTempDir()
                val setupDir = File(Paths.get(tempDir.absolutePath, "SetupData").toUri())
                val exercisesDir = File(Paths.get(tempDir.absolutePath, "Aufgaben").toUri())
                val root = File(Paths.get("").toAbsolutePath().toString())
                val repoRoot = if (root.name == "GeneratorUI") root.parentFile else root

                //Settings
                @Suppress("NAME_SHADOWING") //Name shadowing is used on purpose for readability
                val targetFile = //adds .pdf to the target file if needed
                        if (targetFile.extension != "pdf" && !options.saveLatex) File(targetFile.absolutePath + ".pdf")
                        else targetFile

                //Status variables
                var successful = true

                //Streams (Console-Output)
                val out = PipedOutputStream()
                val writer = out.writer()
                outputStream?.connect(out)

                fun writeOutputLine(text: String) {
                    writer.apply { write(text); if (!text.endsWith("\n")) write("\n"); flush(); }
                }

                fun handelTargetFileExisting(targetFile: File): Boolean {
                    val result = AtomicBoolean(true) //using atomic bool here just to be safe in regards to async behavior
                    writeOutputLine("Datei Konflikt: Die Zieldatei ist bereits vorhanden!")
                    val latch = CountDownLatch(1)
                    Platform.runLater {
                        val alert = Alert(Alert.AlertType.WARNING)
                        alert.title = "Error whilst starting generation"
                        alert.headerText = "Die Zieldatei existiert bereits!"
                        alert.contentText = "Die angegebene Ziel Datei existiert bereits, oder der Zielordner enthält bereits eine Datei mit dem Namen \"${targetFile.nameWithoutExtension}.pdf\"." +
                                "\nSoll die vorhandene(n) Datei(en) überschrieben werden?"
                        alert.buttonTypes.clear()
                        alert.buttonTypes.addAll(ButtonType.YES, ButtonType.CANCEL)
                        val res = alert.showAndWait()
                        if (res.isPresent && res.get() == ButtonType.YES) {
                            if (targetFile.isFile) {
                                writeOutputLine("Lösche Datei...\n" +
                                        if (targetFile.delete()) "Löschen erfolgreich." else "Löschen fehlgeschlagen.")
                            } else if (targetFile.isDirectory) targetFile.listFiles().find { file -> file.name == targetFile.name }?.delete()
                        } else {
                            writeOutputLine("Die angegebene Zieldatei existiert bereits, oder der Zielordner enthält bereits eine Datei mit dem Namen \"${targetFile.nameWithoutExtension}.pdf\"...")
                            result.set(false)
                        }
                        latch.countDown()
                    }
                    try {
                        latch.await()//waiting for the dialog to return
                    } catch (e: Throwable) {
                        throw e
                    }
                    return result.get()
                }

                fun startDocument() {
                    writeOutputLine("Starting Document...")
                    //copy SetupData to tempDir
                    var x = File(Paths.get("SetupData").toAbsolutePath().toString())
                    if (!x.exists()) {//find folder in case of debugging mode
                        x = x.parentFile.parentFile
                        x = x.listFiles().find { file -> file.name == "SetupData" }!!
                    }
                    writeOutputLine("SetupData in ${x.absolutePath} gefunden!")
                    writeOutputLine("Kopiere SetupDate nach ${setupDir.absolutePath}")
                    x.copyRecursively(setupDir, true)
                    writeOutputLine("Done..")

                    document.appendln("""
\documentclass[12pt]{article}
\input{SetupData/usepackage}
\begin{document}
    \setTitel{${targetFile.nameWithoutExtension}}
    \input{SetupData/pagesetup}
    """.trimIndent())

                    writeOutputLine("Created document header.")
                    exercisesDir.mkdir()
                    writeOutputLine("Created directory for exercises")
                }

                fun endDocument() {
                    document.appendln("""
\end{document}
%this document was automatically generated
        """.trimIndent())
                    writeOutputLine("finished creating of the document text")
                }

                fun compileLatex(latexFile: File): Boolean {
                    //create command...
                    val command = "pdflatex -shell-escape -synctex=1 -interaction=nonstopmode ${latexFile.absolutePath}"

                    writeOutputLine("Starting latex compiler with:")
                    writeOutputLine(command)

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

                fun checkArguments(topics: Map<Topic, List<SubTopic>>, targetFile: File, options: Options) {
                    writeOutputLine("starting generation...")
                    writeOutputLine("checking Arguments...")

                    if (options.subTopicExerciseCount <= 0) throw IllegalArgumentException("SubtopicExerciseCount has to be greater than 0")
                    if (targetFile.isDirectory && options.saveLatex) writeOutputLine("Warning!: selected to save latex project, but target folder name is not a Directory!")

                    if (targetFile.exists() && (!options.saveLatex && targetFile.isFile || targetFile.isDirectory && targetFile.listFiles().any { file -> file.name == targetFile.name })) {
                        if (!handelTargetFileExisting(targetFile)) //either deletes the existing file or throws an exception
                        {
                            writeOutputLine("Überschreiben der Datei abgelehnt...")
                            throw IllegalStateException("User decided against deleting an existing file. Stopping execution...")
                        }
                    }
                }

                fun generatePDF() {
                    //save latex file
                    val latexFile = File(Paths.get(tempDir.absolutePath, "${targetFile.nameWithoutExtension}.tex").toUri())

                    writeOutputLine("saving generated text to ${latexFile.absolutePath}")
                    latexFile.writeText(document.toString())
                    writeOutputLine("Done..")

                    //if saveTex: copy result's tex structure into the targetFile's folder
                    if (options.saveLatex) {
                        writeOutputLine("save tex files is enabled.\nCopying project files to target location ${targetFile.absolutePath}")
                        tempDir.copyRecursively(File(targetFile.absolutePath), true)
                        writeOutputLine("Done..")
                    }

                    writeOutputLine("starting latex compiler...")
                    //call command to trigger latex interpreter
                    if (compileLatex(latexFile)) //command will be run synchronously
                        writeOutputLine("\n\nPDF erfolgreich erstellt!")
                    else {
                        writeOutputLine("\n\nEin Fehler ist aufgetreten.\n Es wurde keine keine pdf erstellt")
                        throw Exception("Something went wrong while compiling the LaTeX code. See the output for more information.")
                    }

                    //if saveTex:  copy pdf in targetFile's folder
                    val pdfFile = File(latexFile.absolutePath.removeSuffix(".tex") + ".pdf")
                    writeOutputLine("Kopiere Ausgabe in den/die Zielordner/-datei: ${targetFile.absolutePath}")
                    if (options.saveLatex) {
                        pdfFile.copyTo(File(Paths.get(targetFile.absolutePath, pdfFile.name).toUri()))
                    } else {
                        //else copy pdf into targetFile
                        pdfFile.copyTo(targetFile)
                    }
                }

                try {
                    //prevents generators to have the same target file.
                    if (!TargetFilesInUse.add(targetFile)) {
                        writeOutputLine("Abbruch..\nDie angegebene Zieldatei wird bereits als Zieldatei in einer laufenden Generierung verwendet!")
                        throw IllegalArgumentException("A generator Task is already running with the same target file.")
                    }
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

                        writeOutputLine("generating Text for topic \"${topic.nameWithoutExtension}\"...")
                        //let the topic generate its text depending on the generation style
                        val topicText =
                                if (options.randomSubTopics) topic.generateText(options.subTopicsCount, options.subTopicExerciseCount)
                                else topic.generateText(subtopics, options.subTopicExerciseCount)
//TODO: answer generation
                        writeOutputLine("writing Text to file ${topicDoc.absolutePath}")
                        //add subtopics/exercise text to partial document
                        topicDoc.appendText(topicText)

                        writeOutputLine("appending text to document")
                        // \include tex file into mainDocument
                        document.appendln("""    \include{Aufgaben/${topicDoc.nameWithoutExtension}}""")
                        writeOutputLine("Done..")
                    }
                    //end main document
                    endDocument()
                    //generate pdf file and copies it to target (also copies tex if necessary )
                    generatePDF()
                    TargetFilesInUse.remove(targetFile)
                } catch (e: Exception) {
                    successful = false
                    writeOutputLine("Ein Fehler ist aufgetreten, beende Generierung...")
                    println("Stopped generating, because:")
                    println(e)
                } finally {
                    //cleanup
                    out.close()
                    tempDir.deleteRecursively()
                    onFinished(successful)
                }
            }
        }
    }
}