package cat.gencat.access.views.statistics

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.preferencesCurrentCourse
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*
import java.text.NumberFormat
import java.util.*

class StatisticsByCentreNoGestionadaView : View(Utils.APP_TITLE + ": Estades no gestionades per centre") {
    val controller: GesticusController by inject()
    var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesNoGestionadesPerCentre()

    override val root = borderpane {
        center = barchart("ESTADES NO GESTIONADES PER CENTRE DEL CURS ${preferencesCurrentCourse()}", CategoryAxis(), NumberAxis()) {
            series("Centre") {
                map.forEach {
                    data(it.key, it.value)
                }
            }
        }
    }
}
