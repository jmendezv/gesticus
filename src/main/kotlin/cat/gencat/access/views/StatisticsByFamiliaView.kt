package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.currentCourseYear
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*
import java.text.NumberFormat
import java.util.*

class StatisticsByFamiliaView : View(Utils.APP_TITLE + ": Estades gestionades per familia") {
    val controller: GesticusController by inject()
    var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesPerFamillia()

    override val root = borderpane {
        center = barchart("ESTADES GESTIONADES PER FAMILIA DEL CURS ${currentCourseYear()}", CategoryAxis(), NumberAxis()) {
            series("Familia") {
                map.forEach {
                    data(it.key, it.value)
                }
            }
        }
    }
}

