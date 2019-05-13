package cat.gencat.access.model

import java.time.LocalDate

/*
*
* SELECT forteco_cursos_t.codi AS [codi_curs],
* forteco_cursos_t.nom AS [nom_curs],
* forteco_cursos_t.empresa AS [nom_empresa],
* forteco_cursos_t.data_inici AS [data_inici],
* forteco_cursos_t.data_final AS [data_final],
* forteco_docents_t.nif AS [nif_docent],
* professors_t.noms as [noms_docent],
* professors_t.email AS [email_docent],
* professors_t.cos AS [cos_docent],
* professors_t.centre AS [nom_centre],
* professors_t.delegacio_territorial AS [nom_delegacio],
* professors_t.especialitat AS [nom_especialitat]
* */
data class FortecoBean(val codiCurs: String,
                       val nomCurs: String,
                       val nomEmpresa: String,
                       val dataInici: LocalDate,
                       val dataFinal: LocalDate,
                       val nifDocent: String,
                       val nomDocent: String,
                       val emailDocent: String,
                       val cosDocent: String,
                       val nomCentre: String,
                       val nomDelegacio: String,
                       val nomEspecialitat: String) {
}