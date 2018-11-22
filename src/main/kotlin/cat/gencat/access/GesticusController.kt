package cat.gencat.access

import tornadofx.*
import java.io.File
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

    fun reloadPdf(file: File): Pair<Estada, Empresa> = gesticusDb.reloadPdf(file)

}