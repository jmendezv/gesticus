package cat.gencat.access.model

import cat.gencat.access.functions.initValue
import javafx.beans.property.ObjectProperty
import tornadofx.*

class TutorCertificationModel(tutor: TutorCertification)
    : ItemViewModel<TutorCertification>(tutor) {
    val hores = bind(autocommit = true) {
        item?.horesProperty
    }
    val dni = bind(autocommit = true) {
        item?.dniProperty
    }
}