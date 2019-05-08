package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.*
import cat.gencat.access.model.AutoritzacioViewModel
import cat.gencat.access.os.GesticusOs
import tornadofx.*
import java.io.File

/* Wizard are ok for complex forms */
class AutoritzacioView : Wizard(
        Utils.APP_TITLE,
        "Autorització d'una ordre de serveis."
) {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()

    init {
//        controller.printEstructuraPdf(PATH_TO_DESPESES_PROPOSTA)
//        graphic = resources.imageview("/graphics/autoritzacio.gif")
        add(DadesPersonalsView::class)
        add(DadesDesplaçamentView::class)
        add(DadesFinançamentIBestretaView::class)
    }

    /* This method creates a new directory where to copy all stuff */
    private fun creaDirectori(): String {

        val transaction = Integer.toHexString(System.currentTimeMillis().toInt())
        val newDir = "$PATH_TO_DESPESES$transaction"
        File(newDir).mkdir()
        return newDir
    }

    private fun creaPdfs(where: String) {
        controller.creaSollicitudsDespesaPdf(File(PATH_TO_DESPESES_PROPOSTA), model.item, where)
    }

    private fun copyInforme(where: String) {
        GesticusOs.copyFile(PATH_TO_DESPESES_INFORME, where)
    }

    private fun creaZip(directori: String, destinacio: String) {
        GesticusOs.zipDirectory(directori, destinacio)
    }

    private fun lliuraZip(to: String, file: String) {
        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                SUBJECT_GENERAL,
                BODY_AUTORITZACIO_DESPESES
                        .replace("?1", "Bon dia ${model.nomDestinatari.value}")
                        .replace("?2", "${model.motiuDesplaçament.value}"),
                listOf(file),
                listOf(to))
    }

    /* If the user clicks the Finish button, the onSave function in the Wizard itself is activated */
    override fun onSave() {
        super.onSave()
        model.commit()
        runAsync {
            val dirTo = creaDirectori()
            creaPdfs(dirTo)
            copyInforme("$dirTo\\informe.doc")
            creaZip(dirTo, dirTo + ".zip")
            lliuraZip(model.emailDestinatari.value, dirTo + ".zip")

        } ui {
//            runLater {
                information(Utils.APP_TITLE, "S'ha lliurat el correu correctament")
//            }
        }

    }

    override fun onCancel() {
        super.onCancel()

    }

}
