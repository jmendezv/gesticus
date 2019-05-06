package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.AutoritzacioViewModel
import tornadofx.*

class SollicitudBestretaView : View("Sol·licitud bestreta") {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()

    override val root = form {

    }

    override fun onSave() {
        super.onSave()
    }
}
