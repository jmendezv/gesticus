package cat.gencat.access.controllers

import cat.gencat.access.db.*
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.pdf.GesticusPdf
import tornadofx.*
import java.io.File
import kotlin.system.exitProcess

class GesticusController: Controller() {

    private val gesticusDb: GesticusDb = GesticusDb()

    private val gesticusPdf = GesticusPdf()

    fun menuTanca() {
        gesticusDb.close()
        GesticusMailUserAgent.cancelFutures()
        exitProcess(0)
    }

    fun preLoadData(): Unit {
        gesticusDb.preLoadDataFromAccess()
    }

    fun loadDataByDocentIdFromPdf(nif: String): Registre? = gesticusPdf.loadDataByDocentIdFromPdf(nif)

    fun reloadPdf(file: File): Pair<Estada, Empresa>? = gesticusPdf.parsePdf(file)

    fun getRegistreFromPdf(file: File) = gesticusPdf.getRegistreFromPdf(file)

    fun saveEstada(nif: String, estada: Estada, empresa: Empresa): Boolean = gesticusDb.saveEstada(nif, estada, empresa)

    fun findRegistreByCodiEstada(codiEstada: String): Registre? =
        gesticusDb.findRegistreByCodiEstada(codiEstada)

    fun queryCandidats(): List<String> = gesticusDb.queryCandidats()

    fun readDataByDocentIdFromDb(nif: String): Registre? = gesticusPdf.readDataByDocentIdFromDb(nif)

    fun queryEstadesAndSeguiments(nif: String?) = gesticusDb.queryEstadesAndSeguiments(nif)

    fun insertEstatDeEstada(numeroEstada: String, estat: EstatsSeguimentEstada, comentaris: String): Boolean = gesticusDb.insertEstatDeEstada(numeroEstada, estat, comentaris)

    fun checkEstats() = gesticusDb.checkEstats()

    fun doBaixa(nif: String, value: Boolean) = gesticusDb.doBaixa(nif, value)

    fun doDocumentada() = gesticusDb.doDocumentada()

    fun doFinalitzada() = gesticusDb.doFinalitzada()

}