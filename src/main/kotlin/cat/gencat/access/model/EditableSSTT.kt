package cat.gencat.access.model

import javafx.beans.property.Property
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class EditableSSTT(codi: String? = null, nom: String? = null, correu1: String? = null, correu2: String? = null) {

    val codiProperty = SimpleStringProperty(this, "codi", codi)
    var codi: String by codiProperty

    val nomProperty = SimpleStringProperty(this, "nom", nom)
    var nom by nomProperty

    val correu1Property = SimpleStringProperty(this, "correu1", correu1)
    var correu1 by correu1Property

    val correu2Property = SimpleStringProperty(this, "correu2", correu2)
    var correu2 by correu2Property

    override fun toString(): String {
        return "$codi $nom $correu1 $correu2"
    }
}

class EditableSSTTModel : ItemViewModel<EditableSSTT>() {
    val codi: Property<String> = bind(EditableSSTT::codiProperty)
    val nom: Property<String> = bind(EditableSSTT::nomProperty)
    val correu1: Property<String> = bind(EditableSSTT::correu1Property)
    val correu2: Property<String> = bind(EditableSSTT::correu2Property)

    override fun toString(): String =
            "${codi.value} ${nom.value} ${correu1.value} ${correu2.value}"
}

