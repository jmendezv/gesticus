package cat.gencat.access.views


import cat.gencat.access.model.EmailModel
import tornadofx.*

class EmailClient : View("My View") {

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
            field("Clau:") {
                passwordfield(model.clau) {
                    tooltip("Clau secreta")
                }
            }

            hbox(10.0) {
                button("Enviar") {
                    action {
                        model.commit()
                        // sendEmail(model.item)
                        this@EmailClient.close()
                    }
                }
                button("Cancel·lar") {
                    action {
                        model.rollback()
                        this@EmailClient.close()
                    }
                }
            }

        }
    }

}
