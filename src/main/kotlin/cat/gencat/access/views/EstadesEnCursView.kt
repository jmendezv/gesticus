package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.events.EstadaEnCursSearchEvent
import cat.gencat.access.events.EstadaSearchEvent
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
class EstadesEnCursView : View(Utils.APP_TITLE) {

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
                            normalize(estada.nomProfessor).contains(normalize(query), true)
                            ||
                            normalize(estada.cognom1Professor).contains(normalize(query), true)
                            ||
                            normalize(estada.cognom2Professor).contains(normalize(query), true)
                            ||
                            normalize(estada.emailProfessor).contains(normalize(query), true)
                            ||
                            normalize(estada.nifProfessor).contains(normalize(query), true)
                            ||
                            normalize(estada.nomEmpresa).contains(normalize(query), true)
                }
                prefWidth = 255.0
            }
            label("Aquest filtre s'aplicarà a codi, nom d'empresa, nom del docent, nif i email ")
        }

        tableview(estadesFiltered) {
            column("Codi", EstadaEnCurs::codi)
            column("Empresa", EstadaEnCurs::nomEmpresa).prefWidth(120.0)
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

    }

    inline fun normalize(str: String): String =
            REGEX_UNACCENT.replace(Normalizer.normalize(str, Normalizer.Form.NFD), "")
}
