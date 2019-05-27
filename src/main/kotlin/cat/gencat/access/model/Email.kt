package cat.gencat.access.model

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class Email(de: String = "",
            pera: String = "",
            territori: String = "",
            motiu: String = "",
            cos: String = "") {

    val deProperty = SimpleStringProperty(de)
    var de by deProperty

    val territoriProperty = SimpleStringProperty(territori)
    var territori by territoriProperty

    val peraProperty = SimpleStringProperty(pera)
    var pera by peraProperty

    val motiuProperty = SimpleStringProperty(motiu)
    var motiu by motiuProperty

    val cosProperty = SimpleStringProperty(cos)
    var cos by cosProperty

    override fun toString(): String {
        return "$de $pera $motiu $cos"
    }
}

class EmailModel : ItemViewModel<Email>(Email()) {
    val de = bind(Email::deProperty)
    val pera = bind(Email::peraProperty)
    val territori = bind(Email::territoriProperty)
    val motiu = bind(Email::motiuProperty)
    val cos = bind(Email::cosProperty)
}
