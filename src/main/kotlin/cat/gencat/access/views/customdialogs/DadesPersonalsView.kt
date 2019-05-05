package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.Autoritzacio
import cat.gencat.access.model.AutoritzacioViewModel
import cat.gencat.access.model.DadesPersonals
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

class DadesPersonalsView : View("My View") {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()
    val element = SimpleObjectProperty<DadesPersonals>()

    override val root = vbox(3.0) {
        hbox(3.0) {
            tableview(items = model.sollicitants.value.observable()) {
                column("NIF", DadesPersonals::nif)
                column("Nom", DadesPersonals::nom)
                column("Email", DadesPersonals::email)
                column("Carrec", DadesPersonals::carrec)
                column("Unitat orgànica", DadesPersonals::unitatOrganica)
                bindSelected(element)
            }
            form {
                fieldset("Dades personals") {
                    field("DNI") {
                        textfield(element.value.nif)
                    }
                    field("DNI") {
                        textfield(element.value.nif)
                    }
                    field("DNI") {
                        textfield(element.value.nif)
                    }
                    field("DNI") {
                        textfield(element.value.nif)
                    }
                    field("DNI") {
                        textfield(element.value.nif)
                    }
                }
            }

        }
    }

    override fun onSave() {
        isComplete = model.item.sollicitants.size > 0
    }
}
