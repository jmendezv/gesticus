package cat.gencat.access.views

//import org.controlsfx.control.Notifications
import cat.gencat.access.functions.Utils
import cat.gencat.access.model.TutorCertification
import cat.gencat.access.model.TutorCertificationModel
import tornadofx.*

class TutorCertificationView : View(Utils.APP_TITLE + ": Cestificació d'estada") {

    val model: TutorCertificationModel = TutorCertificationModel(TutorCertification())

    override val root = form {

        fieldset("Certificat Tutor/a") {

            field("DNI/NIE") {
                textfield(model.dni) {

                }.required()
            }

            field("Hores") {
                textfield(model.hores){

                }.required()
            }

        }

        hbox(10.0) {

            button("Desa") {
                action {
                    model.commit {
                        //                    val certification = model.item
//                    Notifications.create()
//                            .title("Certification created")
//                            .text("${certification.dni} was born ${certification.hores}")
//                            .owner(this)
//                            .showInformation()
                    }
                    this@TutorCertificationView.close()
                }
                enableWhen(model.valid)
            }

            button("Cancel·la") {
                action {
                    model.item = null
                    this@TutorCertificationView.close()
                }
            }
        }

    }

}
