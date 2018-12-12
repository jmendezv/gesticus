package cat.gencat.access.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class TutorCertification {
    val horesProperty = SimpleStringProperty()
    var hores by horesProperty
    val dniProperty = SimpleStringProperty()
    var dni by dniProperty

    override fun toString() = "$dni $hores"
}
