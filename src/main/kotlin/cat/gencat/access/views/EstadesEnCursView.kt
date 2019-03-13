package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.db.EstatsSeguimentEstadaEnum
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.events.EstadaEnCursSearchEvent
import cat.gencat.access.events.EstadaSearchEvent
import cat.gencat.access.functions.BODY_ESTADA_EN_CURS
import cat.gencat.access.functions.BODY_SUMMARY
import cat.gencat.access.functions.SUBJECT_GENERAL
import cat.gencat.access.functions.Utils
import cat.gencat.access.model.EstadaEnCurs
import cat.gencat.access.model.EstadaEnCursModel
import cat.gencat.access.model.EstadaSearch
import cat.gencat.access.model.EstadaSearchModel
import javafx.geometry.Pos
import tornadofx.*
import java.text.Normalizer

/*
*
* This view implements and overall search within a estades en curs context
*
* */
class EstadesEnCursView : View(Utils.APP_TITLE + ": Estades en curs") {

    val controller: GesticusController by inject()
    val estades = controller.getEstadesEnCurs().observable()
    val estadesFiltered = SortedFilteredList(estades)
    val model: EstadaEnCursModel by inject()
    val REGEX_UNACCENT = "\\p{InCOMBINING_DIACRITICAL_MARKS}+".toRegex()

    override val root = vbox(5.0) {
        tornadofx.insets(all = 10.0)

        hbox(10.0, Pos.CENTER_LEFT) {
            label("Filtre") { }
            textfield {
                promptText = "Filter text"
                estadesFiltered.filterWhen(textProperty()) { query, estada ->
                    normalize(estada.codi).contains(normalize(query), true)
                            ||
                            normalize(estada.nifProfessor).contains(normalize(query), true)
                            ||
                            normalize(estada.nomProfessor).contains(normalize(query), true)
                            ||
                            normalize(estada.cognom1Professor).contains(normalize(query), true)
                            ||
                            normalize(estada.cognom2Professor).contains(normalize(query), true)
                            ||
                            normalize(estada.emailProfessor).contains(normalize(query), true)
                            ||
                            normalize(estada.telefonProfessor).contains(normalize(query), true)
                            ||
                            normalize(estada.nomEmpresa).contains(normalize(query), true)
                            ||
                            normalize(estada.municipiEmpresa).contains(normalize(query), true)
                            ||
                            normalize(estada.contacteNom).contains(normalize(query), true)
                            ||
                            normalize(estada.contacteCarrec).contains(normalize(query), true)
                            ||
                            normalize(estada.contacteTelefon).contains(normalize(query), true)
                            ||
                            normalize(estada.contacteEmail).contains(normalize(query), true)
                }
                prefWidth = 255.0
            }
            label("Aquest filtre s'aplicarà a la majoria de camps ")
        }

        tableview(estadesFiltered) {
            column("Codi", EstadaEnCurs::codi)
            column("Empresa", EstadaEnCurs::nomEmpresa)
            column("Municipi", EstadaEnCurs::municipiEmpresa)
            column("Contacte", EstadaEnCurs::contacteNom)
            column("Càrrec", EstadaEnCurs::contacteCarrec)
            column("Telèfon", EstadaEnCurs::contacteTelefon)
            column("Email", EstadaEnCurs::contacteEmail)
            column("Data d'inici", EstadaEnCurs::dataInici)
            column("Data final", EstadaEnCurs::dataFinal)
            column("Tractament", EstadaEnCurs::tractamentProfessor)
            column("Nom docent", EstadaEnCurs::nomProfessor)
            column("Primer cognom", EstadaEnCurs::cognom1Professor)
            column("Segon cognom", EstadaEnCurs::cognom2Professor)
            column("NIF", EstadaEnCurs::nifProfessor)
            column("Telèfon", EstadaEnCurs::telefonProfessor)
            column("Email", EstadaEnCurs::emailProfessor)

            bindSelected(model)
            onDoubleClick {
                runLater {
                    fire(EstadaEnCursSearchEvent(model.item))
                }
                close()
            }
            columnResizePolicy = SmartResize.POLICY
        }

        hbox(10.0, alignment = Pos.BOTTOM_CENTER) {
            button("Sol·licita visita") {
                enableWhen(model.codi.isNotBlank())
                setOnAction {
                    runAsyncWithProgress {
                        val email1 = model.contacteEmail.value
                        val email2 = model.emailProfessor.value
                        val numeroEstada = model.codi.value
                        val empresa = model.nomEmpresa.value
                        val municipi = model.municipiEmpresa.value
                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                SUBJECT_GENERAL,
                                BODY_ESTADA_EN_CURS
                                        .replace("?1", numeroEstada)
                                        .replace("?2", empresa)
                                        .replace("?3", municipi)
                                ,
                                listOf(),
                                listOf(email1, email2))
                    }
                    information(Utils.APP_TITLE, "S'ha lliurat el correu correctament.")
                }
            }
        }

    }

    inline fun normalize(str: String): String =
            REGEX_UNACCENT.replace(Normalizer.normalize(str, Normalizer.Form.NFD), "")
}
