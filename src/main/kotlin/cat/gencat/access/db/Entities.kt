package cat.gencat.access.db

import java.time.LocalDate

data class Registre(var estada: Estada? = Estada(), var empresa: Empresa? = Empresa(), var docent: Docent? = Docent(), var centre: Centre? = Centre(), var sstt: SSTT? = SSTT())

data class Estada(val numeroEstada: String = "", val codiCentre: String = "", val tipusEstada: String = "", val dataInici: LocalDate = LocalDate.now(), val dataFinal: LocalDate = LocalDate.now().plusDays(11), var descripcio: String = "", var comentaris: String = "")

data class Empresa(val identificacio: Identificacio = Identificacio(), val personaDeContacte: PersonaDeContacte = PersonaDeContacte(), val tutor: Tutor = Tutor())

data class Identificacio(val nif: String? = "", val nom: String? = "", val direccio: String? = "", val cp: String = "", val municipi: String? = "")
data class PersonaDeContacte(val nom: String? = "", val carrec: String? = "", val telefon: String? = "", val email: String? = "")
data class Tutor(val nom: String? = "", val carrec: String? = "", val telefon: String? = "", val email: String? = "")

data class Docent(val nif: String = "", val nom: String = "", val destinacio: String = "", val especialitat: String = "", val email: String = "", val telefon: String = "")

data class Centre(val codi: String = "", val nom: String = "", val direccio: String = "", val cp: String = "", val municipi: String = "", val director: String? = "", val telefon: String? = "", val email: String = "")

data class SSTT(val codi: String = "", val nom: String = "", val municipi: String = "", val coordinador: String = "", val telefon: String = "", val emailCSPD: String = "", val emailCRHD: String = "")

/*
* REGISTRADA: Donada d'alta al Gesticus.
* COMUNICADA: Lliurada carta al docent, centre, empresa i si és de tipus B també a EditableSSTT
* INICIADA: Ha començat a anar a l'empresa.
* ACABADA: Ha acabat el període de l'estada en empresa. Comunicar que ha de lliurar la documentació abans d'un mes per tal de procedir la tancament.
* DOCUMENTADA: Ha lliurat el certificat d'empresa, la memòria i el full d'avaluació: Comunicar que hem rebut la documetació i que procedim al tancament administratiu de l'estada.
* TANCADA: Registrada al GTAF i tancada. Comunicar que en breu tindrà reconegut el certificat al XTEC.
* BAIXA: Causa baixa voluntària. Compte potser existeix o no existeix una estada com a tal. Habitualment no existira una estada. Comunicar que procedim a la gestió de la baixa voluntària. Per tal de donar accés al següent de la llista si n'hi ha cal copiar-lo de llista_espera_t a admesos_t, eliminar-lo de llista_espera_t i notificar-li que ha estat admès ...
* */
enum class EstatsSeguimentEstadaEnum {
    REGISTRADA, COMUNICADA, INICIADA, ACABADA, DOCUMENTADA, TANCADA, BAIXA
}
