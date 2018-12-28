package cat.gencat.access.os

import javafx.scene.control.Alert
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class GesticusOs {

    companion object {

        /* This method copies source to destination */
        @Throws(IOException::class)
        fun copy(source: String, destination: String): Boolean {
            if (!File(source).exists()) {
                Alert(Alert.AlertType.ERROR, "El fitxer $source no existeix").showAndWait()
                return false
            }
            Files.copy(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING)
            return true
        }

        /* This method copies source to destination */
        @Throws(IOException::class)
        fun move(source: String, destination: String): Boolean {
            if (!File(source).exists()) {
                Alert(Alert.AlertType.ERROR, "El fitxer $source no existeix").showAndWait()
                return false
            }
            Files.move(Paths.get(source), Paths.get(destination), StandardCopyOption.ATOMIC_MOVE)
            return true

        }

    }

}