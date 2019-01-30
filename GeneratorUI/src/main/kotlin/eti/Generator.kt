import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Paths
import kotlin.random.Random



fun main(args: Array<String>) {
    val workingDic = Paths.get("").toAbsolutePath().toString()
    val workingDicFile = File(workingDic).parentFile

    val targetFile = File("$workingDic\\derp.tex")
    genDoc("",targetFile)
}

fun genDoc(exerciseString :String, targetFile:File)
{
    val text = """
\documentclass[12pt]{article}
\input{../SetupData/usepackage}
\begin{document}
    \setTitel{${targetFile.name}}
   \input{../SetupData/pagesetup}
        $exerciseString
\end{document}
""".trimIndent()
    val writer = FileWriter(targetFile)
    writer.append(text)
    writer.flush()
    writer.close()
}

 fun getTwoRandomExcercisesFromTopic(exerciseTopic: File) : String {
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

 fun getRandomExercisesFromSubTopic(subTopic: File, count: Int = 0): String {
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

 fun getLinesOfFile(file: File): String {
    if (!file.isFile) throw IOException("This is not a File!")
    return file.readText()
}