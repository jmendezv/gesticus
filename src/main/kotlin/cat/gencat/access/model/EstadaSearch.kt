package cat.gencat.access.model

import javafx.beans.property.SimpleStringProperty
import tornadofx.getValue
import tornadofx.setValue

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