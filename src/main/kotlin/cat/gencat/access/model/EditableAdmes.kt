package cat.gencat.access.model

import javafx.beans.property.Property
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class EditableAdmes(nif: String? = null, nom: String? = null, email: String? = null) {

    val nifProperty = SimpleStringProperty(this, "codi", nif)
    var nif: String by nifProperty

    val nomProperty = SimpleStringProperty(this, "nom", nom)
    var nom by nomProperty

    val emailProperty = SimpleStringProperty(this, "correu1", email)
    var email by emailProperty

    override fun toString(): String {
        return "$nif $nom $email"
    }
}

class EditableAdmesModel : ItemViewModel<EditableAdmes>() {
    val nif: Property<String> = bind(EditableAdmes::nifProperty)
    val nom: Property<String> = bind(EditableAdmes::nomProperty)
    val email: Property<String> = bind(EditableAdmes::emailProperty)

    override fun toString(): String =
            "${nif.value} ${nom.value} ${email.value}"
}

