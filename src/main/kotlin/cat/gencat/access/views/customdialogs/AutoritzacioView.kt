package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.AutoritzacioViewModel
import tornadofx.*

/* NOTE: USE A WIZARD */
class AutoritzacioView : Wizard(
    "Autorització d'una ordre de serveis",
    "Per al personal al servei de l'Administració i de la despesa corresponent."
) {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()

    init {
        graphic = resources.imageview("/graphics/autoritzacio.gif")
        add(DadesPersonalsView::class)
        add(DadesDesplaçamentView::class)
        add(DadesFinançamentView::class)
        add(SollicitudBestretaView::class)
        add(SignaturaResponsableView::class)
    }


}
