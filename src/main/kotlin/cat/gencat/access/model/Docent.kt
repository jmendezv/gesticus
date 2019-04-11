package cat.gencat.access.model

import javafx.beans.property.Property
import javafx.beans.property.SimpleStringProperty

import tornadofx.*

class Docent(name: String? = null, title: String? = null) {

    val nameProperty = SimpleStringProperty(this, "name", name)

    var name: String by nameProperty

    val titleProperty = SimpleStringProperty(this, "title", title)

    var title: String by titleProperty

}


class DocentModel : ItemViewModel<Docent>() {
    val name: Property<String> = bind(Docent::name)
    val title: Property<String> = bind(Docent::title)
}

