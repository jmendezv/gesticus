package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.CORREU_LOCAL1
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.APP_TITLE
import cat.gencat.access.model.EmailModel
import javafx.collections.FXCollections
import javafx.scene.control.ButtonType
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

/*
*
*
* */
class EmailClientDirectorsView : View(Utils.APP_TITLE + ": Client de correu per a directors") {

    //    val model: EmailModel by inject()
    val model: EmailModel = EmailModel()
    val controller: GesticusController by inject()

    override val root = form {

        fieldset {
            field("De:") {
                combobox(model.de) {
                    items = FXCollections.observableArrayList(CORREU_LOCAL1)
                    selectionModel.selectFirst()
                    tooltip("Adreça de correu d'origen")
                }
            }
            field("Per a:") {
                combobox(model.pera) {
                    //items = controller.getFamilies().observable()
                    items.add("TOTHOM")
                    selectionModel.selectFirst()
                    tooltip("Col·lectiu al que va adreçat")
                }
            }
            field("Territori:") {
                combobox(model.territori) {
                    items = FXCollections.observableArrayList("TOTHOM", "Barcelona", "Tarragona", "Lleida", "Girona")
                    selectionModel.selectFirst()
                    tooltip("Motiu del missatge")
                }
            }
            field("Motiu:") {
                combobox(model.motiu) {
                    items = FXCollections.observableArrayList("Comunicat Estades Formatives", "Formació FORTECO", "Difusió Formació")
                    selectionModel.selectFirst()
                    tooltip("Motiu del missatge")
                }
            }
            field("Cos:") {
                textarea(model.cos) {
                    tooltip("Cos del missatge")
                }
            }
            hbox(10.0) {
                button("Carrega cos") {
                    action {
                        val filters = arrayOf(FileChooser.ExtensionFilter("Fitxers HTML", "*.htm?"))
                        val files: List<File> =
                            chooseFile("Selecciona un cos pel missatge", filters, FileChooserMode.Single)
//                        val file: File? = chooseDirectory("Selecciona un cos pel missatge", File(PATH_TO_MESSAGES))
                        val html =
                            if (files.isNotEmpty()) files[0].bufferedReader().readLines().joinToString(separator = "\n") else ""
//                        val html = file?.run {
//                            bufferedReader().readLines().joinToString(separator = "\n")
//                        } ?: ""
                        model.cos.value = html
                    }
                }
                button("Enviar") {
                    enableWhen(model.cos.isNotBlank())
                    action {
                        model.commit()
                        confirmation(
                            APP_TITLE,
                            "Vols enviar aquest missatge a tots el docents de l'especialitat ${model.pera.value}?"
                        ) {
                            if (it == ButtonType.OK) {
                                runAsyncWithProgress {
                                    controller.sendEmailADocents(model.item)
                                }
                            }
                        }
                    }
                }
                button("Tanca") {
                    action {
                        model.rollback()
                        this@EmailClientDirectorsView.close()
                    }
                }
            }
        }
    }
}
