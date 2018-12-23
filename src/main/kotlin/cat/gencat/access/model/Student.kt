package cat.gencat.access.model

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.time.LocalDate

class Student(name: String? = null, birthday: LocalDate? = null, street: String? = null, zip: String? = null, city: String? = null) {

    val nameProperty = SimpleStringProperty(this, "name", name)
    var name by nameProperty

    val birthdayProperty = SimpleObjectProperty<LocalDate>(this, "birthday", birthday)
    var birthday by birthdayProperty

    val streetProperty = SimpleStringProperty(this, "street", street)
    var street by streetProperty

    val zipProperty = SimpleStringProperty(this, "zip", zip)
    var zip by zipProperty

    val cityProperty = SimpleStringProperty(this, "city", city)
    var city by cityProperty

    override fun toString(): String {
        return name
    }
}

class StudentModel : ItemViewModel<Student>(Student()) {
    val name = bind(Student::nameProperty)
    val birthday = bind(Student::birthdayProperty)
    val street = bind(Student::streetProperty)
    val zip = bind(Student::zipProperty)
    val city = bind(Student::cityProperty)
}

