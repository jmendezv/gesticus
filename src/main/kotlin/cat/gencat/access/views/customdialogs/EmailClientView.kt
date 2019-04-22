package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.db.GesticusDb
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.APP_TITLE
import cat.gencat.access.model.Email
import cat.gencat.access.model.EmailModel
import javafx.scene.control.ButtonType
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

/*
* TODO("This view will send custom email to specific families from professors_t")
* */
class EmailClientView : View(Utils.APP_TITLE + ": Client de correu") {

    val model: EmailModel by inject()
    val controller: GesticusController by inject()

    override val root = form {

        fieldset {
            field("De:") {
                textfield(model.de) {
                    tooltip("Adreça de correu d'origen")
                }
            }
            field("Per a:") {
                combobox(model.pera) {
                    items = controller.getFamilies().observable()
                    tooltip("Col·lectiu al que va adreçat")
                }
            }
            field("Motiu:") {
                textfield(model.motiu) {
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
                        val files: List<File> = chooseFile("Select some text files", filters, FileChooserMode.Single)
                        val html = files[0].bufferedReader().readLines().joinToString(separator = "\n")
                        model.cos.value = html
                    }
                }
                button("Enviar") {
                    action {
                        model.commit()
                        confirmation(APP_TITLE, "Vols enviar aquest missatge a ${model.pera.value} docents?") {
                            if (it == ButtonType.OK) {
                                sendEmail()
                            }
                        }
                    }
                }
                button("Cancel·lar") {
                    action {
                        model.rollback()
                        this@EmailClientView.close()
                    }
                }
            }

        }
    }
    /* TODO("Retrive data and send email") */
    fun sendEmail(): Unit {}
}
