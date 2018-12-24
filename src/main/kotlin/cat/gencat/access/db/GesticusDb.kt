package cat.gencat.access.db

import cat.gencat.access.functions.PATH_TO_DB
import cat.gencat.access.functions.currentCourseYear
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalDateTime

/* Tots els docents, centres, sstts */
const val preLoadJoinQuery: String = "SELECT professors_t.nif as [professors_nif], professors_t.noms as [professors_noms], professors_t.destinacio as [professors_destinacio], professors_t.especialitat as [professors_especialitat], professors_t.email AS [professors_email], professors_t.telefon as [professors_telefon], centres_t.C_Centre as [centres_codi], centres_t.NOM_Centre AS [centres_nom], centres_t.[Adreça] as [centres_direccio], centres_t.[C_Postal] as [centres_codipostal], centres_t.NOM_Municipi AS [centres_municipi], directors_t.Nom & ' ' & directors_t.[Cognoms] AS [directors_nom], centres_t.TELF as [centres_telefon], [nom_correu] & '@' & [@correu] AS [centres_email], sstt_t.[codi] as [sstt_codi], sstt_t.nom AS [sstt_nom], delegacions_t.Municipi as [delegacions_municipi], delegacions_t.[coordinador 1] as [delegacions_coordinador], delegacions_t.[telf coordinador 1] as [delegacions_telefon_coordinador], sstt_t.[correu_1] as [sstt_correu_1], sstt_t.[correu_2] as [sstt_correu_2]\n" +
        "FROM (((centres_t LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C) INNER JOIN professors_t ON centres_t.C_Centre = professors_t.c_centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[codi]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació];\n"

/* docent, centre i sstt d'un nif concret nif en forma 099999999A */
const val findRegistreByNif: String = "SELECT professors_t.nif as [professors_nif], professors_t.noms as [professors_noms], professors_t.destinacio as [professors_destinacio], professors_t.especialitat as [professors_especialitat], professors_t.email AS [professors_email], professors_t.telefon as [professors_telefon], centres_t.C_Centre as [centres_codi], centres_t.NOM_Centre AS [centres_nom], centres_t.[Adreça] as [centres_direccio], centres_t.[C_Postal] as [centres_codipostal], centres_t.NOM_Municipi AS [centres_municipi], directors_t.Nom & ' ' & directors_t.[Cognoms] AS [directors_nom], centres_t.TELF as [centres_telefon], [nom_correu] & '@' & [@correu] AS [centres_email], sstt_t.[codi] as [sstt_codi], sstt_t.nom AS [sstt_nom], delegacions_t.Municipi as [delegacions_municipi], delegacions_t.[coordinador 1] as [delegacions_coordinador], delegacions_t.[telf coordinador 1] as [delegacions_telefon_coordinador], sstt_t.[correu_1] as [sstt_correu_1], sstt_t.[correu_2] as [sstt_correu_2]\n" +
        "FROM (((centres_t LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C) INNER JOIN professors_t ON centres_t.C_Centre = professors_t.c_centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[codi]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació];\n" +
        "WHERE professors_nif = ?;"

/* Cada estada te un codi unic del tipus 0001230600/2018-2019 que incorpora lany */
const val findEstadaByCodiEstadaQuery: String = "SELECT estades_t.codi AS estades_codi_estada, estades_t.tipus_estada AS estades_tipus_estada, estades_t.data_inici AS estades_data_inici, estades_t.data_final AS estades_data_final, estades_t.descripcio AS estades_descripcio, estades_t.comentaris AS estades_comentaris, estades_t.nif_empresa AS estades_nif_empresa, estades_t.nom_empresa AS estades_nom_empresa, estades_t.direccio_empresa AS estades_direccio_empresa, estades_t.codi_postal_empresa AS estades_codi_postal_empresa, estades_t.municipi_empresa AS estades_municipi_empresa, estades_t.contacte_nom AS estades_contacte_nom, estades_t.contacte_carrec AS estades_contacte_carrec, estades_t.contacte_telefon AS estades_contacte_telefon, estades_t.contacte_email AS estades_contacte_email, estades_t.tutor_nom AS estades_tutor_nom, estades_t.tutor_carrec AS estades_tutor_carrec, estades_t.tutor_telefon AS estades_tutor_telefon, estades_t.tutor_email AS estades_tutor_email, estades_t.nif_professor AS estades_nif_professor, professors_t.noms AS professors_nom, professors_t.destinacio AS professors_destinacio, professors_t.especialitat AS professors_especialitat, professors_t.email AS professors_email, professors_t.telefon AS professors_telefon, centres_t.C_Centre AS centres_codi_centre, centres_t.NOM_Centre AS centres_nom_centre, centres_t.[Adreça] as [centres_direccio], centres_t.NOM_Municipi AS centres_municipi, centres_t.C_Postal as [centres_codipostal], [directors_t].[Cognoms] & \", \" & [directors_t].[Nom] AS directors_nom_director, centres_t.TELF AS centres_telefon, centres_t.[nom_correu] & \"@\" & [@correu] AS centres_email_centre, delegacions_t.[Codi delegació] AS delegacions_codi_delegacio, delegacions_t.delegació AS delegacions_nom_delegacio, delegacions_t.Municipi AS delegacions_municipi, delegacions_t.[coordinador 1] as [delegacions_cap_de_servei], delegacions_t.[telf coordinador 1] as [delegacions_telefon_cap_de_servei] , sstt_t.[correu_1] AS [sstt_correu_1], sstt_t.[correu_2] as [sstt_correu_2]\n" +
        "FROM ((((estades_t INNER JOIN centres_t ON estades_t.codi_centre = centres_t.C_Centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[codi]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació]) INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif) LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C\n" +
        "WHERE estades_codi_estada = ?;"

//const val findEstadaByNifCursTipusQuery: String = "SELECT estades_t.codi AS estades_codi_estada, estades_t.tipus_estada AS estades_tipus_estada, estades_t.data_inici AS estades_data_inici, estades_t.data_final AS estades_data_final, estades_t.descripcio AS estades_descripcio, estades_t.comentaris AS estades_comentaris, estades_t.nif_empresa AS estades_nif_empresa, estades_t.nom_empresa AS estades_nom_empresa, estades_t.direccio_empresa AS estades_direccio_empresa, estades_t.codi_postal_empresa AS estades_codi_postal_empresa, estades_t.municipi_empresa AS estades_municipi_empresa, estades_t.contacte_nom AS estades_contacte_nom, estades_t.contacte_carrec AS estades_contacte_carrec, estades_t.contacte_telefon AS estades_contacte_telefon, estades_t.contacte_email AS estades_contacte_email, estades_t.tutor_nom AS estades_tutor_nom, estades_t.tutor_carrec AS estades_tutor_carrec, estades_t.tutor_telefon AS estades_tutor_telefon, estades_t.tutor_email AS estades_tutor_email, estades_t.nif_professor AS estades_nif_professor, professors_t.noms AS professors_nom, professors_t.destinacio AS professors_destinacio, professors_t.especialitat AS professors_especialitat, professors_t.email AS professors_email, professors_t.telefon AS professors_telefon, centres_t.C_Centre AS centres_codi_centre, centres_t.NOM_Centre AS centres_nom_centre, centres_t.[Adreça] as [centres_direccio], centres_t.NOM_Municipi AS centres_municipi, centres_t.C_Postal as [centres_codipostal], [directors_t].[Cognoms] & \", \" & [directors_t].[Nom] AS directors_nom_director, centres_t.TELF AS centres_telefon, centres_t.[nom_correu] & \"@\" & [@correu] AS centres_email_centre, delegacions_t.[Codi delegació] AS delegacions_codi_delegacio, delegacions_t.delegació AS delegacions_nom_delegacio, delegacions_t.Municipi AS delegacions_municipi, delegacions_t.[coordinador 1] as [delegacions_cap_de_servei], delegacions_t.[telf coordinador 1] as [delegacions_telefon_cap_de_servei] , sstt_t.[correu_1] AS [sstt_correu_1], sstt_t.[correu_2] as [sstt_correu_2]\n" +
//        "FROM ((((estades_t INNER JOIN centres_t ON estades_t.codi_centre = centres_t.C_Centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[codi]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació]) INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif) LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C\n" +
//        "WHERE estades_t.nif_professor = ? AND estades_t.curs = ? AND estades_t.tipus_estada = ?;"


/*
* codi, curs, nif_professor, codi_centre, nif_empresa,
* nom_empresa, direccio_empresa, codi_postal_empresa, municipi_empresa tipus_estada,
* data_inici, data_final, contacte_nom, contacte_carrec, contacte_telefon, contacte_email,
* tutor_nom, tutor_carrec, tutor_telefon, tutor_email, descripcio, comentaris
* */
const val insertEstadesQuery: String = "INSERT INTO estades_t (codi, curs, nif_professor, codi_centre, nif_empresa, " +
        "nom_empresa, direccio_empresa, codi_postal_empresa, municipi_empresa, tipus_estada, data_inici, " +
        "data_final, contacte_nom, contacte_carrec, contacte_telefon, contacte_email, " +
        "tutor_nom, tutor_carrec, tutor_telefon, tutor_email, descripcio, " +
        "comentaris) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

/* Quan es fan canvis en una estada es fa aquest update */
const val updateEstadesQuery: String = "UPDATE estades_t SET curs = ?, nif_professor = ?, codi_centre = ?, nif_empresa = ?, " +
        "nom_empresa = ?, direccio_empresa = ?, codi_postal_empresa = ?, municipi_empresa = ?, tipus_estada = ?, data_inici = ?, " +
        "data_final = ?, contacte_nom = ?, contacte_carrec = ?, contacte_telefon = ?, contacte_email = ?, " +
        "tutor_nom = ?, tutor_carrec = ?, tutor_telefon = ?, tutor_email = ?, descripcio = ?, " +
        "comentaris = ? WHERE codi = ?"


const val insertSeguimentQuery: String = "INSERT INTO seguiment_t (codi, estat, comentaris) VALUES (?, ?, ?)"

const val queryCandidats = "SELECT [candidats_t].Id AS id, [candidats_t].nif AS nif, [candidats_t].nom AS nom, [candidats_t].email AS email, [candidats_t].curs AS curs\n" +
        "FROM candidats_t ORDER BY [nom];"

const val queryCandidatsProva = "SELECT [candidats_prova_t].Id AS id, [candidats_prova_t].nif AS nif, [candidats_prova_t].nom AS nom, [candidats_prova_t].email AS email, [candidats_prova_t].curs AS curs\n" +
        "FROM candidats_prova_t ORDER BY [nom];"

const val queryAdmesos = "SELECT admesos_t.Id AS id, admesos_t.nif AS nif, admesos_t.nom AS nom, admesos_t.email AS email, admesos_t.curs AS curs \n" +
        "FROM admesos_t WHERE nif = ? AND curs = ?;"

/* Hauria de ser un Singleton */
class GesticusDb {

    lateinit var conn: Connection
    val registres = ArrayList<Registre>()

    init {
        loadDriver()
        connect()
    }

    /* This method loads the ucanaccess driver */
    private fun loadDriver(): Unit {
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver")
    }

    /* This method connects to the microsoft access database  */
    private fun connect(): Unit {
        println("Connecting...")
        conn = DriverManager.getConnection("jdbc:ucanaccess://$PATH_TO_DB;memory=true;openExclusive=false;ignoreCase=true")
        println("Connected to ${conn.metaData.databaseProductName}.")
    }

    /* This method loads all docents, centres and sstt into registres */
    fun preLoadDataFromAccess(): Unit {
        println("Loading data, please wait...")
        val st = conn.createStatement()
        val rs = st.executeQuery(preLoadJoinQuery)
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
                    rs.getString("centres_direccio"),
                    rs.getString("centres_codipostal"),
                    rs.getString("centres_municipi"),
                    rs.getString("directors_nom"),
                    rs.getString("centres_telefon"),
                    rs.getString("centres_email"))

            val sstt = SSTT(
                    rs.getString("sstt_codi"),
                    rs.getString("sstt_nom"),
                    rs.getString("delegacions_municipi"),
                    rs.getString("delegacions_coordinador"),
                    rs.getString("delegacions_telefon_coordinador"),
                    rs.getString("sstt_correu_1"),
                    rs.getString("sstt_correu_2"))

            registres.add(Registre(null, null, docent, centre, sstt))

        }
        println("Data loaded.")
    }

    private fun existsEstada(codi: String): Boolean {
        val estadaSts = conn.prepareStatement(findEstadaByCodiEstadaQuery)
        estadaSts.setString(1, codi)
        val result = estadaSts.executeQuery()
        val ret = result.next()
        estadaSts.closeOnCompletion()
        return ret
    }

    private fun isDocentAdmes(nif: String): Boolean {
        val estadaSts = conn.prepareStatement(queryAdmesos)
        estadaSts.setString(1, nif)
        estadaSts.setString(2, currentCourseYear())
        val result = estadaSts.executeQuery()
        val ret = result.next()
        estadaSts.closeOnCompletion()
        return ret
    }

    /*
    *
    * Guarda o actualitza una estada si ja existeix
    *
    * */
    fun saveEstada(nif: String, estada: Estada, empresa: Empresa): Boolean {

        val ret = true

        if (existsEstada(estada.numeroEstada)) {
            val resp = Alert(Alert.AlertType.CONFIRMATION, "Estada ja existeix, modificar?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL)
                    .showAndWait()
            if (resp.isPresent) {
                if (resp.get() == ButtonType.YES) {
                    updateEstada(nif, estada, empresa)
                }
            }
        } else {
            insertEstada(nif, estada, empresa)
        }
        return ret
    }


    private fun updateEstada(nif: String, estada: Estada, empresa: Empresa): Boolean {
        val estadaSts = conn.prepareStatement(updateEstadesQuery)

        estadaSts.setString(1, currentCourseYear())
        estadaSts.setString(2, nif)
        estadaSts.setString(3, estada.codiCentre)
        estadaSts.setString(4, empresa.identificacio.nif)
        estadaSts.setString(5, empresa.identificacio.nom)
        estadaSts.setString(6, empresa.identificacio.direccio)
        estadaSts.setString(7, empresa.identificacio.cp)
        estadaSts.setString(8, empresa.identificacio.municipi)
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
        estadaSts.setString(22, estada.numeroEstada)

        return try {
            estadaSts.execute()
            val alert = Alert(Alert.AlertType.CONFIRMATION, "$nif afegit correctament")
            alert.showAndWait()
            true
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            false
        } finally {
            estadaSts.closeOnCompletion()
        }

    }

    private fun insertEstada(nif: String, estada: Estada, empresa: Empresa): Boolean {

        if (!isDocentAdmes(nif)) {
            Alert(Alert.AlertType.ERROR, "El/La docent amb NIF $nif no té una estada concedidad").showAndWait()
            return false
        }

        val estadaSts = conn.prepareStatement(insertEstadesQuery)
        estadaSts.setString(1, estada.numeroEstada)
        estadaSts.setString(2, currentCourseYear())
        estadaSts.setString(3, nif)
        estadaSts.setString(4, estada.codiCentre)
        estadaSts.setString(5, empresa.identificacio.nif)
        estadaSts.setString(6, empresa.identificacio.nom)
        estadaSts.setString(7, empresa.identificacio.direccio)
        estadaSts.setString(8, empresa.identificacio.cp)
        estadaSts.setString(9, empresa.identificacio.municipi)
        estadaSts.setString(10, estada.tipusEstada)
        estadaSts.setDate(11, java.sql.Date.valueOf(estada.dataInici))
        estadaSts.setDate(12, java.sql.Date.valueOf(estada.dataFinal))
        estadaSts.setString(13, empresa.personaDeContacte.nom)
        estadaSts.setString(14, empresa.personaDeContacte.carrec)
        estadaSts.setString(15, empresa.personaDeContacte.telefon)
        estadaSts.setString(16, empresa.personaDeContacte.email)
        estadaSts.setString(17, empresa.tutor.nom)
        estadaSts.setString(18, empresa.tutor.carrec)
        estadaSts.setString(19, empresa.tutor.telefon)
        estadaSts.setString(20, empresa.tutor.email)
        estadaSts.setString(21, estada.descripcio)
        estadaSts.setString(22, estada.comentaris)

        return try {
            estadaSts.execute()
            Alert(Alert.AlertType.INFORMATION, "$nif afegit correctament").showAndWait()

            val seguimentSts = conn.prepareStatement(insertSeguimentQuery)
            seguimentSts.setString(1, estada.numeroEstada)
            seguimentSts.setString(2, EstatsSeguimentEstada.REGISTRADA.name)
            seguimentSts.setString(3, "Estada creada el ${LocalDateTime.now().toString()}")

            return try {
                seguimentSts.execute()
                Alert(Alert.AlertType.INFORMATION, "Taula de seguiment actualitzada correctament").showAndWait()
                true

            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
                return false
            } finally {
                seguimentSts.closeOnCompletion()
            }


        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            return false
        } finally {
            estadaSts.closeOnCompletion()
        }

    }


    /*
    * 0001230600/2018-2019
    * SELECT estades_t.codi AS estades_codi_estada,
    * estades_t.tipus_estada AS estades_tipus_estada,
    * estades_t.data_inici AS estades_data_inici,
    * estades_t.data_final AS estades_data_final,
    * estades_t.descripcio AS estades_descripcio,
    * estades_t.comentaris AS estades_comentaris,
    * estades_t.nif_empresa AS estades_nif_empresa,
    * estades_t.nom_empresa AS estades_nom_empresa,
    * estades_t.direccio_empresa AS estades_direccio_empresa,
    * estades_t.codi_postal_empresa AS estades_codi_postal,
    * estades_t.municipi_empresa AS estades_municipi_empresa,
    * estades_t.contacte_nom AS estades_contacte_nom,
    * estades_t.contacte_carrec AS estades_contacte_carrec,
    * estades_t.contacte_telefon AS estades_contacte_telefon,
    * estades_t.contacte_email AS estades_contacte_email,
    * estades_t.tutor_nom AS estades_tutor_nom,
    * estades_t.tutor_carrec AS estades_tutor_carrec,
    * estades_t.tutor_telefon AS estades_tutor_telefon,
    * estades_t.tutor_email AS estades_tutor_email,
    * estades_t.nif_professor AS estades_nif_professor,
    * professors_t.noms AS professors_nom,
    * professors_t.destinacio AS professors_destinacio,
    * professors_t.especialitat AS professors_especialitat,
    * professors_t.email AS professors_email,
    * professors_t.telefon AS professors_telefon,
    * centres_t.C_Centre AS centres_codi_centre,
    * centres_t.NOM_Centre AS centres_nom_centre,
    * centres_t.NOM_Municipi AS centres_municipi,
    * [directors_t].[Cognoms] & \", \" & [directors_t].[Nom] AS directors_nom_director,
    * centres_t.TELF AS centres_telefon,
    * centres_t.[nom_correu] & \"@\" & [@correu] AS centres_email_centre,
    * delegacions_t.[Codi delegació] AS delegacions_codi_delegacio,
    * delegacions_t.delegació AS delegacions_nom_delegacio,
    * delegacions_t.Municipi AS delegacions_municipi,
    * delegacions_t.[coordinador 1] as [delegacions_cap_de_servei],
    * delegacions_t.[telf coordinador 1] as [delegacions_telefon_cap_de_servei] ,
    * delegacions_t.[correu electrònic] AS [stt_correu_1]\n"
    *
    * */
    fun findRegistreByCodiEstada(codiEstada: String): Registre? {

        val estadaSts = conn.prepareStatement(findEstadaByCodiEstadaQuery)
        estadaSts.setString(1, codiEstada)
        val rs = estadaSts.executeQuery()
        // found
        if (rs.next()) {
            with(rs) {
                val estada = Estada(
                        getString("estades_codi_estada"),
                        getString("centres_codi_centre"),
                        getString("estades_tipus_estada"),
                        LocalDate.parse(getString("estades_data_inici").substring(0, 10)),
                        LocalDate.parse(getString("estades_data_final").substring(0, 10)),
                        getString("estades_descripcio"),
                        getString("estades_comentaris"))
                val identificacio = Identificacio(
                        getString("estades_nif_empresa"),
                        getString("estades_nom_empresa"),
                        getString("estades_direccio_empresa"),
                        getString("estades_codi_postal_empresa"),
                        getString("estades_municipi_empresa")
                )
                val contacte = PersonaDeContacte(
                        getString("estades_contacte_nom"),
                        getString("estades_contacte_carrec"),
                        getString("estades_contacte_telefon"),
                        getString("estades_contacte_email")
                )
                val tutor = Tutor(
                        getString("estades_tutor_nom"),
                        getString("estades_tutor_carrec"),
                        getString("estades_tutor_telefon"),
                        getString("estades_tutor_email")
                )
                val empresa = Empresa(identificacio, contacte, tutor)
                val docent = Docent(
                        getString("estades_nif_professor"),
                        getString("professors_nom"),
                        getString("professors_destinacio"),
                        getString("professors_especialitat"),
                        getString("professors_email"),
                        getString("professors_telefon")
                )
                val centre = Centre(
                        getString("centres_codi_centre"),
                        getString("centres_nom_centre"),
                        getString("centres_direccio"),
                        getString("centres_codipostal"),
                        getString("centres_municipi"),
                        getString("directors_nom_director"),
                        getString("centres_telefon"),
                        getString("centres_email_centre")
                )
                val sstt = SSTT(
                        getString("delegacions_codi_delegacio"),
                        getString("delegacions_nom_delegacio"),
                        getString("delegacions_municipi"),
                        getString("delegacions_cap_de_servei"),
                        getString("delegacions_telefon_cap_de_servei"),
                        getString("sstt_correu_1"),
                        getString("sstt_correu_1")
                )
                return Registre(estada, empresa, docent, centre, sstt)
            }

        }
        return null
    }

    /*
    *
    * This method returns a list of emails of those who sent a valid evalisa
    *
    * */
    fun queryCandidats(): List<String> {
        val statement: Statement = conn.createStatement()
        val rs: ResultSet = statement.executeQuery(queryCandidats)
        val candidats = mutableListOf<String>()
        while (rs.next()) {
            val email = rs.getString("email")
            candidats.add(email)
        }
        return candidats
    }

    /*
    *
    * This method returns a list of emails of those who sent a valid evalisa
    * and were selected
    *
    * */
    fun queryAdmesos(): List<String> {
        val statement: Statement = conn.createStatement()
        val rs: ResultSet = statement.executeQuery(queryAdmesos)
        val admesos = mutableListOf<String>()
        while (rs.next()) {
            val email = rs.getString("email")
            admesos.add(email)
        }
        return admesos
    }

    fun findRegistreByNifDocent(nif: String?): Registre? {
        val estadaSts = conn.prepareStatement(findRegistreByNif)
        estadaSts.setString(1, nif)
        val rs = estadaSts.executeQuery()
        // found
        if (rs.next()) {
            with(rs) {
                val estada = Estada(
                        getString("estades_codi_estada"),
                        getString("centres_codi_centre"),
                        getString("estades_tipus_estada"),
                        LocalDate.parse(getString("estades_data_inici").substring(0, 10)),
                        LocalDate.parse(getString("estades_data_final").substring(0, 10)),
                        getString("estades_descripcio"),
                        getString("estades_comentaris"))
                val identificacio = Identificacio(
                        getString("estades_nif_empresa"),
                        getString("estades_nom_empresa"),
                        getString("estades_direccio_empresa"),
                        getString("estades_codi_postal_empresa"),
                        getString("estades_municipi_empresa")
                )
                val contacte = PersonaDeContacte(
                        getString("estades_contacte_nom"),
                        getString("estades_contacte_carrec"),
                        getString("estades_contacte_telefon"),
                        getString("estades_contacte_email")
                )
                val tutor = Tutor(
                        getString("estades_tutor_nom"),
                        getString("estades_tutor_carrec"),
                        getString("estades_tutor_telefon"),
                        getString("estades_tutor_email")
                )
                val empresa = Empresa(identificacio, contacte, tutor)
                val docent = Docent(
                        getString("estades_nif_professor"),
                        getString("professors_nom"),
                        getString("professors_destinacio"),
                        getString("professors_especialitat"),
                        getString("professors_email"),
                        getString("professors_telefon")
                )
                val centre = Centre(
                        getString("centres_codi_centre"),
                        getString("centres_nom_centre"),
                        getString("centres_direccio"),
                        getString("centres_codipostal"),
                        getString("centres_municipi"),
                        getString("directors_nom_director"),
                        getString("centres_telefon"),
                        getString("centres_email_centre")
                )
                val sstt = SSTT(
                        getString("delegacions_codi_delegacio"),
                        getString("delegacions_nom_delegacio"),
                        getString("delegacions_municipi"),
                        getString("delegacions_cap_de_servei"),
                        getString("delegacions_telefon_cap_de_servei"),
                        getString("sstt_correu_1"),
                        getString("sstt_correu_1")
                )
                return Registre(estada, empresa, docent, centre, sstt)
            }

        }
        return null
    }

    fun close(): Unit {
        println("Closing connection.")
        conn.close()
    }


}
