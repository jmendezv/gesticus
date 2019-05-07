package cat.gencat.access.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class DadesPersonals(nif: String = "", nom: String = "", email: String = "", carrer: String = "", unitatOrganica: String = "") {
    val unitatOrganicaProperty = SimpleStringProperty(unitatOrganica)
    var unitatOrganica by unitatOrganicaProperty
    val carrecProperty = SimpleStringProperty(carrer)
    var carrec by carrecProperty
    val emailProperty = SimpleStringProperty(email)
    var email by emailProperty
    val nomProperty = SimpleStringProperty(nom)
    var nom by nomProperty
    val nifProperty = SimpleStringProperty(nif)
    var nif by nifProperty
}

class DadesPersonalsViewModel : ItemViewModel<DadesPersonals>(DadesPersonals()) {
    var unitatOrganica = bind(DadesPersonals::unitatOrganicaProperty)
    var carrec = bind(DadesPersonals::carrecProperty)
    var email = bind(DadesPersonals::emailProperty)
    var nom = bind(DadesPersonals::nomProperty)
    var nif = bind(DadesPersonals::nifProperty)
}

