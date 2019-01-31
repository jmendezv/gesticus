package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.currentCourseYear
import javafx.scene.chart.PieChart
import tornadofx.*
import java.text.NumberFormat
import java.util.*

class StatisticsByCosView  : View("Gèsticus") {
    val controller: GesticusController by inject()
    var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesPerCos()

    val homes = map["Cos de professors tècnics"] ?: 0.0
    val dones = map["Cos de professors de secundària"] ?: 0.0
    val total = (homes + dones)

    val homesPercentage = homes / total
    val homesPercentageStr = format.format(homesPercentage)

    val donesPercentage = dones / total
    val donesPercentageStr = format.format(donesPercentage)

    val items = listOf(
            PieChart.Data("Professors Tècnics $homesPercentageStr", homes),
            PieChart.Data("Professors de Secundària $donesPercentageStr", dones)
    ).observable()

    override val root = borderpane {
        center = piechart("Distribució per cos ${currentCourseYear()}") {
            for (item in items) {
                data.add(item)
            }
        }
    }
}