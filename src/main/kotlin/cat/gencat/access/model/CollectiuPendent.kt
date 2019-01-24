package cat.gencat.access.model

data class CollectiuPendent(
        val nif: String,
        val tractament: String,
        val nom: String,
        val cognom1: String,
        val cognom2: String,
        val familia: String,
        val especialitat: String,
        val email: String,
        val sexe: String,
        val centre: String,
        val municipi: String,
        val delegacio: String,
        val telefon: String) {
}