package cat.gencat.access.controllers

import cat.gencat.access.db.Empresa
import cat.gencat.access.db.Estada
import cat.gencat.access.db.GesticusDb
import cat.gencat.access.db.Registre
import cat.gencat.access.email.GesticusMailUserAgent
import tornadofx.*
import java.io.File
import kotlin.system.exitProcess

class GesticusController: Controller() {

    val gesticusDb: GesticusDb = GesticusDb()

    fun menuTanca() {
        gesticusDb.close()
        GesticusMailUserAgent.cancelFutures()
        exitProcess(0)
    }

    fun preLoadData(): Unit {
        gesticusDb.preLoadDataFromAccess()
    }

    fun findDataByDocentId(nif: String): Registre? = gesticusDb.findRegistreByDocentId(nif)

    fun reloadPdf(file: File): Pair<Estada, Empresa>? = gesticusDb.reloadPdf(file)

    fun saveEstada(nif: String, estada: Estada, empresa: Empresa): Boolean = gesticusDb.saveEstada(nif, estada, empresa)

    fun findRegistreByCodiEstada(codiEstada: String): Registre? =
        gesticusDb.findRegistreByCodiEstada(codiEstada)


}