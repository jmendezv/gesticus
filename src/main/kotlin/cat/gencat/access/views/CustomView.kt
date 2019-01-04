package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.model.EditableSSTT
import cat.gencat.access.model.EditableSSTTModel
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import tornadofx.*
import java.time.LocalDate
import java.time.Period


class CustomView : View() {

    val sstt by inject<EditableSSTTModel>()

    val controller by inject<GesticusController>()

    override val root =
        tableview(FXCollections.observableArrayList(controller.findAllEditableSSTT())) {
        column("Codi", EditableSSTT::codi)
        column("Nom", EditableSSTT::nom)
        column("Coordinador", EditableSSTT::correu1)
        column("Personal", EditableSSTT::correu2)
        columnResizePolicy = SmartResize.POLICY
    }

}

