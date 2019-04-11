package cat.gencat.access.views.editor

import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class Visita(id: Long, estadesCodi: String, curs: String, tipus: String, data: Date, hora: Time, comentaris: String) {
    val idProperty = SimpleLongProperty(id)
    var id by idProperty
    val estadesCodiProperty = SimpleStringProperty(estadesCodi)
    var estadesCodi by estadesCodiProperty
    val cursProperty = SimpleStringProperty(curs)
    var curs by cursProperty
    val tipusProperty = SimpleStringProperty(tipus)
    var tipus by tipusProperty
    val dataProperty = SimpleObjectProperty<Date>(data)
    var data by dataProperty
    val horaProperty = SimpleObjectProperty<Time>(hora)
    var hora by horaProperty
    val comentarisProperty = SimpleStringProperty(comentaris)
    var comentaris by comentarisProperty
}

class VisitaModel : ItemViewModel<Visita>() {
    val id = bind(Visita::idProperty)
    val estadesCodi = bind(Visita::estadesCodiProperty)
    val curs = bind(Visita::cursProperty)
    val tipus = bind(Visita::tipusProperty)
    val data = bind(Visita::dataProperty)
    val hora = bind(Visita::horaProperty)
    val comentaris = bind(Visita::comentarisProperty)
}

