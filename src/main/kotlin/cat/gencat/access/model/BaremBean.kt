package cat.gencat.access.model

import java.util.*

data class BaremBean(
        val id: Long,
        val nif: String,
        val nom: String,
        val email: String,
        val curs: String,
        val privat: Boolean,
        val nou: Boolean,
        val dual: Boolean,
        val grup: Boolean,
        val interi: Boolean,
        val repetidor: Boolean,
        var enEspera: Boolean,
        val notaProjecte: Double,
        val notaAngituitat: Double,
        val notaFormacio: Double,
        val notaTreballsDesenvolupats: Double,
        val notaAltresTitulacions: Double,
        val notaCatedratic: Double,
        val codiGrup: String = "000",
        val notaIndividual: Double,
        val notaGrup: Double,
        val comentaris: String)