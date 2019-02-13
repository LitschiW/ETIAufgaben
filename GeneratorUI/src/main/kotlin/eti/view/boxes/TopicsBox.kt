package eti.view.boxes

import eti.data.ExerciseDirectory
import eti.data.TopicSelectorListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*

class TopicsBox(private val changeListener: TopicSelectorListener) : View() {
    val display =
            vbox {
                background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
                border = Border(BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(0.0)))
            }

    override val root = vbox {
        label("Aufgbenbereiche") {
            vboxConstraints { margin = Insets(10.0) }
        }
        gridpane {
            vboxConstraints {
                marginLeft = 20.0
                vgrow = Priority.ALWAYS
            }
            scrollpane {
                add(display)
                isFitToWidth = true
                style = "-fx-background-color:transparent;"
            }
            background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
            border = Border(BorderStroke(Color(0.78431372549, 0.78431372549, 0.78431372549, 1.0), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(2.0)))
            minWidth = 230.0
        }
    }

    init {
        for (topic in ExerciseDirectory.Topics) {
            display.add(checkbox(topic.name) {
                paddingAll = 5
                selectedProperty().addListener(ChangeListener<Boolean> { observableValue: ObservableValue<out Boolean>?,
                                                                         oldValue: Boolean,
                                                                         newValue: Boolean ->
                    changeListener.onTopicChanged(topic, newValue)
                })
            })
        }
    }
}
