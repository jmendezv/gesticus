package com.example.demo.view

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.functions.icon
import cat.gencat.access.model.EditableAdmes
import cat.gencat.access.model.EditableAdmesModel
import cat.gencat.access.model.EditableSSTT
import cat.gencat.access.model.EditableSSTTModel
import cat.gencat.access.styles.Styles
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.TextInputDialog
import javafx.scene.layout.BorderPane
import tornadofx.*
import kotlin.concurrent.thread

class AdmesosEditorView : View(APP_TITLE) {

    val controller: GesticusController by inject()

    val serveis =
            controller.getAdmesos()

    val model = EditableAdmesModel()

    override val root = BorderPane()

    init {
        with(root) {
            center {
                tableview(serveis.observable()) {
                    // getter -> read only field
                    column("Codi", EditableAdmes::nif)
                    column("Nom", EditableAdmes::nom)
                    // property -> editable field
                    column("Email", EditableAdmes::emailProperty)
                    bindSelected(model)
                }
            }

            right {
                form {
                    fieldset("Edit admes") {
                        field("NIF") {
                            textfield(model.nif) {
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
                        field("Email") {
                            textfield(model.email)
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
//                            button("Cadastro").icon(FontAwesomeIcon.EURO)
//                            button("").icon(MaterialDesignIcon.AIRPLANE, 60.0)
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
        val result = controller.updateAdmesos(EditableAdmes(
                model.nif.value,
                model.nom.value,
                model.email.value
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