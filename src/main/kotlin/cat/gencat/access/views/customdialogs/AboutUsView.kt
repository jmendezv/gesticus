package cat.gencat.access.views.customdialogs

import cat.gencat.access.functions.Utils
import javafx.geometry.Pos
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AboutUsView : View(Utils.APP_TITLE + ": Sobre nosaltres") {
    override val root = vbox(15.0,Pos.CENTER) {
        label("")
        label("  Gestió d'Estades Formatives Tipus B  ")
        label("  Formació Contínua del Professorat d'FP  ")
        label("  Direcció General d'Ordenació de Formació Professional  ")
        label("  Inicial i Ensenyaments de Règim Especial  ")
        label("  Departament d'Educació  ")
        label("")
        label("  Darrera actualització ${LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}  ")
        label("")
    }
}
