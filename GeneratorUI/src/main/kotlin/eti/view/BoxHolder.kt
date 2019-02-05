package eti.view

import eti.view.boxes.OptionsBox
import eti.view.boxes.SubTopicsBox
import eti.view.boxes.TopicsBox
import javafx.geometry.Pos
import tornadofx.*

class BoxHolder : View() {
    val topicBox = TopicsBox()
    val subTopicsBox = SubTopicsBox()
    val optionsBox = OptionsBox()

    override val root = gridpane {
        alignment = Pos.TOP_CENTER
    }

    init {
        root.add(topicBox.root, 0, 0)
        root.add(subTopicsBox.root, 1, 0)
        root.add(optionsBox.root, 2, 0)
    }
}
