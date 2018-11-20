package cat.gencat.access

import tornadofx.*
import kotlin.system.exitProcess

class GesticusController: Controller() {

    val gesticusDb: GesticusDb = GesticusDb()

    fun menuTanca() {
        gesticusDb.close()
        exitProcess(0)
    }

    fun preLoadData(): Unit {
        gesticusDb.preLoadDataFromAccess()
    }

    fun findDataByDocentId(nif: String): Registre? = gesticusDb.findRegistreByDocentId(nif)


    fun loadEmpresaAndEstadaFromPdf(nif: String): Pair<Estada, Empresa> = gesticusDb.loadEmpresaAndEstadaFromPdf(nif)

}