package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.functions.currentCourseYear
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*
import java.text.NumberFormat
import java.util.*

class StatisticsBySSTTView : View(APP_TITLE) {
    val controller: GesticusController by inject()
    //var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesPerSSTT()

    override val root = borderpane {
        center = barchart("ESTADES GESTIONADES PER SERVEI TERRITORIAL DEL CURS ${currentCourseYear()}", CategoryAxis(), NumberAxis()) {
            series("Servei/Consorci") {
                map.forEach {
                    data(it.key, it.value)
                }
            }
        }
    }
}

