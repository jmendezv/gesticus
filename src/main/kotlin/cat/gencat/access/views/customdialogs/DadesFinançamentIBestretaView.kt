package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.AutoritzacioViewModel
import tornadofx.*

class DadesFinançamentIBestretaView : View("Dades del finançament i Bestreta") {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()

    override val root = form {
        fieldset("Dades del finançament") {
            hbox(5.0) {
                field {
                    checkbox("Finançament extern", model.finançamentExtern)
                }
                field {
                    textfield(model.finançamentExternDescripcio)
                }
            }
        }
        /*
        * PDCheckBox Check Box1 -> Off
        * PDCheckBox Check Box2 -> Off
        * PDCheckBox Check Box3 -> Off
        * PDCheckBox Check Box4 -> Sí
        *
        * */
        fieldset("Bestreta (s'ha de justificar en un termini de 10 dies)") {
            hbox {
                checkbox("No", model.bestretaNo)
                checkbox("Sí, per allotjament", model.bestretaSiPerAllotjament)
                checkbox("Sí, per a manutenció", model.bestretaSiPerManutencio)
                checkbox("Sí, altres", model.bestretaSiAltres)
                textfield(model.bestretaSiAltresDescripcio) {
                    prefColumnCount = 40
                }
            }
        }
        fieldset("Responsable") {
            hbox(5.0) {
                field("Nom") {
                    textfield(model.nomResponsable) {
                        required()
                    }
                }
                field("Càrrec") {
                    textfield(model.carrecResponsable) {
                        prefColumnCount = 30
                        required()
                    }

                }
            }
        }
        fieldset("Destinatari") {
            hbox(5.0) {
                field("Nom") {
                    textfield(model.nomDestinatari).required()
                }
                field("Email") {
                    textfield(model.emailDestinatari) {
                        prefColumnCount = 30
                        required()
                    }
                }
            }
        }

    }

    override fun onSave() {
        super.onSave()
        model.commit()
    }
}
