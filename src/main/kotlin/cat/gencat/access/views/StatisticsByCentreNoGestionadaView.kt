package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.APP_TITLE
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*
import java.text.NumberFormat
import java.util.*

import cat.gencat.access.functions.Utils.Companion.currentCourseYear

class StatisticsByCentreNoGestionadaView : View(APP_TITLE) {
    val controller: GesticusController by inject()
    var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesNoGestionadesPerCentre()

    override val root = borderpane {
        center = barchart("ESTADES NO GESTIONADES PER CENTRE DEL CURS ${currentCourseYear()}", CategoryAxis(), NumberAxis()) {
            series("Centre") {
                map.forEach {
                    data(it.key, it.value)
                }
            }
        }
    }
}
