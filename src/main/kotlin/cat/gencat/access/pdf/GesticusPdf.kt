package cat.gencat.access.pdf


import cat.gencat.access.db.*
import cat.gencat.access.functions.*
import cat.gencat.access.functions.Utils.Companion.currentCourseYear
import cat.gencat.access.functions.Utils.Companion.errorNotification
import cat.gencat.access.functions.Utils.Companion.parseDate
import cat.gencat.access.functions.Utils.Companion.toCatalanDateFormat
import cat.gencat.access.functions.Utils.Companion.warningNotification
import cat.gencat.access.model.Autoritzacio
import javafx.scene.control.Alert
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.*
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.util.*


/* Singleton */
object GesticusPdf {

    /* Aquest map conte tots els valors introduïts en una sol·licitud */
    private val pdfMap = mutableMapOf<String, String>()

    val gesticusDb = GesticusDb

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

    /* This method loads all fields from a pdf file into pdfMap */
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
    * This method looks for a file named after a nifFilename, the first one if there are many
    * It might be useful at the beginning but eventualy there might be more than one file
    * nifFilename is a nif amb format 099999999A*.pdf
    * */
    fun createMapFromPdf(nifFilename: String): Boolean {

        val files: List<String> =
                File(PATH_TO_FORMS + currentCourseYear()).list().filter {
                    it.contains("${nifFilename.substring(0, 10)}")
                }.toList()

        if (files.isNotEmpty()) {
            if (files.size > 1) {
                warningNotification(
                        Utils.APP_TITLE,
                        "Hi ha més d'un fitxer amb el nom $nifFilename, es mostra el primer"
                )
            }
            val file: File = File(PATH_TO_FORMS + currentCourseYear(), files[0])
            return createMapFromPdf(file)
        } else {
            errorNotification(Utils.APP_TITLE, "No es troba el fitxer $nifFilename")
        }
        return false
    }

    /*
    * Crea una parell Estada i Empresa relativa a una estada tipus A
    * */
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
                        else -> "Aquesta estada no esta documentat si es fa en alternança o no"
                    }
                    Estada(
                            id, pdfMap[FORM_A_FIELD_CODI_CENTRE_ESTADA]
                            ?: "0", "A", inici, fi, descripcio, comentaris
                    )
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
                            pdfMap[FORM_A_FIELD_MUNICIPI_EMPRESA]!!
                    )

                    val personaDeContacte = PersonaDeContacte(
                            pdfMap[FORM_A_FIELD_NOM_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_CARREC_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_TELEFON_PERSONA_DE_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_EMAIL_EMPRESA]!!
                    )

                    // L'email del tutor no està documentat, escric el de la persona de contacte
                    val tutor = Tutor(
                            pdfMap[FORM_A_FIELD_NOM_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_CARREC_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_TELEFON_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_A_FIELD_EMAIL_EMPRESA]!!
                    )

                    Empresa(identficacio, personaDeContacte, tutor)
                } catch (error: Exception) {
                    Empresa(
                            Identificacio("", "", "", "", ""),
                            PersonaDeContacte("", "", "", ""),
                            Tutor("", "", "", "")
                    )
                }

        return Pair(estada, empresa)

    }

    /*
    * Crea una parell Estada i Empresa relativa a una estada tipus B
    * */
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
                        else -> "Aquesta estada no esta documentat si es fa en alternança o no"
                    }
                    Estada(
                            id, pdfMap[FORM_B_FIELD_CODI_CENTRE_ESTADA]
                            ?: "0", "B", inici, fi, descripcio, comentaris
                    )
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
                            pdfMap[FORM_B_FIELD_MUNICIPI_EMPRESA]!!
                    )

                    val personaDeContacte = PersonaDeContacte(
                            pdfMap[FORM_B_FIELD_NOM_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_CARREC_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_TELEFON_PERSONA_DE_CONTACTE_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_EMAIL_EMPRESA]!!
                    )

                    // L'email del tutor no està documentat, escric el de la persona de contacte
                    val tutor = Tutor(
                            pdfMap[FORM_B_FIELD_NOM_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_CARREC_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_TELEFON_TUTOR_EMPRESA]!!,
                            pdfMap[FORM_B_FIELD_EMAIL_EMPRESA]!!
                    )

                    Empresa(identficacio, personaDeContacte, tutor)
                } catch (error: Exception) {
                    Empresa(
                            Identificacio("", "", "", "", ""),
                            PersonaDeContacte("", "", "", ""),
                            Tutor("", "", "", "")
                    )
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
            "A" -> {
                return createEmpresaAndEstadaTipusAFromMap(nextEstadaNumber)
            }
            else -> {
                return createEmpresaAndEstadaTipusBFromMap(nextEstadaNumber)
            }
        }

    }

    /*
    * This method returns a pair of Estada and Empresa if form was successfully read
    * */
    private fun loadEmpresaAndEstadaFromPdf(nif: String, tipusEstada: String): Pair<Estada, Empresa> {
        createMapFromPdf(nif)
        //printMap()
        return createEmpresaAndEstadaFromMap(tipusEstada)
    }

    /*
    * This method gets docent, centre and sstt from registres and estada, empresa form pdf form
    * NIF sempre en format 099999999A
    * */
    fun loadDataByDocentIdFromPdf(nif: String, tipusEstada: String): Registre? {

        val registres = gesticusDb.registres

        //println(registres.size)

        val pair: Pair<Estada, Empresa>? = loadEmpresaAndEstadaFromPdf(nif, tipusEstada)

        registres.forEach {

            if (it.docent?.nif == nif) {
                // Ho hem trobat tot
                pdfMap.put("codi_centre", it.centre?.codi ?: "")
                it.estada = pair?.first
                it.empresa = pair?.second
                return it
            }
        }
        // Nomes tenim Estada i Empresa
        return Registre(pair?.first, pair?.second)

    }

    /*
    * Retorna un par Estada i Empresa con o sin datos
    * */
    fun parsePdf(file: File, tipusEstada: String): Pair<Estada, Empresa>? {
        if (createMapFromPdf(file))
            return createEmpresaAndEstadaFromMap(tipusEstada)
        else
            return Estada() to Empresa()
    }

    /*
    * Retorna un Registre a partir d'un fitxer que representat una estada de tipus A o B
    * */
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

    /*
    * Aquest mètodes escriu  el nom del camp i el seu valor recursivament quan no es terminal
    * */
    fun printNonTerminalFields(field: PDField) {

        when (field) {
            is PDNonTerminalField -> {
                field.children.forEach {
                    printNonTerminalFields(it)
                }
            }
            is PDTextField -> {
                println("PDTextField ${field.fullyQualifiedName} -> ${field.value}")
            }
            is PDChoice -> {
                println("PDChoice ${field.fullyQualifiedName} -> ${field.value}")
            }
            is PDCheckBox -> {
                println("PDCheckBox ${field.fullyQualifiedName} -> ${field.value}")
            }
            is PDRadioButton -> {
                println("PDRadioButton ${field.fullyQualifiedName} -> ${field.value}")
            }
            else -> {
                println("Altre ${field.fullyQualifiedName} -> ${field.valueAsString}")

            }
        }
    }

    /*
    * Create a map with all this info
    * */
    fun printStructure(file: File) {

        val doc = PDDocument.load(file)
        val catalog = doc.documentCatalog
        // val pdMetadata = catalog.getMetadata()
        val form: PDAcroForm = catalog.acroForm ?: return

        val fields: MutableList<PDField>? = form.fields

        System.out.println("Fields: ${fields?.size}")

        fields?.forEach {
            printNonTerminalFields(it)
        }

        doc.close()
    }

    /*
    * This method will create a pdf file for each sol·licitant
    * */
    fun creaSollicitudsDespesaPdf(form: File, autoritzacio: Autoritzacio, whereToCopy: String) {

        val doc = PDDocument.load(form)
        doc.isAllSecurityToBeRemoved = true
        val catalog = doc.documentCatalog
        // val pdMetadata = catalog.getMetadata()
        val form: PDAcroForm = catalog.acroForm ?: return
        // val fields: MutableList<PDField>? = form.fields

        form.getField("Destinació.0.0").setValue(autoritzacio.origen)
        form.getField("Destinació.0.1").setValue(autoritzacio.destinacio)
        form.getField("Motiu").setValue(autoritzacio.motiuDesplaçament)
        form.getField("Dia.0").setValue(autoritzacio.dataAnada.dayOfMonth.toString())
        form.getField("Mes.0").setValue(autoritzacio.dataAnada.monthValue.toString())
        form.getField("Any.0").setValue(autoritzacio.dataTornada.year.toString())
        form.getField("Dia.1").setValue(autoritzacio.dataTornada.dayOfMonth.toString())
        form.getField("Mes.1").setValue(autoritzacio.dataTornada.monthValue.toString())
        form.getField("Any.1").setValue(autoritzacio.dataTornada.year.toString())
        form.getField("Horari.0").setValue(autoritzacio.horaAnada)
        form.getField("Horari.1").setValue(autoritzacio.horaTornada)
        form.getField("Creditor.0").setValue(autoritzacio.creditor)
        form.getField("Creditor.1").setValue(autoritzacio.import)
        form.getField("Creditor.1").setValue(autoritzacio.import)
        form.getField("undefined_2.0").setValue(autoritzacio.finançamentExternDescripcio)
        form.getField("undefined_2.1").setValue(autoritzacio.bestretaAltresDescripcio)
        form.getField("Lloc i data.1").setValue(Date().toCatalanDateFormat())
        form.getField("Nom i cognoms1.0").setValue(autoritzacio.nomResponsable)
        form.getField("Càrrec1.0").setValue(autoritzacio.carrecResponsable)
        if (autoritzacio.mitjaTransportAvio)
            (form.getField("Group2") as PDRadioButton).value = "Opción1"
        else  if (autoritzacio.mitjaTransportTren)
            (form.getField("Group2") as PDRadioButton).value = "Opción2"
        else  if (autoritzacio.mitjaTransportAltres) {
            (form.getField("Group2") as PDRadioButton).value = "Opción3"
            form.getField("undefined").setValue(autoritzacio.mitjaTransportAltresComentaris)
        }

        // TODO("Finish up")

        autoritzacio.sollicitants.forEach {
            form.getField("DNI.0.0").setValue(it.nif)
            form.getField("Nom i cognoms").setValue(it.nom)
            form.getField("Unitat orgànica.1").setValue(it.email)
            form.getField("Unitat orgànica.0").setValue(it.unitatOrganica)
            form.getField("Càrrec").setValue(it.carrec)
            doc.save("$whereToCopy\\${it.nif}.pdf")
        }

        doc.close()
    }

    private fun printMap() {
        pdfMap.keys.forEach {
            println("'$it' -> '${pdfMap[it]}'")
        }
    }

}

/*
Fields: 27
PDTextField DNI.0.0 -> 000000000
PDTextField Nom i cognoms -> aaaaaaaaaaaaaaaa
PDTextField Unitat orgànica.1 -> aaaaa@aaaa
PDTextField Unitat orgànica.0 -> unitat
PDTextField Càrrec -> carrec
PDTextField Destinació.0.0 -> orign
PDTextField Destinació.0.1 -> destinaico
PDTextField Motiu -> motiu
PDTextField Dia.0 -> 12
PDTextField Dia.1 -> 20
PDTextField Mes.0 -> 02
PDTextField Mes.1 -> 03
PDTextField Any.0 -> 2019
PDTextField Any.1 -> 2019
PDTextField Horari.0 -> 19:20
PDTextField Horari.1 -> 20:33

PDRadioButton Group2 -> Opción2
PDTextField undefined -> alrres comentaris
PDTextField Mitjà de transport -> allotjament
PDRadioButton gropu4 -> Opción1
PDTextField Creditor.0 -> credit
PDTextField Creditor.1 -> 1233
PDRadioButton Group1 -> Opción2
PDTextField undefined_2.0 -> descripcio dinancçament
PDTextField undefined_2.1 -> altres desbreta
PDCheckBox Check Box1 -> Off
PDCheckBox Check Box2 -> Off
PDCheckBox Check Box3 -> Off
PDCheckBox Check Box4 -> Sí
PDTextField Lloc i data.1 ->
PDTextField Lloc i data.0 ->
Altre Signature1 ->
PDTextField Nom i cognoms1.0 -> don juan espinor
PDTextField Nom i cognoms1.1 ->
PDTextField Càrrec1.0 -> dire
PDTextField Càrrec1.1 ->
Altre Signature2 ->
PDCheckBox Check Box5 -> Off
 */