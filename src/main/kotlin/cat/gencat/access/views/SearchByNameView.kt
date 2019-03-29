package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.events.EstadaSearchEvent
import cat.gencat.access.functions.Utils
import cat.gencat.access.model.EstadaSearch
import cat.gencat.access.model.EstadaSearchModel
import javafx.geometry.Pos
import tornadofx.*
import java.text.Normalizer

/*
*
* This view implements and overall search
*
* */
class SearchByNameView : View(Utils.APP_TITLE + ": Totes les estades") {

    val controller: GesticusController by inject()
    val estades = controller.getAllEstades().observable()
    val estadesFiltered = SortedFilteredList(estades)
    val model: EstadaSearchModel by inject()
    val REGEX_UNACCENT = "\\p{InCOMBINING_DIACRITICAL_MARKS}+".toRegex()

    override val root = vbox(5.0) {
        tornadofx.insets(all = 10.0)

        hbox(10.0, Pos.CENTER_LEFT) {
            label("Filtre") {  }
            textfield {
                promptText = "Filter text"
                estadesFiltered.filterWhen(textProperty()) { query, estada ->
                    normalize(estada.codi).contains(normalize(query), true)
                    ||
                    normalize(estada.nomDocent).contains(normalize(query), true)
                    ||
                    normalize(estada.nomEmpresa).contains(normalize(query), true)
                    ||
                    normalize(estada.emailDocent).contains(normalize(query), true)
                    ||
                    normalize(estada.nifDocent).contains(normalize(query), true)
                }
                prefWidth = 255.0
            }
            label("Aquest filtre s'aplicarà a codi, nom d'empresa, nom del docent, nif i email ")
        }

        tableview(estadesFiltered) {
            column("Codi", EstadaSearch::codi)
            column("Empresa", EstadaSearch::nomEmpresa).prefWidth(120.0)
            column("Inici", EstadaSearch::dataInici)
            column("Final", EstadaSearch::dataFinal)
            column("Docent", EstadaSearch::nomDocent)
            column("NIF", EstadaSearch::nifDocent)
            column("Email", EstadaSearch::emailDocent)
            bindSelected(model)
            onDoubleClick {
                runLater {
                    fire(EstadaSearchEvent(model.item))
                }
                close()
            }
            columnResizePolicy = SmartResize.POLICY
        }

    }

     fun normalize(str: String): String =
            REGEX_UNACCENT.replace(Normalizer.normalize(str, Normalizer.Form.NFD), "")
}
