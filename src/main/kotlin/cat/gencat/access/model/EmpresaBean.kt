package cat.gencat.access.model

data class EmpresaBean(val id: String,
                       val nif: String,
                       val nom: String,
                       val direccio: String,
                       val cp: String,
                       val municipi: String,
                       val telefon: String,
                       val email: String,
                       val pcTracte: String,
                       val pcNom: String,
                       val pcCarrec: String,
                       val pcTelefon: String) {
}