package eti.view.boxes

import eti.data.SubTopic
import javafx.geometry.Insets
import tornadofx.*

class SubTopicsBox : View() {
    override val root = vbox {
        label("Subaufgbenbereich") {
            vboxConstraints { margin = Insets(10.0) }
        }
        scrollpane {
            listview<SubTopic> {
            }
            vboxConstraints {
                marginLeft = 20.0
                minWidth = 230.0
            }
            isFitToWidth = true
        }
    }
}
