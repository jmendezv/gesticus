package cat.gencat.access

import java.time.LocalDate

data class Registre(var estada: Estada? = Estada(), var empresa: Empresa? = Empresa(), var docent: Docent? = Docent(), var centre: Centre? = Centre(), var sstt: SSTT? = SSTT())

data class Estada(val numeroEstada: String = "", val codiCentre: String = "", val tipusEstada: String = "", val dataInici: LocalDate = LocalDate.now(), val dataFinal: LocalDate = LocalDate.now().plusDays(11), var descripcio: String = "", var comentaris: String = "")

data class Empresa(val identificacio: Identificacio = Identificacio(), val personaDeContacte: PersonaDeContacte = PersonaDeContacte(), val tutor: Tutor = Tutor())

data class Identificacio(val nif: String? = "", val nom: String? = "", val direccio: String? = "", val cp: String = "", val municipi: String? = "")
data class PersonaDeContacte(val nom: String? = "", val carrec: String? = "", val telefon: String? = "", val email: String? = "")
data class Tutor(val nom: String? = "", val carrec: String? = "", val telefon: String? = "", val email: String? = "")

data class Docent(val nif: String = "", val nom: String = "", val destinacio: String = "", val especialitat: String = "", val email: String = "", val telefon: String = "")

data class Centre(val codi: String = "", val nom: String = "", val municipi: String = "", val director: String? = "", val telefon: String? = "", val email: String = "")

data class SSTT(val codi: String = "", val nom: String = "", val municipi: String = "", val coordinador: String = "", val telefon: String = "", val email: String = "")

/*
* INICIAL: Donada d'alta
* ENVIADA: Lliurada a Centre, Empresa i SSTT si és tipus B
* BAIXA: Causa baixa voluntària. Cal donar accés al seguent de la llista si n'hi ha
* ACABADA: Ha acabat el període de l'estada en empresa. Encara ha de lliurar la documentació
* FALTA_MEMORIA: No ha lliurat la memòria o la memòria no ha estat acceptada. L'ha de repetir
* REGISTRAT: Registrat al GTAF
* TANCADA: Tancada al GTAF
*
* */
enum class SeguimentEstats {
    INICIAL, ENVIADA, BAIXA, ACABADA, FALTA_MEMORIA, REGISTRADA, TANCADA
}
