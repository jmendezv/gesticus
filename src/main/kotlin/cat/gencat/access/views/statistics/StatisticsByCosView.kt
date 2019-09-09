package cat.gencat.access.views.statistics

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.preferencesCurrentCourse
import javafx.scene.chart.PieChart
import tornadofx.*
import java.text.NumberFormat
import java.util.*

class StatisticsByCosView  : View(Utils.APP_TITLE + ": Estades gestionades per cos") {
    val controller: GesticusController by inject()
    var format = NumberFormat.getPercentInstance(Locale.US)

    val map = controller.countTotalEstadesPerCos()

    val tecnics = map["Cos de professors tècnics"] ?: 0.0
    val secundaria = map["Cos de professors de secundària"] ?: 0.0
    val total = (tecnics + secundaria)

    val tecnicsPercentage = tecnics / total
    val tecnicsPercentageStr = format.format(tecnicsPercentage)

    val secundariaPercentage = secundaria / total
    val secundariaPercentageStr = format.format(secundariaPercentage)

    val items = listOf(
            PieChart.Data("Professors Tècnics $tecnicsPercentageStr", tecnics),
            PieChart.Data("Professors de Secundària $secundariaPercentageStr", secundaria)
    ).observable()

    override val root = borderpane {
        center = piechart("ESTADES GESTIONADES PER COS DEL CURS ${preferencesCurrentCourse()}") {
            for (item in items) {
                data.add(item)
            }
        }
    }
}