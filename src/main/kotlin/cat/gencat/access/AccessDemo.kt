package cat.gencat.access

import com.healthmarketscience.jackcess.*
import com.opencsv.CSVWriter
import com.opencsv.CSVWriter.DEFAULT_LINE_END
import com.opencsv.CSVWriter.NO_QUOTE_CHARACTER
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.io.File
import java.sql.Types


data class Customer(val id: Int, val firstName: String, var numEmployees: Int, var isActive: Boolean)

lateinit var conn: Connection

val customers = ArrayList<Customer>()

/*
* Not necessary for newer Drivers
* */
private fun loadDriver(): Unit {
    Class.forName("net.ucanaccess.jdbc.UcanaccessDriver")
}

private fun connect(): Unit {
    println("Connecting...")
    conn = DriverManager.getConnection("jdbc:ucanaccess://dades.accdb;memory=true;openExclusive=false;ignoreCase=true")
    println(conn.metaData.databaseProductName)

}

private fun showTables(): Unit {
    // Show Tables & Queries en file
    val md = conn.metaData

    val rsTables = md.getTables(null, null, "%", null)
    while (rsTables.next()) {
        println(rsTables.getString(3))
    }
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
            NO_QUOTE_CHARACTER,
            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
            DEFAULT_LINE_END)

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
    while(!cursor.isAfterLast) {
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

fun main(args: Array<String>) {


    try {

//        connect()
//        showTables()
//        listCustomers()
//        writeCustomersToCSVFile()

//        listCustomers2()
//        listQueries()
        listCustomers3()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
//        conn?.close()
    }
}


// Not working
//        val beanToCsv: StatefulBeanToCsv<Customer> = StatefulBeanToCsvBuilder<Customer>(writer)
//                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
//                .build()
//
//        beanToCsv.write(customers)
//
//        writer.flush()
//        writer.close()