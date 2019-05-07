package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.PATH_TO_DESPESES
import cat.gencat.access.functions.PATH_TO_DESPESES_PROPOSTA
import cat.gencat.access.functions.Utils
import cat.gencat.access.model.AutoritzacioViewModel
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

    private fun creaZip() {}

    private fun lliuraZip() {}

    /* If the user clicks the Finish button, the onSave function in the Wizard itself is activated */
    override fun onSave() {
        super.onSave()
        model.commit()
        val dirTo = creaDirectori()
        creaPdfs(dirTo)
        creaZip()
        lliuraZip()
    }

    override fun onCancel() {
        super.onCancel()

    }

}
