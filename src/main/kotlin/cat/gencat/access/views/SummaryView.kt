package cat.gencat.access.views

import cat.gencat.access.functions.APP_TITLE
import javafx.geometry.Pos
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SummaryView : View(APP_TITLE) {

    val summary: String by params

    override val root = vbox(15.0, Pos.CENTER) {
        label("")
        label("RESUMEN")
        label("")
        label(summary)
        label("")
    }
}
