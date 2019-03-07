package com.example.demo.view

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.model.EditableSSTT
import cat.gencat.access.model.EditableSSTTModel
import cat.gencat.access.styles.Styles
import javafx.scene.control.Alert
import javafx.scene.layout.BorderPane
import tornadofx.*

class SSTTEditorView : View(Utils.APP_TITLE) {

    val controller: GesticusController by inject()

    val serveis =
            controller.getServeisTerritorials()

    val model = EditableSSTTModel()

    override val root = BorderPane()

    init {
        with(root) {
            center {
                tableview(serveis.observable()) {
                    // getter -> read only field
                    column("Codi", EditableSSTT::codi)
                    column("Nom", EditableSSTT::nom)
                    // property -> editable field
                    column("Email 1", EditableSSTT::correu1Property)
                    column("Email 2", EditableSSTT::correu2Property)
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
                            textfield(model.correu1)
                        }
                        field("RRHH") {
                            textfield(model.correu2)
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
//                            button("Add") {
//                                enableWhen(model.dirty)
//                                action {
//                                    addNewSSTT()
//                                }
//                            }
//                            progressbar {
//                                thread {
//                                    for (i in 1..100) {
//                                        Platform.runLater { progress = i.toDouble() / 100.0 }
//                                        Thread.sleep(100)
//                                    }
//                                }
//                            }
//                            progressindicator {
//                                thread {
//                                    for (i in 1..100) {
//                                        Platform.runLater { progress = i.toDouble() / 100.0 }
//                                        Thread.sleep(100)
//                                    }
//                                }
//                            }
                        }
                    }
                }
            }
        }
    }

    private fun save() {
        // Flush changes from the text fields into the model
        model.commit()
        val result = controller.updateSSTT(EditableSSTT(
                model.codi.value,
                model.nom.value,
                model.correu1.value,
                model.correu2.value
        ))
        val msg = if (result)
            "El registre s'ha actualitzat correctament"
        else
            "No s'ha pogut actualitzar el registre"
        Alert(Alert.AlertType.INFORMATION, msg).show()
//        serveis.remove(model.item)
//        serveis.add(model.item)
//        serveis.asyncItems { controller.getServeisTerritorials() }
//        println("Saving ${model}")
    }

    private fun addNewSSTT() {
        model.commit()
        //serveis.add(EdtitableSSTT())
    }

}