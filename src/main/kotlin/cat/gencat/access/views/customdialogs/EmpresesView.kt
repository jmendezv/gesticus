package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.BODY_CONTACTE_EMPRESA
import cat.gencat.access.functions.SUBJECT_GENERAL
import cat.gencat.access.functions.Utils
import cat.gencat.access.model.EmpresaBean
import javafx.geometry.Pos
import tornadofx.*
import java.text.Normalizer

class EmpresesView : View(Utils.APP_TITLE + ": Històric empreses") {

    val controller: GesticusController by inject()
    val empreses = controller.getAllLongEmpreses().observable()
    val empresesFiltered = SortedFilteredList(empreses)
    val REGEX_UNACCENT = "\\p{InCOMBINING_DIACRITICAL_MARKS}+".toRegex()

    fun normalize(str: String): String =
            REGEX_UNACCENT.replace(Normalizer.normalize(str, Normalizer.Form.NFD), "")

    override val root = borderpane {

        top = hbox(10.0, Pos.CENTER_LEFT) {
            label("Filtre") {  }
            textfield {
                promptText = "Filter text"
                empresesFiltered.filterWhen(textProperty()) { query, empresa ->
                    normalize(empresa.nom).contains(normalize(query), true)
                            ||
                            normalize(empresa.direccio).contains(normalize(query), true)
                            ||
                            normalize(empresa.municipi).contains(normalize(query), true)
                            ||
                            normalize(empresa.email).contains(normalize(query), true)
                            ||
                            normalize(empresa.pcNom).contains(normalize(query), true)
                            ||
                            normalize(empresa.pcCarrec).contains(normalize(query), true)
                            ||
                            normalize(empresa.pcTelefon).contains(normalize(query), true)
                }
                prefWidth = 255.0
            }
            label("Aquest filtre s'aplicarà a tots els camps")
        }

        center = tableview(empresesFiltered) {
            readonlyColumn("Id", EmpresaBean::id)
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
