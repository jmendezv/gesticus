package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.functions.currentCourseYear
import javafx.scene.chart.PieChart
import tornadofx.*
import java.util.Locale
import java.text.NumberFormat


class StatisticsProgressView : View(APP_TITLE) {
    val controller: GesticusController by inject()
    var format = NumberFormat.getPercentInstance(Locale.US)

    val admesos = controller.countTotalAdmesos()

    val gestionades = controller.countTotalEstades()
    val baixes = controller.countTotalBaixesAdmesos()
    val pendents = (admesos - baixes - gestionades)

    val gestionadesPercentage = gestionades / admesos
    val gestionadesPercentageStr = format.format(gestionadesPercentage)

    val pendentsPercentage = pendents / admesos
    val pendentsPercentageStr = format.format(pendentsPercentage)

    val baixesPercentage = baixes / admesos
    val baixesPercentageStr = format.format(baixesPercentage)


    val items = listOf(
            PieChart.Data("Gestionades $gestionadesPercentageStr", gestionades.toDouble()),
            PieChart.Data("Pendents $pendentsPercentageStr", pendents.toDouble()),
            PieChart.Data("Baixes $baixesPercentageStr", baixes.toDouble())
    ).observable()

    override val root = borderpane {
        center = piechart("PROGRÉS ESTADES GESTIONADES DEL CURS ${currentCourseYear()}") {
            for (item in items) {
                data.add(item)
            }
        }
    }
}