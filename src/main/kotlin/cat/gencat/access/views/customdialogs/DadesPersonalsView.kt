package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.AutoritzacioViewModel
import cat.gencat.access.model.DadesPersonals
import cat.gencat.access.model.DadesPersonalsViewModel
import javafx.beans.property.SimpleObjectProperty
import org.controlsfx.control.Notifications
import tornadofx.*

class DadesPersonalsView : View("Dades personals") {

//    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()
    val model: AutoritzacioViewModel by inject()
    val sollicitants = model.sollicitants.value
    val sollicitant = DadesPersonalsViewModel()

    override val root = vbox(3.0) {
        hbox(3.0) {
            tableview(items = sollicitants) {
                column("NIF", DadesPersonals::nif)
                column("Nom", DadesPersonals::nom)
                column("Email", DadesPersonals::email)
                column("Carrec", DadesPersonals::carrec)
                column("Unitat orgànica", DadesPersonals::unitatOrganica)
                bindSelected(sollicitant)
            }
            form {
                fieldset("Dades personals") {
                    field("DNI") {
                        textfield(sollicitant.nif) {
                            action {
                                findDocentByNIF(this.text)
                            }
                        }

                    }
                    field("Nom") {
                        textfield(sollicitant.nom)
                    }
                    field("Email") {
                        textfield(sollicitant.email)
                    }
                    field("Càrrec") {
                        textfield(sollicitant.carrec)
                    }
                    field("Unitat orgànica") {
                        textfield(sollicitant.unitatOrganica)
                    }
                }
                hbox {
                    button("Afegeix") {
                        setOnAction {
                            sollicitant.commit()
                            sollicitants.add(sollicitant.item)
                        }
                    }
                    button("Reset") {
                        setOnAction {
                           sollicitants.clear()
                        }
                    }
//                    button("Desa") {
//                        setOnAction {
//                            Notifications.create()
//                                .title("")
//                                .text("")
//                                .owner(this@DadesPersonalsView)
//                                .showInformation()
//                        }
//                    }
                }
            }

        }
    }

    /* Aquest mètode a de retornar el cos i l'insitut d'aquest docent */
    private fun findDocentByNIF(nif: String) {
    }

    override fun onSave() {
//        isComplete = model.item.sollicitants.size > 0
    }
}
