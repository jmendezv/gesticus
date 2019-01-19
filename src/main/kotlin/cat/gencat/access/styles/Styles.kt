package cat.gencat.access.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {



        val label by cssclass()
        val heading by cssclass()
        val button by cssclass()
        val readOnlytextField by cssclass()
        val editableTextField by cssclass()

        /* Colors definition */
        val readOnlyColor = c("#ACB1B1", 0.75)
        val editableColor = c("#EAF3F3", 0.75)

        /* A mixin defines common properties that can be applied to multiple selectors */
        val readOnlyMixing = mixin {
            backgroundColor += readOnlyColor
            textFill = Color.CHOCOLATE
            fontWeight = FontWeight.BOLD
            fontStyle = FontPosture.ITALIC
        }

        val editableMixing = mixin {
            backgroundColor += editableColor
            textFill = Color.BLACK
            fontWeight = FontWeight.BOLD
            fontStyle = FontPosture.ITALIC
        }
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 12.px
            fontWeight = FontWeight.BOLD
        }
        button {
        }
        readOnlytextField {
            +readOnlyMixing
        }
        editableTextField {
            +editableMixing
        }
    }

}
