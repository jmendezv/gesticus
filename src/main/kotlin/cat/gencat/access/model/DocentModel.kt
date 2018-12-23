package cat.gencat.access.model

import javafx.beans.property.Property
import tornadofx.*

class DocentModel : ItemViewModel<Docent>() {
    val name: Property<String> = bind(Docent::name)
    val title: Property<String> = bind(Docent::title)
}

