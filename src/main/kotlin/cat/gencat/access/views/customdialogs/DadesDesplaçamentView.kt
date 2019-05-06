package cat.gencat.access.views.customdialogs

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.AutoritzacioViewModel
import tornadofx.*

class DadesDesplaçamentView : View("Dades desplaçament") {

    val model: AutoritzacioViewModel by inject()
    val controller: GesticusController by inject()

    override val root = form {
        fieldset("Dades del desplaçament") {
            hbox(5.0) {
                field("Origen") {
                    textfield(model.origen)
                }
                field("Destinació") {
                    textfield(model.destinacio)
                }
            }
            field("Motiu del desplaçament") {
                textfield(model.motiuDesplaçament)
            }
            hbox(5.0) {
                field("Data anada") {
                    datepicker(model.dataAnada)
                }
                field("Hora anada") {
                    textfield(model.horaAnada)
                }
            }
            hbox(5.0) {
                field("Data tornada") {
                    datepicker(model.dataTornada)
                }
                field("Hora tornada") {
                    textfield(model.horaTornada)
                }
            }
        }
        fieldset("Mitjà de transport") {
            hbox(5.0) {
                field {
                    checkbox("Avió", model.mitjaTransportAvio)
                }
                field {
                    checkbox("Tren", model.mitjaTransportTren)
                }
                field {
                    checkbox("Altres", model.mitjaTransportAltres)
                }
                field {
                    textfield(model.mitjaTransportAltresComentaris)
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
    }


}
