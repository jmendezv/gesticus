package cat.gencat.access.views

import cat.gencat.access.model.StudentModel
import cat.gencat.access.styles.GesticusStyles.Companion.zip
import org.controlsfx.control.Notifications
import tornadofx.*

class CustomerForm : View("Register Customer") {
    val model : StudentModel by inject()

    override val root = form {
        fieldset("Personal Information") {
            field("Name") {
                textfield(model.name).required()
            }

            field("Birthday") {
                datepicker(model.birthday)
            }
        }

        fieldset("Address") {
            field("Street") {
                textfield(model.street).required()
            }
            field("Zip / City") {
                textfield(model.zip) {
                    addClass(zip)
                    required()
                }
                textfield(model.city).required()
            }
        }

        hbox {
            button("Save") {
                action {
                    model.commit {
                        val customer = model.item
                        Notifications.create()
                                .title("Customer saved!")
                                .text("${customer.name} was born ${customer.birthday}\nand lives in\n${customer.street}, ${customer.zip} ${customer.city}")
                                .owner(this)
                                .showInformation()
                    }
                }

                enableWhen(model.valid)
            }
            button("Cancel") {
                action {
                    model.rollback()
                }

            }
        }


    }

}

