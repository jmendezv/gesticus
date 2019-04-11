package cat.gencat.access.views.statistics

import cat.gencat.access.functions.Utils
import javafx.geometry.Pos
import tornadofx.*

/*
 * This view should be a tableview with buttons in every row to send warning email
 * and also a button to send email to all of them?
 *
 * numero estada - profe amb tractament - professor email - nom empresa - data inici - data final - button send email
 *
 *  */
class SummaryView : View(Utils.APP_TITLE) {

    val summary: String by params

    override val root = vbox(15.0, Pos.CENTER) {
        label("")
        label("RESUMEN")
        label("")
        label(summary)
        label("")
    }
}
