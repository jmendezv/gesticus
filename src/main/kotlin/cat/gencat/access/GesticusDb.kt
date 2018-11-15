package cat.gencat.access

import com.healthmarketscience.jackcess.ColumnBuilder
import com.healthmarketscience.jackcess.Database
import com.healthmarketscience.jackcess.DatabaseBuilder
import com.healthmarketscience.jackcess.TableBuilder
import com.opencsv.CSVWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Types
import java.time.LocalDate


data class Registre(val estada: Estada?, val empresa: Empresa?, val docent: Docent?, val centre: Centre?, val sstt: SSTT?)

data class Estada(val id: String, val codiCentre: String, val tipusEstada: String, val dataInici: LocalDate, val dataFinal: LocalDate, var comentaris: String)

data class Empresa(val identficacio: Identficacio, val personaDeContacte: PersonaDeContacte, val tutor: Tutor)

data class Identficacio(val nif: String, val nom: String, val direccio: String, val cp: String, val location: String)
data class PersonaDeContacte(val nom: String, val carrec: String, val telefon: String, val email: String)
data class Tutor(val nom: String, val carrec: String, val telefon: String, val email: String)

data class Docent(val nif: String, val nom: String, val destinacio: String, val especialitat: String, val email: String, val telefon: String)

data class Centre(val codi: String, val nom: String, val municipi: String, val responsable: String?, val telefon: String?, val email: String)

data class SSTT(val codi: String, val nom: String, val municipi: String, val coordinador: String, val telefon: String, val email: String)

const val path: String = "D:\\Users\\39164789k\\Desktop\\app_estades\\gesticus.accdb"

const val joinQuery: String = "SELECT professors_t.nif as [professors_nif], professors_t.noms as [professors_noms], professors_t.destinacio as [professors_destinacio], professors_t.especialitat as [professors_especialitat], professors_t.email AS [professors_email], professors_t.telefon as [professors_telefon], centres_t.C_Centre as [centres_codi], centres_t.NOM_Centre AS [centres_nom], centres_t.NOM_Municipi AS [centres_municipi], directors_t.Nom AS [directors_nom], centres_t.TELF as [centres_telefon], [nom_correu] & '@' & [@correu] AS [centres_email], sstt_t.[Codi ST] as [sstt_codi], sstt_t.SSTT AS [sstt_nom], delegacions_t.Municipi as [delegacions_municipi], delegacions_t.[coordinador 1] as [delegacions_coordinador], delegacions_t.[telf coordinador 1] as [delegacions_telefon_coordinador], sstt_t.[Correu 1] as [sstt.correu1]\n" +
        "FROM (((centres_t LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C) INNER JOIN professors_t ON centres_t.C_Centre = professors_t.c_centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[Codi ST]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació];\n"

class GesticusDb {

    lateinit var conn: Connection
    val registres = ArrayList<Registre>()
//    val customers: MutableList<Customer> = mutableListOf<Customer>()
//    val customers: java.util.ArrayList<Customer> = java.util.ArrayList<Customer>()
//    val customers: ArrayList<Customer> = ArrayList<Customer>()
//    val customers: List<Customer> = listOf<Customer>()
//    val customers: List<Customer> = emptyList<Customer>()

    init {
        loadDriver()
        connect()
    }

    private fun loadDriver(): Unit {
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver")
    }

    private fun connect(): Unit {
        println("Connecting...")
        conn = DriverManager.getConnection("jdbc:ucanaccess://${path};memory=true;openExclusive=false;ignoreCase=true")
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

    fun findDocentById(nif: String): Registre? {
        registres.forEach {
            if (it.docent?.nif == nif) {
                return it
            }
        }
        return null
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
//        val table = DatabaseBuilder.open(File(path)).getTable("sstt_t")
//        for (row in table) {
//            System.out.println("Column 'FirstName' has value: ${row["SSTT"]}")
//        }
//
//    }
//
//    private fun listCustomers3(): Unit {
//
//        val db = DatabaseBuilder.open(File(path))
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
//        val queries = DatabaseBuilder.open(File(path)).queries
//        queries.forEach {
//            println("name ${it.name} type ${it.type} sql ${it.toSQLString()}")
//        }
//    }
//
//    private fun createTable(): Unit {
//        val db = DatabaseBuilder.create(Database.FileFormat.V2010, File(path))
//        val newTable = TableBuilder("NewTable")
//                .addColumn(ColumnBuilder("a")
//                        .setSQLType(Types.INTEGER))
//                .addColumn(ColumnBuilder("b")
//                        .setSQLType(Types.VARCHAR))
//                .toTable(db)
//        newTable.addRow(1, "foo")
//    }

