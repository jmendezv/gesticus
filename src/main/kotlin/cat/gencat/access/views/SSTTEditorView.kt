package cat.gencat.access.views

import cat.gencat.access.controllers.CustomController
import cat.gencat.access.model.SSTT
import cat.gencat.access.model.SSTTModel
import cat.gencat.access.styles.Styles
import javafx.scene.control.TextInputDialog
import javafx.scene.layout.BorderPane
import tornadofx.*

class SSTTEditorView : View("Serveis Territorials") {

    val controller: CustomController by inject()

    val serveis =
            controller.getServeisTerritorials()

    val model = SSTTModel()

    override val root = BorderPane()

    init {
        with(root) {
            center {
                tableview(serveis) {
                    // getter -> read only field
                    column("Codi", SSTT::codi)
                    column("Nom", SSTT::nom)
                    // property -> editable field
                    column("Email 1", SSTT::email1Property)
                    column("Email 2", SSTT::email2Property)
                    bindSelected(model)
                }
            }

            right {
                form {
                    fieldset("Edit person") {
                        field("Codi") {
                            textfield(model.codi) {
                                isEditable = false
                                addClass(Styles.readOnlytextField)
                            }
                        }
                        field("Nom") {
                            textfield(model.nom) {
                                isEditable = false
                                addClass(Styles.readOnlytextField)
                            }
                        }
                        field("Coordinador") {
                            textfield(model.email1)
                        }
                        field("RRHH") {
                            textfield(model.email2)
                        }
                        hbox(10.0) {
                            button("Save") {
                                enableWhen(model.dirty)
                                action {
                                    save()
                                }
                            }
                            button("Reset")
                                    .action {
                                        model.rollback()
                                    }
                            button("Add")
                                    .action {
                                        addNewSSTT()
                                    }
                        }
                    }
                }
            }
        }
    }

    private fun save() {
        // Flush changes from the text fields into the model
        model.commit()
//        serveis.remove(model.item)
//        serveis.add(model.item)
//        serveis.asyncItems { controller.getServeisTerritorials() }
        println("Saving ${model}")
    }

    private fun addNewSSTT() {
        //serveis.add(SSTT())
        TextInputDialog("NIF A999999999A")
                .showAndWait()
                .ifPresent { nom ->
                    println(nom)
                }
    }

}