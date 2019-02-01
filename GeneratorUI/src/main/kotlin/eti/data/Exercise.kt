package eti.data

import java.io.File
import java.net.URI

class Exercise : File {
    constructor(pathname: String?) : super(pathname)
    constructor(parent: String?, child: String?) : super(parent, child)
    constructor(parent: File?, child: String?) : super(parent, child)
    constructor(uri: URI?) : super(uri)

    fun getExerciseText(): String {
        return "\n${readText()}\n"
    }
}