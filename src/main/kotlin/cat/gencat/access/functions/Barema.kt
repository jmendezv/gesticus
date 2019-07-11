package cat.gencat.access.functions

import cat.gencat.access.db.GesticusDb
import cat.gencat.access.model.BaremBean
import cat.gencat.access.pdf.GesticusPdf
import java.io.FileInputStream
import java.util.*

object Barema {

    private val gesticusDb: GesticusDb = GesticusDb
    private val gesticusPdf = GesticusPdf

    private var allBarem: List<BaremBean> = listOf()
    private var allBaremPrivats: List<BaremBean> = listOf()
    private var allBaremPrivatsCiclesNous: List<BaremBean> = listOf()
    private var allBaremPrivatsDual: List<BaremBean> = listOf()
    private var allBaremPrivatsGrups: List<BaremBean> = listOf()
    private var allBaremPrivatsIndividuals: List<BaremBean> = listOf()
    private var allBaremPrivatsRepetidors: List<BaremBean> = listOf()
    private var allBaremPublics: List<BaremBean> = listOf()
    private var allBaremPublicsCiclesNous: List<BaremBean> = listOf()
    private var allBaremPublicsDual: List<BaremBean> = listOf()
    private var allBaremPublicsGrups: List<BaremBean> = listOf()
    private var allBaremPublicsIndividuals: List<BaremBean> = listOf()
    private var allBaremPublicsRepetidorss: List<BaremBean> = listOf()

    // Properties

    private var totalEstadesConcedides: Int = 0
    private var totalEstadesDG: Int = 0
    private var totalEstadesPrivades: Int = 0
    private var totalEstadesPubliques: Int = 0


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
    * total estades
    * reserva DG
    * estades privades
    * estades publiques
    *
    * */
    private fun readBaremaProperties() {
        val properties = Properties()
        val inputStream = FileInputStream(PATH_TO_BAREM_CONFIG_FILE)
        // Alternativament
        // val reader = FileReader(PATH_TO_BAREM_CONFIG_FILE)
        properties.load(inputStream)
        properties.forEach { (k, v) ->
            println("clau $k valor $v")

        }
    }

    /*
    * Habitualemnt de le sol·licituds privades hi ha molt poques.
    *
    * Tot aquest tractament a la pràctica no caldria
    *
    * */
    private fun treatPrivats() {

        // TODO("Obtenir aquest limit i els altres des d'un fitxer extern barem.xml/barem.properties/bd")
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