package cat.gencat.access.model

import cat.gencat.access.db.EstatsSeguimentEstadaEnum
import cat.gencat.access.functions.MILLISECONDS_IN_A_DAY
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.text.DateFormatter

class Summary(codiEstada: String? = null,
              nomDocentAmbTractament: String? = null,
              emailDocent: String? = null,
              nomEmpresa: String? = null,
              dataInici: Date? = null,
              dataFinal: Date? = null,
              estat: String? = null,
              comentari: String? = null) {

    val codiEstadaProperty = SimpleStringProperty(this, "codiEstada", codiEstada)
    var codiEstada by codiEstadaProperty

    val nomDocentAmbTractamentProperty = SimpleStringProperty(this, "nomDocentAmbTractament", nomDocentAmbTractament)
    var nomDocentAmbTractament by nomDocentAmbTractamentProperty

    val emailDocentProperty = SimpleStringProperty(this, "emailDocent", emailDocent)
    var emailDocent by emailDocentProperty

    val nomEmpresaProperty = SimpleStringProperty(this, "nomEmpresa", nomEmpresa)
    var nomEmpresa by nomEmpresaProperty

    val estatProperty = SimpleStringProperty(this, "estat", estat)
    var estat by estatProperty

    val comentariProperty = SimpleStringProperty(this, "comentari", comentari)
    var comentari by comentariProperty

    val inici = SimpleDateFormat("dd/MM/yyyy").format(dataInici)
    val fi = SimpleDateFormat("dd/MM/yyyy").format(dataFinal)
    val interval = ((Date().time - dataFinal?.time!!) / MILLISECONDS_IN_A_DAY)
}

class SummaryModel : ItemViewModel<Summary>() {
    val codiEstada = bind(Summary::codiEstadaProperty)
    val nomDocentAmbTractament = bind(Summary::nomDocentAmbTractamentProperty)
    val emailDocent = bind(Summary::emailDocentProperty)
    val nomEmpresa = bind(Summary::nomEmpresaProperty)
    val estat = bind(Summary::estatProperty)
    val comentari = bind(Summary::comentariProperty)
    val inici = bind(Summary::inici)
    val fi = bind(Summary::fi)
    val interval = bind(Summary::interval)


}