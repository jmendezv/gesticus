package cat.gencat.access.model

data class Barem(
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
        //var enEspera: Boolean,
        val notaProjecte: Double,
        val notaAngituitat: Double,
        val notaFormacio: Double,
        val notaTreballsDesenvolupats: Double,
        val notaAltresTitulacions: Double,
        val notaCatedratic: Double,
        val codiGrup: String,
        val comentaris: String) {
    val notaFinal = notaProjecte + notaAngituitat + notaFormacio + notaTreballsDesenvolupats + notaAltresTitulacions + notaCatedratic
}
