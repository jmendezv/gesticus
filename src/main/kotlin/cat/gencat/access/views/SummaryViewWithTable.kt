package cat.gencat.access.views

import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.model.Summary
import javafx.geometry.Pos
import tornadofx.*

class SummaryViewWithTable : View(APP_TITLE) {

    val summary: List<Summary> by params

    override val root = tableview(summary.observable()) {


    }

}
