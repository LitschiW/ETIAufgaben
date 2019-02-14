package eti.view

import eti.app.Styles
import eti.data.Options
import eti.data.SubTopic
import eti.data.Topic
import eti.view.boxes.OptionsBox
import eti.view.boxes.SubTopicsBox
import eti.view.boxes.TopicsBox
import javafx.geometry.Pos
import tornadofx.*

class BoxHolder(private val optionsObserver: OptionsObserver? = null) : View(), OptionsObserver, TopicSelectorObserver {

    val topicBox = TopicsBox(this)
    val subTopicsBox = SubTopicsBox()
    val optionsBox = OptionsBox(this)

    override val root = hbox {
        add(topicBox.root)
        add(subTopicsBox.root)
        add(optionsBox.root)
        minHeight = Styles.boxHeight
        maxHeight = Styles.boxHeight
        alignment = Pos.TOP_CENTER
    }

    override fun onTopicChanged(topic: Topic, checked: Boolean) {
        subTopicsBox.onTopicChanged(topic, checked) //forwarding event
    }

    override fun onOptionschanged(opt: Options) {
        optionsObserver?.onOptionschanged(opt)
        subTopicsBox.onOptionschanged(opt)
        subTopicsBox.root.isDisable = opt.randomSubTopics
    }

    fun getSelection(): Map<Topic, List<SubTopic>> {
        return subTopicsBox.getSelectedMap()
    }
}
