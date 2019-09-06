package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.model.EstadaQuery
import cat.gencat.access.model.SeguimentQuery
import javafx.application.Platform
import tornadofx.*

/* 033886366Y */
class SeguimentEstadesView(nif: String) : View(Utils.APP_TITLE + ": Seguiment estades") {

    val controller: GesticusController by inject()
    //val nif: String? by param()
    val estadesAndSeguiment = controller.queryEstadesAndSeguiments(nif)

    init {
        if (estadesAndSeguiment.isEmpty()) {
            Platform.runLater {
                Utils.infoNotification(Utils.APP_TITLE, "La taula és buida.")
            }
        }
    }

    override val root = tableview(estadesAndSeguiment.observable()) {
        readonlyColumn("Docent", EstadaQuery::nomDocent)
        readonlyColumn("Codi", EstadaQuery::codi)
        readonlyColumn("NIF", EstadaQuery::nif)
        readonlyColumn("Any", EstadaQuery::any)
        readonlyColumn("Empresa", EstadaQuery::nomEmpresa)
        readonlyColumn("Data inici", EstadaQuery::dataInici)
        readonlyColumn("Data final", EstadaQuery::dataFinal)
        rowExpander(expandOnDoubleClick = true) {
            paddingLeft = expanderColumn.width
            tableview(it.seguiments.observable()) {
                readonlyColumn("Id", SeguimentQuery::id)
                readonlyColumn("Estat", SeguimentQuery::comentarisEstat)
                readonlyColumn("Data", SeguimentQuery::data)
            }
        }
    }

}
