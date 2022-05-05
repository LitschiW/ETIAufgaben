package eti.data.latex

import java.util.*

class UsepackageDocument(initialText: String = "") : LatexDocument(initialText) {

    private val packages = mutableMapOf<String, Int>()

    @Synchronized
    fun addUsepackage(name: String, priority:Int = 0) {
        if(packages.containsKey(name))
        {
            packages[name] = Math.max(packages[name]!!,priority)
        }
        else packages[name] = priority
    }


    override fun getText(): String {
        val sorted =packages.entries.toSortedSet(kotlin.Comparator { o1, o2 ->
            o1.value - o2.value
        })

        for (entry in sorted)
        {
            textBuilder.append("\\usepackage{${entry.key}}")
        }

    }
}