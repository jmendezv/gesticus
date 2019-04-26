package cat.gencat.access.model

import java.sql.Date

data class EmpresaSeguimentBean(val id : Int, val empresaId: Int, val data: Date, val comentaris: String) {
}