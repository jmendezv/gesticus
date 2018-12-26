package cat.gencat.access.model

data class EstadaQuery(val codi: String, val nomDocent: String, val nif: String, val any: Int) {
    lateinit var seguiments: List<SeguimentQuery>

}