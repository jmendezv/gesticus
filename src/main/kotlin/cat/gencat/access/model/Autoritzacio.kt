package cat.gencat.access.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import tornadofx.*
import java.time.LocalDate
import java.time.LocalDateTime

class Autoritzacio {
    val sollicitantsProperty = SimpleObjectProperty<ObservableList<DadesPersonals>>(observableList<DadesPersonals>())
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

    val mitjaTransportAvioProperty = SimpleBooleanProperty(true)
    var mitjaTransportAvio by mitjaTransportAvioProperty

    val mitjaTransportTrenProperty = SimpleBooleanProperty(false)
    var mitjaTransportTren by mitjaTransportTrenProperty

    val mitjaTransportAltresProperty = SimpleBooleanProperty(false)
    var mitjaTransportAltres by mitjaTransportAltresProperty

    val altresProperty = SimpleStringProperty("Desplaçaments locals amb taxi, metro, bus, etc.")
    var altres by altresProperty

    val allotjamentProperty = SimpleStringProperty("")
    var allotjament by allotjamentProperty

    val reservaProperty = SimpleBooleanProperty(true)
    var reserva by reservaProperty

    val creditorProperty = SimpleStringProperty("")
    var creditor by creditorProperty

    val importProperty = SimpleDoubleProperty(0.0)
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

    val carrecResponsableProperty = SimpleStringProperty("Director general")
    var carrecResponsable by carrecResponsableProperty

    val nomDestinatariProperty = SimpleStringProperty("Perfecta Gil")
    var nomDestinatari by nomDestinatariProperty

    val emailDestinatariProperty = SimpleStringProperty("perfecta.gil@gencat.cat")
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
    val mitjaTransportAvio = bind(Autoritzacio::mitjaTransportAvioProperty)
    val mitjaTransportTren = bind(Autoritzacio::mitjaTransportTrenProperty)
    val mitjaTransportAltres = bind(Autoritzacio::mitjaTransportAltresProperty)
    val mitjaTransportAltresComentaris = bind(Autoritzacio::altresProperty)
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
