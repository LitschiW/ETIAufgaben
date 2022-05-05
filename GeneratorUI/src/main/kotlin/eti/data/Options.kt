package eti.data

data class Options(val saveLatex: Boolean = false,
                   val generateAnswers: Boolean = false,
                   val randomSubTopics: Boolean = false,
                   val subTopicExerciseCount: Int = 2,
                   val subTopicsCount: Int = -1)
        //TODO: alternative output files : .ps and .div