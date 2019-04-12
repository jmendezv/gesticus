package cat.gencat.access.model

import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import java.sql.Time
import java.util.*
import tornadofx.*
import java.time.LocalDate
import java.time.LocalTime

class Visita(id: Long = 0, estadesCodi: String = "", curs: String = "", tipus: String = "", data: LocalDate = LocalDate.now(), hora: String = "", comentaris: String = "") {
    val idProperty = SimpleLongProperty(id)
    var id by idProperty
    val estadesCodiProperty = SimpleStringProperty(estadesCodi)
    var estadesCodi by estadesCodiProperty
    val cursProperty = SimpleStringProperty(curs)
    var curs by cursProperty
    val tipusProperty = SimpleStringProperty(tipus)
    var tipus by tipusProperty
    val dataProperty = SimpleObjectProperty<LocalDate>(data)
    var data by dataProperty
    val horaProperty = SimpleStringProperty(hora)
    var hora by horaProperty
    val comentarisProperty = SimpleStringProperty(comentaris)
    var comentaris by comentarisProperty
}

class VisitaModel(visita: Visita = Visita()) : ItemViewModel<Visita>(visita) {
    val id = bind(Visita::idProperty)
    val estadesCodi = bind(Visita::estadesCodiProperty)
    val curs = bind(Visita::cursProperty)
    val tipus = bind(Visita::tipusProperty)
    val data = bind(Visita::dataProperty)
    val hora = bind(Visita::horaProperty)
    val comentaris = bind(Visita::comentarisProperty)
}

