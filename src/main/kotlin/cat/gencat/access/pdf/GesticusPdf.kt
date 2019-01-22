package cat.gencat.access.pdf

import cat.gencat.access.db.*
import cat.gencat.access.functions.*
import javafx.scene.control.Alert
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.*
import java.io.File
import java.io.IOException
import java.time.LocalDate

/* Singleton */
class GesticusPdf {

    /* Aquest map conte tots els valors introduïts en una sol·licitud */
    private val pdfMap = mutableMapOf<String, String>()

    val gesticusDb = GesticusDb()

    /* Create a map with all this info */
    private fun loadNonTerminalFields(field: PDNonTerminalField) {

        field.children.forEach {
            when (it) {
                is PDNonTerminalField -> loadNonTerminalFields(it)
                is PDTextField -> {
                    pdfMap[it.fullyQualifiedName] = it.value
                    //println("${field.fullyQualifiedName} -> ${pdfMap[field.fullyQualifiedName]}")
                }
                is PDChoice -> {
                    pdfMap[it.fullyQualifiedName] = it.richTextValue
                    //println("${field.fullyQualifiedName} -> ${pdfMap[field.fullyQualifiedName]}")
                }
                is PDCheckBox -> {
                    pdfMap[it.fullyQualifiedName] = it.value
                    //println("${field.fullyQualifiedName} -> ${pdfMap[field.fullyQualifiedName]}")
                }
                is PDRadioButton -> {
                    pdfMap[it.fullyQualifiedName] = it.value
                    //println("${field.fullyQualifiedName} -> ${pdfMap[field.fullyQualifiedName]}")
                }
            }
        }
    }

    /* This method loads all fields from a pdf file into a map pdfMap */
    @Throws(IOException::class)
    private fun createMapFromPdf(file: File): Boolean {

        val doc = PDDocument.load(file)
        val catalog = doc.documentCatalog
        // val pdMetadata = catalog.getMetadata()
        val form: PDAcroForm = catalog.acroForm ?: return false

        val fields: MutableList<PDField>? = form.fields

        //System.out.println("Fields: ${fields?.size}")

        pdfMap.clear()

        fields?.apply {
            for (field in this) {
                when (field) {
                    is PDNonTerminalField -> loadNonTerminalFields(field)
                    is PDTextField -> {
                        pdfMap[field.fullyQualifiedName] = field.value
                        //println("${field.fullyQualifiedName} -> ${pdfMap[field.fullyQualifiedName]}")
                    }
                    is PDChoice -> {
                        pdfMap[field.getFullyQualifiedName()] = field.richTextValue
                        //println("${field.fullyQualifiedName} -> ${pdfMap[field.fullyQualifiedName]}")
                    }
                    is PDCheckBox -> {
                        pdfMap[field.getFullyQualifiedName()] = field.getValue()
                        //println("${field.fullyQualifiedName} -> ${pdfMap[field.fullyQualifiedName]}")
                    }
                    is PDRadioButton -> {
                        pdfMap[field.fullyQualifiedName] = field.value
                        //println("${field.fullyQualifiedName} -> ${pdfMap[field.fullyQualifiedName]}")
                    }
                    else -> {
                        pdfMap[field.fullyQualifiedName] = field.valueAsString
                        //println("${field.fullyQualifiedName} -> ${pdfMap[field.fullyQualifiedName]}")

                    }
                }
            }
        }

        doc.close()

        return true
    }

    /*
    *
    * This method looks for a file named after a nifFilename, the first one if there are many
    *
    * It might be useful at the beginning but eventualy there might be more than one file
    *
    * nifFilename is a nif amb format 099999999A*.pdf
    *
    * */
    fun createMapFromPdf(nifFilename: String): Boolean {

        val files: List<String> =
                File(PATH_TO_FORMS + currentCourseYear()).list().filter {
                    it.contains("${nifFilename.substring(0, 10)}")
                }.toList()

        if (files.isNotEmpty()) {
            if (files.size > 1) {
                Alert(Alert.AlertType.ERROR, "Hi ha més d'un fitxer amb el nom $nifFilename").showAndWait()
            }
            val file: File = File(PATH_TO_FORMS + currentCourseYear(), files[0])
            return createMapFromPdf(file)
        }

        return false
    }

    private fun createEmpresaAndEstadaTipusAFromMap(nextEstadaNumber: String): Pair<Estada, Empresa> {

        val estada =
                try {
                    val id = nextEstadaNumber
                    val sector = pdfMap[FORM_A_FIELD_SECTOR_EMPRESA] ?: "not informat"
                    val tipus = pdfMap[FORM_A_FIELD_TIPUS_EMPRESA] ?: "no informat"
                    val inici = parseDate(pdfMap[FORM_A_FIELD_DATA_INICI_ESTADA] ?: "")
                    val fi = parseDate(pdfMap[FORM_A_FIELD_DATA_FI_ESTADA] ?: "")
                    val descripcio = "Aquesta estada es fa al sector ${sector}, de tipus ${tipus}"
                    val comentaris = when (pdfMap[FORM_A_FIELD_FP_DUAL_ESTADA]) {
                        "Opción1" -> "Aquesta estada es fa en alternança"
                        "Opción2" -> "Aquesta estada no es fa en alternança"
                        else -> ""
                    }
                    Estada(id, pdfMap[FORM_A_FIELD_CODI_CENTRE_ESTADA]
                            ?: "0", "A", inici, fi, descripcio, comentaris)
                } catch (error: Exception) {
                    // error.printStackTrace()
                    Alert(Alert.AlertType.INFORMATION, error.message).show()
                    Estada("", "", "B", LocalDate.now(), LocalDate.now().plusWeeks(2), "", "")
                }

        val empresa =
                try {

                    val identficacio = Identificacio(
                            pdfMap[FORM_A_FIELD_NIF_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_NOM_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_DIRECCIO_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_CP_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_MUNICIPI_EMPRESA]!!)

                    val personaDeContacte = PersonaDeContacte(
                            pdfMap[FORM_A_FIELD_NOM_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_CARREC_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_TELEFON_PERSONA_DE_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_EMAIL_EMPRESA]!!)

                    // L'email del tutor no està documentat, escric el de la persona de contacte
                    val tutor = Tutor(
                            pdfMap[FORM_A_FIELD_NOM_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_CARREC_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_TELEFON_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_EMAIL_EMPRESA]!!)

                    Empresa(identficacio, personaDeContacte, tutor)
                } catch (error: Exception) {
                    Empresa(Identificacio("", "", "", "", ""),
                            PersonaDeContacte("", "", "", ""),
                            Tutor("", "", "", ""))
                }

        return Pair(estada, empresa)

    }

    private fun createEmpresaAndEstadaTipusBFromMap(nextEstadaNumber: String): Pair<Estada, Empresa> {

        val estada =
                try {
                    val id = nextEstadaNumber
                    val sector = pdfMap[FORM_B_FIELD_SECTOR_EMPRESA] ?: "not informat"
                    val tipus = pdfMap[FORM_B_FIELD_TIPUS_EMPRESA] ?: "no informat"
                    val inici = parseDate(pdfMap[FORM_B_FIELD_DATA_INICI_ESTADA] ?: "")
                    val fi = parseDate(pdfMap[FORM_B_FIELD_DATA_FI_ESTADA] ?: "")
                    val descripcio = "Aquesta estada es fa al sector ${sector}, de tipus ${tipus}"
                    val comentaris = when (pdfMap[FORM_B_FIELD_FP_DUAL_ESTADA]) {
                        "Opción1" -> "Aquesta estada es fa en alternança"
                        "Opción2" -> "Aquesta estada no es fa en alternança"
                        else -> ""
                    }
                    Estada(id, pdfMap[FORM_B_FIELD_CODI_CENTRE_ESTADA]
                            ?: "0", "B", inici, fi, descripcio, comentaris)
                } catch (error: Exception) {
                    // error.printStackTrace()
                    Alert(Alert.AlertType.INFORMATION, error.message).show()
                    Estada("", "", "B", LocalDate.now(), LocalDate.now().plusWeeks(2), "", "")
                }

        val empresa =
                try {

                    val identficacio = Identificacio(
                            pdfMap[FORM_B_FIELD_NIF_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_NOM_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_DIRECCIO_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_CP_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_MUNICIPI_EMPRESA]!!)

                    val personaDeContacte = PersonaDeContacte(
                            pdfMap[FORM_B_FIELD_NOM_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_CARREC_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_TELEFON_PERSONA_DE_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_EMAIL_EMPRESA]!!)

                    // L'email del tutor no està documentat, escric el de la persona de contacte
                    val tutor = Tutor(
                            pdfMap[FORM_B_FIELD_NOM_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_CARREC_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_TELEFON_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_EMAIL_EMPRESA]!!)

                    Empresa(identficacio, personaDeContacte, tutor)
                } catch (error: Exception) {
                    Empresa(Identificacio("", "", "", "", ""),
                            PersonaDeContacte("", "", "", ""),
                            Tutor("", "", "", ""))
                }

        return Pair(estada, empresa)
    }

    /*
    * This method return a pair of estada and empresa with data form pdf form or empty
    * */
    private fun createEmpresaAndEstadaFromMap(tipusEstada: String): Pair<Estada, Empresa> {

        val nextEstadaNumber: String = gesticusDb.getNextEstadaNumber()

        /*  */

        when (tipusEstada) {
            "A" -> {return createEmpresaAndEstadaTipusAFromMap(nextEstadaNumber)}
            else -> {return createEmpresaAndEstadaTipusBFromMap(nextEstadaNumber)}
        }

    }

    /*
    *
    * This method returns a pair of Estada and Empresa if form was successfully read
    *
    * */
    private fun loadEmpresaAndEstadaFromPdf(nif: String, tipusEstada: String): Pair<Estada, Empresa> {
        createMapFromPdf(nif)
        //printMap()
        return createEmpresaAndEstadaFromMap(tipusEstada)
    }

    /*
    * This method gets docent, centre and sstt from registres and
    * estada, empresa form pdf form
    *
    * NIF sempre en format 099999999A
    * */
    fun loadDataByDocentIdFromPdf(nif: String, tipusEstada: String): Registre? {

        val registres = gesticusDb.registres

        //println(registres.size)

        val pair: Pair<Estada, Empresa>? = loadEmpresaAndEstadaFromPdf(nif, tipusEstada)

        registres.forEach {

            if (it.docent?.nif == nif) {
                pdfMap.put("codi_centre", it.centre?.codi ?: "")
                it.estada = pair?.first
                it.empresa = pair?.second
                return it
            }
        }
        return Registre(pair?.first, pair?.second)

    }

    fun parsePdf(file: File, tipusEstada: String): Pair<Estada, Empresa>? {
        if (createMapFromPdf(file))
            return createEmpresaAndEstadaFromMap(tipusEstada)
        else
            return Estada() to Empresa()
    }

    fun getRegistreFromPdf(file: File, tipusEstada: String): Registre? {

        if (createMapFromPdf(file)) {
            // Del pdf agafo estada, empresa
            val empresaEstada = parsePdf(file, tipusEstada)
//            println(empresaEstada?.first ?: "empresa is null")
//            println(empresaEstada?.second ?: "estada is null")
            // Amb el nif busco docent, centre i sstt de la bd
            val nif = pdfMap[FORM_B_FIELD_NIF_DOCENT]
//            println(nif)
            val registre = gesticusDb.findRegistreByNifDocent(nif)
//            println(registre)
            registre?.estada = empresaEstada?.first ?: Estada()
            registre?.empresa = empresaEstada?.second ?: Empresa()
            return registre
        }
        return null
    }

    /*
   * This method gets a record from registres, where estada and empresa are null
   * */
    fun readDataByDocentIdFromDb(nif: String, tipusEstada: String): Registre? {

        val registres = gesticusDb.registres

        val pair: Pair<Estada, Empresa>? = loadEmpresaAndEstadaFromPdf(nif, tipusEstada)

        registres.forEach {
            if (it.docent?.nif == nif) {
                pdfMap.put("codi_centre", it.centre?.codi ?: "")
                it.estada = pair?.first
                it.empresa = pair?.second
                return it
            }
        }
        return Registre(pair?.first, pair?.second)
    }

    private fun printMap() {
        pdfMap.keys.forEach {
            println("'$it' -> '${pdfMap[it]}'")
        }
    }

}