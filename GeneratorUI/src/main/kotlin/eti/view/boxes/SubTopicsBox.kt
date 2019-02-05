package eti.view.boxes

import eti.data.SubTopic
import javafx.geometry.Insets
import tornadofx.*

class SubTopicsBox : View() {
    override val root = vbox {
        label("Unteraufgbenbereich") {
            vboxConstraints { margin = Insets(10.0) }
        }
        scrollpane {
            listview<SubTopic> {

            }
            vboxConstraints {
                margin = Insets(0.0, 0.0, 20.0, 20.0)
                minWidth = 100.0
            }
        }
    }
}
