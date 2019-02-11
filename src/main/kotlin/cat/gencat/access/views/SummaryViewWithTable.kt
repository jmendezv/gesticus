package cat.gencat.access.views

import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.*
import cat.gencat.access.model.Summary
import javafx.geometry.Pos
import tornadofx.*
import tornadofx.WizardStyles.Companion.graphic

class SummaryViewWithTable : View(APP_TITLE) {

    val summary: List<Summary> by params

    override val root = borderpane {

        center = tableview(summary.observable()) {
                    readonlyColumn("Nom", Summary::nomDocentAmbTractament)
                    readonlyColumn("Codi", Summary::codiEstada)
                    readonlyColumn("Empresa", Summary::nomEmpresa)
                    readonlyColumn("Inici", Summary::inici)
                    readonlyColumn("Final", Summary::fi)
                    readonlyColumn("Interval", Summary::interval).cellFormat {
                        if (it > 30) {
                            style = "-fx-background-color:#a94442; -fx-text-fill:white"
                            text = it.toString() + " dies"
                        } else {
                            text = it.toString() + " dies"
                        }
                    }
                    readonlyColumn("Estat", Summary::estat)
                    readonlyColumn("Comentari", Summary::comentari)
                    readonlyColumn("Acció", Summary::emailDocent).cellFormat {
                        graphic = hbox(spacing = 5) {
                            button("Notifica estat").action {
                                val email = summary.get(tableRow.index).emailDocent
                                val nom = summary.get(tableRow.index).nomDocentAmbTractament
                                val dies = summary.get(tableRow.index).interval
                                val numeroEstada = summary.get(tableRow.index).codiEstada
                                val empresa = summary.get(tableRow.index).nomEmpresa
                                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                        SUBJECT_GENERAL,
                                        BODY_SUMMARY
                                                .replace("?1", nom)
                                                .replace("?2", "$dies")
                                                .replace("?3", numeroEstada)
                                                .replace("?4", empresa)
                                        ,
                                        listOf(),
                                        listOf(email))
                            }
                        }
                    }
                }

        bottom = hbox(50) {
            button("Notifica tothom") {
                setOnAction {
                    summary.forEach {
                        val email = it.emailDocent
                        val nom = it.nomDocentAmbTractament
                        val dies = it.interval
                        val numeroEstada = it.codiEstada
                        val empresa = it.nomEmpresa
                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                SUBJECT_GENERAL,
                                BODY_SUMMARY
                                        .replace("?1", nom)
                                        .replace("?2", "$dies")
                                        .replace("?3", numeroEstada)
                                        .replace("?4", empresa)
                                ,
                                listOf(),
                                listOf(email))
                    }
                    infoNotification(APP_TITLE, "S'ha enviat una notificació a ${summary.size} docents")
                }
            }
        }


    }
}
