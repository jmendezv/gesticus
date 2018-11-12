package cat.gencat.access

data class Docent(val dni: String, val nom: String, val destinacio: String, val especialitat: String, val email: String, val telefon: String)

class GesticusDb {

    fun findDocentById(dni: String): Docent {

        return Docent("029029866W", "ABAD BUENO, Juan de Dios", "IN", "ORGANITZACIÓ I GESTIO COMERCIAL", "jabad3@xtec.cat", "655236204")

    }

}