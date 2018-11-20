package cat.gencat.access

import java.time.LocalDate

data class Registre(var estada: Estada?, var empresa: Empresa?, var docent: Docent?, var centre: Centre?, var sstt: SSTT?)

data class Estada(val numeroEstada: String, val codiCentre: String, val tipusEstada: String, val dataInici: LocalDate, val dataFinal: LocalDate, var descripcio: String, var comentaris: String)

data class Empresa(val identificacio: Identificacio, val personaDeContacte: PersonaDeContacte, val tutor: Tutor)

data class Identificacio(val nif: String?, val nom: String?, val direccio: String?, val cp: String?, val municipi: String?)
data class PersonaDeContacte(val nom: String?, val carrec: String?, val telefon: String?, val email: String?)
data class Tutor(val nom: String?, val carrec: String?, val telefon: String?, val email: String?)

data class Docent(val nif: String, val nom: String, val destinacio: String, val especialitat: String, val email: String, val telefon: String)

data class Centre(val codi: String, val nom: String, val municipi: String, val director: String?, val telefon: String?, val email: String)

data class SSTT(val codi: String, val nom: String, val municipi: String, val coordinador: String, val telefon: String, val email: String)

