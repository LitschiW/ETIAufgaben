package eti.view

import eti.app.Styles
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Control
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import tornadofx.*
import java.io.File
import java.nio.file.Paths
import javax.swing.GroupLayout

class MainView : View("ETI Generator") {
    override val root =
            borderpane {
                top = gridpane {
                    vbox {
                        label("Aufgabentypen auswählen:") {
                            font = Font(20.0)
                            vboxConstraints {
                                marginBottom = 5.0
                            }
                        }
                        hbox {
                            scrollpane {
                                minHeight = 300.0
                                minWidth = 200.0
                                maxHeight = 300.0
                                maxWidth = 200.0
                                vbox {
                                    /* example box
                                  checkbox("2") {
                                      font = Font(15.0)
                                  vboxConstraints { margin= Insets(5.0) }
                              }*/
                                }
                            }
                            progressindicator {
                                maxWidth = 28.0
                                maxHeight = 28.0
                                hboxConstraints {
                                    marginLeft = 10.0
                                }
                            }
                            vboxConstraints {
                                marginLeft = 20.0
                            }
                        }


                        gridpaneConstraints {
                            columnRowIndex(0, 0)
                            margin = Insets(15.0)
                        }
                    }
                    hbox {
                        label("max. Teilaufgabenanzahl") {
                            font = Font(18.0)
                        }
                        textfield {
                            alignment = Pos.BASELINE_RIGHT
                            text = "0"
                            font = Font(15.0)
                            minWidth = 50.0
                            maxWidth = 50.0
                            hboxConstraints {
                                marginLeft = 10.0
                            }
                        }
                        gridpaneConstraints {
                            columnRowIndex(1, 0)
                            margin = Insets(15.0)
                            marginTop = 50.0
                        }
                    }
                }
                bottom = vbox {

                    hbox {
                        textfield {
                            hgrow = Priority.ALWAYS
                            hboxConstraints {
                                marginLeft=10.0
                            }
                            apply {
                                text = getNextFilePath()
                            }
                        }
                        button("Open...") {
                            hboxConstraints {
                                marginLeftRight(10.0)
                            }
                        }
                    }
                    borderpane {
                        left = button("Quit") {
                            borderpaneConstraints {
                                margin = Insets(5.0)
                            }
                        }
                        right = button("Save") {
                            borderpaneConstraints {
                                margin = Insets(5.0)
                            }
                        }
                    }
                    borderpaneConstraints {
                        margin = Insets(10.0)
                    }
                }
            }


    private fun getNextFilePath():String{
        val currDir = File(Paths.get("").toAbsolutePath().toUri())
        var nb = 0
        for (file in currDir.listFiles()) {
            if(file.isFile && file.name == "AB_$nb")
                nb++
        }
        return "$currDir\\AB_$nb.pdf"
    }
}
