package cat.gencat.access.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Color.rgb
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.*

class GesticusStyles : Stylesheet() {

    /* tableview */
    val cellBorderColor = rgb(168, 214, 205)
    val oddCellColor = rgb(105, 132, 173)
    val highlightColor = rgb(244, 244, 244)
    val textColor = rgb(89, 89, 89)
    val evenCellColor = rgb(168, 199, 214)

    companion object {
        val heading by cssclass()
        val zip by cssclass()
        val flat = mixin {
            backgroundInsets += box(0.px)
            borderColor += box(Color.DARKGRAY)
        }
        val dangerButton by cssclass()
        val table by cssclass()
        val form by cssclass()
        val visites by cssclass()
    }

    init {

        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        /* selector */

        s(button, textInput) {
            +flat
            fontWeight = FontWeight.BOLD
        }

        passwordField {
            +flat
            backgroundColor += Color.RED
        }

        dangerButton {
            backgroundInsets += box(0.px)
            fontWeight = FontWeight.BOLD
            fontSize = 20.px
            padding = box(10.px)
        }
        visites {
            /* tableview */
            label {
                //backgroundColor += Color.LIGHTGRAY
                font = Font.font("Times New Roman")
                fontSize = 14.px
            }

            tableView {
                tableCell {
                    borderColor += box(textColor)
                }
                tableRowCell {
                    and(odd) {
                        backgroundColor += oddCellColor
                        and(hover) {
                            backgroundColor += highlightColor
                        }
                    }
                    and(even) {
                        and(hover) {
                            backgroundColor += highlightColor
                        }
                        backgroundColor += evenCellColor
                    }
                }
                tableColumn {

                    label {
                        backgroundColor += oddCellColor
                    }
                }
                fixedCellSize = 36.px
                fontSize = 18.px
                font = Font.font("Times New Roman")
            }
            backgroundColor += Color.LIGHTGRAY

            form {
                padding = box(25.px)
                prefWidth = 450.px
                backgroundColor += Color.CADETBLUE

//                s(zip) {
//                    maxWidth = 60.px
//                    minWidth = maxWidth
//                }
            }
        }


    }
}