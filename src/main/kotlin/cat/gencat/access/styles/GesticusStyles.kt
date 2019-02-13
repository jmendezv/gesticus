package cat.gencat.access.styles

import javafx.scene.text.FontWeight
import org.intellij.lang.annotations.JdkConstants
import tornadofx.*


class GesticusStyles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val zip by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        s(form) {
            padding = box(25.px)
            prefWidth = 450.px

            s(zip) {
                maxWidth = 60.px
                minWidth = maxWidth
            }
        }
    }
}