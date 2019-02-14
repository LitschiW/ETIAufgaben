package eti.view

import eti.data.Topic

interface TopicSelectorObserver {
    fun onTopicChanged(topic: Topic, checked: Boolean)
}