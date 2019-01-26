package cat.gencat.access.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class TutorCertification {

    val dniProperty = SimpleStringProperty()
    var dni by dniProperty

    val horesProperty = SimpleStringProperty()
    var hores by horesProperty

    override fun toString() = "$dni $hores"
}

class TutorCertificationModel(tutor: TutorCertification) : ItemViewModel<TutorCertification>(tutor) {

    val dni = bind(autocommit = true) {
        item?.dniProperty
    }

    val hores = bind(autocommit = true) {
        item?.horesProperty
    }

}