package cat.gencat.access.db

import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.*
import cat.gencat.access.model.*
import cat.gencat.access.reports.GesticusReports
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextInputDialog
import java.sql.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.Date


/*

Use a LEFT JOIN operation to create a left outer join. Left outer joins include all of the records from the first (left) of two tables, even if there are no matching values for records in the second (right) table.

Use a RIGHT JOIN operation to create a right outer join. Right outer joins include all of the records from the second (right) of two tables, even if there are no matching values for records in the first (left) table.

For example, you could use LEFT JOIN with the Departments (left) and Employees (right) tables to select all departments, including those that have no employees assigned to them. To select all employees, including those who are not assigned to a department, you would use RIGHT JOIN.

The following example shows how you could join the Categories and Products tables on the CategoryID field. The query produces a list of all categories, including those that contain no products:

SELECT CategoryName, ProductName FROM Categories LEFT JOIN Products ON Categories.CategoryID = Products.CategoryID;

You can use an INNER JOIN operation in any FROM clause. This is the most common type of join. Inner joins combine records from two tables whenever there are matching values in a field common to both tables.

You can use INNER JOIN with the Departments and Employees tables to select all the employees in each department. In contrast, to select all departments (even if some have no employees assigned to them) or all employees (even if some are not assigned to a department), you can use a LEFT JOIN or RIGHT JOIN operation to create an outer join.

The following example shows how you could join the Categories and Products tables on the CategoryID field:

 SELECT CategoryName, ProductName
FROM Categories INNER JOIN Products
ON Categories.CategoryID = Products.CategoryID;

A LEFT JOIN or a RIGHT JOIN may be nested inside an INNER JOIN, but an INNER JOIN may not be nested inside a LEFT JOIN or a RIGHT JOIN.


<Application ts="1493453326772" uri="http://fxldemo.tornado.no/" launch="no.tornado.FxlDemo">
<lib file="controlsfx.jar" checksum="901192049" size="985420"/>
<lib file="fxldemo-2.0.jar" checksum="1416987018" size="7051"/>
<updateText>Updating...</updateText>
<updateLabelStyle>-fx-font-weight: bold;</updateLabelStyle>
<progressBarStyle>-fx-pref-width: 200;</progressBarStyle>
<wrapperStyle>-fx-spacing: 10; -fx-padding: 25;</wrapperStyle>
<parameters>--myOption=myValue --myOtherOption=myOtherValue</parameters>
<cacheDir>USERLIB/FxlDemo</cacheDir>
<acceptDowngrade>false</acceptDowngrade>
<lingeringUpdateScreen>false</lingeringUpdateScreen>
</Application>

 */

const val DRIVER_NAME = "net.ucanaccess.jdbc.UcanaccessDriver"

/* Tots els docents, centres, sstts */
const val preLoadJoinQuery: String =
        "SELECT professors_t.nif as [professors_nif], professors_t.noms as [professors_noms], iif(professors_t.sexe = 'H', 'Sr. ', 'Sra. ') & professors_t.nom & ' ' & professors_t.cognom_1 & ' ' & professors_t.cognom_2 as [professors_nom_amb_tractament], professors_t.destinacio as [professors_destinacio], professors_t.especialitat as [professors_especialitat], professors_t.email AS [professors_email], professors_t.telefon as [professors_telefon], centres_t.C_Centre as [centres_codi], centres_t.NOM_Centre AS [centres_nom], centres_t.[Adreça] as [centres_direccio], centres_t.[C_Postal] as [centres_codipostal], centres_t.NOM_Municipi AS [centres_municipi], directors_t.[tractament (Sr_Sra)] as [directors_tractament], (directors_t.carrec & ' ' & directors_t.Nom & ' ' & directors_t.[Cognoms]) AS [directors_nom], centres_t.TELF as [centres_telefon], [nom_correu] & '@' & [@correu] AS [centres_email], sstt_t.[codi] as [sstt_codi], sstt_t.nom AS [sstt_nom], delegacions_t.Municipi as [delegacions_municipi], delegacions_t.[coordinador 1] as [delegacions_coordinador], delegacions_t.[telf coordinador 1] as [delegacions_telefon_coordinador], sstt_t.[correu_1] as [sstt_correu_1], sstt_t.[correu_2] as [sstt_correu_2]\n" +
                "FROM (((centres_t LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C) INNER JOIN professors_t ON centres_t.C_Centre = professors_t.c_centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[codi]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació];\n"

/* Find Centre and associated SSTT by Centre.codi */
const val findCentreAndSSTTByCentreCodiQuery: String =
        "SELECT centres_t.C_Centre as [centres_codi], centres_t.NOM_Centre AS [centres_nom], centres_t.[Adreça] as [centres_direccio], centres_t.[C_Postal] as [centres_codipostal], centres_t.NOM_Municipi AS [centres_municipi], (directors_t.carrec & ' ' & directors_t.Nom & ' ' & directors_t.[Cognoms]) AS [directors_nom], centres_t.TELF as [centres_telefon], [nom_correu] & '@' & [@correu] AS [centres_email], sstt_t.[codi] as [sstt_codi], sstt_t.nom AS [sstt_nom], delegacions_t.Municipi as [delegacions_municipi], delegacions_t.[coordinador 1] as [delegacions_coordinador], delegacions_t.[telf coordinador 1] as [delegacions_telefon_coordinador], sstt_t.[correu_1] as [sstt_correu_1], sstt_t.[correu_2] as [sstt_correu_2]\n" +
                "FROM (((centres_t LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C)) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[codi]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació] \n " +
                "WHERE centres_t.[C_Centre] = ?;"

/* Find EditableSSTT by SSTT.codi */
const val findSSTTBySSTTCodiQuery: String =
        "SELECT sstt_t.[codi] as [sstt_codi], sstt_t.nom AS [sstt_nom], delegacions_t.Municipi as [delegacions_municipi], delegacions_t.[coordinador 1] as [delegacions_coordinador], delegacions_t.[telf coordinador 1] as [delegacions_telefon_coordinador], sstt_t.[correu_1] as [sstt_correu_1], sstt_t.[correu_2] as [sstt_correu_2]\n" +
                "FROM sstt_t LEFT JOIN delegacions_t ON sstt_t.[codi] = delegacions_t.[Codi delegació] \n" +
                "WHERE sstt_t.[codi] = ?;"

/* Find all SSTT */
const val findAllSSTTQuery: String =
        "SELECT sstt_t.[codi] as [sstt_codi], sstt_t.nom AS [sstt_nom], sstt_t.[correu_1] as [sstt_correu_1], sstt_t.[correu_2] as [sstt_correu_2] FROM sstt_t;"

const val findAllAdmesosQuery: String =
        "SELECT admesos_t.[nif] as [admesos_nif], admesos_t.nom AS [admesos_nom], admesos_t.[email] as [admesos_email], admesos_t.[curs] as [admesos_curs], admesos_t.[baixa] as [admesos_baixa] FROM admesos_t;"

/* docent, centre i sstt d'un nif concret nif en forma 099999999A */
const val findRegistreByNif: String =
        "SELECT professors_t.nif as [professors_nif], professors_t.noms as [professors_noms], iif(professors_t.sexe = 'H', 'Sr. ', 'Sra. ') & professors_t.nom & ' ' & professors_t.cognom_1 & ' ' & professors_t.cognom_2 as [professors_nom_amb_tractament], professors_t.destinacio as [professors_destinacio], professors_t.especialitat as [professors_especialitat], professors_t.email AS [professors_email], professors_t.telefon as [professors_telefon], centres_t.C_Centre as [centres_codi], centres_t.NOM_Centre AS [centres_nom], centres_t.[Adreça] as [centres_direccio], centres_t.[C_Postal] as [centres_codipostal], centres_t.NOM_Municipi AS [centres_municipi], directors_t.carrec & ' ' & directors_t.Nom & ' ' & directors_t.[Cognoms] AS [directors_nom], centres_t.TELF as [centres_telefon], [nom_correu] & '@' & [@correu] AS [centres_email], sstt_t.[codi] as [sstt_codi], sstt_t.nom AS [sstt_nom], delegacions_t.Municipi as [delegacions_municipi], delegacions_t.[coordinador 1] as [delegacions_coordinador], delegacions_t.[telf coordinador 1] as [delegacions_telefon_coordinador], sstt_t.[correu_1] as [sstt_correu_1], sstt_t.[correu_2] as [sstt_correu_2]\n" +
                "FROM (((centres_t LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C) INNER JOIN professors_t ON centres_t.C_Centre = professors_t.c_centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[codi]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació] \n" +
                "WHERE professors_t.nif = ?;"

/* Cada estada te un codi unic del tipus 0001230600/2018-2019 que incorpora lany */
const val findEstadaByCodiEstadaQuery: String =
        "SELECT estades_t.codi AS estades_codi, estades_t.tipus_estada AS estades_tipus_estada, estades_t.data_inici AS estades_data_inici, estades_t.data_final AS estades_data_final, estades_t.descripcio AS estades_descripcio, estades_t.comentaris AS estades_comentaris, estades_t.nif_empresa AS estades_nif_empresa, estades_t.nom_empresa AS estades_nom_empresa, estades_t.direccio_empresa AS estades_direccio_empresa, estades_t.codi_postal_empresa AS estades_codi_postal_empresa, estades_t.municipi_empresa AS estades_municipi_empresa, estades_t.contacte_nom AS estades_contacte_nom, estades_t.contacte_carrec AS estades_contacte_carrec, estades_t.contacte_telefon AS estades_contacte_telefon, estades_t.contacte_email AS estades_contacte_email, estades_t.tutor_nom AS estades_tutor_nom, estades_t.tutor_carrec AS estades_tutor_carrec, estades_t.tutor_telefon AS estades_tutor_telefon, estades_t.tutor_email AS estades_tutor_email, estades_t.nif_professor AS estades_nif_professor, professors_t.noms AS professors_nom, iif(professors_t.sexe = 'H', 'Sr. ', 'Sra. ') & professors_t.nom & ' ' & professors_t.cognom_1 & ' ' & professors_t.cognom_2 as [professors_nom_amb_tractament], professors_t.destinacio AS professors_destinacio, professors_t.especialitat AS professors_especialitat, professors_t.email AS professors_email, professors_t.telefon AS professors_telefon, centres_t.C_Centre AS centres_codi, centres_t.NOM_Centre AS centres_nom, centres_t.[Adreça] as [centres_direccio], centres_t.NOM_Municipi AS centres_municipi, centres_t.C_Postal as [centres_codipostal], directors_t.carrec & ' ' & [directors_t].[Cognoms] & ',' & [directors_t].[Nom] AS directors_nom_director, centres_t.TELF AS centres_telefon, centres_t.[nom_correu] & \"@\" & [@correu] AS centres_email_centre, delegacions_t.[Codi delegació] AS delegacions_codi_delegacio, delegacions_t.delegació AS delegacions_nom_delegacio, delegacions_t.Municipi AS delegacions_municipi, delegacions_t.[coordinador 1] as [delegacions_cap_de_servei], delegacions_t.[telf coordinador 1] as [delegacions_telefon_cap_de_servei] , sstt_t.[nom] AS [sstt_nom], sstt_t.[correu_1] AS [sstt_correu_1], sstt_t.[correu_2] as [sstt_correu_2]\n" +
                "FROM ((((estades_t INNER JOIN centres_t ON estades_t.codi_centre = centres_t.C_Centre) INNER JOIN sstt_t ON centres_t.C_Delegació = sstt_t.[codi]) LEFT JOIN delegacions_t ON centres_t.C_Delegació = delegacions_t.[Codi delegació]) INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif) LEFT JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C\n" +
                "WHERE estades_t.codi = ?;"


const val findEstadaCodiByNifQuery: String =
        "SELECT estades_t.codi AS estades_codi FROM estades_t WHERE estades_t.nif_professor = ? AND estades_t.curs = ?;"


/* DESC perque volem la ultima */
const val findLastEstadaNumberQuery: String = "SELECT estades_t.codi AS estades_codi " +
        "FROM estades_t ORDER BY [estades_codi] DESC LIMIT 1;"

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
const val updateEstadesQuery: String =
        "UPDATE estades_t SET curs = ?, nif_professor = ?, codi_centre = ?, nif_empresa = ?, " +
                "nom_empresa = ?, direccio_empresa = ?, codi_postal_empresa = ?, municipi_empresa = ?, tipus_estada = ?, data_inici = ?, " +
                "data_final = ?, contacte_nom = ?, contacte_carrec = ?, contacte_telefon = ?, contacte_email = ?, " +
                "tutor_nom = ?, tutor_carrec = ?, tutor_telefon = ?, tutor_email = ?, descripcio = ?, " +
                "comentaris = ? WHERE codi = ?"

const val estadesAndSeguimentQuery =
        "SELECT [estades_t].codi as estades_codi, [estades_t].nif_professor as estades_nif, [estades_t].curs as [estades_curs], [seguiment_t].estat as seguiment_estat, [seguiment_t].data as seguiment_data FROM estades_t LEFT JOIN seguiment_t ON [estades_t].codi = [seguiment_t].codi ORDER BY [estades_t].nif_professor ASC;"

const val findNomAndEmailByNIFQuery =
        "SELECT admesos_t.nif as [admesos_nif], professors_t.email as [professors_email], iif(professors_t.sexe = 'H', 'Benvolgut ', 'Benvolguda ') &  professors_t.nom & ','  as [professors_tracte] FROM admesos_t INNER JOIN professors_t ON admesos_t.nif = professors_t.nif WHERE  admesos_t.nif = ?;"
/*
* Totes les gestionades
* */
const val allEstadesQuery =
        "SELECT [estades_t].codi as estades_codi, estades_t.nom_empresa AS estades_nom_empresa, [estades_t].curs as [estades_curs], [estades_t].data_inici as [estades_data_inici], [estades_t].data_final as [estades_data_final], iif(professors_t.sexe = 'H', 'Sr. ', 'Sra. ') & professors_t.nom & ' ' & professors_t.cognom_1 & ' ' & professors_t.cognom_2 as [professors_nom_amb_tractament], professors_t.email AS professors_email FROM [estades_t] LEFT JOIN [professors_t] ON [estades_t].nif_professor = [professors_t].nif WHERE [estades_t].curs = ?;"

const val estadesByNifQuery =
        "SELECT [estades_t].codi as estades_codi, [estades_t].nif_professor as estades_nif_professor, [estades_t].curs as [estades_curs], [estades_t].nom_empresa as [estades_nom_empresa], [estades_t].data_inici as [estades_data_inici], [estades_t].data_final as [estades_data_final],  [professors_t].noms as professors_noms, iif(professors_t.sexe = 'H', 'Sr. ', 'Sra. ') & professors_t.nom & ' ' & professors_t.cognom_1 & ' ' & professors_t.cognom_2 as [professors_nom_amb_tractament] FROM estades_t LEFT JOIN professors_t ON [estades_t].nif_professor = [professors_t].nif WHERE [estades_t].nif_professor LIKE ? ORDER BY [estades_t].curs, [estades_t].nif_professor ASC;"

/*
*
* SELECT estades_t.codi, estades_t.curs, estades_t.nif_professor, seguiment_t.data, seguiment_t.estat
FROM estades_t INNER JOIN seguiment_t ON estades_t.codi = seguiment_t.codi;

* */
const val seguimentForCodiEstadaQuery =
        "SELECT [seguiment_t].id as [seguiment_id], [seguiment_t].estat as [seguiment_estat], [seguiment_t].comentaris as [seguiment_comentaris], [seguiment_t].data as [seguiment_data] FROM seguiment_t WHERE [seguiment_t].codi = ? ORDER BY [seguiment_t].codi ASC;"

const val lastSeguimentForCodiEstadaQuery =
        "SELECT top 1 seguiment_t.id, [seguiment_t].estat as [seguiment_estat], [seguiment_t].data as [seguiment_data] FROM seguiment_t WHERE [seguiment_t].codi = ? ORDER BY [seguiment_t].id DESC"

const val estadesByCodiEstadaQuery =
        "SELECT [estades_t].codi as estades_codi, [estades_t].nif_professor as estades_nif_professor, [estades_t].curs as [estades_curs] FROM estades_t WHERE estades_t.[codi] = ?;"

/* codi, estat, data, comentaris */
const val insertSeguimentQuery: String = "INSERT INTO seguiment_t (codi, estat, comentaris) VALUES (?, ?, ?)"

const val allCandidatsQuery =
        "SELECT [candidats_t].Id AS id, [candidats_t].nif AS nif, [candidats_t].nom AS nom, [candidats_t].email AS email, [candidats_t].curs AS curs\n" +
                "FROM candidats_t ORDER BY [nom];"

//const val queryCandidatsProva = "SELECT [candidats_prova_t].Id AS id, [candidats_prova_t].nif AS nif, [candidats_prova_t].nom AS nom, [candidats_prova_t].email AS email, [candidats_prova_t].curs AS curs\n" +
//        "FROM candidats_prova_t ORDER BY [nom];"

const val admesosByNifQuery =
        "SELECT admesos_t.Id AS id, admesos_t.nif AS nif, admesos_t.nom AS nom, admesos_t.email AS email, admesos_t.curs AS curs \n" +
                "FROM admesos_t WHERE nif LIKE ? AND curs = ?;"

const val admesosByNameQuery =
        "SELECT admesos_t.Id AS id, admesos_t.nif AS nif, admesos_t.nom AS nom, admesos_t.email AS email, admesos_t.curs AS curs \n" +
                "FROM admesos_t WHERE nom LIKE ? AND curs = ?;"

const val admesosGetBaixaEstatQuery =
        "SELECT admesos_t.nom as [admesos_nom], admesos_t.baixa as [admesos_baixa] FROM admesos_t \n" +
                "WHERE admesos_t.nif = ? AND admesos_t.curs = ?;"

const val admesosSetBaixaToTrueFalseQuery = "UPDATE admesos_t SET admesos_t.baixa = ? \n" +
        "WHERE admesos_t.nif = ? AND admesos_t.curs = ?;"

/* Quan es fan canvis en una estada es fa aquest update */
const val updateSSTTQuery: String =
        "UPDATE sstt_t SET sstt_t.correu_1 = ?, sstt_t.correu_2 = ? WHERE sstt_t.codi = ?"

const val updateAdmesosQuery: String =
        "UPDATE admesos_t SET admesos_t.email = ? WHERE admesos_t.nif = ?"

const val findAllSanitarisSenseEstadaQuery =
        "SELECT admesos_t.nif AS admesos_nif, professors_t.tractament AS professors_tractament, professors_t.nom AS professors_nom, professors_t.cognom_1 AS professors_cognom_1, professors_t.cognom_2 AS professors_cognom_2, professors_t.familia AS professors_familia, professors_t.especialitat AS professors_especialitat, admesos_t.email AS admesos_email, professors_t.sexe AS professors_sexe, professors_t.centre AS professors_centre, professors_t.municipi AS professors_municipi, professors_t.delegacio_territorial AS professors_delegacio_territorial, professors_t.telefon AS professors_telefon, admesos_t.baixa AS admesos_baixa, estades_t.codi AS estades_codi\n" +
                "FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor) LEFT JOIN professors_t ON admesos_t.nif = professors_t.nif\n" +
                "WHERE (((professors_t.familia) = ?) AND ((admesos_t.baixa)=False) AND ((estades_t.codi) Is Null))\n" +
                "ORDER BY professors_t.delegacio_territorial;\n"

const val countTotalAdmesosQuery =
        "SELECT Count(admesos_t.nif) AS [admesos_total]\n" +
                "FROM admesos_t\n" +
                "WHERE admesos_t.curs = ?;"

const val countBaixesFromAdmesosQuery =
        "SELECT Count(admesos_t.nif) AS [admesos_baixes]\n" +
                "FROM admesos_t\n" +
                "WHERE admesos_t.baixa=True AND admesos_t.curs = ?;"

const val countTotalEstadesQuery =
        "SELECT Count(estades_t.codi) AS [estades_total]\n" +
                "FROM estades_t WHERE  estades_t.curs = ? \n"

const val countTotalEstadesPerCentreQuery =
        "SELECT top 10 centres_t.NOM_Centre AS nom_centre, Count(estades_t.codi) AS total_estades\n" +
                "FROM estades_t INNER JOIN centres_t ON estades_t.codi_centre = centres_t.C_Centre\n" +
                "WHERE estades_t.curs = ?\n" +
                "GROUP BY centres_t.NOM_Centre\n" +
                "ORDER BY  Count(estades_t.codi) DESC;"

const val countTotalEstadesNoGestionadesPerCentreQuery =
        "SELECT  TOP 10 centres_t.NOM_Centre AS nom_centre, Count(centres_t.C_Centre) AS total_estades\n" +
                "FROM professors_t INNER JOIN centres_t ON professors_t.c_centre = centres_t.C_Centre\n" +
                "WHERE professors_t.nif IN\n" +
                "(SELECT admesos_t.nif FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor) \n" +
                "WHERE admesos_t.baixa = FALSE AND estades_t.codi IS NULL)\n" +
                "GROUP BY centres_t.NOM_Centre\n" +
                "ORDER BY Count(centres_t.C_Centre) DESC;"

const val countTotalEstadesPerFamiliaQuery =
        "SELECT TOP 10 professors_t.familia AS nom_familia, COUNT(estades_t.codi) AS total_estades\n" +
                "FROM estades_t INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif\n" +
                "WHERE (((estades_t.curs) = ?))\n" +
                "GROUP BY professors_t.familia\n" +
                "ORDER BY Count(estades_t.codi) DESC;"

const val countTotalEstadesNoGestionadesPerFamiliaQuery =
        "SELECT TOP 10 professors_t.familia as nom_familia, COUNT(professors_t.nif) AS total_estades\n" +
                "FROM professors_t \n" +
                "WHERE professors_t.nif IN\n" +
                "(SELECT admesos_t.nif FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor) \n" +
                "WHERE admesos_t.baixa = FALSE AND estades_t.codi IS NULL)\n" +
                "GROUP BY  professors_t.familia\n" +
                "ORDER BY  count(professors_t.nif) DESC"

const val countTotalEstadesPerSSTTQuery =
        "SELECT sstt_t.nom, Count(estades_t.codi) AS total_estades\n" +
                "FROM (estades_t INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif) INNER JOIN sstt_t ON professors_t.c_sstt = sstt_t.codi\n" +
                "WHERE (((estades_t.curs)= ?))\n" +
                "GROUP BY sstt_t.nom\n" +
                "ORDER BY Count(estades_t.codi) DESC;"

const val countTotalEstadesNoGestionadesPerSSTTQuery =
        "SELECT sstt_t.nom, Count(professors_t.nif) AS total_estades\n" +
                "FROM (admesos_t LEFT JOIN professors_t ON admesos_t.nif = professors_t.nif) LEFT JOIN sstt_t ON professors_t.c_sstt = sstt_t.codi\n" +
                "WHERE sstt_t.nom IS NOT NULL AND professors_t.nif IN\n" +
                "(SELECT admesos_t.nif FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor) WHERE admesos_t.baixa = FALSE AND estades_t.codi IS NULL )\n" +
                "GROUP BY sstt_t.nom\n" +
//                "HAVING sstt_t.nom <> Null\n" +
                "ORDER BY Count(professors_t.nif) DESC;"

const val countTotalEstadesPerSexeQuery =
        "SELECT professors_t.sexe AS professors_sexe, Count(estades_t.codi) AS total_estades\n" +
                "FROM estades_t INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif\n" +
                "WHERE (((estades_t.curs) = ?))\n" +
                "GROUP BY professors_t.sexe\n" +
                "ORDER BY Count(estades_t.codi) DESC;"

const val countTotalEstadesNoGestionadesPerSexeQuery =
        "SELECT professors_t.sexe, Count(professors_t.nif) AS total_estades\n" +
                "FROM (admesos_t LEFT JOIN professors_t ON admesos_t.nif = professors_t.nif)\n" +
                "WHERE professors_t.nif IN\n" +
                "(SELECT admesos_t.nif FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor) WHERE admesos_t.baixa = FALSE AND estades_t.codi IS NULL )\n" +
                "GROUP BY professors_t.sexe\n" +
                "ORDER BY  Count(professors_t.nif) DESC;"

const val countTotalEstadesPerCosQuery =
        "SELECT professors_t.cos AS professors_cos, Count(estades_t.codi) AS total_estades\n" +
                "FROM estades_t INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif\n" +
                "WHERE (((estades_t.curs) = ?))\n" +
                "GROUP BY professors_t.cos\n" +
                "ORDER BY Count(estades_t.codi) DESC;"

const val countTotalEstadesNoGestionadesPerCosQuery =
        "SELECT professors_t.cos AS professors_cos, Count(professors_t.nif) AS total_estades\n" +
                "FROM professors_t\n" +
                "WHERE professors_t.nif IN\n" +
                "(SELECT admesos_t.nif FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor)  WHERE admesos_t.baixa = FALSE AND estades_t.codi IS NULL)\n" +
                "GROUP BY professors_t.cos\n" +
                "ORDER BY Count(professors_t.nif) DESC;\n"


const val allFamiliesQuery =
        "SELECT DISTINCT professors_t.familia\n" +
                "FROM admesos_t INNER JOIN professors_t ON admesos_t.nif = professors_t.nif\n" +
                "WHERE professors_t.nif IN\n" +
                "(SELECT admesos_t.nif FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor)  WHERE estades_t.codi IS NULL)\n" +
                "ORDER BY professors_t.familia;\n"


const val estadesPendentsPerFamiliaQuery =
        "SELECT admesos_t.nif AS professors_nif, professors_t.tractament AS professors_tractament, professors_t.noms AS professors_nom, professors_t.telefon AS professors_telefon, professors_t.email AS professors_email, professors_t.especialitat AS professors_especialitat, centres_t.NOM_Municipi AS centres_municipi, centres_t.NOM_Centre AS centres_nom\n" +
                "FROM (admesos_t INNER JOIN professors_t ON admesos_t.nif = professors_t.nif) INNER JOIN centres_t ON professors_t.c_centre = centres_t.C_Centre\n" +
                "WHERE (((professors_t.familia)= ?) AND ((professors_t.nif) In (SELECT admesos_t.nif FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor) \n" +
                "WHERE admesos_t.baixa = FALSE AND estades_t.codi IS NULL)))\n" +
                "ORDER BY professors_t.especialitat, centres_t.NOM_Municipi, centres_t.NOM_Centre;"

/*
*
* TODO("Cal notificar els directors de cada centre una relació de docents que han sol·licitat una estada B abans de tancar la llista provisional")
*
* */
const val relacioSolicitudsEstadesPerCentreQuery =
        "SELECT centres_t.NOM_Centre, centres_t.Correu_electronic, directors_t.[tractament (Sr_Sra)], directors_t.Nom, directors_t.Cognoms, professors_t.tractament, candidats_t.nom, professors_t.familia, professors_t.especialitat\n" +
                "FROM (candidats_t INNER JOIN professors_t ON candidats_t.nif = professors_t.nif) INNER JOIN (centres_t INNER JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C) ON professors_t.c_centre = centres_t.C_Centre\n" +
                "ORDER BY centres_t.NOM_Centre;\n"

/* Hauria de ser un Singleton */
object GesticusDb {

    lateinit var conn: Connection
    val registres = ArrayList<Registre>()

    init {
        loadDriver()
        connect()
    }

    /* This method loads the ucanaccess driver */
    private fun loadDriver(): Unit {
        Class.forName(DRIVER_NAME)
        writeToLog("${LocalDate.now()} Driver $DRIVER_NAME loaded")
    }

    /* This method connects to the microsoft access database  */
    private fun connect(): Unit {
        writeToLog("${LocalDate.now()} Connecting...")
        conn =
                DriverManager.getConnection("jdbc:ucanaccess://$PATH_TO_DB;memory=true;openExclusive=false;ignoreCase=true")
        writeToLog("${LocalDate.now()}  Connected to ${conn.metaData.databaseProductName}.")
    }

    /* This method loads all docents, centres and sstt's from db into registres */
    fun preLoadDataFromAccess(): Unit {
        writeToLog("${LocalDate.now()} Loading data, please wait...")
        val st = conn.createStatement()
        val rs = st.executeQuery(preLoadJoinQuery)
        while (rs.next()) {

            val docent = Docent(
                    rs.getString("professors_nif"),
                    rs.getString("professors_nom_amb_tractament"),
                    rs.getString("professors_destinacio"),
                    rs.getString("professors_especialitat"),
                    rs.getString("professors_email"),
                    rs.getString("professors_telefon")
            )

            val centre = Centre(
                    rs.getString("centres_codi"),
                    rs.getString("centres_nom"),
                    rs.getString("centres_direccio"),
                    rs.getString("centres_codipostal"),
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
                    rs.getString("sstt_correu_1"),
                    rs.getString("sstt_correu_2")
            )

            registres.add(Registre(null, null, docent, centre, sstt))

        }
        writeToLog("${LocalDate.now()} Data loaded.")
    }

    /* Mira si ya existeix un estada(codi) */
    private fun existsEstada(codi: String): Boolean {
        val estadaSts = conn.prepareStatement(findEstadaByCodiEstadaQuery)
        estadaSts.setString(1, codi)
        val result = estadaSts.executeQuery()
        val ret = result.next()
        estadaSts.closeOnCompletion()
        return ret
    }

    /* Nomels els docents a amdemos_t poden fer gestionades */
    private fun isDocentAdmes(nif: String): Boolean {
        val estadaSts = conn.prepareStatement(admesosByNifQuery)
        estadaSts.setString(1, nif)
        estadaSts.setString(2, currentCourseYear())
        val result = estadaSts.executeQuery()
        val ret = result.next()
        estadaSts.closeOnCompletion()
        return ret
    }

    /* Guarda o actualitza una estada si ja existeix */
    fun saveEstada(registre: Registre): Boolean {

        val nif: String = registre.docent!!.nif
        val estada: Estada = registre.estada!!
        val empresa: Empresa = registre.empresa!!

        if (!isDocentAdmes(nif)) {
            Alert(Alert.AlertType.ERROR, "El/La docent amb NIF $nif no té una estada concedidad")
                    .show()
            return false
        }

        val ret = true

        if (existsEstada(estada.numeroEstada)) {
            Alert(
                    Alert.AlertType.CONFIRMATION,
                    "L'estada ${estada.numeroEstada} ja existeix, vols modificar-la?",
                    ButtonType.YES,
                    ButtonType.NO,
                    ButtonType.CANCEL
            )
                    .showAndWait()
                    .ifPresent {
                        if (it == ButtonType.YES) {
                            updateEstada(nif, estada, empresa)
                        }
                    }

        } else {
            insertEstada(registre)
        }
        return ret
    }

    /* updateEstadesQuery */
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
            val alert = Alert(Alert.AlertType.CONFIRMATION, "L'estada de $nif ha estat modificada correctament")
            alert.showAndWait()
            true
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            false
        } finally {
            estadaSts.closeOnCompletion()
        }

    }

    fun existeixNumeroDeEstada(numeroEstada: String): Boolean {
        val existsEstadaStatement = conn.prepareStatement(estadesByCodiEstadaQuery)
        existsEstadaStatement.setString(1, numeroEstada)
        val rs = existsEstadaStatement.executeQuery()
        val existsEstada = rs.next()
        rs.close()
        return existsEstada
    }

    fun insertSeguimentDeEstada(numeroEstada: String, estat: EstatsSeguimentEstadaEnum, comentaris: String): Boolean {

        if (!existeixNumeroDeEstada(numeroEstada)) {
            // Alert(Alert.AlertType.ERROR, "No existeix cap estada amb número $numeroEstada").showAndWait()
            return false
        }
        val seguimentSts = conn.prepareStatement(insertSeguimentQuery)
        seguimentSts.setString(1, numeroEstada)
        seguimentSts.setString(2, estat.name)
        seguimentSts.setString(3, comentaris)

        return try {
            val count = seguimentSts.executeUpdate()
            if (count == 1) {
                val registre = findRegistreByCodiEstada(numeroEstada)
                val nif = registre?.docent?.nif!!
                val emailAndTracte = findEmailAndTracteByNif(nif)
                when (estat) {
                    EstatsSeguimentEstadaEnum.REGISTRADA -> {
                        Alert(
                                Alert.AlertType.CONFIRMATION,
                                "Estada ${registre.estada?.numeroEstada} afegida correctament. Vols enviar un correu de confirmació a ${registre!!.docent!!.nom}?"
                        )
                                .showAndWait()
                                .ifPresent {
                                    if (it == ButtonType.OK) {
                                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                                SUBJECT_GENERAL,
                                                BODY_ALTA
                                                        .replace("?1", emailAndTracte?.second ?: "Benvolgut/da,")
                                                        .replace("?2", numeroEstada)
                                                ,
                                                listOf(),
                                                listOf<String>(CORREU_LOCAL1, emailAndTracte?.first.orEmpty())
                                        )
                                        infoNotification(
                                                APP_TITLE,
                                                "S'ha enviat un correu de confirmació d'estada $numeroEstada registrada a ${registre!!.docent!!.nom}"
                                        )
                                    }
                                }
                    }
                    /* Una estada queda comunicada quan s'envia una carta a Docent, Centre, Empresa i SSTT */
                    EstatsSeguimentEstadaEnum.COMUNICADA -> {
                    }
                    EstatsSeguimentEstadaEnum.INICIADA -> {
                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                SUBJECT_GENERAL,
                                BODY_INICIADA
                                        .replace("?1", emailAndTracte?.second ?: "Benvolgut/da,")
                                        .replace("?2", numeroEstada)
                                ,
                                listOf(),
                                listOf<String>(CORREU_LOCAL1, emailAndTracte!!.first)
                        )
                        infoNotification(
                                APP_TITLE,
                                "S'ha enviat un correu de confirmació d'estada $numeroEstada iniciada a ${registre!!.docent!!.nom}"
                        )
                    }
                    EstatsSeguimentEstadaEnum.ACABADA -> {
                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                SUBJECT_GENERAL,
                                BODY_ACABADA
                                        .replace("?1", emailAndTracte?.second ?: "Benvolgut/da,")
                                        .replace("?2", numeroEstada)
                                ,
                                listOf(),
                                listOf<String>(CORREU_LOCAL1, emailAndTracte!!.first)
                        )
                        infoNotification(
                                APP_TITLE,
                                "S'ha enviat un correu de confirmació d'estada $numeroEstada acabada a ${registre?.docent?.nom}"
                        )
                    }
                    EstatsSeguimentEstadaEnum.DOCUMENTADA -> {
                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                SUBJECT_GENERAL,
                                BODY_DOCUMENTADA
                                        .replace("?1", emailAndTracte?.second ?: "Benvolgut/da,")
                                        .replace("?2", numeroEstada)
                                ,
                                listOf(),
                                listOf<String>(CORREU_LOCAL1, emailAndTracte!!.first)
                        )
                        infoNotification(
                                APP_TITLE,
                                "S'ha enviat un correu de confirmació d'estada $numeroEstada documentada a ${registre?.docent?.nom}"
                        )
                    }
                    EstatsSeguimentEstadaEnum.BAIXA -> {

                    }
                    EstatsSeguimentEstadaEnum.TANCADA -> {
                        // TODO("BODY_TANCADA pending")
                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                SUBJECT_GENERAL,
                                BODY_TANCADA
                                        .replace("?1", emailAndTracte?.second ?: "Benvolgut/da,")
                                        .replace("?2", numeroEstada),
                                listOf(),
                                listOf<String>(CORREU_LOCAL1, emailAndTracte!!.first)
                        )
                        infoNotification(
                                APP_TITLE,
                                "S'ha enviat un correu de confirmació d'estada número $numeroEstada tancada a ${registre?.docent?.nom}"
                        )
                    }
                }
            } else {
                Alert(Alert.AlertType.CONFIRMATION, "l'estada ${numeroEstada} no s'ha afegir correctament")
            }

            true

        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            return false
        } finally {
            seguimentSts.closeOnCompletion()
        }
    }

    /* insertEstadesQuery */
    private fun insertEstada(registre: Registre): Boolean {

        val nif: String = registre.docent!!.nif

        if (!isDocentAdmes(nif)) {
            Alert(Alert.AlertType.ERROR, "El/La docent amb NIF $nif no té una estada concedidad").showAndWait()
            return false
        }

        val estada: Estada = registre.estada!!
        val empresa: Empresa = registre.empresa!!

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
            val ret = estadaSts.executeUpdate()
            if (ret == 1) {
                insertSeguimentDeEstada(estada.numeroEstada, EstatsSeguimentEstadaEnum.REGISTRADA, "registrada")
            }
            true
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            return false
        } finally {
            estadaSts.closeOnCompletion()
        }

    }

    /* Cada estada a estades_t te un codi unic del tipus 0001230600/2018-2019 que incorpora lany */
    fun findRegistreByCodiEstada(codiEstada: String): Registre? {

        val estadaSts = conn.prepareStatement(findEstadaByCodiEstadaQuery)
        estadaSts.setString(1, codiEstada)
        val rs = estadaSts.executeQuery()
        // found
        if (rs.next()) {
            with(rs) {
                val estada = Estada(
                        getString("estades_codi").clean(),
                        getString("centres_codi".clean()),
                        getString("estades_tipus_estada".clean()),
                        LocalDate.parse(getString("estades_data_inici").substring(0, 10)),
                        LocalDate.parse(getString("estades_data_final").substring(0, 10)),
                        getString("estades_descripcio").clean(),
                        getString("estades_comentaris").clean()
                )
                val identificacio = Identificacio(
                        getString("estades_nif_empresa").clean(),
                        getString("estades_nom_empresa").clean(),
                        getString("estades_direccio_empresa").clean(),
                        getString("estades_codi_postal_empresa").clean(),
                        getString("estades_municipi_empresa").clean()
                )
                val contacte = PersonaDeContacte(
                        getString("estades_contacte_nom").clean(),
                        getString("estades_contacte_carrec").clean(),
                        getString("estades_contacte_telefon").clean(),
                        getString("estades_contacte_email").clean()
                )
                val tutor = Tutor(
                        getString("estades_tutor_nom").clean(),
                        getString("estades_tutor_carrec").clean(),
                        getString("estades_tutor_telefon").clean(),
                        getString("estades_tutor_email").clean()
                )
                val empresa = Empresa(identificacio, contacte, tutor)
                val docent = Docent(
                        getString("estades_nif_professor"),
//                        getString("professors_nom"),
                        getString("professors_nom_amb_tractament"),
                        getString("professors_destinacio"),
                        getString("professors_especialitat"),
                        getString("professors_email"),
                        getString("professors_telefon")
                )
                val centre = Centre(
                        getString("centres_codi"),
                        getString("centres_nom"),
                        getString("centres_direccio"),
                        getString("centres_codipostal"),
                        getString("centres_municipi"),
                        getString("directors_nom_director"),
                        getString("centres_telefon"),
                        getString("centres_email_centre")
                )
                val sstt = SSTT(
                        getString("delegacions_codi_delegacio"),
                        getString("sstt_nom"),
                        getString("delegacions_municipi"),
                        getString("delegacions_cap_de_servei"),
                        getString("delegacions_telefon_cap_de_servei"),
                        getString("sstt_correu_1"),
                        getString("sstt_correu_2")
                )
                return Registre(estada, empresa, docent, centre, sstt)
            }

        }
        return null
    }

    fun findEstadaCodiByNif(nif: String): String? {
        val estadaSts = conn.prepareStatement(findEstadaCodiByNifQuery)
        estadaSts.setString(1, nif)
        estadaSts.setString(2, currentCourseYear())
        val rs = estadaSts.executeQuery()
        // found
        if (rs.next()) {
            val codiEstada = rs.getString("estades_codi")
            return codiEstada
        }
        return null
    }

    /* This methods finds the estada number associated with a nif findEstadaCodiByNifQuery */
    fun findRegistreByNif(nif: String): Registre? {

        val codiEstada = findEstadaCodiByNif(nif)
        codiEstada?.apply {
            return findRegistreByCodiEstada(codiEstada)
        }
        return null
    }

    /* This method returns a list of emails of those who sent a valid evalisa */
    fun queryCandidats(): List<String> {
        val statement: Statement = conn.createStatement()
        val rs: ResultSet = statement.executeQuery(allCandidatsQuery)
        val candidats = mutableListOf<String>()
        while (rs.next()) {
            val email = rs.getString("email")
            candidats.add(email)
        }
        return candidats
    }

    /* This method returns a list of EstadaQuery  */
    fun queryEstadesAndSeguiments(nif: String? = null): List<EstadaQuery> {

        val statement = conn.prepareStatement(estadesByNifQuery)
        statement.setString(1, nif)
        val rs: ResultSet = statement.executeQuery()
        val estades = mutableListOf<EstadaQuery>()
        while (rs.next()) {
            val estadaQuery =
                    EstadaQuery(
                            rs.getString("estades_codi"),
                            rs.getString("professors_noms"),
                            rs.getString("estades_nif_professor"),
                            rs.getInt("estades_curs"),
                            rs.getString("estades_nom_empresa"),
                            rs.getDate("estades_data_inici"),
                            rs.getDate("estades_data_final")
                    )
            estadaQuery.seguiments = querySeguimentPerEstada(estadaQuery.codi)
            estades.add(estadaQuery)
        }
        return estades
    }

    /* This method returns a list of emails of those who sent a valid evalisa and were selected */
    private fun querySeguimentPerEstada(codiEstada: String): List<SeguimentQuery> {
        val statement = conn.prepareStatement(seguimentForCodiEstadaQuery)
        statement.setString(1, codiEstada)
        val rs: ResultSet = statement.executeQuery()
        val seguiments = mutableListOf<SeguimentQuery>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        while (rs.next()) {
            val seguiment = SeguimentQuery(
                    rs.getString("seguiment_id"),
                    rs.getString("seguiment_comentaris"),
                    LocalDate.parse(rs.getString("seguiment_data").substring(0, 10), formatter)
            )
            seguiments.add(seguiment)
        }
        return seguiments
    }

    /* This method returns a list of emails of those who sent a valid evalisa and were selected */
    fun queryAdmesos(): List<String> {
        val statement: Statement = conn.createStatement()
        val rs: ResultSet = statement.executeQuery(admesosByNifQuery)
        val admesos = mutableListOf<String>()
        while (rs.next()) {
            val email = rs.getString("email")
            admesos.add(email)
        }
        return admesos
    }

    /* Retorna un Registre amb docent, centre i sstt d'un nif concret nif en forma 099999999A */
    fun findRegistreByNifDocent(nif: String?): Registre? {
        val estadaSts = conn.prepareStatement(findRegistreByNif)
        estadaSts.setString(1, nif)
        val rs = estadaSts.executeQuery()
        // found
        if (rs.next()) {
            with(rs) {
                val docent = Docent(
                        getString("professors_nif"),
                        getString("professors_nom_amb_tractament"),
                        getString("professors_destinacio"),
                        getString("professors_especialitat"),
                        getString("professors_email"),
                        getString("professors_telefon")
                )
                val centre = Centre(
                        getString("centres_codi"),
                        getString("centres_nom"),
                        getString("centres_direccio"),
                        getString("centres_codipostal"),
                        getString("centres_municipi"),
                        getString("directors_nom"),
                        getString("centres_telefon"),
                        getString("centres_email")
                )
                val sstt = SSTT(
                        getString("sstt_codi"),
                        getString("sstt_nom"),
                        getString("delegacions_municipi"),
                        getString("delegacions_coordinador"),
                        getString("delegacions_telefon_coordinador"),
                        getString("sstt_correu_1"),
                        getString("sstt_correu_2")
                )
                return Registre(null, null, docent, centre, sstt)
            }

        }
        return Registre(Estada(), Empresa(), Docent(), Centre(), SSTT())
    }

    /* findLastEstadaNumberQuery */
    fun getNextEstadaNumber(): String {
        val estadaSts = conn.createStatement()
        val result = estadaSts.executeQuery(findLastEstadaNumberQuery)
        val ret = if (result.next())
            nextEstadaNumber(result.getString("estades_codi"))
        else
            "0000600/${currentCourseYear()}-${Integer.parseInt(currentCourseYear()) + 1}"
        estadaSts.closeOnCompletion()
        return ret
    }

    fun checkStatusSummary(): String {

        val allEstades = conn.prepareStatement(allEstadesQuery)
        allEstades.setString(1, currentCourseYear())
        val allEstadesResultSet = allEstades.executeQuery()
        val buffer = StringBuffer()
        while (allEstadesResultSet.next()) {
            val numeroEstada = allEstadesResultSet.getString("estades_codi")
            val seguiments = conn.prepareStatement(lastSeguimentForCodiEstadaQuery)
            seguiments.setString(1, numeroEstada)
            val lastSeguimentFromEstada = seguiments.executeQuery()
            if (lastSeguimentFromEstada.next()) {
                val professorAmbTractament = allEstadesResultSet.getString("professors_nom_amb_tractament")
                val professorEmail = allEstadesResultSet.getString("professors_email")
                val nomEmpresa = allEstadesResultSet.getString("estades_nom_empresa")
                val dataInici = allEstadesResultSet.getDate("estades_data_inici")
                val dataFinal = allEstadesResultSet.getDate("estades_data_final")
                // val avui = Date()
                val darrerEstat =
                        EstatsSeguimentEstadaEnum.valueOf(lastSeguimentFromEstada.getString("seguiment_estat"))
                when (darrerEstat) {
                    /* Estada Registrada a Gèsticus però no comunicada a: centre, empresa, docent ni SSTT */
                    EstatsSeguimentEstadaEnum.REGISTRADA -> {
                        buffer.append("L'estada número ${numeroEstada} de $professorAmbTractament ($professorEmail) a $nomEmpresa de $dataInici a $dataFinal esta registrada però no comunicada")
                                .append("\n")
                    }
                    /* Estada Acabada però no ha lliurat la documentació */
                    EstatsSeguimentEstadaEnum.ACABADA -> {
                        buffer.append("L'estada número ${numeroEstada} de $professorAmbTractament ($professorEmail) a $nomEmpresa de $dataInici a $dataFinal esta acabada però no documentada")
                                .append("\n")
                    }
                    /* Estada Documentada però no tancada al GTAF */
                    EstatsSeguimentEstadaEnum.DOCUMENTADA -> {
                        buffer.append("L'estada número ${numeroEstada} de $professorAmbTractament ($professorEmail) a $nomEmpresa de $dataInici a $dataFinal esta documentada però no tancada")
                                .append("\n")
                    }
                    /* Do nothing, estada en un estat consistent */
                    else -> {

                    }
                }
            }
        }
        allEstades.closeOnCompletion()
        return buffer.toString()
    }

    fun checkStatusTableSummary(): List<Summary> {

        val allEstades = conn.prepareStatement(allEstadesQuery)
        allEstades.setString(1, currentCourseYear())
        val allEstadesResultSet = allEstades.executeQuery()
        val summary = mutableListOf<Summary>()
        while (allEstadesResultSet.next()) {
            val numeroEstada = allEstadesResultSet.getString("estades_codi")
            val seguiments = conn.prepareStatement(lastSeguimentForCodiEstadaQuery)
            seguiments.setString(1, numeroEstada)
            val lastSeguimentFromEstada = seguiments.executeQuery()
            if (lastSeguimentFromEstada.next()) {
                val professorAmbTractament = allEstadesResultSet.getString("professors_nom_amb_tractament")
                val professorEmail = allEstadesResultSet.getString("professors_email")
                val nomEmpresa = allEstadesResultSet.getString("estades_nom_empresa")
                val dataInici = allEstadesResultSet.getDate("estades_data_inici")
                val dataFinal = allEstadesResultSet.getDate("estades_data_final")
                // val avui = Date()
                val darrerEstat =
                        EstatsSeguimentEstadaEnum.valueOf(lastSeguimentFromEstada.getString("seguiment_estat"))
                when (darrerEstat) {
                    /* Estada Registrada a Gèsticus però no comunicada a: centre, empresa, docent ni SSTT */
                    EstatsSeguimentEstadaEnum.REGISTRADA -> {
                        summary.add(Summary(numeroEstada, professorAmbTractament, professorEmail, nomEmpresa, dataInici, dataFinal, darrerEstat, "Registrada però no comunicada"))
                    }
                    /* Estada Acabada però no ha lliurat la documentació */
                    EstatsSeguimentEstadaEnum.ACABADA -> {
                        summary.add(Summary(numeroEstada, professorAmbTractament, professorEmail, nomEmpresa, dataInici, dataFinal, darrerEstat, "Acabada però no documentada"))
                    }
                    /* Estada Documentada però no tancada al GTAF */
                    EstatsSeguimentEstadaEnum.DOCUMENTADA -> {
                        summary.add(Summary(numeroEstada,professorAmbTractament, professorEmail,nomEmpresa,dataInici,dataFinal,darrerEstat, "Documentada però no tancada"))
                    }
                    /* Do nothing, estada en un estat consistent */
                    else -> {

                    }
                }
            }
        }
        allEstades.closeOnCompletion()
        return summary
    }

    /*
    * Actualitza l'estat de les gestionades de COMUNICADA a INICIADA i d'INICIADA a ACABADA
    *
    * */
    fun checkStatusUpdateBd(): Unit {

        try {
            val allEstades = conn.prepareStatement(allEstadesQuery)
            allEstades.setString(1, currentCourseYear())
            val allEstadesResultSet = allEstades.executeQuery()
            while (allEstadesResultSet.next()) {
                val numeroEstada = allEstadesResultSet.getString("estades_codi")
                val seguiments = conn.prepareStatement(lastSeguimentForCodiEstadaQuery)
                seguiments.setString(1, numeroEstada)
                val lastSeguimentFromEstada = seguiments.executeQuery()
                if (lastSeguimentFromEstada.next()) {
                    val professorAmbTractament = allEstadesResultSet.getString("professors_nom_amb_tractament")
                    val professorEmail = allEstadesResultSet.getString("professors_email")
                    val dataInici = allEstadesResultSet.getDate("estades_data_inici")
                    val dataFinal = allEstadesResultSet.getDate("estades_data_final")
                    val avui = Date()
                    val darrerEstat =
                            EstatsSeguimentEstadaEnum.valueOf(lastSeguimentFromEstada.getString("seguiment_estat"))
                    when (darrerEstat) {
                        /* Una estada esta comunicada quan hem notificat a centre, empresa, docent is sstt */
                        EstatsSeguimentEstadaEnum.COMUNICADA -> {
//                        if (avui.after(dataFinal)) {
//                            // set estat INICIADA
//                            insertSeguimentDeEstada(numeroEstada, EstatsSeguimentEstadaEnum.INICIADA, "Estada Iniciada")
//                            // set estat ACABADA
//                            insertSeguimentDeEstada(numeroEstada, EstatsSeguimentEstadaEnum.ACABADA, "Estada acabada")
//                        }
                            /* DE COMUNICADA a INICIADA*/
                            if (avui.after(dataInici)) {
                                // set estat INICIADA
                                insertSeguimentDeEstada(numeroEstada, EstatsSeguimentEstadaEnum.INICIADA, "Estada Iniciada")
                            }
                        }
                        /* D'INICIADA a ACABADA*/
                        EstatsSeguimentEstadaEnum.INICIADA -> {
                            if (avui.after(dataFinal)) {
                                // set estat ACABADA
                                insertSeguimentDeEstada(numeroEstada, EstatsSeguimentEstadaEnum.ACABADA, "Estada acabada")
                            }
                        }
                        /* Do nothing */
                        else -> {

                        }
                    }
                }
            }
            allEstades.closeOnCompletion()
        } catch (error: java.lang.Exception) {
            errorNotification(APP_TITLE, error.message)
        }
    }

    fun checkStatusAcabadaSendEmail(): Unit {

        try {
            val allEstades = conn.prepareStatement(allEstadesQuery)
            allEstades.setString(1, currentCourseYear())
            val allEstadesResultSet = allEstades.executeQuery()
            while (allEstadesResultSet.next()) {
                val numeroEstada = allEstadesResultSet.getString("estades_codi")
                val seguiments = conn.prepareStatement(lastSeguimentForCodiEstadaQuery)
                seguiments.setString(1, numeroEstada)
                val lastSeguimentFromEstada = seguiments.executeQuery()
                if (lastSeguimentFromEstada.next()) {
                    val professorAmbTractament = allEstadesResultSet.getString("professors_nom_amb_tractament")
                    val professorEmail = allEstadesResultSet.getString("professors_email")
                    val dataInici = allEstadesResultSet.getDate("estades_data_inici")
                    val dataFinal = allEstadesResultSet.getDate("estades_data_final")
                    val avui = Date()
                    val darrerEstat =
                            EstatsSeguimentEstadaEnum.valueOf(lastSeguimentFromEstada.getString("seguiment_estat"))
                    when (darrerEstat) {
                        // Esta acabada i un mes després encara no ha lliurat la documentació
                        EstatsSeguimentEstadaEnum.ACABADA -> {

                            val inOneMonth = dataFinal.toLocalDate().plus(1, ChronoUnit.MONTHS)
                            if (LocalDate.now().isAfter(inOneMonth)) {
                                infoNotification(
                                        APP_TITLE,
                                        "Enviant correu a $professorAmbTractament perquè l'estada número ${numeroEstada} va acabar el ${dataFinal} i encara no esta documentada"
                                )
                                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                        SUBJECT_GENERAL,
                                        BODY_RECORDATORI_ESTADA_ACABADA
                                                .replace("?1", professorAmbTractament)
                                                .replace("?2", dataFinal.toString())
                                                .replace("?3", numeroEstada),
                                        listOf(),
                                        listOf(professorEmail)
                                )
                            }
                        }
                        /* Do nothing */
                        else -> {

                        }
                    }
                }
            }
            allEstades.closeOnCompletion()
        } catch (error: java.lang.Exception) {
            errorNotification(APP_TITLE, error.message)
        }

    }

    /* Cal marcar la baixa a admesos_t perque de fet l'estada no existeix admesosSetBaixaToTrueQuery */
    fun doBaixa(nif: String, value: Boolean): Unit {

        // Primer cal verificar que no esta en el mateix estat que volem posar
        try {
            val getBaixaStatement: PreparedStatement = conn.prepareStatement(admesosGetBaixaEstatQuery)
            getBaixaStatement.setString(1, nif)
            getBaixaStatement.setString(2, currentCourseYear())

            val resultSet = getBaixaStatement.executeQuery()
            if (resultSet.next()) {
                val nom = resultSet.getString("admesos_nom")
                val baixa = resultSet.getBoolean("admesos_baixa")
                if (value == baixa) {
                    val msg = if (value) "ja esta de baixa" else "ja esta d'alta"
                    errorNotification("Gèsticus", "$nom $msg")
                    return
                }
            } else {
                errorNotification(APP_TITLE, "El registre amb NIF $nif no es troba")
                return
            }
        } catch (error: java.lang.Exception) {
            errorNotification(APP_TITLE, error.message)
            return
        }

        try {
            val setBaixaStatement: PreparedStatement = conn.prepareStatement(admesosSetBaixaToTrueFalseQuery)
            setBaixaStatement.setBoolean(1, value)
            setBaixaStatement.setString(2, nif)
            setBaixaStatement.setString(3, currentCourseYear())
            val altaBaixa = if (value) "de baixa" else "d'alta"
            val count = setBaixaStatement.executeUpdate()
            if (count == 1) {
                val numeroEstada = findEstadaCodiByNif(nif)
                numeroEstada?.apply {
                    /* Generalment no hi haura una estada en curs però per si de cas */
                    insertSeguimentDeEstada(this, EstatsSeguimentEstadaEnum.BAIXA, "Baixa voluntària")
                }
                val registre = findRegistreByNifDocent(nif)
                infoNotification("Gèsticus", "La sol·licitud d'estada amb NIF $nif ha estat donat $altaBaixa correctament.")
                if (value) {
                    val nom = registre?.docent?.nom
                    val al = if (nom!!.startsWith("Sr.")) "al" else if (nom!!.startsWith("Sra.")) "a la" else "a"
                    Alert(
                            Alert.AlertType.CONFIRMATION,
                            "Vols enviar un correu de confirmació $al ${nom}?"
                    )
                            .showAndWait()
                            .ifPresent {
                                if (it == ButtonType.OK) {
                                    val emailTracte = findEmailAndTracteByNif(nif)
                                    GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                            SUBJECT_GENERAL,
                                            BODY_BAIXA.replace("?1", emailTracte?.second.orEmpty()),
                                            listOf(),
                                            listOf<String>(CORREU_LOCAL1, emailTracte?.first.orEmpty())
                                    )
                                }
                            }

                }
            } else {
                errorNotification(APP_TITLE, "No s'ha trobat el registre $nif a la taula 'admesos_t'")
            }
        } catch (error: SQLException) {
            errorNotification(APP_TITLE, "No s'ha trobat el registre $nif a la taula 'admesos_t'")
        }
    }

    /* This method returns a Centre, EditableSSTT pair according to centre.codi or an empty Centre, EditableSSTT pair object if not found */
    fun findCentreAndSSTT(codiCentre: String): Pair<Centre, SSTT> {
        val findCentreAndSSTTByCentreCodiStatement = conn.prepareStatement(findCentreAndSSTTByCentreCodiQuery)
        findCentreAndSSTTByCentreCodiStatement.setString(1, codiCentre)
        val result = findCentreAndSSTTByCentreCodiStatement.executeQuery()
        return if (result.next()) {
            with(result) {
                val centre = Centre(
                        getString("centres_codi"),
                        getString("centres_nom"),
                        getString("centres_direccio"),
                        getString("centres_codipostal"),
                        getString("centres_municipi"),
                        getString("directors_nom"),
                        getString("centres_telefon"),
                        getString("centres_email_centre")
                )
                val sstt = SSTT(
                        getString("delegacions_codi"),
                        getString("delegacions_nom"),
                        getString("delegacions_municipi"),
                        getString("delegacions_cap_de_servei"),
                        getString("delegacions_telefon_cap_de_servei"),
                        getString("sstt_correu_1"),
                        getString("sstt_correu_2")
                )
                Pair(centre, sstt)
            }
        } else {
            val centre = Centre()
            val sstt = SSTT()
            Pair(centre, sstt)
        }
    }

    /* This method returns a all EditableSSTT's */
    fun getServeisTerritorials(): List<EditableSSTT> {
        val allEditableSSTTs = mutableListOf<EditableSSTT>()
        val findAllSSTTStatement = conn.createStatement()
        val result = findAllSSTTStatement.executeQuery(findAllSSTTQuery)
        while (result.next()) {
            with(result) {
                allEditableSSTTs.add(
                        EditableSSTT(
                                getString("sstt_codi"),
                                getString("sstt_nom"),
                                getString("sstt_correu_1"),
                                getString("sstt_correu_2")
                        )
                )
            }
        }
        return allEditableSSTTs

    }

    /* findNomAndEmailByNIFQuery */
    fun findEmailAndTracteByNif(nif: String): Pair<String, String>? {
        val findEmailByNifStatement = conn.prepareStatement(findNomAndEmailByNIFQuery)
        findEmailByNifStatement.setString(1, nif)
        val result = findEmailByNifStatement.executeQuery()
        return if (result.next()) {
            with(result) {
                val email = getString("professors_email")
                val tracte = getString("professors_tracte")
                email to tracte
            }
        } else {
            null
        }
    }

    /* This method returns a SSTT according to sstt.codi or an empty SSTT object if not found */
    fun findSSTT(codiSSTT: String): SSTT {
        val findSSTTBySSTTCodiStatement = conn.prepareStatement(findSSTTBySSTTCodiQuery)
        findSSTTBySSTTCodiStatement.setString(1, codiSSTT)
        val result = findSSTTBySSTTCodiStatement.executeQuery()
        return if (result.next()) {
            with(result) {
                SSTT(
                        getString("delegacions_codi"),
                        getString("delegacions_nom"),
                        getString("delegacions_municipi"),
                        getString("delegacions_cap_de_servei"),
                        getString("delegacions_telefon_cap_de_servei"),
                        getString("sstt_correu_1"),
                        getString("sstt_correu_2")
                )
            }
        } else {
            SSTT()
        }

    }

    /* updateSSTTQuery */
    fun updateSSTT(editableSSTT: EditableSSTT): Boolean {
        val updateSSTTStatement = conn.prepareStatement(updateSSTTQuery)
        updateSSTTStatement.setString(1, editableSSTT.correu1)
        updateSSTTStatement.setString(2, editableSSTT.correu2)
        updateSSTTStatement.setString(3, editableSSTT.codi)
        val result = updateSSTTStatement.executeUpdate()
        updateSSTTStatement.closeOnCompletion()
        return result == 1
    }

    fun getAdmesos(): List<EditableAdmes> {
        val allEditableAdmesos = mutableListOf<EditableAdmes>()
        val findAllAdmesostatement = conn.createStatement()
        val result = findAllAdmesostatement.executeQuery(findAllAdmesosQuery)
        while (result.next()) {
            with(result) {
                allEditableAdmesos.add(
                        EditableAdmes(
                                getString("admesos_nif"),
                                getString("admesos_nom"),
                                getString("admesos_email"),
                                getString("admesos_curs"),
                                getBoolean("admesos_baixa")
                        )
                )
            }
        }
        return allEditableAdmesos

    }

    /*
    * findAllSanitarisSenseEstadaQuery
    *
    * SELECT admesos_t.nif AS admesos_nif, professors_t.tractament AS professors_tractament, professors_t.nom AS professors_nom, professors_t.cognom_1 AS professors_cognom_1, professors_t.cognom_2 AS professors_cognom_2, professors_t.familia AS professors_familia, professors_t.especialitat AS professors_especialitat, admesos_t.email AS admesos_email, professors_t.sexe AS professors_sexe, professors_t.centre AS professors_centre, professors_t.municipi AS professors_municipi, professors_t.delegacio_territorial AS professors_delegacio_territorial, professors_t.telefon AS professors_telefon, admesos_t.baixa AS admesos_baixa, estades_t.codi AS estades_codi\n" +
                "FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor) LEFT JOIN professors_t ON admesos_t.nif = professors_t.nif\n" +
                "WHERE (((professors_t.familia)='Sanitat') AND ((admesos_t.baixa)=False) AND ((estades_t.codi) Is Null))\n" +
                "ORDER BY professors_t.delegacio_territorial;
    *
    * TODO("Finish up")
    * */
    fun findAllColletiuSenseEstada(familia: String = "Sanitat"): List<CollectiuPendent>? {
        val collectiu = arrayListOf<CollectiuPendent>()
        val findAllCollectiuSenseEstadaStatement = conn.prepareStatement(findAllSanitarisSenseEstadaQuery)
        findAllCollectiuSenseEstadaStatement.setString(1, familia)
        val result = findAllCollectiuSenseEstadaStatement.executeQuery()
        while (result.next()) {
            with(result) {
                collectiu.add(
                        CollectiuPendent(
                                getString("admesos_nif"),
                                getString("professors_tractament"),
                                getString("professors_nom"),
                                getString("professors_cognom_1"),
                                getString("professors_cognom_2"),
                                getString("professors_familia"),
                                getString("professors_especialitat"),
                                getString("admesos_email"),
                                getString("professors_sexe"),
                                getString("professors_centre"),
                                getString("professors_municipi"),
                                getString("professors_delegacio_territorial"),
                                getString("professors_telefon")
                        )
                )
            }

        }
        return collectiu
    }

    /* updateSSTTQuery */
    fun updateAdmesos(editableAdmes: EditableAdmes): Boolean {
        val updateAdmesStatement = conn.prepareStatement(updateAdmesosQuery)
        updateAdmesStatement.setString(1, editableAdmes.email)
        updateAdmesStatement.setString(2, editableAdmes.nif)
        val result = updateAdmesStatement.executeUpdate()
        updateAdmesStatement.closeOnCompletion()
        return result == 1
    }

    fun countTotalAdmesos(): Double {
        var total = 1
        val countTotalAdmesosStatement = conn.prepareStatement(countTotalAdmesosQuery)
        countTotalAdmesosStatement.setString(1, currentCourseYear())
        val result = countTotalAdmesosStatement.executeQuery()
        if (result.next()) {
            total = result.getInt("admesos_total")
        }
        return total.toDouble()
    }

    fun countTotalBaixesAdmesos(): Double {
        var total = 1
        val countTotalBaixesAdmesosStatement = conn.prepareStatement(countBaixesFromAdmesosQuery)
        countTotalBaixesAdmesosStatement.setString(1, currentCourseYear())
        val result = countTotalBaixesAdmesosStatement.executeQuery()
        if (result.next()) {
            total = result.getInt("admesos_baixes")
        }
        return total.toDouble()
    }

    fun countTotalEstades(): Double {
        var total = 1
        val countTotalEstadesStatement = conn.prepareStatement(countTotalEstadesQuery)
        countTotalEstadesStatement.setString(1, currentCourseYear())
        val result = countTotalEstadesStatement.executeQuery()
        if (result.next()) {
            total = result.getInt("estades_total")
        }
        return total.toDouble()
    }

    /*countTotalEstadesPerCentreQuery*/
    fun countTotalEstadesPerCentre(): Map<String, Double> {
        val countTotalEstadesPerCentreStatement = conn.prepareStatement(countTotalEstadesPerCentreQuery)
        countTotalEstadesPerCentreStatement.setString(1, currentCourseYear())
        val result = countTotalEstadesPerCentreStatement.executeQuery()
        val columnsMap = mutableMapOf<String, Double>()
        while (result.next()) {
            columnsMap[result.getString(1)] = result.getDouble(2)
        }
        return columnsMap
    }

    fun countTotalEstadesNoGestionadesPerCentre(): Map<String, Double> {
        val countTotalEstadesPerCentreStatement = conn.prepareStatement(countTotalEstadesNoGestionadesPerCentreQuery)
//        countTotalEstadesPerCentreStatement.setString(1, currentCourseYear())
        val result = countTotalEstadesPerCentreStatement.executeQuery()
        val columnsMap = mutableMapOf<String, Double>()
        while (result.next()) {
            columnsMap[result.getString(1)] = result.getDouble(2)
        }
        return columnsMap
    }

    /*countTotalEstadesPerFamiliaQuery*/
    fun countTotalEstadesPerFamillia(): Map<String, Double> {
        val countTotalEstadesPerFamiliaStatement = conn.prepareStatement(countTotalEstadesPerFamiliaQuery)
        countTotalEstadesPerFamiliaStatement.setString(1, currentCourseYear())
        val result = countTotalEstadesPerFamiliaStatement.executeQuery()
        val columnsMap = mutableMapOf<String, Double>()
        while (result.next()) {
            columnsMap[result.getString(1)] = result.getDouble(2)
        }
        return columnsMap
    }


    /*countTotalEstadesNoGestionadesPerFamiliaQuery*/
    fun countTotalEstadesNoGestionadesPerFamillia(): Map<String, Double> {
        val countTotalEstadesPerFamiliaStatement = conn.prepareStatement(countTotalEstadesNoGestionadesPerFamiliaQuery)
//        countTotalEstadesPerFamiliaStatement.setString(1, currentCourseYear())
        val result = countTotalEstadesPerFamiliaStatement.executeQuery()
        val columnsMap = mutableMapOf<String, Double>()
        while (result.next()) {
            columnsMap[result.getString(1)] = result.getDouble(2)
        }
        return columnsMap
    }

    /*countTotalEstadesPerSSTTQuery*/
    fun countTotalEstadesPerSSTT(): Map<String, Int> {
        val countTotalEstadesPerSSTTStatement = conn.prepareStatement(countTotalEstadesPerSSTTQuery)
        countTotalEstadesPerSSTTStatement.setString(1, currentCourseYear())
        val result = countTotalEstadesPerSSTTStatement.executeQuery()
        val columnsMap = mutableMapOf<String, Int>()
        while (result.next()) {
            columnsMap[result.getString(1)] = result.getInt(2)
        }
        return columnsMap
    }

    fun countTotalEstadesNoGestionadesPerSSTT(): Map<String, Int> {
        val columnsMap = mutableMapOf<String, Int>()
        try {
            val countTotalEstadesPerSSTTStatement = conn.prepareStatement(countTotalEstadesNoGestionadesPerSSTTQuery)
//        countTotalEstadesPerSSTTStatement.setString(1, currentCourseYear())
            val result = countTotalEstadesPerSSTTStatement.executeQuery()

            while (result.next()) {
                columnsMap[result.getString(1)] = result.getInt(2)
            }

        } catch (error: Exception) {
            println(error.message)
        }
        return columnsMap
    }

    /* countTotalEstadesPerSexeQuery */
    fun countTotalEstadesPerSexe(): Map<String, Double> {
        val countTotalEstadesPerSexeStatement = conn.prepareStatement(countTotalEstadesPerSexeQuery)
        countTotalEstadesPerSexeStatement.setString(1, currentCourseYear())
        val result = countTotalEstadesPerSexeStatement.executeQuery()
        val columnsMap = mutableMapOf<String, Double>()
        while (result.next()) {
            columnsMap[result.getString(1)] = result.getDouble(2)
        }
        return columnsMap
    }

    fun countTotalEstadesNoGestionadesPerSexe(): Map<String, Double> {
        val countTotalEstadesPerSexeStatement = conn.prepareStatement(countTotalEstadesNoGestionadesPerSexeQuery)
//        countTotalEstadesPerSexeStatement.setString(1, currentCourseYear())
        val result = countTotalEstadesPerSexeStatement.executeQuery()
        val columnsMap = mutableMapOf<String, Double>()
        while (result.next()) {
            columnsMap[result.getString(1)] = result.getDouble(2)
        }
        return columnsMap
    }

    /* countTotalEstadesPerCosQuery */
    fun countTotalEstadesPerCos(): Map<String, Double> {
        val countTotalEstadesPerCosStatement = conn.prepareStatement(countTotalEstadesPerCosQuery)
        countTotalEstadesPerCosStatement.setString(1, currentCourseYear())
        val result = countTotalEstadesPerCosStatement.executeQuery()
        val columnsMap = mutableMapOf<String, Double>()
        while (result.next()) {
            columnsMap[result.getString(1)] = result.getDouble(2)
        }
        return columnsMap
    }

    fun countTotalEstadesNoGestionadesPerCos(): Map<String, Double> {
        val countTotalEstadesPerCosStatement = conn.prepareStatement(countTotalEstadesNoGestionadesPerCosQuery)
//        countTotalEstadesPerCosStatement.setString(1, currentCourseYear())
        val result = countTotalEstadesPerCosStatement.executeQuery()
        val columnsMap = mutableMapOf<String, Double>()
        while (result.next()) {
            columnsMap[result.getString(1)] = result.getDouble(2)
        }
        return columnsMap
    }

    /* estadesPendentsPerFamiliaQuery */
    fun docentsPendentsPerFamilia(familia: String): List<EstadaPendent> {
        val estadesPendentsPerFamiliaStatement = conn.prepareStatement(estadesPendentsPerFamiliaQuery)
        estadesPendentsPerFamiliaStatement.setString(1, familia)
        val result = estadesPendentsPerFamiliaStatement.executeQuery()
        val estadesPendents = mutableListOf<EstadaPendent>()
        while (result.next()) {
            estadesPendents.add(EstadaPendent(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getString(7),
                    result.getString(8)
            ))
        }
        return estadesPendents
    }

    fun doLlistatPendentsPerFamilies(): Boolean {
        val allFamiliesStatement = conn.prepareStatement(allFamiliesQuery)
        val result = allFamiliesStatement.executeQuery()
        while (result.next()) {
            val familia = result.getString(1)
            GesticusReports.createCartaPendentsFamiliaHTML(familia, docentsPendentsPerFamilia(familia))
        }
        allFamiliesStatement.closeOnCompletion()
        return true
    }

    fun sendRecordatoriPendentsPerFamilies(): Boolean {
        val dataDialog = TextInputDialog("31/03/2019")
        dataDialog.title = APP_TITLE
        dataDialog.headerText = "Data final de lliurament de sol·licituds"
        dataDialog.contentText = "Data"
        val data = dataDialog.showAndWait().get()
        val allFamiliesStatement = conn.prepareStatement(allFamiliesQuery)
        val result = allFamiliesStatement.executeQuery()
        while (result.next()) {
            val familia = result.getString(1)
            val docents = docentsPendentsPerFamilia(familia)
            docents.forEach {
                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                        SUBJECT_GENERAL,
                        BODY_RECORDATORI_ESTADA_PENDENT
                                .replace("?1", "${it.professorsTractament} ${it.professorsNom},")
                                .replace("?2", familia)
                                .replace("?3", it.professorsEspecialitat)
                                .replace("?4", "${currentCourseYear()}-${nextCourseYear()}")
                                .replace("?5", data)
                        ,
                        listOf(),
                        listOf(it.professorsEmail))
            }
        }
        allFamiliesStatement.closeOnCompletion()
        return true
    }

    fun close(): Unit {
        writeToLog("${LocalDate.now()} Closing connection.")
        conn.close()
    }

}
