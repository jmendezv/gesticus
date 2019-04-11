package cat.gencat.access.views.customdialogs


import cat.gencat.access.functions.Utils
import cat.gencat.access.model.EmailModel
import tornadofx.*

class EmailClientView : View(Utils.APP_TITLE+ ": Client de correu") {

    val model: EmailModel by inject()

    override val root = form {

        fieldset {
            field("De:") {
                textfield(model.de) {
                    tooltip("Adreça de correu d'origen")
                }
            }
            field("Per a:") {
                textfield(model.pera) {
                    tooltip("Adreça de correu del destinatari")
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
                button("Enviar") {
                    action {
                        model.commit()
                        // sendEmail(model.item)
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

}
