package cat.gencat.access.functions

import cat.gencat.access.db.GesticusDb
import cat.gencat.access.model.BaremBean
import cat.gencat.access.pdf.GesticusPdf

object Barema {

    private val gesticusDb: GesticusDb = GesticusDb
    private val gesticusPdf = GesticusPdf

    private var allBarem: List<BaremBean> = listOf()
    private var allBaremPrivats: List<BaremBean> = listOf()
    private var allBaremPublics: List<BaremBean> = listOf()

    /*
* TODO("Review")
*
* */
    public fun barema() {
        allBarem = gesticusDb.getBarem()

        allBaremPrivats = allBarem
            .filter { barem ->
                barem.privat
            }

        allBaremPublics = allBarem
            .filter { barem ->
                !barem.privat
            }

        treatPrivats()
        treatPublics()

    }

    /*
    * Habitualemnt de le sol·licituds privades hi ha molt poques.
    *
    * Tot aquest tractament a la pràctica no caldria
    *
    * */
    private fun treatPrivats() {

        // TODO("Obtenir aquest limit i els altres des d'un fitxer extern barem.xml/barem.properties")
        val limit = 10

        // Si hi ha més sol·licituds privades que places
        if (allBaremPrivats.size > limit) {

            val ciclesNous = allBaremPrivats
                .filter { barem ->
                    barem.nou
                }.toMutableList()

            val dual = allBaremPrivats
                .filter { barem ->
                    !barem.nou && barem.dual
                }.toMutableList()

            val grup = allBaremPrivats
                .filter { barem ->
                    !(barem.nou || barem.dual) && barem.grup
                }.toMutableList()

            val individual = allBaremPrivats
                .filter { barem ->
                    !(barem.nou || barem.dual || barem.grup || barem.repetidor)
                }.toMutableList()

            val repetidors = allBaremPrivats
                .filter { barem ->
                    !(barem.nou || barem.dual || barem.grup) || barem.repetidor
                }.toMutableList()

            println("privats tots ${allBaremPrivats.size} nous ${ciclesNous.size} dual sense nous ${dual.size} grup ${grup.size} individuals no repetidors ${individual.size} individuals repetidors ${repetidors.size}")
        } else {

        }

    }

    /*
    * Sol·licituds d'escoles públiques
    * */
    private fun treatPublics() {

        // TODO("Obtenir aquest limit i els altres des d'un fitxer extern barem.xml")
        val limit = 10

        // Si hi ha més demanda que oferta
        if (allBaremPublics.size > limit) {

        }
    }

}