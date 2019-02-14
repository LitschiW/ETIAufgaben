package eti.app

import javafx.scene.control.ScrollPane
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val boxHolder by cssclass()
        val boxRoot by cssclass()
        val checkboxStyle by cssclass()
        val scrollHolder by cssclass()
        val numberInputBox by cssclass()

        const val boxHeight = 350.0
        val elementsPadding = 5.0.px
        val boxWidth = 230.px
    }

    init {
        heading {
            padding = box(5.px, 0.px)
            fontWeight = FontWeight.BOLD
            font = Font("Arial", 16.0)
        }

        val whiteBox = mixin {
            backgroundColor += Paint.valueOf("#FFFFFF")
            minWidth = boxWidth
        }

        boxHolder {
            +whiteBox
            borderColor += CssBox(
                    c("#C8C8C8"),
                    c("#C8C8C8"),
                    c("#C8C8C8"),
                    c("#C8C8C8"))
            borderWidth += box(2.px)
            padding = box(3.px)
        }

        scrollHolder {
            +whiteBox
            hBarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            viewport {
                backgroundColor += Paint.valueOf("#FFFFFF") //removes grey background of a scrollpane viewport.
            }
        }

        boxRoot {
            padding = box(0.px, 0.px, 0.px, 15.px)
        }

        checkboxStyle {
            padding = box(elementsPadding)
        }
    }
}