package cat.gencat.access.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class EstadaSearch(codi: String,
                   nomEmpresa: String,
                   curs: String,
                   dataInici: String,
                   dataFinal: String,
                   nomDocent: String,
                   nifDocent: String,
                   emailDocent: String) {

    val codiProperty = SimpleStringProperty(codi)
    var codi by codiProperty

    val nomEmpresaProperty = SimpleStringProperty(nomEmpresa)
    var nomEmpresa by nomEmpresaProperty

    val cursProperty = SimpleStringProperty(curs)
    var curs by cursProperty

    val dataIniciProperty = SimpleStringProperty(dataInici.substring(0, 10))
    var dataInici by dataIniciProperty

    val dataFinalProperty = SimpleStringProperty(dataFinal.substring(0, 10))
    var dataFinal by dataFinalProperty

    val nomDocentProperty = SimpleStringProperty(nomDocent)
    var nomDocent by nomDocentProperty

    val nifDocentProperty = SimpleStringProperty(nifDocent)
    var nifDocent by nifDocentProperty

    val emailDocentProperty = SimpleStringProperty(emailDocent)
    var emailDocent by emailDocentProperty

}

class EstadaSearchModel : ItemViewModel<EstadaSearch>() {
    val codi = bind(EstadaSearch::codiProperty)
    val nomEmpresa = bind(EstadaSearch::nomEmpresaProperty)
    val curs = bind(EstadaSearch::cursProperty)
    val dataInici = bind(EstadaSearch::dataIniciProperty)
    val dataFinal = bind(EstadaSearch::dataFinalProperty)
    val nomDocent = bind(EstadaSearch::nomDocentProperty)
    val nifDocent = bind(EstadaSearch::nifDocentProperty)
    val emailDocent = bind(EstadaSearch::emailDocentProperty)
}
