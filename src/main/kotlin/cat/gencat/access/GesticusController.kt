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
        gesticusDb.preLoadData()
    }

    fun findDataByDocentId(nif: String): Registre? = gesticusDb.findDataByDocentId(nif)


    fun loadEmpresaFromPdf(nif: String): Empresa? = gesticusDb.loadEmpresaFromPdf(nif)

}