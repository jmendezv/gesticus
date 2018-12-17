package cat.gencat.access.functions

import org.jasypt.util.text.BasicTextEncryptor
import java.time.LocalDate
import java.util.*
import java.util.concurrent.Executors.callable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

const val PATH_TO_REPORTS = "H:\\Mendez\\gesticusv2\\reports"
const val PATH_TO_DB: String = "H:\\Mendez\\gesticusv2\\bd\\gesticus.accdb"
const val PATH_TO_FORMS: String = "H:\\Mendez\\gesticusv2\\forms\\"
const val PATH_TO_TEMPORAL = "H:\\Mendez\\gesticusv2\\temporal"
const val PATH_TO_HELP = "H:\\Mendez\\gesticusv2\\help"
const val PATH_TO_LOG = "H:\\Mendez\\gesticusv2\\log"
const val PATH_TO_LLISTAT_PROVISIONAL = "H:\\Mendez\\gesticusv2\\temporal\\resolucio_provisional_estades_tipus_b_2018.xlsx"
const val PATH_TO_LLISTAT_DEFINITIU = "H:\\Mendez\\gesticusv2\\temporal\\resolucio_definitiva_estades_tipus_b_2018.xlsx"

fun currentCourseYear(): String {
    val month = LocalDate.now().month.value
    /* Entre setembre i desembre és l'any actual, si no és un any menys */
    val year = if (month > 8 && month <= 12) LocalDate.now().year else LocalDate.now().year - 1
    return year.toString()
}

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
//            System.out.println("Column 'FirstName' has value: ${row["SSTT"]}")
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
* 20437852Y_N_I_MaciasCamposJesus.pdf
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
