package cat.gencat.access.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.time.LocalDateTime

enum class TipusTransport {
    AVIO, TREN, ALTRES
}

class Autoritzacio {
    val sollicitantsProperty = SimpleObjectProperty<List<DadesPersonals>>(mutableListOf<DadesPersonals>())
    var sollicitants by sollicitantsProperty

    val origenProperty = SimpleStringProperty("Barcelona")
    var origen by origenProperty

    val destinacioProperty = SimpleStringProperty("")
    var destinacio by destinacioProperty

    val motiuDesplaçamentProperty = SimpleStringProperty("")
    var motiuDesplaçament by motiuDesplaçamentProperty

    val anadaProperty = SimpleObjectProperty<LocalDateTime>(LocalDateTime.now())
    var anada by anadaProperty

    val tornadaProperty = SimpleObjectProperty<LocalDateTime>(LocalDateTime.now().plusDays(1))
    var tornada by tornadaProperty

    val mitjaTransportProperty = SimpleObjectProperty<TipusTransport>(TipusTransport.AVIO)
    var mitjaTransport by mitjaTransportProperty

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

    val bestretaPerAllotjamentProperty = SimpleBooleanProperty(false)
    var bestretaPerAllotjament by bestretaPerAllotjamentProperty

    val bestretaPerManutencioProperty = SimpleBooleanProperty(false)
    var bestretaPerManutencio by bestretaPerManutencioProperty

    val bestretaAltresProperty = SimpleBooleanProperty(false)
    var bestretaAltres by bestretaAltresProperty

    val bestretaAltresDescripcioProperty = SimpleStringProperty("")
    var bestretaAltresDescripcio by bestretaAltresDescripcioProperty

    val nomResponsableProperty = SimpleStringProperty("Joan-Lluís Espinós Espinós")
    var nomResponsable by nomResponsableProperty

    val carrecResponsableProperty = SimpleStringProperty("Director general")
    var carrecResponsable by carrecResponsableProperty

    val nomDestinatariProperty = SimpleStringProperty("")
    var nomDestinatari by nomDestinatariProperty

    val emailDestinatariProperty = SimpleStringProperty("")
    var emailDestinatari by emailDestinatariProperty

}

class AutoritzacioViewModel : ItemViewModel<Autoritzacio>() {
    val sollicitants = bind(Autoritzacio::sollicitantsProperty)
    val origen = bind(Autoritzacio::origenProperty)
    val destinacio = bind(Autoritzacio::destinacioProperty)
    val motiuDesplaçament = bind(Autoritzacio::motiuDesplaçamentProperty)
    val anada = bind(Autoritzacio::anadaProperty)
    val tornada = bind(Autoritzacio::tornadaProperty)
    val mitjaTransport = bind(Autoritzacio::mitjaTransportProperty)
    val altres = bind(Autoritzacio::altresProperty)
    val allotjament = bind(Autoritzacio::allotjamentProperty)
    val reserva = bind(Autoritzacio::reservaProperty)
    val creditor = bind(Autoritzacio::creditorProperty)
    val import = bind(Autoritzacio::importProperty)
    val finançamentExtern = bind(Autoritzacio::finançamentExternProperty)
    val finançamentExternDescripcio = bind(Autoritzacio::finançamentExternDescripcioProperty)
    val bestretaNo = bind(Autoritzacio::bestretaNoProperty)
    val bestretaPerAllotjament = bind(Autoritzacio::bestretaPerAllotjamentProperty)
    val bestretaPerManutencio = bind(Autoritzacio::bestretaPerManutencioProperty)
    val bestretaAltres = bind(Autoritzacio::bestretaAltresProperty)
    val bestretaAltresDescripcio = bind(Autoritzacio::bestretaAltresDescripcioProperty)
    val nomResponsable = bind(Autoritzacio::nomResponsableProperty)
    val carrecResponsable = bind(Autoritzacio::carrecResponsableProperty)
    val nomDestinatari = bind(Autoritzacio::nomDestinatariProperty)
    val emailDestinatari = bind(Autoritzacio::emailDestinatariProperty)
}
