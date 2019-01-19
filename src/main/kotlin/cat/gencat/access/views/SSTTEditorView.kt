package com.example.demo.view

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.EditableSSTT
import cat.gencat.access.model.EditableSSTTModel
import cat.gencat.access.styles.Styles
import javafx.application.Platform
import javafx.scene.control.TextInputDialog
import javafx.scene.layout.BorderPane
import tornadofx.*
import kotlin.concurrent.thread

class SSTTEditorView : View("Serveis Territorials") {

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
                            button("Add")
                                    .action {
                                        addNewSSTT()
                                    }
                            progressbar {
                                thread {
                                    for (i in 1..100) {
                                        Platform.runLater { progress = i.toDouble() / 100.0 }
                                        Thread.sleep(100)
                                    }
                                }
                            }
                            progressindicator {
                                thread {
                                    for (i in 1..100) {
                                        Platform.runLater { progress = i.toDouble() / 100.0 }
                                        Thread.sleep(100)
                                    }
                                }
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
        //serveis.add(EdtitableSSTT())
        TextInputDialog("NIF A999999999A")
                .showAndWait()
                .ifPresent { nom ->
                    println(nom)
                }
    }

}