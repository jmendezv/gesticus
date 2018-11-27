package cat.gencat.access

import javafx.scene.control.Alert
import net.ucanaccess.jdbc.UcanaccessSQLException
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.*
import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toList

const val pathToDatabase: String = "D:\\Users\\39164789k\\Desktop\\app_estades\\gesticus.accdb"

const val pathToPdfs: String = "D:\\Users\\39164789k\\Desktop\\evalises_2018\\"

const val joinQuery: String = "SELECT professors_t.nif as [professors_nif], professors_t.noms as [professors_noms], professors_t.destinacio as [professors_destinacio], professors_t.especialitat as [professors_especialitat], professors_t.email AS [professors_email], professors_t.telefon as [professors_telefon], centres_t.C_Centre as [centres_codi], centres_t.NOM_Centre AS [centres_nom], centres_t.NOM_Municipi AS [centres_municipi], directors_t.Nom AS [directors_nom], centres_t.TELF as [centres_telefon], [nom_correu] & '@' & [@correu] AS [centres_email], sstt_t.[Codi ST] as [sstt_codi], sstt_t.SSTT AS [sstt_nom], delegacions_t.Municipi as [delegacions_municipi], delegacions_t.[coordinador 1] as [delegacions_coordinador], delegacions_t.[telf coordinador 1] as [delegacions_telefon_coordinador], sstt_t.[Correu 1] as [sstt.correu1]\n" +
        "FROM (((centres_t LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C) INNER JOIN professors_t ON centres_t.C_Centre = professors_t.c_centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[Codi ST]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació];\n"

/*
* codi, curs, nif_professor, codi_centre, nif_empresa,
* nom_empresa, direccio_empresa, codi_postal_empresa, tipus_estada, data_inici,
* data_final, contacte_nom, contacte_carrec, contacte_telefon, contacte_email,
* tutor_nom, tutor_carrec, tutor_telefon, tutor_email, descripcio,
* comentaris
* */
const val insertEstadesQuery: String = "INSERT INTO estades_t (codi, curs, nif_professor, codi_centre, nif_empresa, " +
        "nom_empresa, direccio_empresa, codi_postal_empresa, tipus_estada, data_inici, " +
        "data_final, contacte_nom, contacte_carrec, contacte_telefon, contacte_email, " +
        "tutor_nom, tutor_carrec, tutor_telefon, tutor_email, descripcio, " +
        "comentaris) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

const val insertSeguimentQuery: String = "INSERT INTO seguiment_t (codi, estat, comentaris) VALUES (?,?, ?)"


class GesticusDb {

    lateinit var conn: Connection
    val registres = ArrayList<Registre>()
    val pdfMap = mutableMapOf<String, String>()

    init {
        loadDriver()
        connect()
    }

    private fun loadDriver(): Unit {
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver")
    }

    private fun connect(): Unit {
        println("Connecting...")
        conn = DriverManager.getConnection("jdbc:ucanaccess://${pathToDatabase};memory=true;openExclusive=false;ignoreCase=true")
        println("Connected to ${conn.metaData.databaseProductName}.")
    }

    /*
    * Create a map with all this info
    * */
    private fun loadNonTerminalFields(field: PDNonTerminalField) {

        field.children.forEach {

            when (it) {
                is PDNonTerminalField -> loadNonTerminalFields(it)
                is PDTextField -> pdfMap[it.fullyQualifiedName] = it.value
                is PDChoice -> pdfMap[it.fullyQualifiedName] = it.richTextValue
                is PDCheckBox -> pdfMap[it.fullyQualifiedName] = it.value
                is PDRadioButton -> pdfMap[it.fullyQualifiedName] = it.value
            }
        }
    }

    private fun printMap() {
        pdfMap.keys.forEach {
            println("'$it' -> '${pdfMap[it]}'")
        }
    }

    @Throws(IOException::class)
    private fun loadPdfData(file: File): Boolean {

        val doc = PDDocument.load(file)
        val catalog = doc.documentCatalog
        // val pdMetadata = catalog.getMetadata()
        val form: PDAcroForm? = catalog.acroForm

        if (form == null) {
            return false
        }

        val fields: MutableList<PDField>? = form.fields

        System.out.println("Fields: ${fields?.size} Form: ${form.fields.size}")

        pdfMap.clear()

        fields?.apply {
            for (field in this) {
                when (field) {
                    is PDNonTerminalField -> loadNonTerminalFields(field)
                    is PDTextField -> pdfMap[field.fullyQualifiedName] = field.value
                    is PDChoice -> pdfMap[field.getFullyQualifiedName()] = field.richTextValue
                    is PDCheckBox -> pdfMap[field.getFullyQualifiedName()] = field.getValue()
                    is PDRadioButton -> pdfMap[field.fullyQualifiedName] = field.value
                    else -> pdfMap[field.fullyQualifiedName] = field.valueAsString
                }
            }
        }

        doc.close()

        return true
    }

    fun loadPdfData(nif: String): Boolean {

        val files: List<String> =
                File(pathToPdfs).list().filter {
                    it.contains("${nif.substring(1, 9)}")
                }.toList()

        if (files.isNotEmpty()) {
            val file: File = File(pathToPdfs, files[0])
            return loadPdfData(file)
        }

        return false
    }

    fun preLoadDataFromAccess(): Unit {
        println("Loading data, please wait...")
        val st = conn.createStatement()
        val rs = st.executeQuery(joinQuery)
        while (rs.next()) {
            val docent = Docent(
                    rs.getString("professors_nif"),
                    rs.getString("professors_noms"),
                    rs.getString("professors_destinacio"),
                    rs.getString("professors_especialitat"),
                    rs.getString("professors_email"),
                    rs.getString("professors_telefon"))
            val centre = Centre(
                    rs.getString("centres_codi"),
                    rs.getString("centres_nom"),
                    rs.getString("centres_municipi"),
                    rs.getString("directors_nom"),
                    rs.getString("centres_telefon"),
                    rs.getString("centres_email")
            )
            val sstt = SSTT(
                    rs.getString("sstt_codi"),
                    rs.getString("sstt_nom"),
                    rs.getString("delegacions_municipi"),
                    rs.getString("delegacions_coordinador"),
                    rs.getString("delegacions_telefon_coordinador"),
                    rs.getString("sstt.correu1")
            )
            registres.add(Registre(null, null, docent, centre, sstt))
        }
        println("Data loaded.")
        println("Ready.")
    }

    private fun parseDate(dataStr: String): LocalDate {

        lateinit var data: LocalDate

        if (dataStr.equals("\\d\\d/\\d\\d/[0-9]{4}")) {
            data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    ?: LocalDate.now()
        } else if (dataStr.equals("\\d\\d-\\d\\d-[0-9]{4}")) {
            data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    ?: LocalDate.now()
        } else {
            data = LocalDate.now()
        }

        return data
    }

    private fun loadEmpresaAndEstadaFromPdf(nif: String): Pair<Estada, Empresa>? {

        if (loadPdfData(nif)) {
            //printMap()
            return createEmpresaAndEstadaFromMap()
        }

        return null
    }

    private fun createEmpresaAndEstadaFromMap(): Pair<Estada, Empresa> {
        val estada =
                try {
                    val id = "0000600/2018-19"
                    val sector = pdfMap["sector.0"] ?: "No Sector"
                    val tipus = pdfMap["tipus"] ?: "No tipus"
                    val inici = parseDate(pdfMap["inici.0.0"] ?: "")
                    val fi = parseDate(pdfMap["fi"] ?: "")
                    val descripcio = "Aquesta estada es fa al sector ${sector}, de tipus ${tipus}"
                    val comentaris = when (pdfMap["Group1"]) {
                        "Opción1" -> "Aquesta estada es fa en alternança"
                        "Opción2" -> "Aquesta estada no es fa en alternança"
                        else -> ""
                    }
                    Estada(id, pdfMap["codi_centre"]
                            ?: "0", "Estada B", inici, fi, descripcio, comentaris)
                } catch (error: Exception) {
                    // error.printStackTrace()
                    Alert(Alert.AlertType.INFORMATION, error.message).show()
                    Estada("", "", "Tipus B", LocalDate.now(), LocalDate.now().plusWeeks(2), "", "")
                }

        val empresa =
                try {
                    val identficacio = Identificacio(pdfMap["CIF"]!!, pdfMap["nom i cognoms.1"]!!, pdfMap["adreça.0.0"]!!, pdfMap["cp empresa"]!!, pdfMap["municipi"]!!)
                    val personaDeContacte = PersonaDeContacte(pdfMap["nom contacte"]!!, pdfMap["càrrec"]!!, pdfMap["telèfon.1"]!!, pdfMap["adreça.1.0.0"]!!)
                    val tutor = Tutor(pdfMap["nom tutor"]!!, pdfMap["càrrec tutor"]!!, pdfMap["telèfon.2"]!!, "")

                    Empresa(identficacio, personaDeContacte, tutor)
                } catch (error: Exception) {
                    Empresa(Identificacio("", "", "", "", ""),
                            PersonaDeContacte("", "", "", ""),
                            Tutor("", "", "", ""))
                }

        return Pair(estada, empresa)
    }

    fun findRegistreByDocentId(nif: String): Registre? {

        registres.forEach {
            if (it.docent?.nif == nif) {
                pdfMap.put("codi_centre", it.centre?.codi ?: "")
                val pair: Pair<Estada, Empresa>? = loadEmpresaAndEstadaFromPdf(nif)
                it.estada = pair?.first
                it.empresa = pair?.second
                return it
            }
        }
        return null
    }

    fun close(): Unit {
        println("Closing connection.")
        conn.close()
    }

    fun reloadPdf(file: File): Pair<Estada, Empresa>? {
        if (loadPdfData(file))
            return createEmpresaAndEstadaFromMap()
        else
            return null
    }


    fun saveEstada(nif: String, estada: Estada, empresa: Empresa): Boolean {

        val month = LocalDate.now().month.value
        /* Entre setembre i desembre és l'any actual, si no és un any menys */
        val year = if (month > 8 && month <= 12) LocalDate.now().year else LocalDate.now().year - 1

        val estadaSts = conn.prepareStatement(insertEstadesQuery)
        estadaSts.setString(1, estada.numeroEstada)
        estadaSts.setString(2, year.toString())
        estadaSts.setString(3, nif)
        estadaSts.setString(4, estada.codiCentre)
        estadaSts.setString(5, empresa.identificacio.nif)
        estadaSts.setString(6, empresa.identificacio.nom)
        estadaSts.setString(7, empresa.identificacio.direccio)
        estadaSts.setString(8, empresa.identificacio.cp)
        estadaSts.setString(9, estada.tipusEstada)
        estadaSts.setDate(10, java.sql.Date.valueOf(estada.dataInici))
        estadaSts.setDate(11, java.sql.Date.valueOf(estada.dataFinal))
        estadaSts.setString(12, empresa.personaDeContacte.nom)
        estadaSts.setString(13, empresa.personaDeContacte.carrec)
        estadaSts.setString(14, empresa.personaDeContacte.telefon)
        estadaSts.setString(15, empresa.personaDeContacte.email)
        estadaSts.setString(16, empresa.tutor.nom)
        estadaSts.setString(17, empresa.tutor.carrec)
        estadaSts.setString(18, empresa.tutor.telefon)
        estadaSts.setString(19, empresa.tutor.email)
        estadaSts.setString(20, estada.descripcio)
        estadaSts.setString(21, estada.comentaris)

        return try {
            estadaSts.execute()
            val alert = Alert(Alert.AlertType.CONFIRMATION, "$nif afegit correctament")
            alert.showAndWait()

            val seguimentSts = conn.prepareStatement(insertSeguimentQuery)
            seguimentSts.setString(1, estada.numeroEstada)
            seguimentSts.setString(2, SeguimentEstats.INICIAL.name)
            seguimentSts.setString(3, "Sense comentaris")

            return try {
                seguimentSts.execute()
                val alert = Alert(Alert.AlertType.CONFIRMATION, "Estat actualitzat")
                alert.showAndWait()
                true

            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
                return false
            } finally {
                seguimentSts.closeOnCompletion()
            }
            true

        } catch (error: UcanaccessSQLException) {
            //error.printStackTrace()
            Alert(Alert.AlertType.ERROR, "Aquest codi d'estada ja existeix en la base de dades").showAndWait()
            return false
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            return false
        } finally {
            estadaSts.closeOnCompletion()
        }

    }

}
