package cat.gencat.access.views

import cat.gencat.access.model.Docent
import cat.gencat.access.model.DocentModel
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane
import tornadofx.*

class AdmesosEditor : View("My View") {

    override val root = BorderPane()


    // val docents: ObservableList<Docent> = listOf(Docent("John", "Manager"), Docent("Jay", "Worker bee")).observable()

    // val docents =  arrayListOf<Docent>(Docent("John", "Manager"), Docent("Jay", "Worker bee")).observable()

    val docents: ObservableList<Docent> = FXCollections.observableArrayList<Docent>(Docent("John", "Manager"), Docent("Jay", "Worker bee"))


    val docentModel: DocentModel by inject()

    init {

        with(root) {

            bottom {
//                dialog {
//
//                }
            }

//            center {
//
//                tableview(docents) {
//
//                    column("Name", Docent::nameProperty)
//
//                    column("Title", Docent::titleProperty)
//
//// Update the person inside the view docentModel on selection change
//
//                    docentModel.rebindOnChange(this) { selectedDocent ->
//
//                        // val docent = selectedDocent ?: Docent()
//
//                    }
//
//                }
//
//            }

            center {

                form {
                    fieldset("Edit person") {
                        field("Name") {
                            textfield(docentModel.name)
                        }
                        field("Title") {
                            textfield(docentModel.title)
                        }
                        hbox {
                            button("Save") {
                                enableWhen(docentModel.dirty)
                                action { save() }
                            }
                            button("Reset").action {
                                docentModel.rollback()
                            }

                        }
                    }

                }
            }

        }

    }

    private fun save() {

// Flush changes from the text fields into the docentModel

        //docentModel.commit()

// The edited person is contained in the docentModel

        val docent = docentModel

        runLater {
            // docents.setAll(docentModel.item)
            println("Saving ${docent.name.value} / ${docent.title.value}")
        }


// A real application would persist the person here


    }
}