package eti.view.boxes

import eti.app.Styles
import eti.data.ExerciseDirectory
import eti.view.TopicSelectorObserver
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*

class TopicsBox(private val changeObserver: TopicSelectorObserver) : View() {
    val display =
            vbox {
                background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
                border = Border(BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(0.0)))
            }

    override val root = vbox {
        addClass(Styles.boxRoot)
        label("Aufgbenbereiche") {
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

    init {
        for (topic in ExerciseDirectory.Topics) {
            display.add(checkbox(topic.name) {
                paddingAll = 5.0
                selectedProperty().addListener(ChangeListener<Boolean> { observableValue: ObservableValue<out Boolean>?, oldValue: Boolean, newValue: Boolean ->
                    changeObserver.onTopicChanged(topic, newValue)
                })
            })
        }
    }
}
