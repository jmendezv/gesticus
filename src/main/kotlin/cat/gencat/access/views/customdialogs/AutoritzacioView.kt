package cat.gencat.access.views.customdialogs

import tornadofx.*

/*
*
* NOTE: USE A WIZARD
*
* */
class AutoritzacioView : View("Autorització d'una ordre de serveis per al personal al servei de l'Administració i de la despesa corresponent.") {
    override val root = vbox(3.0) {
        hbox(3.0) {
            tableview(items = observableList<String>()) {  }
            form {

            }

        }
    }
}
