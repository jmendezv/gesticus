package cat.gencat.access.views

import cat.gencat.access.db.EstatsSeguimentEstadaEnum
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.*
import cat.gencat.access.model.Summary
import cat.gencat.access.model.SummaryModel
import tornadofx.*

class SummaryViewWithTable : View(APP_TITLE) {

    val summaries: List<Summary> by params

    override val root = borderpane {

        center = tableview(summaries.observable()) {
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

                    button("Notifica estat")
                            .action {
                                val estat = summaries.get(tableRow.index).estat
                                if (estat == EstatsSeguimentEstadaEnum.ACABADA.name) {
                                    val numeroEstada = summaries.get(tableRow.index).codiEstada
                                    val email = summaries.get(tableRow.index).emailDocent
                                    val nom = summaries.get(tableRow.index).nomDocentAmbTractament
                                    val dies = summaries.get(tableRow.index).interval
                                    val empresa = summaries.get(tableRow.index).nomEmpresa
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
                                else {
                                    warning(APP_TITLE, "Només cal recordar les estades acabades")
                                }
                            }
                }
            }
        }


        bottom = hbox(50) {
            button("Notifica tothom") {
                setOnAction {
                    var registres = 0
                    summaries.forEach {
                        val email = it.emailDocent
                        val nom = it.nomDocentAmbTractament
                        val dies = it.interval
                        val numeroEstada = it.codiEstada
                        val empresa = it.nomEmpresa
                        val estat = it.estat
                        if (estat == EstatsSeguimentEstadaEnum.ACABADA.name) {
                            registres++
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
                    infoNotification(APP_TITLE, "S'ha enviat una notificació a ${registres} docents")
                }
            }
        }


    }
}
