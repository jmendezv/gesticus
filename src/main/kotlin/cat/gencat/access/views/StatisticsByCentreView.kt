package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.currentCourseYear
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*
import java.text.NumberFormat
import java.util.*

class StatisticsByCentreView : View(Utils.APP_TITLE + ": Estades gestionades per centre") {
    val controller: GesticusController by inject()
    var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesPerCentre()

    override val root = borderpane {
        center = barchart("ESTADES GESTIONADES PER CENTRE DEL CURS ${currentCourseYear()}", CategoryAxis(), NumberAxis()) {
            series("Centre") {
                map.forEach {
                    data(it.key, it.value)
                }
            }
        }
    }
}
