package eti.view.boxes

import eti.data.Topic
import javafx.geometry.Insets
import tornadofx.*

class TopicsBox : View() {
    override val root = vbox {
        label("Aufgabenbereich") {
            vboxConstraints { margin = Insets(10.0) }
        }
        scrollpane {
            listview<Topic> {
            }
            vboxConstraints {
                margin = Insets(0.0, 0.0, 20.0, 20.0)
                minWidth = 230.0
            }
            isFitToWidth = true
        }
    }
}
