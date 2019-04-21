package cat.gencat.access.views.customdialogs

import cat.gencat.access.db.GesticusDb
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.APP_TITLE
import cat.gencat.access.model.Email
import cat.gencat.access.model.EmailModel
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

/*
* TODO("This view will send custom email to specific families")
* */
class EmailClientView : View(Utils.APP_TITLE + ": Client de correu") {

    val model: EmailModel by inject()
    val database = GesticusDb
    val client = GesticusMailUserAgent

    override val root = form {

        fieldset {
            field("De:") {
                textfield(model.de) {
                    tooltip("Adreça de correu d'origen")
                }
            }
            field("Per a:") {
                combobox(model.pera) {
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
                        println(html)
                    }
                }
                button("Enviar") {
                    action {
                        model.commit()
                        sendEmail(model.item)
                        this@EmailClientView.close()
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

    fun sendEmail(email: Email): Unit {}
}
