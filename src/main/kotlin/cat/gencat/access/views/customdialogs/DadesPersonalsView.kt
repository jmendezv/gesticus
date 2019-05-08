package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.AutoritzacioViewModel
import cat.gencat.access.model.DadesPersonals
import cat.gencat.access.model.DadesPersonalsViewModel
import tornadofx.*

class DadesPersonalsView : View("Dades personals") {

    //    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()
    val model: AutoritzacioViewModel by inject()
    val sollicitants = model.sollicitants.value
    var sollicitant = DadesPersonalsViewModel()

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
                                findSollicitantsByNIF(this.text)
                            }
                        }

                    }
                    field("Nom") {
                        textfield(sollicitant.nom).required()
                    }
                    field("Email") {
                        textfield(sollicitant.email).required()
                    }
                    field("Càrrec") {
                        textfield(sollicitant.carrec).required()
                    }
                    field("Unitat orgànica") {
                        textfield(sollicitant.unitatOrganica).required()
                    }
                }
                hbox {
                    button("Afegeix") {
                        setOnAction {
                            sollicitant.commit()
                            sollicitants.add(DadesPersonals(sollicitant.item.nif, sollicitant.item.nom, sollicitant.item.email, sollicitant.item.carrec, sollicitant.item.unitatOrganica))
                            reset()
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
    private fun findSollicitantsByNIF(nif: String) {
        val reg = controller.findSollicitantByNIF(nif)
        if (reg != null) {
            sollicitant.nom.value = reg.nom
            sollicitant.email.value = reg.email
            sollicitant.carrec.value = "${reg.cos}, ${reg.centre}"
            sollicitant.unitatOrganica.value = reg.unitat
        }
    }

    private fun reset() {
        sollicitant.nif.value = ""
        sollicitant.nom.value = ""
        sollicitant.email.value = ""
        sollicitant.carrec.value = ""
        sollicitant.unitatOrganica.value = ""
    }

    override fun onSave() {
        isComplete = model.item.sollicitants.size > 0
    }
}
