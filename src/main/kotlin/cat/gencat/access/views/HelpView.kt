package cat.gencat.access.views

import cat.gencat.access.functions.Utils
import javafx.geometry.Pos
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HelpView : View(Utils.APP_TITLE + ": Ajuda") {
    override val root = vbox(15.0, Pos.CENTER) {
        label("")
        label("  In progress  ")
        label("")
        label("  Darrera actualització ${LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}  ")
        label("")
    }
}