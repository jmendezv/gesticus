package cat.gencat.access.controllers

import com.example.demo.db.DatabaseProvider
import tornadofx.*

class CustomController : Controller() {

    val databaseProvider = DatabaseProvider()

    fun getServeisTerritorials() = databaseProvider.getServeisTerritorials()

}