package cat.gencat.access.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class DadesPersonals() {
    val unitatOrganicaProperty = SimpleStringProperty("")
    var unitatOrganica by unitatOrganicaProperty
    val carrecProperty = SimpleStringProperty("")
    var carrec by carrecProperty
    val emailProperty = SimpleStringProperty("")
    var email by emailProperty
    val nomProperty = SimpleStringProperty("")
    var nom by nomProperty
    val nifProperty = SimpleStringProperty("")
    var nif by nifProperty
}

class DadesPersonalsViewModel : ItemViewModel<DadesPersonals>() {
    val unitatOrganica = bind(DadesPersonals::unitatOrganicaProperty)
    val carrec = bind(DadesPersonals::carrecProperty)
    val email = bind(DadesPersonals::emailProperty)
    val nom = bind(DadesPersonals::nomProperty)
    val nif = bind(DadesPersonals::nifProperty)
}

