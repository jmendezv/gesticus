package cat.gencat.access.views.customdialogs

import cat.gencat.access.model.AutoritzacioViewModel
import cat.gencat.access.model.DadesPersonals
import tornadofx.*

class DadesPersonalsView : View("My View") {

    val model: AutoritzacioViewModel by inject()

    override val root = vbox(3.0) {
        hbox(3.0) {
            tableview(items = model.sollicitants.value.observable()) {
                column("NIF", DadesPersonals::nif)
                column("Nom", DadesPersonals::nom)
                column("Email", DadesPersonals::email)
                column("Carrec", DadesPersonals::carrec)
                column("Unitat orgànica", DadesPersonals::unitatOrganica)
//                bindSelected()
            }
            form {
                fieldset("Dades personals") {
                    field("DNI") {
                        textfield()
                    }
                }
            }

        }
    }
}
