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

    private val gesticusDb: GesticusDb = GesticusDb()

    fun menuTanca() {
        gesticusDb.close()
        GesticusMailUserAgent.cancelFutures()
        exitProcess(0)
    }

    fun preLoadData(): Unit {
        gesticusDb.preLoadDataFromAccess()
    }

    fun loadDataByDocentIdFromPdf(nif: String): Registre? = gesticusDb.loadDataByDocentIdFromPdf(nif)

    fun reloadPdf(file: File): Pair<Estada, Empresa>? = gesticusDb.reloadPdf(file)

    fun saveEstada(nif: String, estada: Estada, empresa: Empresa): Boolean = gesticusDb.saveEstada(nif, estada, empresa)

    fun findRegistreByCodiEstada(codiEstada: String): Registre? =
        gesticusDb.findRegistreByCodiEstada(codiEstada)

    fun queryCandidats(): List<String> = gesticusDb.queryCandidats()

    fun readDataByDocentIdFromDb(nif: String): Registre? = gesticusDb.readDataByDocentIdFromDb(nif)


}