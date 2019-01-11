package cat.gencat.access.functions

import org.jasypt.util.text.BasicTextEncryptor
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors.callable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

const val PATH_TO_BASE = "H:\\Mendez\\gesticusv2\\"
const val PATH_TO_REPORTS = "${PATH_TO_BASE}reports\\"
const val PATH_TO_DB: String = "${PATH_TO_BASE}bd\\gesticus.accdb"
const val PATH_TO_FORMS: String = "${PATH_TO_BASE}forms\\"
const val PATH_TO_TEMPORAL = "${PATH_TO_BASE}temporal\\"
const val PATH_TO_HELP = "${PATH_TO_BASE}help\\"
const val PATH_TO_LOG = "${PATH_TO_BASE}log\\log.txt"
//const val PATH_TO_LLISTAT_PROVISIONAL = "${PATH_TO_BASE}gesticusv2\\temporal\\resolucio_provisional_estades_tipus_b_2018.xlsx"
//const val PATH_TO_LLISTAT_DEFINITIU = "${PATH_TO_BASE}gesticusv2\\temporal\\resolucio_definitiva_estades_tipus_b_2018.xlsx"
const val PATH_TO_LOGO_HTML = "file:///H:/Mendez/gesticusv2/logos/logo_bn.jpg"
const val PATH_TO_LOGO = "${PATH_TO_BASE}logos\\logo_bn.jpg"

const val PATH_TO_COPY = "${PATH_TO_BASE}historic\\"

/* Form A */
const val FORM_A_FIELD_NOM_EMPRESA = "nom i cognoms.1"
const val FORM_A_FIELD_NOM_DOCENT = "nom i cognoms.0.0"
const val FORM_A_FIELD_MOBIL_DOCENT = "nom i cognoms.0.1"
const val FORM_A_FIELD_EMAIL_DOCENT = "nom i cognoms.0.2"
const val FORM_A_FIELD_NIF_DOCENT = "nom i cognoms.0.3"
// Aquest checkbox val On si esta seleccionat i Off si no ho esta
const val FORM_A_FIELD_TE_EMPRESA = "S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.0"
// Aquest checkbox val On si esta seleccionat i Off si no ho esta
const val FORM_A_FIELD_NO_TE_EMPRESA = "S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.1"
const val FORM_A_FIELD_NIF_EMPRESA = "CIF"
const val FORM_A_FIELD_DIRECCIO_EMPRESA = "adreça.0.0"
const val FORM_A_FIELD_EMAIL_EMPRESA = "adreça.1.0.0"
const val FORM_A_FIELD_MUNICIPI_EMPRESA = "municipi"
const val FORM_A_FIELD_CP_EMPRESA = "cp empresa"
const val FORM_A_FIELD_TELEFON_EMPRESA = "telèfon.0"
const val FORM_A_FIELD_TELEFON_PERSONA_DE_CONTACTE_EMPRESA = "telèfon.1"
const val FORM_A_FIELD_TELEFON_TUTOR_EMPRESA = "telèfon.2"
const val FORM_A_FIELD_NOM_CONTACTE_EMPRESA = "nom contacte"
const val FORM_A_FIELD_CARREC_CONTACTE_EMPRESA = "càrrec"
const val FORM_A_FIELD_NOM_TUTOR_EMPRESA = "nom tutor"
const val FORM_A_FIELD_CARREC_TUTOR_EMPRESA = "càrrec tutor"
const val FORM_A_FIELD_DURADA_HORES_ESTADA = "durada hores.0"
const val FORM_A_FIELD_DATA_INICI_ESTADA = "inici.0.0"
const val FORM_A_FIELD_DATA_FI_ESTADA = "fi"
const val FORM_A_FIELD_HORA_INICI_MATI_ESTADA = "hores1.0"
const val FORM_A_FIELD_HORA_FINAL_MATI_ESTADA = "hores1.1"
const val FORM_A_FIELD_HORA_INICI_TARDA_ESTADA = "hores1.2"
const val FORM_A_FIELD_HORA_FINAL_TARDA_ESTADA = "hores1.3"
const val FORM_A_FIELD_SECTOR_EMPRESA = "sector.0"
const val FORM_A_FIELD_TIPUS_EMPRESA = "tipus"
// Aquest option val Opción1 si esta seleccionat el Sí i Opción2 si esta selcctionat el No
const val FORM_A_FIELD_FP_DUAL_ESTADA = "Group1"
const val FORM_A_FIELD_OBJECTIUS_ESTADA = "objectius"
const val FORM_A_FIELD_ACTIVITATZ_ESTADA = "activitats"
const val FORM_A_FIELD_CODI_CENTRE_ESTADA = "codi_centre"

/* Form B */
const val FORM_B_FIELD_NOM_EMPRESA = "nom i cognoms.1"
const val FORM_B_FIELD_NOM_DOCENT = "nom i cognoms.0.0"
const val FORM_B_FIELD_MOBIL_DOCENT = "nom i cognoms.0.1"
const val FORM_B_FIELD_EMAIL_DOCENT = "nom i cognoms.0.2"
const val FORM_B_FIELD_NIF_DOCENT = "nom i cognoms.0.3"
// Aquest checkbox val On si esta seleccionat i Off si no ho esta
const val FORM_B_FIELD_TE_EMPRESA = "S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.0"
// Aquest checkbox val On si esta seleccionat i Off si no ho esta
const val FORM_B_FIELD_NO_TE_EMPRESA = "S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.1"
const val FORM_B_FIELD_NIF_EMPRESA = "CIF"
const val FORM_B_FIELD_DIRECCIO_EMPRESA = "adreça.0.0"
const val FORM_B_FIELD_EMAIL_EMPRESA = "adreça.1.0.0"
const val FORM_B_FIELD_MUNICIPI_EMPRESA = "municipi"
const val FORM_B_FIELD_CP_EMPRESA = "cp empresa"
const val FORM_B_FIELD_TELEFON_EMPRESA = "telèfon.0"
const val FORM_B_FIELD_TELEFON_PERSONA_DE_CONTACTE_EMPRESA = "telèfon.1"
const val FORM_B_FIELD_TELEFON_TUTOR_EMPRESA = "telèfon.2"
const val FORM_B_FIELD_NOM_CONTACTE_EMPRESA = "nom contacte"
const val FORM_B_FIELD_CARREC_CONTACTE_EMPRESA = "càrrec"
const val FORM_B_FIELD_NOM_TUTOR_EMPRESA = "nom tutor"
const val FORM_B_FIELD_CARREC_TUTOR_EMPRESA = "càrrec tutor"
const val FORM_B_FIELD_DURADA_HORES_ESTADA = "durada hores.0"
const val FORM_B_FIELD_DATA_INICI_ESTADA = "inici.0.0"
const val FORM_B_FIELD_DATA_FI_ESTADA = "fi"
const val FORM_B_FIELD_HORA_INICI_MATI_ESTADA = "hores1.0"
const val FORM_B_FIELD_HORA_FINAL_MATI_ESTADA = "hores1.1"
const val FORM_B_FIELD_HORA_INICI_TARDA_ESTADA = "hores1.2"
const val FORM_B_FIELD_HORA_FINAL_TARDA_ESTADA = "hores1.3"
const val FORM_B_FIELD_SECTOR_EMPRESA = "sector.0"
const val FORM_B_FIELD_TIPUS_EMPRESA = "tipus"
// Aquest option val Opción1 si esta seleccionat el Sí i Opción2 si esta selcctionat el No
const val FORM_B_FIELD_FP_DUAL_ESTADA = "Group1"
const val FORM_B_FIELD_OBJECTIUS_ESTADA = "objectius"
const val FORM_B_FIELD_ACTIVITATZ_ESTADA = "activitats"
const val FORM_B_FIELD_CODI_CENTRE_ESTADA = "codi_centre"

val NIF_REGEXP = "0\\d{8}[A-Z]".toRegex()

val NIE_REGEXP = "[A-Z]\\d{7}[A-Z]]".toRegex()

private fun currentYear(): Int {
    val month = LocalDate.now().month.value
    /* Entre setembre i desembre és l'any actual, si no és un any menys */
    return if (month > 8 && month <= 12) LocalDate.now().year else LocalDate.now().year - 1
}

fun currentCourseYear(): String = currentYear().toString()

fun nextCourseYear(): String = (currentYear() + 1).toString()

// From String to Base 64 encoding
fun String.encode(): String = Base64.getEncoder().encodeToString(this.toByteArray())

// From Base 64 encoding to String
fun String.decode(): String = String(Base64.getDecoder().decode(this))

// Basic encryption
fun String.encrypt(password: String): String {
    val basicTextEncryptor = BasicTextEncryptor()
    basicTextEncryptor.setPassword(password)
    return basicTextEncryptor.encrypt(this)
}

// Basic decryption
fun String.decrypt(password: String): String {
    val basicTextEncryptor = BasicTextEncryptor()
    basicTextEncryptor.setPassword(password)
    return basicTextEncryptor.decrypt(this)
}

// Valid DNI/NIE
fun String.isValidDniNie(): Boolean {

    // 22 termminacions possibles aleatòriament distribuides
    val terminacions = arrayOf("T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E")

    val terminacio = substring(length - 1)

    // DNI 99999999A
    if (matches("\\d{8}[A-Z]".toRegex())) {
        val modul = Integer.parseInt(substring(0, 8)) % 23
        return terminacio == terminacions[modul]
    }
    // NIE que comença per X9999999A, la X cau
    if (matches("[X]\\d{7}[A-Z]".toRegex())) {
        val modul = Integer.parseInt(substring(1, 7)) % 23
        return terminacio == terminacions[modul]
    }
    // NIE que comença per Y9999999A, la Y es substitueix per 1
    if (matches("[Y]\\d{7}[A-Z]".toRegex())) {
        val modul = Integer.parseInt("1" + substring(0, 8)) % 23
        return terminacio == terminacions[modul]
    }
    // NIE que comença per Z9999999A, la Z es substitueix per 2
    if (matches("[Z]\\d{7}[A-Z]".toRegex())) {
        val modul = Integer.parseInt("2" + substring(0, 8)) % 23
        return terminacio == terminacions[modul]
    }

    return false
}

fun <V, T : ScheduledExecutorService> T.schedule(
        delay: Long,
        unit: TimeUnit = TimeUnit.HOURS,
        action: () -> V): ScheduledFuture<*> {
    return schedule(
            callable { action() },
            delay, unit)
}


fun writeToLog(msg: String): Unit {
    val record = "${LocalDateTime.now().toString()} $msg\n"
    Files.write(Paths.get(PATH_TO_LOG), record.lines(), StandardOpenOption.APPEND)
}

/*
*
* Valid data formats:
*
* 99/99/9999
* 9/9/99
* 99-99-9999
* 9-9-99
* 99.99.9999
* 9.9.99
*
* */
fun parseDate(dataStr: String): LocalDate {

    lateinit var data: LocalDate

    if (dataStr.matches("\\d\\d/\\d\\d/[0-9]{4}".toRegex())) {
        data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ?: LocalDate.now()
    } else if (dataStr.matches("\\d/\\d/[0-9]{2}".toRegex())) {
        data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d/M/yy"))
                ?: LocalDate.now()
    } else if (dataStr.matches("\\d\\d-\\d\\d-[0-9]{4}".toRegex())) {
        data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                ?: LocalDate.now()
    } else if (dataStr.matches("\\d-\\d-[0-9]{2}".toRegex())) {
        data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d-M-yy"))
                ?: LocalDate.now()
    } else if (dataStr.matches("\\d\\d\\.\\d\\d\\.[0-9]{4}".toRegex())) {
        data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                ?: LocalDate.now()
    } else if (dataStr.matches("\\d\\.\\d\\.[0-9]{2}".toRegex())) {
        data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d.M.yy"))
                ?: LocalDate.now()
    } else {
        data = LocalDate.now()
    }

    return data
}

/* gets 0001230600/2018-2019 and returns 0001240600/2018-2019 */
fun nextEstadaNumber(codi: String): String {
    val nextNumEstada = Integer.parseInt(codi.substring(3, 6)) + 1
    val numberFormat = NumberFormat.getIntegerInstance()
    numberFormat.minimumIntegerDigits = 3
    numberFormat.maximumIntegerDigits = 3
    val newNumEstada = numberFormat.format(nextNumEstada)
    return codi.substring(0,3) + newNumEstada + codi.substring(6)
}

//
//class GesticusDbModel : ItemViewModel<GesticusDb>() {
//    val conn = bind(GesticusDb::conn)
//    val registres = bind(GesticusDb::registres)
//    val pdfMap = bind(GesticusDb::pdfMap)
//}
//
//
//class GesticusDbModel : ItemViewModel<GesticusDb>() {
//    val conn = bind(GesticusDb::conn)
//    val registres = bind(GesticusDb::registres)
//    val pdfMap = bind(GesticusDb::pdfMap)
//}


//
//    private fun listCustomers(): Unit {
//        val sts = conn.createStatement()
//        val sql = "SELECT [CustomerID], [FirstName], [NumEmployees], [isActive] FROM [CustomerT]"
//        val rsCustomers = sts.executeQuery(sql)
//        val columns = rsCustomers.metaData.columnCount
//        for (c in 1..columns)
//            print(rsCustomers.metaData.getColumnName(c) + " ")
//        println()
//        while (rsCustomers.next()) {
//        }
//    }
//
//    private fun writeCustomersToCSVFile(): Unit {
//
//        val sts = conn.createStatement()
//        val sql = "SELECT [CustomerID], [FirstName], [NumEmployees], [isActive] FROM [CustomerT]"
//        val rsCustomers = sts.executeQuery(sql)
//
//        val writer = Files.newBufferedWriter(Paths.get("filename.csv"))
//
//        // This works ok
//        val csvWriter = CSVWriter(writer,
//                CSVWriter.DEFAULT_SEPARATOR,
//                CSVWriter.NO_QUOTE_CHARACTER,
//                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//                CSVWriter.DEFAULT_LINE_END)
//
//        csvWriter.writeAll(rsCustomers, true, true, true)
//
//        csvWriter.flush()
//        csvWriter.close()
//
//    }
//
//    private fun listCustomers2(): Unit {
//
//        val table = DatabaseBuilder.open(File(PATH_TO_DB)).getTable("sstt_t")
//        for (row in table) {
//            System.out.println("Column 'FirstName' has value: ${row["EditableSSTT"]}")
//        }
//
//    }
//
//    private fun listCustomers3(): Unit {
//
//        val db = DatabaseBuilder.open(File(PATH_TO_DB))
//
//        val table = db.getTable("professors_t")
//
//        val cursor = table.defaultCursor
//        cursor.beforeFirst()
//        cursor.nextRow
//        while (!cursor.isAfterLast) {
//            println(cursor.currentRow["noms"])
//            cursor.nextRow
//        }
//
//        db.close()
//    }
//
//    private fun listQueries(): Unit {
//        val queries = DatabaseBuilder.open(File(PATH_TO_DB)).queries
//        queries.forEach {
//            println("name ${it.name} type ${it.type} sql ${it.toSQLString()}")
//        }
//    }
//
//    private fun createTable(): Unit {
//        val db = DatabaseBuilder.create(Database.FileFormat.V2010, File(PATH_TO_DB))
//        val newTable = TableBuilder("NewTable")
//                .addColumn(ColumnBuilder("a")
//                        .setSQLType(Types.INTEGER))
//                .addColumn(ColumnBuilder("b")
//                        .setSQLType(Types.VARCHAR))
//                .toTable(db)
//        newTable.addRow(1, "foo")
//    }

//    val customers: MutableList<Customer> = mutableListOf<Customer>()
//    val customers: java.util.ArrayList<Customer> = java.util.ArrayList<Customer>()
//    val customers: ArrayList<Customer> = ArrayList<Customer>()
//    val customers: List<Customer> = listOf<Customer>()
//    val customers: List<Customer> = emptyList<Customer>()

/*
*
Fields: 20 Form: 20
'nom i cognoms.1' -> 'IBM Ibérica'
'nom i cognoms.0.0' -> 'Joan Martínez López'
'nom i cognoms.0.1' -> '611909655'
'nom i cognoms.0.2' -> 'jmartinez11@xtec.cat'
'nom i cognoms.0.3' -> '45443789P'
'S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.0' -> 'On'
'S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.1' -> 'Off'
'CIF' -> 'B12345678D'
'adreça.0.0' -> 'C/ Intel, 54'
'adreça.1.0.0' -> 'info@ibm.cat'
'municipi' -> 'Barcelona'
'cp empresa' -> '08005'
'telèfon.0' -> '937678899'
'telèfon.1' -> '938765433'
'telèfon.2' -> '932123345'
'nom contacte' -> 'Carles Romero García'
'càrrec' -> 'Director General'
'nom tutor' -> 'Marta Rius Puig'
'càrrec tutor' -> 'Directora RRHH'
'durada hores.0' -> '80'
'inici.0.0' -> '07/01/2019'
'fi' -> '18/01/2019'
'hores1.0' -> '09:00'
'hores1.1' -> '14:00'
'hores1.2' -> '15:00'
'hores1.3' -> '18:00'
'sector.0' -> 'Tecnològic'
'tipus' -> 'Informàtica i comunicacions'
'Group1' -> 'Opción1'
'objectius' -> 'Objectiu número 1.
Objectiu número 2.'
'activitats' -> 'Activitat número 1.
Activitat número 2.'
*
*
*
20437852Y_N_I_MaciasCamposJesus.pdf
Fields 20
PDTextBox nom i cognoms.1 Universitat de Barcelona  -Campus de l’Alimentació de Torribera
PDTextBox nom i cognoms.0.0 JESÚS MACÍAS CAMPOS
PDTextBox nom i cognoms.0.1 607375784
PDTextBox nom i cognoms.0.2 jmacias6@xtec.cat
PDTextBox nom i cognoms.0.3 20437852Y
PDCheckbox Field S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.0 On
PDCheckbox Field S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.1 Off
PDTextBox CIF Q0818001J
PDTextBox adreça.0.0  Av. Prat de la Riba 171
PDTextBox adreça.1.0.0 mrubiralta@ub.edu
PDTextBox municipi Santa Coloma de Gramemet
PDTextBox cp empresa  08921
PDTextBox telèfon.0  934031980
PDTextBox telèfon.1 934033787
PDTextBox telèfon.2 934034500
PDTextBox nom contacte Mario Rubiralta Alcañiz
PDTextBox càrrec Cap de departament
PDTextBox nom tutor Pedro Marrero i Diego Haro
PDTextBox càrrec tutor Investigadors al Grup de Senyalització cel∙lular en Bioquímica i Biologia Molecular
PDTextBox durada hores.0 80
PDTextBox inici.0.0 03/12/2018
PDTextBox fi 13/12/2018
PDTextBox hores1.0 9
PDTextBox hores1.1 14
PDTextBox hores1.2 15
PDTextBox hores1.3 18
PDTextBox sector.0 Universitat
PDTextBox tipus Biotecnològica
PDRadioButton Field Group1 Opción2
-Aprendre els anàlisis més rellevants.
-Utilització dels materials i reactius necessaris.

*
* */

//
//    @Throws(IOException::class)
//    fun parse(filename: String) {
//        val reader = PdfReader(filename)
//        val rect = Rectangle(36f, 750f, 559f, 806f)
//
//        reader.close()
//    }
//
//    @Throws(IOException::class)
//    fun createPdf(dest: String) {
//        //Initialize PDF writer
//        val writer = PdfWriter(dest)
//        //Initialize PDF document
//        val pdf = PdfDocument(writer)
//        // Initialize document
//        val document = Document(pdf)
//        //Add paragraph to the document
//        document.add(Paragraph("Hello World!"))
//        //Close document
//        document.close()
//    }
//
//    @Throws(IOException::class)
//    fun createPdf2(dest: String) {
//
//        val DOG = "src/main/resources/img/dog.bmp";
//        val FOX = "src/main/resources/img/fox.bmp";
//        val FONT = "src/main/resources/font/FreeSans.ttf";
//        val INTENT = "src/main/resources/color/sRGB_CS_profile.icm"
//        //Initialize PDFA document with output intent
//        val pdf = PdfADocument(PdfWriter(dest),
//                PdfAConformanceLevel.PDF_A_1B,
//                PdfOutputIntent("Custom", "", "http://www.color.org",
//                        "sRGB IEC61966-2.1", FileInputStream(INTENT)))
//        val document = Document(pdf)
//
//        //Fonts need to be embedded
//        val font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, true)
//        val p = Paragraph()
//        p.setFont(font)
//        p.add(Text("The quick brown "))
//        val foxImage = Image(ImageDataFactory.create(FOX))
//        p.add(foxImage)
//        p.add(" jumps over the lazy ")
//        val dogImage = Image(ImageDataFactory.create(DOG))
//        p.add(dogImage)
//
//        document.add(p)
//        document.close()
//    }
//
//    public fun parse(file: File) {
//        val DEST = "D:\\Users\\39164789k\\Desktop\\app_estades\\output.txt"
//
//        val pdfDoc = PdfDocument(PdfReader(file))
//        val fos = FileOutputStream(DEST)
//
//        val strategy = LocationTextExtractionStrategy()
//
//        val parser = PdfCanvasProcessor(strategy)
//        parser.processPageContent(pdfDoc.firstPage)
//        val array = strategy.resultantText.toByteArray(Charset.defaultCharset())
//        fos.write(array)
//
//        fos.flush()
//        fos.close()
//
//        pdfDoc.close()
//
//    }
