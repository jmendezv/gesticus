package cat.gencat.access.model

import cat.gencat.access.db.EstatsSeguimentEstadaEnum
import java.util.*

data class Summary(
        val codiEstada: String,
        val nomDocentAmbTractament: String,
        val emailDocent: String,
        val nomEmpresa: String,
        val dataInici: Date,
        val dataFinal: Date,
        val estat: EstatsSeguimentEstadaEnum,
        val comentari: String) {
}