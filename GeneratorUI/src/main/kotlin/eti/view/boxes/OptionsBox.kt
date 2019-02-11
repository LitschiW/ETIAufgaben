package eti.view.boxes


import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*

class OptionsBox : View() {
    val optionsPadding = 5.0

    val box1 = checkbox("speichere .tex project") { paddingAll = optionsPadding }
    val box2 = checkbox("generiere Antworten") { paddingAll = optionsPadding }
    val box3 = checkbox("zufällige Subaufgabenbereiche") { paddingAll = optionsPadding }
    val subTopicExerciseCountBox = textfield("2") {
        maxWidth = 40.0
        minWidth = 40.0
        alignment = Pos.BASELINE_RIGHT
        hboxConstraints { marginRight = 10.0 }
    }


    override val root = vbox {
        label("Optionen") {
            vboxConstraints { margin = Insets(10.0) }
        }
        vbox {
            add(box1)
            //add(box2)
            add(box3)
            hbox {
                label("max. #Subaufgaben") {
                    paddingAll = optionsPadding
                }
                spacer { }
                add(subTopicExerciseCountBox)
            }


            vboxConstraints {
                margin = Insets(0.0, 20.0, 20.0, 20.0)
                vgrow = Priority.ALWAYS
            }
            background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
            border = Border(BorderStroke(Color(0.78431372549, 0.78431372549, 0.78431372549, 1.0), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(2.0)))
            minWidth = 230.0
            paddingAll = 5
        }
    }

}
