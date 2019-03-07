package cat.gencat.access.views

import cat.gencat.access.db.EstatsSeguimentEstadaEnum
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.functions.BODY_SUMMARY
import cat.gencat.access.functions.SUBJECT_GENERAL
import cat.gencat.access.model.Summary
import tornadofx.*
import cat.gencat.access.functions.Utils.Companion.infoNotification
import javafx.geometry.Pos
import javafx.scene.paint.Color


class SummaryViewWithTable : View(APP_TITLE) {

    val summaries: List<Summary> by params

    override val root = borderpane {

        center = tableview(summaries.observable()) {
            readonlyColumn("Nom", Summary::nomDocentAmbTractament)
            readonlyColumn("Codi", Summary::codiEstada)
            readonlyColumn("Empresa", Summary::nomEmpresa)
            readonlyColumn("Inici", Summary::inici)
            readonlyColumn("Final", Summary::fi)
            readonlyColumn("Interval", Summary::interval)
            readonlyColumn("Estat", Summary::estat).cellFormat {
                if (it == "ACABADA") {
                    style = "-fx-background-color:#a91234; -fx-text-fill:white"
                    // backgroundColor = c("#8b0000")
                    // textFill = Color.WHITE
                } else if (it == "DOCUMENTADA") {
                    style = "-fx-background-color:#12a934; -fx-text-fill:white"
                    // backgroundColor = c("#8b0000")
                    // textFill = Color.WHITE
                }
                text = it.toString()
            }
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
                                    val al = if (nom.startsWith("Sr.")) "al"
                                    else if (nom.startsWith("Sra.")) "a la"
                                    else "al/a la"
                                    infoNotification(APP_TITLE, "S'ha enviat un correu $al $nom correctament")
                                } else {
                                    warning(APP_TITLE, "Només cal recordar les estades acabades")
                                }
                            }
                }
            }
        }

        bottom = hbox(50, alignment = Pos.BOTTOM_CENTER) {
            button("Notifica acabades") {
                setOnAction {
                    runAsyncWithProgress {
                        var registres = 0
                        summaries.forEach {
                            val email = it.emailDocent
                            val nom = it.nomDocentAmbTractament
                            val dies = it.interval
                            val numeroEstada = it.codiEstada
                            val empresa = it.nomEmpresa
                            val estat = it.estat
                            if (estat == EstatsSeguimentEstadaEnum.ACABADA.name) {
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
                                registres++
                            }
                        }
                        runLater {
                            infoNotification(APP_TITLE, "Gèsticus ha enviat una notificació a ${registres} docents")
                        }
                    }
//                    var registres = 0
//                    summaries.forEach {
//                        val email = it.emailDocent
//                        val nom = it.nomDocentAmbTractament
//                        val dies = it.interval
//                        val numeroEstada = it.codiEstada
//                        val empresa = it.nomEmpresa
//                        val estat = it.estat
//                        if (estat == EstatsSeguimentEstadaEnum.ACABADA.name) {
//                            runAsync {
//                                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
//                                        SUBJECT_GENERAL,
//                                        BODY_SUMMARY
//                                                .replace("?1", nom)
//                                                .replace("?2", "$dies")
//                                                .replace("?3", numeroEstada)
//                                                .replace("?4", empresa)
//                                        ,
//                                        listOf(),
//                                        listOf(email))
//                            } ui {
////                                val al = if (nom.startsWith("Sr.")) "al"
////                                else if (nom.startsWith("Sra.")) "a la"
////                                else "al/a la"
////                                infoNotification(APP_TITLE, "S'ha enviat una notificació $al $nom docents")
//                            }
//                            registres++
//                        }
//                    }
//                    infoNotification(APP_TITLE, "Gèsticus esta enviat una notificació a ${registres} docents")
                }
            }
        }


    }
}
