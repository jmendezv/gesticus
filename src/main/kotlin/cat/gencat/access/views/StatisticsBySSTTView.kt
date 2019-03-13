package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.currentCourseYear
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*

class StatisticsBySSTTView : View(Utils.APP_TITLE + ": Estades gestionades per Servei Territorial") {
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

