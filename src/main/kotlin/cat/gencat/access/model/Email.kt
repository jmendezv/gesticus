package cat.gencat.access.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class Email() {

    val deProperty = SimpleStringProperty()
    var de by deProperty

    val peraProperty = SimpleStringProperty()
    var pera by peraProperty

    val motiuProperty = SimpleStringProperty()
    var motiu by motiuProperty

    val cosProperty = SimpleStringProperty()
    var cos by cosProperty

    override fun toString(): String {
        return "$de $pera $motiu $cos"
    }
}

class EmailModel : ItemViewModel<Email>(Email()) {
    val de = bind(Email::deProperty)
    val pera = bind(Email::peraProperty)
    val motiu = bind(Email::motiuProperty)
    val cos = bind(Email::cosProperty)
}
