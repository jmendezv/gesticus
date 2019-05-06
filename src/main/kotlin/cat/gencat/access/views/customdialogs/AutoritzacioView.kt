package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.model.AutoritzacioViewModel
import tornadofx.*

/* Wizard are ok for complex forms */
class AutoritzacioView : Wizard(
        Utils.APP_TITLE,
        "Autorització d'una ordre de serveis."
) {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()

    init {
        //graphic = resources.imageview("/graphics/autoritzacio.gif")
        add(DadesPersonalsView::class)
        add(DadesDesplaçamentView::class)
        add(DadesFinançamentIBestretaView::class)
        //add(SignaturaResponsableView::class)
    }


}
