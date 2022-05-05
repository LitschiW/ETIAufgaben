package eti.data.latex

import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

abstract class LatexDocument(initialText: String = "") {

    protected val textBuilder = StringBuffer(initialText)

    open fun getText(): String = textBuilder.toString()

    /**
     * Creates a file in the target location and writes the text of this latex Document into it.
     *
     * @throws FileNotFoundException if the target file can not be found.
     */
    fun create(targetFile: File): Boolean {
        @Suppress("NAME_SHADOWING")
        var targetFile = targetFile
        if (!targetFile.endsWith(".tex")) targetFile = File(Paths.get(targetFile.absolutePath + ".tex").toUri()) //checking for correct extention

        targetFile.createNewFile()
        if (targetFile.exists() && targetFile.isFile)
            targetFile.writeText(getText())
        else
            throw FileNotFoundException("Couldn't find or create target file!")
        return true
    }


    /**
     * Check if this document is a valid compilable latex document.
     */
    fun isValid(): Boolean {
        throw NotImplementedError()
    }

}