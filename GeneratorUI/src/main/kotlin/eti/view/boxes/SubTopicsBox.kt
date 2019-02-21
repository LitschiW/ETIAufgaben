package eti.view.boxes

import eti.app.Styles
import eti.data.Options
import eti.data.SubTopic
import eti.data.Topic
import eti.view.OptionsObserver
import eti.view.TopicSelectorObserver
import javafx.beans.value.ObservableValue
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import tornadofx.*

class SubTopicsBox : View(), TopicSelectorObserver, OptionsObserver {

    val selected = mutableMapOf<Topic, MutableList<SubTopic>>()
    val display =
            vbox { addClass(Styles.scrollHolder) }

    override val root = vbox {
        addClass(Styles.boxRoot)
        label("Unteraufgabenbereich") {
            addClass(Styles.heading)
        }
        gridpane {
            addClass(Styles.boxHolder)
            vboxConstraints { vgrow = Priority.ALWAYS }
            scrollpane {
                addClass(Styles.scrollHolder)
                add(display)
            }
        }
    }

    fun showTopic(topic: Topic) {
        showSubTopics(topic)
    }

    fun showSubTopics(topic: Topic) {
        if (selected.keys.contains(topic)) return

        val selectedSubTopics = selected[topic]
        display.add(label(topic.name) { font = Font("Arial", 14.5); paddingBottom = 5; paddingTop = 5 })
        for (subTopic in topic.SubTopics) {
            display.add(checkbox(subTopic.nameWithoutExtension + " (${subTopic.Exercises.size})") {
                addClass(Styles.checkboxStyle)
                selectedProperty().set(selectedSubTopics != null && selectedSubTopics.contains(subTopic) || selectedSubTopics == null)
                selectedProperty().addListener(
                        ChangeListener<Boolean> { observableValue: ObservableValue<out Boolean>?, oldValue: Boolean, newValue: Boolean ->
                            if (!newValue && selected[topic] != null && selected[topic]!!.contains(subTopic)) selected[topic]?.remove(subTopic)
                            else if (newValue) {
                                selected.getOrPut(topic, defaultValue = { mutableListOf(subTopic) }).run { if (!contains(subTopic)) add(subTopic) }
                            }
                        })
            })
            if (selectedSubTopics == null)
                selected.getOrPut(topic, defaultValue = { mutableListOf(subTopic) }).run { if (!contains(subTopic)) add(subTopic) }
        }
    }

    fun removeSubTopic(topic: Topic) {
        var remove = false
        var i = 0
        while (i < display.children.size) {
            val child = display.children[i++]
            if (child is Label)
                remove = child.text == topic.name
            if (remove) {
                display.children.remove(child)
                i--
            }
        }
        selected.remove(topic)
    }

    fun getSelectedMap(): Map<Topic, List<SubTopic>> {
        return selected
    }

    override fun onTopicChanged(topic: Topic, checked: Boolean) {
        if (checked) showSubTopics(topic)
        else removeSubTopic(topic)
    }

    override fun onOptionschanged(opt: Options) {
        display.isVisible = !opt.randomSubTopics
    }
}
