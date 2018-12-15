package cat.gencat.access.views

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import tornadofx.*
import java.time.LocalDate
import java.time.Period

data class Person(var id: Int, var name: String, var birthday: LocalDate) {
    var age: Int = 0
        get() = Period.between(birthday, LocalDate.now()).years
}

//class Person(id: Int, name: String, birthday: LocalDate) {
//    val idProperty = SimpleIntegerProperty(id)
//    var id by idProperty
//
//    val nameProperty = SimpleStringProperty(name)
//    var name by nameProperty
//
//    val birthdayProperty = SimpleObjectProperty(birthday)
//    var birthday by birthdayProperty
//
//    var age: Int = 0
//        get() = Period.between(birthday, LocalDate.now()).years
//}

class CustomView : View() {

    override val root = tableview(persons) {
        column("Id", Person::id)
        column("Name", Person::name)
        column("Birthday", Person::birthday)
        column("Age", Person::age).cellFormat {
            text = it.toString()
            style {
                if (it < 18) {
                    backgroundColor += c("#8b0000")
                    textFill = Color.WHITE
                }
            }
        }
        columnResizePolicy = SmartResize.POLICY
    }

    companion object {
        @JvmStatic
        private val persons: ObservableList<Person> = FXCollections.observableArrayList(
                Person(1, "Samantha Stuart", LocalDate.of(1981, 12, 4)),
                Person(2, "Tom Marks", LocalDate.of(2001, 1, 23)),
                Person(3, "Stuart Gills", LocalDate.of(1989, 5, 23)),
                Person(3, "Nicole Williams", LocalDate.of(1998, 8, 11))
        )
    }
}

//class CustomView : View("My View") {
//    override val root = hbox {
//
//    }
//}
