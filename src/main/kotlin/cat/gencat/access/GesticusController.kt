package cat.gencat.access

import javafx.fxml.FXML
import javafx.fxml.Initializable
import tornadofx.*
import java.net.URL
import java.util.*
import kotlin.system.exitProcess

class GesticusController: Controller() {

    val gesticusDb: GesticusDb = GesticusDb()

    fun menuTanca() {
        gesticusDb.close()
        exitProcess(0)
    }

    fun preLoadData(): Unit {
        gesticusDb.preLoadData()
    }

    fun findDocentById(nif: String): Registre? {

        return gesticusDb.findDocentById(nif)

    }


}