package cat.gencat.access.views.customdialogs

import tornadofx.*

/* NOTE: USE A WIZARD */
class AutoritzacioView : Wizard("Autorització d'una ordre de serveis",
    "Per al personal al servei de l'Administració i de la despesa corresponent.") {

    init {
        graphic = resources.imageview("/graphics/autoritzacio.gif")
        add(DadesPersonalsView::class)
        add(DadesDesplaçamentView::class)
        add(DadesFinançamentView::class)
        add(SollicitudBestretaView::class)
        add(SignaturaResponsableView::class)
    }
}
