package cat.gencat.access.views.customdialogs

import tornadofx.*

class DadesPersonalsView : View("My View") {
    override val root = vbox(3.0) {
        hbox(3.0) {
            tableview(items = observableList<String>()) { }
            form {

            }

        }
    }
}
