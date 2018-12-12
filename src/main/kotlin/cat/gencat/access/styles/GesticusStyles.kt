package cat.gencat.access.styles

import javafx.scene.text.FontWeight
import tornadofx.*


class GesticusStyles : Stylesheet() {
    companion object {
        val heading by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
    }
}