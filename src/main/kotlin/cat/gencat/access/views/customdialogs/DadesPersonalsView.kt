package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.AutoritzacioViewModel
import cat.gencat.access.model.DadesPersonals
import cat.gencat.access.model.DadesPersonalsViewModel
import javafx.beans.property.SimpleObjectProperty
import org.controlsfx.control.Notifications
import tornadofx.*

class DadesPersonalsView : View("Dades personals") {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()
    val element = DadesPersonalsViewModel()

    override val root = vbox(3.0) {
        hbox(3.0) {
            tableview(items = model.sollicitants.value) {
                column("NIF", DadesPersonals::nif)
                column("Nom", DadesPersonals::nom)
                column("Email", DadesPersonals::email)
                column("Carrec", DadesPersonals::carrec)
                column("Unitat orgànica", DadesPersonals::unitatOrganica)
                bindSelected(element)
            }
            form {
                fieldset("Dades personals") {
                    field("DNI") {
                        textfield(element.nif)
                    }
                    field("Nom") {
                        textfield(element.nom)
                    }
                    field("Email") {
                        textfield(element.email)
                    }
                    field("Càrrec") {
                        textfield(element.carrec)
                    }
                    field("Unitat orgànica") {
                        textfield(element.unitatOrganica)
                    }
                }
                hbox {
                    button("Afegeix") {
                        setOnAction {
                           element.commit()
                            model.sollicitants.value.add(element.item)
                        }
                    }
                    button("Actualitza") {
                        setOnAction {
                            Notifications.create()
                                .title("")
                                .text("")
                                .owner(this@DadesPersonalsView)
                                .showInformation()
                        }
                    }
                    button("Neteja") {
                        setOnAction {
                            Notifications.create()
                                .title("")
                                .text("")
                                .owner(this@DadesPersonalsView)
                                .showInformation()
                        }
                    }
                    button("Desa") {
                        setOnAction {
                            Notifications.create()
                                .title("")
                                .text("")
                                .owner(this@DadesPersonalsView)
                                .showInformation()
                        }
                    }
                }
            }

        }
    }

    override fun onSave() {
//        isComplete = model.item.sollicitants.size > 0
    }
}
