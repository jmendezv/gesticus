package com.example.demo.db

import cat.gencat.access.model.SSTT
import tornadofx.*

class DatabaseProvider {

    val serveis = mutableListOf<SSTT>(
            SSTT("0822", "Barcelona Comarques", "bc1@gencat.cat", "bc2@gencat.cat"),
            SSTT("0823", "Tarragona", "tar1@gencat.cat", "tar2@gencat.cat"),
            SSTT("0820", "Lleida", "lle1@gencat.cat", "lle2@gencat.cat"),
            SSTT("0824", "Girona", "gir1@gencat.cat", "gir2@gencat.cat"),
            SSTT("0821", "Baix Llobregat", "bai1@gencat.cat", "bai2@gencar.cat")
    ).observable()

    fun getServeisTerritorials() = serveis.sorted { o1, o2 -> o1.codi.compareTo(o2.codi) }


}