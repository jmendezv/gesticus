package cat.gencat.access.views

import cat.gencat.access.functions.APP_TITLE
import cat.gencat.access.functions.currentCourseYear
import cat.gencat.access.reports.TITLE
import javafx.geometry.Pos
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AboutUsView : View(APP_TITLE) {
    override val root = vbox(15.0,Pos.CENTER) {
        label("")
        label("  Gestió d'Estades Formatives Tipus B  ")
        label("  Formació Contínua del Professorat d'FP  ")
        label("  Direcció General d'Ordenació de la Formació Professional  ")
        label("  Inicial i d'Ensenyaments de Règim Especial  ")
        label("  Departament d'Educació  ")
        label("")
        label("  Darrera actualització ${LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}  ")
        label("")
    }
}
