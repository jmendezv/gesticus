package cat.gencat.access.views.customdialogs

import cat.gencat.access.functions.Utils
import cat.gencat.access.styles.GesticusStyles
import javafx.scene.image.Image
import tornadofx.*

class HelpView : View(Utils.APP_TITLE + ": Ajuda") {
    override val root = borderpane {
        addClass(GesticusStyles.help_label)
        top = label("")
        center = vbox {
            add(imageview(Image(resources.get("/help/images/gesticus-gui-menu.png"))))
            add(label("(Database) -> Cerca estada | Seguiment | Nova | Obre PDF | Estat | Tanca"))
            add(label("(Edit) -> Ademsos | SSTT | Visites"))
            add(label("(Comunicats) -> Informes | Correus"))
            add(label("(Notificacions) -> Pendents | Manca Documentació | Col.lectius"))
            add(label("(Estadístiques) -> Progrés | Comparatives gestionades | Comparatives no gestionades | Llistats estades pendents | Llistats estades fetes"))
            add(label("(Formació) -> FORTECO"))
            add(label("(Eines) -> Preferències | Llistat General | Obre PDF | CSV Documentades | CSV Google Maps | Barema"))
            add(label("(Ajuda) -> Ús | Sobre nosaltres"))
            add(imageview(Image(resources.get("/help/images/gesticus-gui-toolbar.png"))))
            add(label("(1)  Cerca estada per codi d'estada/NIF"))
            add(label("(2)  Cerca estada per qualsevol camp"))
            add(label("(3)  Cerca estada en curs per qualsevol camp"))
            add(label("(4)  Seguiment de l'estada actual"))
            add(label("(5)  Obre sol·licitud de tipus B"))
            add(label("(6)  Comunica l'estada actual a totes le entitat implicades: docent, centre, empresa i SSTT"))
            add(label("(7)  Neteja el formulari"))
            add(label("(8)  Gràfica de pastís del progrés de totes les estades"))
            add(label("(9)  Verifica estatus, permet enviar correus a tothom que ja ha acabat i encara no ha presentat la documentació"))
            add(label("(10) Preferències"))
            add(label("(11) Sobre nosaltres"))
            add(label("(12) Sortida"))
            add(imageview(Image(resources.get("/help/images/gesticus-gui-save.png"))))
            add(label("Desa o actualitza l'estada actual"))
        }
        bottom = vbox(5.0) {
            label("")
        }
    }
}