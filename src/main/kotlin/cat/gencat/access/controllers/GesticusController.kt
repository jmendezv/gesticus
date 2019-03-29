package cat.gencat.access.controllers

import cat.gencat.access.db.*
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.model.EditableAdmes
import cat.gencat.access.model.EditableSSTT
import cat.gencat.access.pdf.GesticusPdf
import tornadofx.*
import java.io.File
import kotlin.system.exitProcess

class GesticusController : Controller() {

    private val gesticusDb: GesticusDb = GesticusDb

    private val gesticusPdf = GesticusPdf

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

    fun insertEstatDeEstadaDocumentada(numeroEstada: String, estat: EstatsSeguimentEstadaEnum, comentaris: String, hores: Int) =
            gesticusDb.insertEstatDeEstadaDocumentada(numeroEstada, estat, comentaris, hores)

    fun checkStatusSummary() = gesticusDb.checkStatusTableSummary()

    fun checkStatusAcabadaSendEmail() = gesticusDb.checkStatusAcabadaSendEmail()

    fun checkStatusUpdateBd() = gesticusDb.checkStatusUpdateBd()

    fun doBaixa(nif: String, value: Boolean) = gesticusDb.doBaixa(nif, value)

    /* Aquest mètode revoca una estada concedida: informa docent, centre, empresa i ssttt */
    fun renunciaEstada(registre: Registre) = gesticusDb.renunciaEstada(registre)

    fun findCentreAndSSTT(codiCentre: String): Pair<Centre, SSTT> =
            gesticusDb.findCentreAndSSTT(codiCentre)

    fun findSSTT(codiSSTT: String): SSTT =
            gesticusDb.findSSTT(codiSSTT)

    fun findAllEditableSSTT() = gesticusDb.getServeisTerritorials()

    fun existeixNumeroDeEstada(numeroEstada: String): Boolean =
            gesticusDb.existeixNumeroDeEstada(numeroEstada)

    fun getServeisTerritorials() = gesticusDb.getServeisTerritorials()

    fun updateSSTT(editableSSTT: EditableSSTT) = gesticusDb.updateSSTT(editableSSTT)

    fun getAdmesos() = gesticusDb.getAdmesos()

    fun updateAdmesos(editableAdmes: EditableAdmes) = gesticusDb.updateAdmesos(editableAdmes)

    fun findRegistreByNif(nif: String): Registre? = gesticusDb.findRegistreByNif(nif)

    fun findAllColletiuSenseEstada(familia: String) = gesticusDb.findAllColletiuSenseEstada(familia)

    fun countTotalAdmesos() = gesticusDb.countTotalAdmesos()

    fun countTotalBaixesAdmesos() = gesticusDb.countTotalBaixesAdmesos()

    fun countTotalEstades() = gesticusDb.countTotalEstades()

    fun countTotalEstadesPerCentre() = gesticusDb.countTotalEstadesPerCentre()

    fun countTotalEstadesNoGestionadesPerCentre() = gesticusDb.countTotalEstadesNoGestionadesPerCentre()

    fun countTotalEstadesPerFamillia() = gesticusDb.countTotalEstadesPerFamillia()

    fun countTotalEstadesNoGestionadesPerFamillia() = gesticusDb.countTotalEstadesNoGestionadesPerFamillia()

    fun countTotalEstadesPerSSTT() = gesticusDb.countTotalEstadesPerSSTT()

    fun countTotalEstadesNoGestionadesPerSSTT() = gesticusDb.countTotalEstadesNoGestionadesPerSSTT()

    fun countTotalEstadesPerSexe() = gesticusDb.countTotalEstadesPerSexe()

    fun countTotalEstadesNoGestionadesPerSexe() = gesticusDb.countTotalEstadesNoGestionadesPerSexe()

    fun countTotalEstadesPerCos() = gesticusDb.countTotalEstadesPerCos()

    fun countTotalEstadesNoGestionadesPerCos() = gesticusDb.countTotalEstadesNoGestionadesPerCos()

    fun doLlistatPendentsPerFamilies() = gesticusDb.doLlistatPendentsPerFamilies()

    fun sendRecordatoriPendentsPerFamilies(data: String) = gesticusDb.sendRecordatoriPendentsPerFamilies(data)

    fun generateCSVFileStatusDocumentada() = gesticusDb.generateCSVFileStatusDocumentada()

    fun isDocentAdmes(nif: String) = gesticusDb.isDocentAdmes(nif)

    fun getAllEstades() = gesticusDb.getAllEstades()

    fun getBarem() = gesticusDb.getBarem()

    fun doMemoria() = gesticusDb.doMemoria()

    fun getEstadesEnCurs() = GesticusDb.getEstadesEnCurs()
    
}