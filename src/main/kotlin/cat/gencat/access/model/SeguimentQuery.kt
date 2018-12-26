package cat.gencat.access.model

import java.time.LocalDate

data class SeguimentQuery(val codi: String, val estat: String, val data: LocalDate)