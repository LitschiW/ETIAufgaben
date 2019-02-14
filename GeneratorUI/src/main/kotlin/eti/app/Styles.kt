package eti.app

import javafx.geometry.Insets
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        const val boxWidth = 230.0
        const val boxHeight = 350.0
        const val boxSpacerDistance = 20.0
        const val elementsPadding = 5.0
        val headingFont = Font("Arial", 16.0)
        val boxHeadingMargin = Insets(10.0)
        val background_White = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
        val border_Gray = Border(BorderStroke(Color(0.78431372549, 0.78431372549, 0.78431372549, 1.0), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(2.0)))
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
    }
}