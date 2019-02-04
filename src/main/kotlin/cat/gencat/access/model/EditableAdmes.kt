package cat.gencat.access.model

import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class EditableAdmes(nif: String? = null, nom: String? = null, email: String? = null, curs: String? = null, baixa: Boolean = false) {

    val nifProperty = SimpleStringProperty(this, "codi", nif)
    var nif: String by nifProperty

    val nomProperty = SimpleStringProperty(this, "nom", nom)
    var nom by nomProperty

    val emailProperty = SimpleStringProperty(this, "correu1", email)
    var email by emailProperty

    val cursProperty = SimpleStringProperty(this, "curs", curs)
    var curs by cursProperty

    val baixaProperty = SimpleBooleanProperty(this, "baixa", baixa)
    var baixa by baixaProperty

    override fun toString(): String {
        return "$nif $nom $email $curs $baixa"
    }
}

class EditableAdmesModel : ItemViewModel<EditableAdmes>() {
    val nif: Property<String> = bind(EditableAdmes::nifProperty)
    val nom: Property<String> = bind(EditableAdmes::nomProperty)
    val email: Property<String> = bind(EditableAdmes::emailProperty)
    val curs: Property<String> = bind(EditableAdmes::cursProperty)
    val baixa: Property<Boolean> = bind(EditableAdmes::baixaProperty)

    override fun toString(): String =
            "${nif.value} ${nom.value} ${email.value}"
}

