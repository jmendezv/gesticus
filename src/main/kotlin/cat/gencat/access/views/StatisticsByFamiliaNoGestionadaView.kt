package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.functions.Utils.Companion.currentCourseYear
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*
import java.text.NumberFormat
import java.util.*

class StatisticsByFamiliaNoGestionadaView: View(APP_TITLE) {
    val controller: GesticusController by inject()
    var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesNoGestionadesPerFamillia()

    override val root = borderpane {
        center = barchart("ESTADES NO GESTIONADES PER FAMILIA DEL CURS ${currentCourseYear()}", CategoryAxis(), NumberAxis()) {
            series("Familia") {
                map.forEach {
                    data(it.key, it.value)
                }
            }
        }
    }
}
