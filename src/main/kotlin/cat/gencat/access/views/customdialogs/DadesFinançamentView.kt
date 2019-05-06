package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.AutoritzacioViewModel
import tornadofx.*

class DadesFinançamentView : View("Dades del finançament") {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()

    override val root = form {
        fieldset ("Dades del finançament") {
            hbox(5.0) {
                field {
                    checkbox("Finançament extern", model.finançamentExtern)
                }
                field("Descripció"){
                    textfield(model.finançamentExternDescripcio)
                }
            }
        }

    }

    override fun onSave() {
        super.onSave()
    }
}
