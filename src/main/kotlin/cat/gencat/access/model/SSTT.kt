package cat.gencat.access.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class SSTT(codi: String = "", nom: String = "", email1: String = "", email2: String = "") {
    val codiProperty = SimpleStringProperty(codi)
    var codi by codiProperty
    val nomProperty = SimpleStringProperty(nom)
    var nom by nomProperty
    val email1Property = SimpleStringProperty(email1)
    var email1 by email1Property
    val email2Property = SimpleStringProperty(email2)
    var email2 by email2Property
}

class SSTTModel : ItemViewModel<SSTT>() {
    val codi = bind(SSTT::codiProperty)
    val nom = bind(SSTT::nomProperty)
    val email1 = bind(SSTT::email1Property)
    val email2 = bind(SSTT::email2Property)

    override fun toString(): String =
            "${codi.value} ${nom.value} ${email1.value} ${email2.value}"
}

