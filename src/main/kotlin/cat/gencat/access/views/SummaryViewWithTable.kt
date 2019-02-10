package cat.gencat.access.views

import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.functions.BODY_RECORDATORI_ESTADA_PENDENT
import cat.gencat.access.functions.BODY_SUMMARY
import cat.gencat.access.model.Summary
import javafx.geometry.Pos
import tornadofx.*
import tornadofx.WizardStyles.Companion.graphic

class SummaryViewWithTable : View(APP_TITLE) {

    val summary: List<Summary> by params

    override val root = tableview(summary.observable()) {
        readonlyColumn("Nom", Summary::nomDocentAmbTractament)
        readonlyColumn("Codi", Summary::codiEstada)
        readonlyColumn("Empresa", Summary::nomEmpresa)
        readonlyColumn("Inici", Summary::dataInici)
        readonlyColumn("Final", Summary::dataFinal)
        readonlyColumn("Estat", Summary::estat)
        readonlyColumn("Comentari", Summary::comentari)
        readonlyColumn("Email", Summary::emailDocent).cellFormat {
            graphic = hbox(spacing = 5) {
                button("Notifica estat").action {
                    println(summary.get(tableRow.index).emailDocent)
//                    GesticusMailUserAgent.sendBulkEmailWithAttatchment(
//                            APP_TITLE,
//                            BODY_SUMMARY,
//                            listOf(),
//                            listOf())
                }
            }
        }
    }
}
