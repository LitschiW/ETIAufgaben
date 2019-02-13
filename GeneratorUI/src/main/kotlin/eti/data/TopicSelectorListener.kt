package eti.data

interface TopicSelectorListener {
    fun onTopicChanged(topic: Topic, checked: Boolean)
}