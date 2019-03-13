package cat.gencat.access.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

/*
   * "SELECT
   * estades_t.codi AS estades_codi,
   * estades_t.nom_empresa AS estades_nom_empresa,
   * estades_t.direccio_empresa AS estades_direccio_empresa,
   * estades_t.codi_postal_empresa AS estades_codi_postal_empresa,
   * estades_t.municipi_empresa AS estades_municipi_empresa,
   * estades_t.contacte_nom AS estades_contacte_nom,
   * estades_t.contacte_carrec AS estades_contacte_carrec,
   * estades_t.contacte_telefon as estades_contacte_telefon,
   * estades_t.contacte_email AS estades_contacte_email,
   * estades_t.data_inici AS estades_data_inici,
   * estades_t.data_final AS estades_data_final,
   * estades_t.nif_professor AS estades_nif_professor,
   * professors_t.tractament AS professors_tractament,
   * professors_t.nom AS professors_nom,
   * professors_t.cognom_1 AS professors_cognom1,
   * professors_t.cognom_2 AS professors_cognom2 ,
   * professors_t.sexe AS professors_sexe,
   * professors_t.email AS professors_email,
   * professors_t.telefon AS professors_telefon,
   * professors_t.especialitat AS professors_especialitat,
   * professors_t.familia AS professors_familia,
   * professors_t.centre AS professors_centre,
   * professors_t.municipi AS professors_municipi,
   * professors_t.delegacio_territorial AS professors_delegacio_territorial
   * FROM estades_t INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif
   * WHERE (((estades_t.data_inici)<=Date()) AND ((estades_t.data_final)>=Date()) AND ((estades_t.curs)= ?));"
   *
   * */
class EstadaEnCurs(codi: String,
                   nomEmpresa: String,
                   direccioEmpresa: String,
                   codiPostalEmpresa: String,
                   municipiEmpresa: String,
                   contacteNom: String,
                   contacteCarrec: String,
                   contacteTelefon: String,
                   contacteEmail: String,
                   dataInici: String,
                   dataFinal: String,
                   nifProfessor: String,
                   tractamentProfessor: String,
                   nomProfessor: String,
                   cognom1Professor: String,
                   cognom2Professor: String,
                   sexeProfessor: String,
                   emailProfessor: String,
                   telefonProfessor: String?,
                   especialitatProfessor: String,
                   familiaProfessor: String,
                   centreProfessor: String,
                   municipiProfessor: String,
                   delegacioTerritorialProfessor: String) {

    val delegacioTerritorialProfessorProperty = SimpleStringProperty(delegacioTerritorialProfessor)
    var delegacioTerritorialProfessor by delegacioTerritorialProfessorProperty
    val municipiProfessorProperty = SimpleStringProperty(municipiProfessor)
    var municipiProfessor by municipiProfessorProperty
    val centreProfessorProperty = SimpleStringProperty(centreProfessor)
    var centreProfessor by centreProfessorProperty
    val familiaProfessorProperty = SimpleStringProperty(familiaProfessor)
    var familiaProfessor by familiaProfessorProperty
    val especialitatProfessorProperty = SimpleStringProperty(especialitatProfessor)
    var especialitatProfessor by especialitatProfessorProperty
    val telefonProfessorProperty = SimpleStringProperty(telefonProfessor)
    var telefonProfessor by telefonProfessorProperty
    val emailProfessorProperty = SimpleStringProperty(emailProfessor)
    var emailProfessor by emailProfessorProperty
    val sexeProfessorProperty = SimpleStringProperty(sexeProfessor)
    var sexeProfessor by sexeProfessorProperty
    val cognom2ProfessorProperty = SimpleStringProperty(cognom2Professor)
    var cognom2Professor by cognom2ProfessorProperty
    val cognom1ProfessorProperty = SimpleStringProperty(cognom1Professor)
    var cognom1Professor by cognom1ProfessorProperty
    val nomProfessorProperty = SimpleStringProperty(nomProfessor)
    var nomProfessor by nomProfessorProperty
    val tractamentProfessorProperty = SimpleStringProperty(tractamentProfessor)
    var tractamentProfessor by tractamentProfessorProperty
    val nifProfessorProperty = SimpleStringProperty(nifProfessor)
    var nifProfessor by nifProfessorProperty
    val dataFinalProperty = SimpleStringProperty(dataFinal)
    var dataFinal by dataFinalProperty
    val dataIniciProperty = SimpleStringProperty(dataInici)
    var dataInici by dataIniciProperty
    val contacteEmailProperty = SimpleStringProperty(contacteEmail)
    var contacteEmail by contacteEmailProperty
    val contacteTelefonProperty = SimpleStringProperty(contacteTelefon)
    var contacteTelefon by contacteTelefonProperty
    val contacteCarrecProperty = SimpleStringProperty(contacteCarrec)
    var contacteCarrec by contacteCarrecProperty
    val contacteNomProperty = SimpleStringProperty(contacteNom)
    var contacteNom by contacteNomProperty
    val municipiEmpresaProperty = SimpleStringProperty(municipiEmpresa)
    var municipiEmpresa by municipiEmpresaProperty
    val codiPostalEmpresaProperty = SimpleStringProperty(codiPostalEmpresa)
    var codiPostalEmpresa by codiPostalEmpresaProperty
    val direccioEmpresaProperty = SimpleStringProperty(direccioEmpresa)
    var direccioEmpresa by direccioEmpresaProperty
    val nomEmpresaProperty = SimpleStringProperty(nomEmpresa)
    var nomEmpresa by nomEmpresaProperty
    val codiProperty = SimpleStringProperty(codi)
    var codi by codiProperty
}

class EstadaEnCursModel : ItemViewModel<EstadaEnCurs>() {
    val delegacioTerritorialProfessor = bind(EstadaEnCurs::delegacioTerritorialProfessorProperty)
    val municipiProfessor = bind(EstadaEnCurs::municipiProfessorProperty)
    val centreProfessor = bind(EstadaEnCurs::centreProfessorProperty)
    val familiaProfessor = bind(EstadaEnCurs::familiaProfessorProperty)
    val especialitatProfessor = bind(EstadaEnCurs::especialitatProfessorProperty)
    val telefonProfessor = bind(EstadaEnCurs::telefonProfessorProperty)
    val emailProfessor = bind(EstadaEnCurs::emailProfessorProperty)
    val sexeProfessor = bind(EstadaEnCurs::sexeProfessorProperty)
    val cognom2Professor = bind(EstadaEnCurs::cognom2ProfessorProperty)
    val cognom1Professor = bind(EstadaEnCurs::cognom1ProfessorProperty)
    val nomProfessor = bind(EstadaEnCurs::nomProfessorProperty)
    val tractamentProfessor = bind(EstadaEnCurs::tractamentProfessorProperty)
    val nifProfessor = bind(EstadaEnCurs::nifProfessorProperty)
    val dataFinal = bind(EstadaEnCurs::dataFinalProperty)
    val dataInici = bind(EstadaEnCurs::dataIniciProperty)
    val contacteEmail = bind(EstadaEnCurs::contacteEmailProperty)
    val contacteTelefon = bind(EstadaEnCurs::contacteTelefonProperty)
    val contacteCarrec = bind(EstadaEnCurs::contacteCarrecProperty)
    val contacteNom = bind(EstadaEnCurs::contacteNomProperty)
    val municipiEmpresa = bind(EstadaEnCurs::municipiEmpresaProperty)
    val codiPostalEmpresa = bind(EstadaEnCurs::codiPostalEmpresaProperty)
    val direccioEmpresa = bind(EstadaEnCurs::direccioEmpresaProperty)
    val nomEmpresa = bind(EstadaEnCurs::nomEmpresaProperty)
    val codi = bind(EstadaEnCurs::codiProperty)
}

