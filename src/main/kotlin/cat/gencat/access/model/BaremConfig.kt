package cat.gencat.access.model

data class BaremConfig(
        val id: Long,
        val curs: Int,
        val placesPubliques: Int,
        val placesPrivades: Int,
        val reservaDg: Double,
        val reservaDual: Double,
        val reservaCiclesNous: Double,
        val reservaGrups: Double,
        val reservaInterins: Double) {
}