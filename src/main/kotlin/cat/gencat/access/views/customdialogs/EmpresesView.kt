package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.model.EmpresaBean
import tornadofx.*

class EmpresesView : View(Utils.APP_TITLE + ": Històric empreses") {

    val controller: GesticusController by inject()
    val empreses = controller.getAllLongEmpreses()

    override val root = borderpane {

        center = tableview(empreses.observable()) {
            readonlyColumn("Nom", EmpresaBean::nom)
            readonlyColumn("Direcció", EmpresaBean::direccio)
            readonlyColumn("Municipi", EmpresaBean::municipi)
            readonlyColumn("Responsable", EmpresaBean::pcNom)
            readonlyColumn("E-mail", EmpresaBean::email)
//            readonlyColumn("Telèfon", EmpresaBean::pcTelefon)
            readonlyColumn("Acció", EmpresaBean::email).cellFormat {

                graphic = hbox(spacing = 5) {

                    button("Sol·licita visita")
                            .action {
                                //                                val estat = summaries.get(tableRow.index).estat
//                                if (estat == EstatsSeguimentEstadaEnum.ACABADA.name) {
//                                    val numeroEstada = summaries.get(tableRow.index).codiEstada
//                                    val email = summaries.get(tableRow.index).emailDocent
//                                    val nom = summaries.get(tableRow.index).nomDocentAmbTractament
//                                    val dies = summaries.get(tableRow.index).interval
//                                    val empresa = summaries.get(tableRow.index).nomEmpresa
//                                    GesticusMailUserAgent.sendBulkEmailWithAttatchment(
//                                            SUBJECT_GENERAL,
//                                            BODY_SUMMARY
//                                                    .replace("?1", nom)
//                                                    .replace("?2", faDies(dies.toInt()))
//                                                    .replace("?3", numeroEstada)
//                                                    .replace("?4", empresa)
//                                            ,
//                                            listOf(),
//                                            listOf(email))
//                                    val al = if (nom.startsWith("Sr.")) "al"
//                                    else if (nom.startsWith("Sra.")) "a la"
//                                    else "al/a la"
//                                    Utils.infoNotification(Utils.APP_TITLE, "S'ha enviat un correu $al $nom correctament")
//                                } else {
//                                    warning(Utils.APP_TITLE, "Només cal recordar les estades acabades")
//                                }
                            }
                }
            }
        }

//        bottom = hbox(50, alignment = Pos.BOTTOM_CENTER) {
//            button("Notifica acabades") {
//                setOnAction {
//                    runAsyncWithProgress {
//                    }
//                }
//            }
//        }
    }
}
