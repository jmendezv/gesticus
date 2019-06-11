package cat.gencat.access.model

import java.util.*

data class CSVBean(
    val codyAny: String,
    val codiActivitat: String,
    val codiPersona: String,
    val codiCentreTreball: String,
    val nomActivitat: String,
    val numHoresPrevistes: Int,
    val dataInici: Date,
    val dataFinal: Date)