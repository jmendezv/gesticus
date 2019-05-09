package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.AutoritzacioViewModel
import javafx.scene.control.ToggleGroup
import tornadofx.*

class DadesDesplaçamentView : View("Dades desplaçament") {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()

    private val mitjaDeTransportToggleGroup = ToggleGroup()

    init {
        mitjaDeTransportToggleGroup.bind(model.mitjaTransport)
    }

    override val root = form {
        fieldset("Dades del desplaçament") {
            hbox(5.0) {
                field("Origen") {
                    textfield(model.origen).required()
                }
                field("Destinació") {
                    textfield(model.destinacio) {
                        prefColumnCount = 30
                        required()
                    }
                }
            }
            field("Motiu del desplaçament") {
                textfield(model.motiuDesplaçament).required()
            }
            hbox(5.0) {
                field("Data anada") {
                    datepicker(model.dataAnada).required()
                }
                field("Hora anada") {
                    textfield(model.horaAnada).required()
                }
            }
            hbox(5.0) {
                field("Data tornada") {
                    datepicker(model.dataTornada).required()
                }
                field("Hora tornada") {
                    textfield(model.horaTornada).required()
                }
            }
        }
        fieldset("Mitjà de transport") {
            hbox(5.0) {
                field {
                    radiobutton("Avió", mitjaDeTransportToggleGroup) {
                        isSelected = true
                    }
                }
                field {
                    radiobutton("Tren", mitjaDeTransportToggleGroup) {
                    }
                }
                field {
                    radiobutton("Altres", mitjaDeTransportToggleGroup) {
                    }
                }
                field {
                    textfield(model.mitjaTransportAltresComentaris) {
                        //                        enableWhen(model.mitjaTransportAltres)
                        prefColumnCount = 40
                        required()
                    }
                }
            }
        }
        fieldset("Altres") {
            hbox(5.0) {
                field("Allotjament") {
                    textfield(model.allotjament)
                }
                field {
                    checkbox("Reserva a càrrec de la Secció d'Habilitació i Indemnitzacions", model.reserva)
                }
            }
        }
        fieldset("Despeses d'inscripció") {
            hbox(5.0) {
                field("Creditor") {
                    textfield(model.creditor)
                }
                field("Import") {
                    textfield(model.import)
                }
            }
        }
    }

    override fun onSave() {
        super.onSave()
        model.commit()
    }


}
