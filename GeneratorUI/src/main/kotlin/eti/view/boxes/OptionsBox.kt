package eti.view.boxes

import eti.data.Options
import eti.data.OptionsObserver
import eti.extensions.isChecked
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*

class OptionsBox(private val optionsObserver: OptionsObserver) : View(), ChangeListener<Any> {
    val optionsPadding = 5.0

    val box1 = checkbox("speichere LaTeX Projekt") { paddingAll = optionsPadding }.apply { selectedProperty().addListener(this@OptionsBox) }
    val box2 = checkbox("generiere Antworten") { paddingAll = optionsPadding }.apply { selectedProperty().addListener(this@OptionsBox) }
    val box3 = checkbox("zufällige Subaufgabenbereiche") { paddingAll = optionsPadding }.apply { selectedProperty().addListener(this@OptionsBox) }
    val subTopicExerciseCountBox = textfield("2") {
        maxWidth = 40.0
        minWidth = 40.0
        alignment = Pos.BASELINE_RIGHT
        hboxConstraints { marginRight = 10.0 }
    }.apply { textProperty().addListener(this@OptionsBox) }

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

    override fun changed(observable: ObservableValue<out Any>?, oldValue: Any?, newValue: Any?) {
        onOptionsChanged()
    }

    private fun onOptionsChanged() {
        optionsObserver.onOptionschanged(Options(
                box1.isChecked(),
                box2.isChecked(),
                box3.isChecked(),
                subTopicExerciseCountBox.text.toIntOrNull() ?: Int.MAX_VALUE))
    }
}

