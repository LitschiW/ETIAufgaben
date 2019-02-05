package eti.view.boxes


import javafx.geometry.Insets
import javafx.scene.control.CheckBox
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*

class OptionsBox : View() {
    val box1: CheckBox = checkbox("speichere .tex project") { }
    val box2 = checkbox("generiere Antworten") { }
    val box3 = checkbox("randomisiere Unteraufgabenbereiche") { }
    override val root = vbox {
        label("Optionen") {
            vboxConstraints { margin = Insets(10.0) }
        }
        vbox {
            box1
            //box2
            box3
            vboxConstraints {
                margin = Insets(0.0, 0.0, 20.0, 20.0)
            }
            minWidth = 100.0
            background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
            border = Border(BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(1.0)))
        }
    }

}
