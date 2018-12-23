package cat.gencat.access.model

import javafx.beans.property.SimpleStringProperty

import tornadofx.*

class Docent(name: String? = null, title: String? = null) {

    val nameProperty = SimpleStringProperty(this, "name", name)

    var name: String by nameProperty

    val titleProperty = SimpleStringProperty(this, "title", title)

    var title: String by titleProperty

}