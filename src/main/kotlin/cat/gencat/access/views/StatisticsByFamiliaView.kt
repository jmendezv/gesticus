package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.functions.currentCourseYear
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*
import java.text.NumberFormat
import java.util.*

class StatisticsByFamiliaView : View(APP_TITLE) {
    val controller: GesticusController by inject()
    var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesPerFamillia()

    override val root = borderpane {
        center = barchart("Estades per familia curs ${currentCourseYear()}", CategoryAxis(), NumberAxis()) {
            series("Familia") {
                map.forEach {
                    data(it.key, it.value)
                }
            }
        }
    }
}

