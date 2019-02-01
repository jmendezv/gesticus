package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.model.EstadaQuery
import cat.gencat.access.model.SeguimentQuery
import tornadofx.*

/* 033886366Y */
class SeguimentEstadesView(nif: String) : View(APP_TITLE) {

    val controller: GesticusController by inject()
    //val nif: String? by param()
    val estadesAndSeguiment = controller.queryEstadesAndSeguiments(nif)

    init {
        println("en init")
    }

    override val root = tableview(estadesAndSeguiment.observable()) {
        readonlyColumn("Codi", EstadaQuery::codi)
        readonlyColumn("Any", EstadaQuery::any)
        readonlyColumn("Nom", EstadaQuery::nomDocent)
        readonlyColumn("NIF", EstadaQuery::nif)
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
