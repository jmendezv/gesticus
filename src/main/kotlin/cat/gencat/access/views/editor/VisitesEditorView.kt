package cat.gencat.access.views.editor

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.functions.Utils
import cat.gencat.access.functions.Utils.Companion.APP_TITLE
import cat.gencat.access.model.Visita
import cat.gencat.access.model.VisitaModel
import cat.gencat.access.styles.GesticusStyles
import javafx.application.Platform
import javafx.scene.control.ButtonType
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import tornadofx.*
import java.time.LocalDate


/*
*
* visites_t(id*, estades_codi**, curs, tipus (de seguiment, de captació), data, hora, comentaris)
* */
class VisitesEditorView : View(Utils.APP_TITLE + ": Visites") {

    val controller: GesticusController by inject()

    var visites: MutableList<Visita> =
            controller.getVisites()

    //    val model: VisitaModel by inject()
    val model: VisitaModel = VisitaModel()

    override val root = BorderPane()

    init {
        if (visites.isEmpty()) {
            Platform.runLater {
                Utils.infoNotification(Utils.APP_TITLE, "La taula és buida.")
            }
        }
        with(root) {
            addClass(GesticusStyles.visites)
            center {
                tableview(visites.observable()) {
                    column("Codi", Visita::estadesCodi)
                    column("Curs", Visita::curs)
                    column("Tipus", Visita::tipus)
                    column("Data", Visita::data)
                    column("Hora", Visita::hora)
                    column("Comentaris", Visita::comentaris)
                    enableCellEditing()
                    regainFocusAfterEdit()
                    bindSelected(model)
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    prefWidth = 775.0
                    prefHeight = 450.0
                    vboxConstraints {
                        vGrow = Priority.ALWAYS
                    }
                    onEditCommit {
                        // updateVisita()
                    }
                    columnResizePolicy = SmartResize.POLICY
                }
            }

            right {
                form {
                    fieldset("Edit admes") {
                        field("Codi") {
                            textfield(model.estadesCodi) {
                                //isEditable = false
                                //addClass(Styles.readOnlytextField)
                            }
                        }
                        field("Curs") {
                            textfield(model.curs) {
                                //isEditable = false
                                //addClass(Styles.readOnlytextField)
                            }
                        }
                        field("Tipus") {
                            combobox(model.tipus, listOf("Seguiment", "Gestió"))
                        }
                        field("Data") {
                            datepicker(model.data) {
                                value = LocalDate.now()
                            }
                        }
                        field("Hora") {
                            textfield(model.hora)
                        }
                        field("Comentaris") {
                            textfield(model.comentaris)
                        }
                        hbox(10.0) {
                            button("Save") {
                                enableWhen(model.dirty)
                                action {
                                    updateVisita()
                                }
                            }
                            button("Reset")
                                    .action {
                                        model.rollback()
                                    }
                            button("Add") {
                                enableWhen(model.dirty)
                                action {
                                    addVisita()
                                }
                            }
                            button("Informe") {
                                action {
                                    generaInforme()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateVisita() {

        confirmation("Vols actualitzar el registre actual?") {
            when {
                it == ButtonType.OK -> {
                    // Flush changes from the text fields into the model
                    model.commit()
                    val result = controller.updateVisita(model.item)
                    val msg = if (result) {
                        visites = controller.getVisites()
                        "El registre s'ha actualitzat correctament"
                    } else {
                        "No s'ha pogut actualitzar el registre"
                    }
                    information(APP_TITLE, msg)
                }
            }
        }

    }

    private fun addVisita() {
        model.commit()
        val result: Boolean = controller.saveVisita(model.item)
        visites.add(model.item)
        val msg = if (result)
            "El registre s'ha actualitzat correctament"
        else
            "No s'ha pogut actualitzar el registre"
        information(APP_TITLE, msg)
    }

    private fun generaInforme() {
        controller.generaInformeVisites()
    }

}