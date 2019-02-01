package cat.gencat.access.views

import cat.gencat.access.functions.APP_TITLE
import javafx.geometry.Pos
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HelpView : View(APP_TITLE) {
    override val root = vbox(15.0, Pos.CENTER) {
        label("")
        label("  In progress  ")
        label("")
        label("  Darrera actualització ${LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}  ")
        label("")
    }
}