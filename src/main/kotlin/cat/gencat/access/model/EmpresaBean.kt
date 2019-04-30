package cat.gencat.access.model

import javafx.beans.property.Property
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

/* This object is used in ImpresesView */
class EmpresaBean(
    id: Int = 0,
    nif: String = "",
    nom: String = "",
    direccio: String = "",
    cp: String = "",
    municipi: String = "",
    telefon: String = "",
    email: String = "",
    pcTracte: String = "",
    pcNom: String = "",
    pcCarrec: String = "",
    pcTelefon: String = ""
) {
    val pcTelefonProperty = SimpleStringProperty(pcTelefon)
    var pcTelefon by pcTelefonProperty
    val pcCarrecProperty = SimpleStringProperty(pcCarrec)
    var pcCarrec by pcCarrecProperty
    val pcNomProperty = SimpleStringProperty(pcNom)
    var pcNom by pcNomProperty
    val pcTracteProperty = SimpleStringProperty(pcTracte)
    var pcTracte by pcTracteProperty
    val emailProperty = SimpleStringProperty(email)
    var email by emailProperty
    val telefonProperty = SimpleStringProperty(telefon)
    var telefon by telefonProperty
    val municipiProperty = SimpleStringProperty(municipi)
    var municipi by municipiProperty
    val cpProperty = SimpleStringProperty(cp)
    var cp by cpProperty
    val direccioProperty = SimpleStringProperty(direccio)
    var direccio by direccioProperty
    val nomProperty = SimpleStringProperty(nom)
    var nom by nomProperty
    val nifProperty = SimpleStringProperty(nif)
    var nif by nifProperty
    val idProperty = SimpleIntegerProperty(id)
    var id by idProperty

    //    val seguimentsProperty = SimpleObjectProperty<List<EmpresaSeguimentBean>>()
    lateinit var seguiments: List<EmpresaSeguimentBean>

}

class EmpresaBeanModel : ItemViewModel<EmpresaBean>() {
    val id = bind(EmpresaBean::idProperty)
    val nif = bind(EmpresaBean::nifProperty)
    val nom = bind(EmpresaBean::nomProperty)
    val direccio = bind(EmpresaBean::direccioProperty)
    val cp = bind(EmpresaBean::cpProperty)
    val municipi = bind(EmpresaBean::municipiProperty)
    val telefon = bind(EmpresaBean::telefonProperty)
    val email = bind(EmpresaBean::emailProperty)
    val pcTracte = bind(EmpresaBean::pcTracteProperty)
    val pcNom = bind(EmpresaBean::pcNomProperty)
    val pcCarrec = bind(EmpresaBean::pcCarrecProperty)
    val pcTelefon = bind(EmpresaBean::pcTelefonProperty)
}

