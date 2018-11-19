package cat.gencat.access


import com.itextpdf.io.IOException
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfReader
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDate
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFontFactory
import java.io.FileInputStream
import com.itextpdf.kernel.pdf.PdfOutputIntent
import com.itextpdf.kernel.pdf.PdfAConformanceLevel
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Text
import com.itextpdf.pdfa.PdfADocument
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.*
import java.io.FileOutputStream
import java.nio.charset.Charset

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
data class Registre(val estada: Estada?, val empresa: Empresa?, val docent: Docent?, val centre: Centre?, val sstt: SSTT?)

data class Estada(val id: String, val codiCentre: String, val tipusEstada: String, val dataInici: LocalDate, val dataFinal: LocalDate, var comentaris: String)

data class Empresa(val identficacio: Identficacio, val personaDeContacte: PersonaDeContacte, val tutor: Tutor)

data class Identficacio(val nif: String?, val nom: String?, val direccio: String?, val cp: String?, val municipi: String?)
data class PersonaDeContacte(val nom: String?, val carrec: String?, val telefon: String?, val email: String?)
data class Tutor(val nom: String?, val carrec: String?, val telefon: String?, val email: String?)

data class Docent(val nif: String, val nom: String, val destinacio: String, val especialitat: String, val email: String, val telefon: String)

data class Centre(val codi: String, val nom: String, val municipi: String, val responsable: String?, val telefon: String?, val email: String)

data class SSTT(val codi: String, val nom: String, val municipi: String, val coordinador: String, val telefon: String, val email: String)

const val pathToDatabase: String = "D:\\Users\\39164789k\\Desktop\\app_estades\\gesticus.accdb"

const val pathToPdfs: String = "D:\\Users\\39164789k\\Desktop\\evalises_2018\\"

const val joinQuery: String = "SELECT professors_t.nif as [professors_nif], professors_t.noms as [professors_noms], professors_t.destinacio as [professors_destinacio], professors_t.especialitat as [professors_especialitat], professors_t.email AS [professors_email], professors_t.telefon as [professors_telefon], centres_t.C_Centre as [centres_codi], centres_t.NOM_Centre AS [centres_nom], centres_t.NOM_Municipi AS [centres_municipi], directors_t.Nom AS [directors_nom], centres_t.TELF as [centres_telefon], [nom_correu] & '@' & [@correu] AS [centres_email], sstt_t.[Codi ST] as [sstt_codi], sstt_t.SSTT AS [sstt_nom], delegacions_t.Municipi as [delegacions_municipi], delegacions_t.[coordinador 1] as [delegacions_coordinador], delegacions_t.[telf coordinador 1] as [delegacions_telefon_coordinador], sstt_t.[Correu 1] as [sstt.correu1]\n" +
        "FROM (((centres_t LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C) INNER JOIN professors_t ON centres_t.C_Centre = professors_t.c_centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[Codi ST]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació];\n"

class GesticusDb {

    lateinit var conn: Connection
    val registres = ArrayList<Registre>()
    val pdfMap = mutableMapOf<String, String>()

//    val customers: MutableList<Customer> = mutableListOf<Customer>()
//    val customers: java.util.ArrayList<Customer> = java.util.ArrayList<Customer>()
//    val customers: ArrayList<Customer> = ArrayList<Customer>()
//    val customers: List<Customer> = listOf<Customer>()
//    val customers: List<Customer> = emptyList<Customer>()

    init {
        loadDriver()
        connect()
    }

    @Throws(IOException::class)
    fun parse(filename: String) {
        val reader = PdfReader(filename)
        val rect = Rectangle(36f, 750f, 559f, 806f)

        reader.close()
    }

    @Throws(IOException::class)
    fun createPdf(dest: String) {
        //Initialize PDF writer
        val writer = PdfWriter(dest)
        //Initialize PDF document
        val pdf = PdfDocument(writer)
        // Initialize document
        val document = Document(pdf)
        //Add paragraph to the document
        document.add(Paragraph("Hello World!"))
        //Close document
        document.close()
    }

    @Throws(IOException::class)
    fun createPdf2(dest: String) {

        val DOG = "src/main/resources/img/dog.bmp";
        val FOX = "src/main/resources/img/fox.bmp";
        val FONT = "src/main/resources/font/FreeSans.ttf";
        val INTENT = "src/main/resources/color/sRGB_CS_profile.icm"
        //Initialize PDFA document with output intent
        val pdf = PdfADocument(PdfWriter(dest),
                PdfAConformanceLevel.PDF_A_1B,
                PdfOutputIntent("Custom", "", "http://www.color.org",
                        "sRGB IEC61966-2.1", FileInputStream(INTENT)))
        val document = Document(pdf)

        //Fonts need to be embedded
        val font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, true)
        val p = Paragraph()
        p.setFont(font)
        p.add(Text("The quick brown "))
        val foxImage = Image(ImageDataFactory.create(FOX))
        p.add(foxImage)
        p.add(" jumps over the lazy ")
        val dogImage = Image(ImageDataFactory.create(DOG))
        p.add(dogImage)

        document.add(p)
        document.close()
    }

    public fun parse(file: File) {
        val DEST = "D:\\Users\\39164789k\\Desktop\\app_estades\\output.txt"

        val pdfDoc = PdfDocument(PdfReader(file))
        val fos = FileOutputStream(DEST)

        val strategy = LocationTextExtractionStrategy()

        val parser = PdfCanvasProcessor(strategy)
        parser.processPageContent(pdfDoc.firstPage)
        val array = strategy.resultantText.toByteArray(Charset.defaultCharset())
        fos.write(array)

        fos.flush()
        fos.close()

        pdfDoc.close()

    }

    /*
    * Create a map with all this info
    * */
    fun listNonTerminalField(field: PDNonTerminalField) {


        field.children.forEach {

            when (it) {
                is PDTextField -> {
                    val pdTextbox: PDTextField = it
                    pdfMap.put(pdTextbox.fullyQualifiedName, pdTextbox.value)
                    System.out.println("PDTextBox " + pdTextbox.fullyQualifiedName + " " + pdTextbox.value)
                }
                is PDChoice -> {
                    val pdChoiceField: PDChoice = it
                    pdfMap.put(pdChoiceField.fullyQualifiedName, pdChoiceField.richTextValue)
                    System.out.println("PDChoice Field " + pdChoiceField.getFullyQualifiedName() + " " + pdChoiceField.getValue());
                }
                is PDCheckBox -> {
                    val pdCheckbox: PDCheckBox = it
                    pdfMap.put(pdCheckbox.fullyQualifiedName, pdCheckbox.value)
                    System.out.println("PDCheckbox Field " + pdCheckbox.getFullyQualifiedName() + " " + pdCheckbox.getValue());
                }
                is PDRadioButton -> {
                    val pdRadioButton: PDRadioButton = it
                    pdfMap.put(pdRadioButton.fullyQualifiedName, pdRadioButton.value)
                    System.out.println("PDRadioButton Field " + pdRadioButton.getFullyQualifiedName() + " " + pdRadioButton.getValue());
                }
                is PDNonTerminalField -> {
                    listNonTerminalField(it)
                }
            }
        }
    }

    fun loadEmpresa(doc: PDDocument) {

        val catalog = doc.getDocumentCatalog()
        val pdMetadata = catalog.getMetadata()
        val form = catalog.getAcroForm()
        val fields = form.getFields()

        System.out.println("Fields ${fields.size}")

        for (field in fields) {

            when (field) {
                is PDNonTerminalField -> listNonTerminalField(field)
                is PDTextField -> {
                    val pdTextbox: PDTextField = field
                    pdfMap.put(pdTextbox.fullyQualifiedName, pdTextbox.value)
                    System.out.println("PDTextBox " + pdTextbox.getFullyQualifiedName() + " " + pdTextbox.getValue())
                }
                is PDChoice -> {
                    val pdChoiceField: PDChoice = field
                    pdfMap.put(pdChoiceField.getFullyQualifiedName(), pdChoiceField.richTextValue)
                    System.out.println("PDChoice Field " + pdChoiceField.getFullyQualifiedName() + " " + pdChoiceField.getValue());
                }
                is PDCheckBox -> {
                    val pdCheckbox: PDCheckBox = field
                    pdfMap.put(pdCheckbox.getFullyQualifiedName(), pdCheckbox.getValue())
                    System.out.println("PDCheckbox Field " + pdCheckbox.getFullyQualifiedName() + " " + pdCheckbox.getValue());
                }
                is PDRadioButton -> {
                    val pdRadioButton: PDRadioButton = field
                    pdfMap.put(pdRadioButton.fullyQualifiedName, pdRadioButton.value)
                    System.out.println("PDRadioButton Field " + pdRadioButton.getFullyQualifiedName() + " " + pdRadioButton.getValue());
                }
                else -> {
                    pdfMap.put(field.fullyQualifiedName, field.valueAsString)
                    System.out.print(field)
                    System.out.print(" = ")
                    System.out.print(field.javaClass)
                    System.out.println()
                }
            }
        }
        doc.close()
    }

    private fun loadDriver(): Unit {
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver")
    }

    private fun connect(): Unit {
        println("Connecting...")
        conn = DriverManager.getConnection("jdbc:ucanaccess://${pathToDatabase};memory=true;openExclusive=false;ignoreCase=true")
        println("Connected to ${conn.metaData.databaseProductName}.")
    }

    fun preLoadData(): Unit {
        println("Loading data, please wait.")
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
    }

    fun findDataByDocentId(nif: String): Registre? {
        registres.forEach {
            if (it.docent?.nif == nif) {
                return it
            }
        }
        return null
    }

    fun loadEmpresaFromPdf(nif: String): Empresa? {

        val empresa =
                try {
                    val files: List<String> =
                            File(pathToPdfs).list().filter {
                                it.startsWith("${nif.substring(1, 9)}")
                            }.toList()

                    if (files.size == 1) {
                        println(files[0])
                        val file: File = File(pathToPdfs, files[0])
                        // parse(file)
                        loadEmpresa(PDDocument.load(file))
                    }

                    val identficacio = Identficacio(pdfMap["CIF"]!!, pdfMap["nom i cognoms.1"]!!, pdfMap["adreça.0.0"]!!, pdfMap["cp empresa"]!!, pdfMap["municipi"]!!)
                    val personaDeContacte = PersonaDeContacte(pdfMap["nom contacte"]!!, pdfMap["càrrec"]!!, pdfMap["telèfon.1"]!!, pdfMap["adreça.1.0.0"]!!)
                    val tutor = Tutor(pdfMap["nom tutor"]!!, pdfMap["càrrec tutor"]!!, pdfMap["telèfon.2"]!!, "")

                    Empresa(identficacio, personaDeContacte, tutor)
                } catch (error: Exception) {
                    null
                }

        return empresa
    }

    fun close(): Unit {
        println("Closing connection.")
        conn.close()
    }

}


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
//        val table = DatabaseBuilder.open(File(pathToDatabase)).getTable("sstt_t")
//        for (row in table) {
//            System.out.println("Column 'FirstName' has value: ${row["SSTT"]}")
//        }
//
//    }
//
//    private fun listCustomers3(): Unit {
//
//        val db = DatabaseBuilder.open(File(pathToDatabase))
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
//        val queries = DatabaseBuilder.open(File(pathToDatabase)).queries
//        queries.forEach {
//            println("name ${it.name} type ${it.type} sql ${it.toSQLString()}")
//        }
//    }
//
//    private fun createTable(): Unit {
//        val db = DatabaseBuilder.create(Database.FileFormat.V2010, File(pathToDatabase))
//        val newTable = TableBuilder("NewTable")
//                .addColumn(ColumnBuilder("a")
//                        .setSQLType(Types.INTEGER))
//                .addColumn(ColumnBuilder("b")
//                        .setSQLType(Types.VARCHAR))
//                .toTable(db)
//        newTable.addRow(1, "foo")
//    }

