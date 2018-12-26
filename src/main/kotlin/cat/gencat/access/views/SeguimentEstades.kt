package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.EstadaQuery
import cat.gencat.access.model.SeguimentQuery
import tornadofx.*

class SeguimentEstades : View("My View") {

    val controller: GesticusController by inject()

    val estadesAndSeguiment = controller.queryEstadesAndSeguiments()

    override val root = tableview(estadesAndSeguiment.observable()) {
        column("Codi", EstadaQuery::codi::class)
        column("Any", EstadaQuery::any::class)
        column("Nom", EstadaQuery::nomDocent::class)
        column("NIF", EstadaQuery::nif::class)
        rowExpander(expandOnDoubleClick = true) {
            paddingLeft = expanderColumn.width
            tableview(it.seguiments.observable()) {
                column("Codi", SeguimentQuery::codi::class)
                column("Estat", SeguimentQuery::estat::class)
                column("Data", SeguimentQuery::data::class)
            }
        }
    }

}
