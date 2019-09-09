package cat.gencat.access.views.statistics

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.preferencesCurrentCourse
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*

class StatisticsBySSTTNoGestionadaView  : View(Utils.APP_TITLE + ": Estades no gestionades per Servei Territorial") {
    val controller: GesticusController by inject()
    //var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesNoGestionadesPerSSTT()

    override val root = borderpane {

        center = barchart("ESTADES NO GESTIONADES PER SERVEI TERRITORIAL DEL CURS ${preferencesCurrentCourse()}", CategoryAxis(), NumberAxis()) {
            series("Servei/Consorci") {
                map.forEach {
                    data(it.key, it.value).also {
                    }
                }
            }
        }
    }
}

