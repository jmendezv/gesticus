package cat.gencat.access.model

import java.time.LocalDate
import java.util.*

data class EstadaQuery(val codi: String, val nomDocent: String, val nif: String, val any: Int, val nomEmpresa: String, val dataInici: Date, val dataFinal: Date) {
    lateinit var seguiments: List<SeguimentQuery>

}