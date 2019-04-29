package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.BODY_CONTACTE_EMPRESA
import cat.gencat.access.functions.SUBJECT_GENERAL
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.APP_TITLE
import cat.gencat.access.model.EmpresaBean
import cat.gencat.access.model.EmpresaSeguimentBean
import javafx.geometry.Pos
import tornadofx.*
import java.text.Normalizer

class EmpresesView : View(Utils.APP_TITLE + ": Històric empreses") {

    val controller: GesticusController by inject()
    val empreses = controller.getAllLongEmpreses().observable()
    val empresesFiltered = SortedFilteredList(empreses)
    val REGEX_UNACCENT = "\\p{InCOMBINING_DIACRITICAL_MARKS}+".toRegex()

    private fun normalize(str: String): String =
        REGEX_UNACCENT.replace(Normalizer.normalize(str, Normalizer.Form.NFD), "")

    private fun sendEmail(nomAmbTractament: String, vararg emails: String) {
        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
            SUBJECT_GENERAL,
            BODY_CONTACTE_EMPRESA
                .replace("?1", nomAmbTractament)
            ,
            listOf(),
            listOf(*emails)
        )
    }

    private fun register(id: Int, nom: String) {
        controller.insertSeguimentEmpresa(
            id,
            "S'ha notificat a $nom una petició de reunió correctament"
        )
    }

    override val root = borderpane {

        top = hbox(10.0, Pos.CENTER_LEFT) {
            label("Filtre") { }
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
                                val id =  empreses[index].id
                                val email = empreses[index].email
                                val nomAmbTractament = "${empreses[index].pcTracte} ${empreses[index].pcNom}"
                                sendEmail(nomAmbTractament, email)
                                register(id, empreses[index].pcNom)
                                runLater {
                                    information(
                                            APP_TITLE,
                                            "S'ha enviat un correu a ${empreses[index].pcNom} i s'ha enregistrat correctament."
                                    )
                                }

                            }
                        }
                }
            }
            rowExpander(expandOnDoubleClick = true) {
                paddingLeft = expanderColumn.width
                tableview(it.seguiments.observable()) {
                    readonlyColumn("Id", EmpresaSeguimentBean::id)
                    readonlyColumn("Data", EmpresaSeguimentBean::data)
                    readonlyColumn("Comentaria", EmpresaSeguimentBean::comentaris)
                }
            }
        }
    }
}
