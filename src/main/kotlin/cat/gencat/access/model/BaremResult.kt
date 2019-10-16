package cat.gencat.access.model

data class BaremResult(
        val nif: String,
        val nom: String,
        val centre: String,
        val notaProjecte: Double,
        val notaAngituitat: Double,
        val notaCatedratic: Double,
        val notaFormacio: Double,
        val notaTreballsDesenvolupats: Double,
        val notaAltresTitulacions: Double,
        val total: Double) {
}