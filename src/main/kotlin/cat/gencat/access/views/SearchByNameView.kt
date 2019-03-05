package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.model.EstadaSearch
import javafx.geometry.Pos
import tornadofx.*
import java.text.Normalizer

class SearchByNameView : View(APP_TITLE) {

    val controller: GesticusController by inject()
    val estades = controller.getAllEstades().observable()
    val estadesFiltered = SortedFilteredList(estades)
    val REGEX_UNACCENT = "\\p{InCOMBINING_DIACRITICAL_MARKS}+".toRegex()


    override val root = vbox(5.0) {

        hbox(10.0, Pos.CENTER_LEFT) {
            label("Filtre") {  }
            textfield {
                promptText = "Filter text"
                estadesFiltered.filterWhen(textProperty()) { query, estada ->
                    normalize(estada.nomDocent).contains(normalize(query), true)
                }
                prefWidth = 255.0
            }
        }

        tableview(estadesFiltered) {
            column("Codi", EstadaSearch::codi)
            column("Empresa", EstadaSearch::nomEmpresa).prefWidth(200.0)
            column("Inici", EstadaSearch::dataInici)
            column("Final", EstadaSearch::dataFinal)
            column("Docent", EstadaSearch::nomDocent)
            column("Email", EstadaSearch::emailDocent)
            columnResizePolicy = SmartResize.POLICY
        }

    }

    inline fun normalize(str: String): String =
            REGEX_UNACCENT.replace(Normalizer.normalize(str, Normalizer.Form.NFD), "")
}
