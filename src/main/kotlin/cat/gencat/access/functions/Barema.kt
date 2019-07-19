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
    // Estades de 2, 3 i 4 setmanes
    private var totalEstadesConcedides: Int = 120
    private var percentatgeEstadesDG: Double = 10.0
    private var totalEstadesPubliques: Int = 100
    // 30 * 2 + 35 * 3 + 35 * 4
    private var totalSetmanesEstadesPubliques: Int = 305
    private var totalEstadesPubliquesDeDuesSetmanes: Int = 153
    private var totalEstadesPrivades: Int = 20
    // 6 * 2 + 7 * 3 + 7 * 4
    private var totalSetmanesEstadesPrivades: Int = 61
    private var totalEstadesPrivadesDeDuesSetmanes: Int = 30
    private var totalEstadesDeDuesSetmanes = 0
    private var totalEstadesDeDuesSetmanesLliures = 0
    private var totalEstadesDeDuesSetmanesDG = 0

    /*
    * TODO("Review")
    *
    * */
    public fun barema() {
        allBarem = gesticusDb.getBarem()
        readBaremaProperties()

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

    private fun showProperties(properties: Properties): Unit {
        properties.forEach { (k, v) ->
            println("clau $k valor $v")
        }
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
        totalEstadesConcedides =
            properties.getProperty("total-estades-concedides").toInt()
        percentatgeEstadesDG =
            properties.getProperty("percentatge-estades-dg").toDouble()
        totalEstadesPubliques =
            properties.getProperty("total-estades-publiques").toInt()
        totalEstadesPrivades =
            properties.getProperty("total-estades-privades").toInt()
        totalSetmanesEstadesPubliques =
            properties.getProperty("total-setmanes-estades-publiques").toInt()
        totalEstadesPubliquesDeDuesSetmanes =
            properties.getProperty("total-estades-publiques-de-dues-setmanes").toInt()
        totalSetmanesEstadesPrivades =
            properties.getProperty("total-setmanes-estades-privades").toInt()
        totalEstadesPrivadesDeDuesSetmanes =
            properties.getProperty("total-estades-privades-de-dues-setmanes").toInt()
        totalEstadesDeDuesSetmanes =
            totalEstadesPubliquesDeDuesSetmanes + totalEstadesPrivadesDeDuesSetmanes
        totalEstadesDeDuesSetmanesDG = (totalEstadesDeDuesSetmanes * percentatgeEstadesDG).toInt()
        totalEstadesDeDuesSetmanesLliures = totalEstadesDeDuesSetmanes - totalEstadesDeDuesSetmanesDG
        /*
        * Cal aplicar un factor corrector sobre les estades que es reserva la DG.
        * Senzillament, les resto de les públiques.
        * També hagues pogut trobar la part proporcional d'estades publiques i privades
        * però donat que les privades no s'exhaureixem mai ho simplifico així.
        * */
        totalEstadesPubliquesDeDuesSetmanes -= totalEstadesDeDuesSetmanesDG
    }

    /*
    * This method inserts all data into taula
    *
    * And a report is provided for all barem related taules to present to the Comission
    *
    * */
    private fun saveEstades(taula: String, estades: List<BaremBean>) {
        gesticusDb.saveEstades(taula, estades)


    }

    /*
    * Habitualemnt de le sol·licituds privades hi ha molt poques.
    *
    * Tot aquest tractament a la pràctica no caldria
    *
    * */
    private fun treatPrivats() {

        // Si hi ha més sol·licituds privades que places
        if (allBaremPrivats.size > totalEstadesPrivadesDeDuesSetmanes) {

            allBaremPrivatsCiclesNous = allBaremPrivats
                .filter { barem ->
                    barem.nou
                }.toList()

            allBaremPrivatsDual = allBaremPrivats
                .filter { barem ->
                    !barem.nou && barem.dual
                }.toMutableList()

            allBaremPrivatsGrups = allBaremPrivats
                .filter { barem ->
                    !(barem.nou || barem.dual) && barem.grup
                }.toMutableList()

            allBaremPrivatsIndividuals = allBaremPrivats
                .filter { barem ->
                    !(barem.nou || barem.dual || barem.grup || barem.repetidor)
                }.toMutableList()

            allBaremPrivatsRepetidors = allBaremPrivats
                .filter { barem ->
                    !(barem.nou || barem.dual || barem.grup) || barem.repetidor
                }.toMutableList()

            println("Privats tots ${allBaremPrivats.size} nous ${allBaremPrivatsCiclesNous.size} dual sense nous ${allBaremPrivatsDual.size} grup ${allBaremPrivatsGrups.size} individuals no repetidors ${allBaremPrivatsIndividuals.size} individuals repetidors ${allBaremPrivatsRepetidors.size}")
        } else {
            println("La demanda de privats no supera la oferta")
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