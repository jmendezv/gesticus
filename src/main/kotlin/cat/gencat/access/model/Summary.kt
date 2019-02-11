package cat.gencat.access.model

import cat.gencat.access.db.EstatsSeguimentEstadaEnum
import cat.gencat.access.functions.MILLISECONDS_IN_A_DAY
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.text.DateFormatter

data class Summary(
        val codiEstada: String,
        val nomDocentAmbTractament: String,
        val emailDocent: String,
        val nomEmpresa: String,
        val dataInici: Date,
        val dataFinal: Date,
        val estat: EstatsSeguimentEstadaEnum,
        val comentari: String) {

    val inici = SimpleDateFormat("dd/MM/yyyy").format(dataInici)
    val fi = SimpleDateFormat("dd/MM/yyyy").format(dataFinal)
    val interval = ((Date().time - dataFinal.time) / MILLISECONDS_IN_A_DAY)
}