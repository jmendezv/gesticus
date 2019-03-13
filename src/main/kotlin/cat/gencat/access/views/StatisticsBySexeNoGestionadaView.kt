package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.currentCourseYear
import javafx.scene.chart.PieChart
import tornadofx.*
import java.text.NumberFormat
import java.util.*


class StatisticsBySexeNoGestionadaView : View(Utils.APP_TITLE + ": Estades no gestionades per sexe") {
    val controller: GesticusController by inject()
    var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesNoGestionadesPerSexe()

    val homes = map["H"] ?: 0.0
    val dones = map["D"] ?: 0.0
    val total = (homes + dones)

    val homesPercentage = homes / total
    val homesPercentageStr = format.format(homesPercentage)

    val donesPercentage = dones / total
    val donesPercentageStr = format.format(donesPercentage)

    val items = listOf(
            PieChart.Data("Homes $homesPercentageStr", homes),
            PieChart.Data("Dones $donesPercentageStr", dones)
    ).observable()

    override val root = borderpane {
        center = piechart("ESTADES NO GESTIONADES PER SEXE DEL CURS ${currentCourseYear()}") {
            for (item in items) {
                data.add(item)
            }
        }
    }
}