package eti.data.latex

/**
 * Class that represents the main document in regards to this generator project.
 * It can be used to handle all interactions with the .tex text.
 * Keep in mind that this is not a file but simply a wrapper for a StringBuffer.
 * This implementation is therefore threadsafe.
 *
 * To write the generated text into a file use  LatexFile.create(File).
 */
class MainDocument(initialText: String = "") : LatexDocument(initialText) {

    private val setupDoc = SetupDocument()
    private val usepackageDoc = UsepackageDocument()

    //properties

    /**
     * Sets the title of the document.
     */
    var Title: String = ""

    fun addUsepackage(name: String) {

    }

    fun addCommand(commandtext: String) {

    }

    fun addExerciseText(topicName: String) {
        textBuilder.append("""    \include{Aufgaben/$topicName}""")
    }


    override fun getText(): String {
        textBuilder.insert(0, """
\documentclass[12pt]{article}
\input{SetupData/usepackage}
\begin{document}
    \setTitel{$Title}
    \input{SetupData/pagesetup}
    """.trimIndent())



        textBuilder.append("""
\end{document}
%this document was automatically generated
        """.trimIndent())

        return textBuilder.toString()
    }
}