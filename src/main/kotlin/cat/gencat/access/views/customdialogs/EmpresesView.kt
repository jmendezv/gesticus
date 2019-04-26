package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.BODY_CONTACTE_EMPRESA
import cat.gencat.access.functions.SUBJECT_GENERAL
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

                    button("Sol·licita entrevista")
                        .action {
                            runAsyncWithProgress {
                                val index = tableRow.index
                                val email = empreses[index].email
                                val nomAmbTractament = "${empreses[index].pcTracte} ${empreses[index].pcNom}"
                                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                    SUBJECT_GENERAL,
                                    BODY_CONTACTE_EMPRESA
                                        .replace("?1", nomAmbTractament)
                                    ,
                                    listOf(),
                                    listOf(email)
                                )
                            }
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
