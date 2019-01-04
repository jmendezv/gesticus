package cat.gencat.access.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class EditableSSTT(codi: String? = null, nom: String? = null, correu1: String? = null, correu2: String? = null) {

    val codiProperty = SimpleStringProperty(this, "codi", codi)
    var codi by codiProperty

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
    val codi = bind(EditableSSTT::codiProperty)
    val nom = bind(EditableSSTT::nomProperty)
    val correu1 = bind(EditableSSTT::correu1Property)
    val correu2 = bind(EditableSSTT::correu2Property)
}

