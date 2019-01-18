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

    fun loadDataByDocentIdFromPdf(nif: String, tipusEstada: String): Registre? =
            gesticusPdf.loadDataByDocentIdFromPdf(nif, tipusEstada)

    fun parsePdf(file: File, tipusEstada: String): Pair<Estada, Empresa>? =
            gesticusPdf.parsePdf(file, tipusEstada)

    fun getRegistreFromPdf(file: File, tipusEstada: String): Registre? =
            gesticusPdf.getRegistreFromPdf(file, tipusEstada)

    fun saveEstada(registre: Registre): Boolean =
            gesticusDb.saveEstada(registre)

    fun findRegistreByCodiEstada(codiEstada: String): Registre? =
            gesticusDb.findRegistreByCodiEstada(codiEstada)

    fun queryCandidats(): List<String> = gesticusDb.queryCandidats()

    fun readDataByDocentIdFromDb(nif: String, tipusEstada: String): Registre? =
            gesticusPdf.readDataByDocentIdFromDb(nif, tipusEstada)

    fun queryEstadesAndSeguiments(nif: String?) =
            gesticusDb.queryEstadesAndSeguiments(nif)

    fun insertEstatDeEstada(numeroEstada: String, estat: EstatsSeguimentEstadaEnum, comentaris: String): Boolean =
            gesticusDb.insertSeguimentDeEstada(numeroEstada, estat, comentaris)

    fun checkEstats() = gesticusDb.checkEstats()

    fun doBaixa(nif: String, value: Boolean) = gesticusDb.doBaixa(nif, value)

    fun findCentreAndSSTT(codiCentre: String): Pair<Centre, SSTT> =
            gesticusDb.findCentreAndSSTT(codiCentre)

    fun findSSTT(codiSSTT: String): SSTT =
            gesticusDb.findSSTT(codiSSTT)

    fun findAllEditableSSTT() = gesticusDb.findAllEditableSSTT()

    fun existeixNumeroDeEstada(numeroEstada: String): Boolean =
            gesticusDb.existeixNumeroDeEstada(numeroEstada)

}