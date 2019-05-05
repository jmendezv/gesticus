package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.AutoritzacioViewModel
import tornadofx.*

class DadesFinançamentView : View("My View") {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()

    override val root = borderpane {

    }

    override fun onSave() {
        super.onSave()
    }
}
