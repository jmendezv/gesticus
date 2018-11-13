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

data class Docent(val nif: String, val nom: String, val destinacio: String, val especialitat: String, val email: String, val telefon: String)

data class Customer(val id: Int, val firstName: String, var numEmployees: Int, var isActive: Boolean)

const val query: String = "SELECT professors_t.nif, professors_t.noms, professors_t.email AS [email professor], centres_t.NOM_Centre AS institut, [nom_correu] & '@' & [@correu] AS [email_centre], directors_t.carrec, directors_t.Nom AS responsable, sstt_t.SSTT AS sstt, sstt_t.[Correu 1] AS email\n" +
        "FROM ((centres_t LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C) INNER JOIN professors_t ON centres_t.C_Centre = professors_t.c_centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[Codi ST];\n"

class GesticusDb {

    lateinit var conn: Connection

    val docents = ArrayList<Docent>()

    private fun loadDriver(): Unit {
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver")
    }

    private fun connect(): Unit {
        println("Connecting...")
        conn = DriverManager.getConnection("jdbc:ucanaccess://dades.accdb;memory=true;openExclusive=false;ignoreCase=true")
        println(conn.metaData.databaseProductName)

    }


    private fun listCustomers(): Unit {
        val sts = conn.createStatement()
        val sql = "SELECT [CustomerID], [FirstName], [NumEmployees], [isActive] FROM [CustomerT]"
        val rsCustomers = sts.executeQuery(sql)
        val columns = rsCustomers.metaData.columnCount
        for (c in 1..columns)
            print(rsCustomers.metaData.getColumnName(c) + " ")
        println()
        while (rsCustomers.next()) {
            val customer = Customer(rsCustomers.getInt("CustomerID"),
                    rsCustomers.getString("FirstName"),
                    rsCustomers.getInt("NumEmployees"),
                    rsCustomers.getBoolean("IsActive"))
            customers.add(customer)
            for (c in 1..columns) {
                print(rsCustomers.getString(c) + " ")
            }
            println(customer)
        }
    }

    private fun writeCustomersToCSVFile(): Unit {

        val sts = conn.createStatement()
        val sql = "SELECT [CustomerID], [FirstName], [NumEmployees], [isActive] FROM [CustomerT]"
        val rsCustomers = sts.executeQuery(sql)

        val writer = Files.newBufferedWriter(Paths.get("customers.csv"))

        // This works ok
        val csvWriter = CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)

        csvWriter.writeAll(rsCustomers, true, true, true)

        csvWriter.flush()
        csvWriter.close()

    }

    private fun listCustomers2(): Unit {

        val table = DatabaseBuilder.open(File("dades.accdb")).getTable("CustomerT")
        for (row in table) {
            System.out.println("Column 'FirstName' has value: ${row["FirstName"]}")
        }

    }

    private fun listCustomers3(): Unit {

        val db = DatabaseBuilder.open(File("dades.accdb"))

        val table = db.getTable("CustomerT")

        val cursor = table.defaultCursor
        cursor.beforeFirst()
        cursor.nextRow
        while (!cursor.isAfterLast) {
            println(cursor.currentRow["FirstName"])
            cursor.nextRow
        }

//    for (row in table) {
//        for (column in table.columns) {
//            val columnName = column.name
//            val value = row[columnName]
//            print("$value ")
//        }
//        println()
//    }

        db.close()
    }

    private fun listQueries(): Unit {
        val queries = DatabaseBuilder.open(File("dades.accdb")).queries
        queries.forEach {
            println("name ${it.name} type ${it.type} sql ${it.toSQLString()}")
        }
    }

    private fun createTable(): Unit {
        val db = DatabaseBuilder.create(Database.FileFormat.V2010, File("mydb.accdb"))
        val newTable = TableBuilder("NewTable")
                .addColumn(ColumnBuilder("a")
                        .setSQLType(Types.INTEGER))
                .addColumn(ColumnBuilder("b")
                        .setSQLType(Types.VARCHAR))
                .toTable(db)
        newTable.addRow(1, "foo")
    }

    fun findDocentById(nif: String): Docent {

        return Docent("029029866W", "ABAD BUENO, Juan de Dios", "IN", "ORGANITZACIÓ I GESTIO COMERCIAL", "jabad3@xtec.cat", "655236204")

    }

}