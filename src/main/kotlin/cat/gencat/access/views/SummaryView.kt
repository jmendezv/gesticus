package cat.gencat.access.views

import cat.gencat.access.functions.APP_TITLE
import javafx.geometry.Pos
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/*
 * This view should be a tableview with buttons in every row to send warning email
 * and also a button to send email to all of them?
 *
 * numero estada - profe amb tractament - professor email - empresa? - data inici - data final - button send email
 *
 *  */
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
