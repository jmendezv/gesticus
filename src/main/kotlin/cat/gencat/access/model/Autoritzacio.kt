package cat.gencat.access.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.time.LocalDate

class Autoritzacio {
    val sollicitantsProperty = SimpleListProperty<DadesPersonals>(observableList())
    var sollicitants by sollicitantsProperty

    val origenProperty = SimpleStringProperty("Barcelona")
    var origen by origenProperty

    val destinacioProperty = SimpleStringProperty("")
    var destinacio by destinacioProperty

    val motiuDesplaçamentProperty = SimpleStringProperty("")
    var motiuDesplaçament by motiuDesplaçamentProperty

    val dataAnadaProperty = SimpleObjectProperty<LocalDate>(LocalDate.now())
    var dataAnada by dataAnadaProperty

    val horaAnadaProperty = SimpleStringProperty("")
    var horaAnada by horaAnadaProperty

    val dataTornadaProperty = SimpleObjectProperty<LocalDate>(LocalDate.now().plusDays(1))
    var dataTornada by dataTornadaProperty

    val horaTornadaProperty = SimpleStringProperty("")
    var horaTornada by horaTornadaProperty

    val mitjaTransportProperty = SimpleStringProperty("Opción3")
    var mitjaTransport by mitjaTransportProperty

    val mitjaTransportAltresComentarisProperty = SimpleStringProperty("Desplaçaments locals amb taxi, metro, bus, etc.")
    var mitjaTransportAltresComentaris by mitjaTransportAltresComentarisProperty

    val allotjamentProperty = SimpleStringProperty("")
    var allotjament by allotjamentProperty

    val reservaProperty = SimpleBooleanProperty(true)
    var reserva by reservaProperty

    val creditorProperty = SimpleStringProperty("")
    var creditor by creditorProperty

    val importProperty = SimpleStringProperty("0.0")
    var import by importProperty

    val finançamentExternProperty = SimpleBooleanProperty(true)
    var finançamentExtern by finançamentExternProperty

    val finançamentExternDescripcioProperty = SimpleStringProperty("Fons Social Europeu")
    var finançamentExternDescripcio by finançamentExternDescripcioProperty

    val bestretaNoProperty = SimpleBooleanProperty(true)
    var bestretaNo by bestretaNoProperty

    val bestretaSiPerAllotjamentProperty = SimpleBooleanProperty(false)
    var bestretaSiPerAllotjament by bestretaSiPerAllotjamentProperty

    val bestretaSiPerManutencioProperty = SimpleBooleanProperty(false)
    var bestretaSiPerManutencio by bestretaSiPerManutencioProperty

    val bestretaSiAltresProperty = SimpleBooleanProperty(false)
    var bestretaSiAltres by bestretaSiAltresProperty

    val bestretaAltresDescripcioProperty = SimpleStringProperty("")
    var bestretaAltresDescripcio by bestretaAltresDescripcioProperty

    val nomResponsableProperty = SimpleStringProperty("Joan-Lluís Espinós Espinós")
    var nomResponsable by nomResponsableProperty

    val carrecResponsableProperty = SimpleStringProperty("Director General")
    var carrecResponsable by carrecResponsableProperty

    val nomDestinatariProperty = SimpleStringProperty("Perfe")
    var nomDestinatari by nomDestinatariProperty

    val emailDestinatariProperty = SimpleStringProperty("perfecta.gil@gencat.cat")
//    val emailDestinatariProperty = SimpleStringProperty("jmendez1@xtec.cat")
    var emailDestinatari by emailDestinatariProperty

}

class AutoritzacioViewModel : ItemViewModel<Autoritzacio>(Autoritzacio()) {
    val sollicitants = bind(Autoritzacio::sollicitantsProperty)
    val origen = bind(Autoritzacio::origenProperty)
    val destinacio = bind(Autoritzacio::destinacioProperty)
    val motiuDesplaçament = bind(Autoritzacio::motiuDesplaçamentProperty)
    val dataAnada = bind(Autoritzacio::dataAnadaProperty)
    val horaAnada = bind(Autoritzacio::horaAnadaProperty)
    val dataTornada = bind(Autoritzacio::dataTornadaProperty)
    val horaTornada = bind(Autoritzacio::horaTornadaProperty)
    val mitjaTransport = bind(Autoritzacio::mitjaTransportProperty)
    val mitjaTransportAltresComentaris = bind(Autoritzacio::mitjaTransportAltresComentarisProperty)
    val allotjament = bind(Autoritzacio::allotjamentProperty)
    val reserva = bind(Autoritzacio::reservaProperty)
    val creditor = bind(Autoritzacio::creditorProperty)
    val import = bind(Autoritzacio::importProperty)
    val finançamentExtern = bind(Autoritzacio::finançamentExternProperty)
    val finançamentExternDescripcio = bind(Autoritzacio::finançamentExternDescripcioProperty)
    val bestretaNo = bind(Autoritzacio::bestretaNoProperty)
    val bestretaSiPerAllotjament = bind(Autoritzacio::bestretaSiPerAllotjamentProperty)
    val bestretaSiPerManutencio = bind(Autoritzacio::bestretaSiPerManutencioProperty)
    val bestretaSiAltres = bind(Autoritzacio::bestretaSiAltresProperty)
    val bestretaSiAltresDescripcio = bind(Autoritzacio::bestretaAltresDescripcioProperty)
    val nomResponsable = bind(Autoritzacio::nomResponsableProperty)
    val carrecResponsable = bind(Autoritzacio::carrecResponsableProperty)
    val nomDestinatari = bind(Autoritzacio::nomDestinatariProperty)
    val emailDestinatari = bind(Autoritzacio::emailDestinatariProperty)
}
