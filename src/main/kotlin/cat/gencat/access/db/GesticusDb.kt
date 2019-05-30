package cat.gencat.access.db

import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.*
import cat.gencat.access.functions.Utils.Companion.APP_TITLE
import cat.gencat.access.functions.Utils.Companion.clean
import cat.gencat.access.functions.Utils.Companion.currentCourseYear
import cat.gencat.access.functions.Utils.Companion.errorNotification
import cat.gencat.access.functions.Utils.Companion.infoNotification
import cat.gencat.access.functions.Utils.Companion.nextCourseYear
import cat.gencat.access.functions.Utils.Companion.nextEstadaNumber
import cat.gencat.access.functions.Utils.Companion.toCatalanDateFormat
import cat.gencat.access.functions.Utils.Companion.writeToLog
import cat.gencat.access.model.*
import cat.gencat.access.os.GesticusOs
import cat.gencat.access.pdf.GesticusPdf
import cat.gencat.access.reports.GesticusReports
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import tornadofx.*
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
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

/* findSollicitantsByNif */
const val findSollicitantsByNif: String =
        "SELECT professors_t.nif as [professors_nif], professors_t.cognom_1 & ' ' & professors_t.cognom_2 & ', ' & professors_t.nom as [professors_nom], professors_t.email AS [professors_email], professors_t.cos as [professors_cos], professors_t.centre as [professors_centre], professors_t.delegacio_territorial as [professors_delegacio] \n" +
                "FROM professors_t \n" +
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

/* Quan es modifican les hores d'una estada es fa aquest update */
const val updateHoresEstadesQuery: String =
        "UPDATE estades_t SET hores_certificades = ? WHERE codi = ?"

const val estadesAndSeguimentQuery =
        "SELECT [estades_t].codi as estades_codi, [estades_t].nif_professor as estades_nif, [estades_t].curs as [estades_curs], [seguiment_estades_t].estat as seguiment_estat, [seguiment_estades_t].data as seguiment_data FROM estades_t LEFT JOIN seguiment_estades_t ON [estades_t].codi = [seguiment_estades_t].codi ORDER BY [estades_t].nif_professor ASC;"

const val findNomAndEmailByNIFQuery =
        "SELECT admesos_t.nif as [admesos_nif], professors_t.email as [professors_email], iif(professors_t.sexe = 'H', 'Benvolgut ', 'Benvolguda ') &  professors_t.nom & ','  as [professors_tracte] FROM admesos_t INNER JOIN professors_t ON admesos_t.nif = professors_t.nif WHERE  admesos_t.nif = ?;"
/*
* Totes les gestionades
* */
const val allEstadesQuery =
        "SELECT [estades_t].codi as estades_codi, estades_t.nom_empresa AS estades_nom_empresa, [estades_t].curs as [estades_curs], [estades_t].data_inici as [estades_data_inici], [estades_t].data_final as [estades_data_final], iif(professors_t.sexe = 'H', 'Sr. ', 'Sra. ') & professors_t.nom & ' ' & professors_t.cognom_1 & ' ' & professors_t.cognom_2 as [professors_nom_amb_tractament], estades_t.nif_professor AS estades_nif_professor, professors_t.email AS professors_email FROM [estades_t] LEFT JOIN [professors_t] ON [estades_t].nif_professor = [professors_t].nif WHERE [estades_t].curs = ?;"

const val allEstadesCSVQuery =
        "SELECT [estades_t].codi as estades_codi, estades_t.nom_empresa AS estades_nom_empresa, estades_t.direccio_empresa AS estades_direccio_empresa, estades_t.codi_postal_empresa AS estades_codi_postal_empresa, estades_t.municipi_empresa AS estades_municipi_empresa, [estades_t].curs as [estades_curs], estades_t.hores_certificades as [estades_hores_certificades], [estades_t].data_inici as [estades_data_inici], [estades_t].data_final as [estades_data_final], professors_t.nom & ' ' & professors_t.cognom_1 & ' ' & professors_t.cognom_2 as [professors_noms], professors_t.email AS professors_email, professors_t.nif as professors_nif FROM [estades_t] LEFT JOIN [professors_t] ON [estades_t].nif_professor = [professors_t].nif WHERE [estades_t].curs = ? ORDER BY [estades_t].codi;"

const val estadesByNifQuery =
        "SELECT [estades_t].codi as estades_codi, [estades_t].nif_professor as estades_nif_professor, [estades_t].curs as [estades_curs], [estades_t].nom_empresa as [estades_nom_empresa], [estades_t].data_inici as [estades_data_inici], [estades_t].data_final as [estades_data_final],  [professors_t].noms as professors_noms, iif(professors_t.sexe = 'H', 'Sr. ', 'Sra. ') & professors_t.nom & ' ' & professors_t.cognom_1 & ' ' & professors_t.cognom_2 as [professors_nom_amb_tractament] FROM estades_t LEFT JOIN professors_t ON [estades_t].nif_professor = [professors_t].nif WHERE [estades_t].nif_professor LIKE ? ORDER BY [estades_t].curs, [estades_t].nif_professor ASC;"

/*
*
* SELECT estades_t.codi, estades_t.curs, estades_t.nif_professor, seguiment_estades_t.data, seguiment_estades_t.estat
FROM estades_t INNER JOIN seguiment_estades_t ON estades_t.codi = seguiment_estades_t.codi;

* */
const val seguimentForCodiEstadaQuery =
        "SELECT [seguiment_estades_t].id as [seguiment_id], [seguiment_estades_t].estat as [seguiment_estat], [seguiment_estades_t].comentaris as [seguiment_comentaris], [seguiment_estades_t].data as [seguiment_data] FROM seguiment_estades_t WHERE [seguiment_estades_t].codi = ? ORDER BY [seguiment_estades_t].codi ASC;"

const val lastSeguimentForCodiEstadaQuery =
        "SELECT top 1 seguiment_estades_t.id, [seguiment_estades_t].estat as [seguiment_estat], [seguiment_estades_t].data as [seguiment_data] FROM seguiment_estades_t WHERE [seguiment_estades_t].codi = ? ORDER BY [seguiment_estades_t].id DESC"

const val estadesByCodiEstadaQuery =
        "SELECT [estades_t].codi as estades_codi, [estades_t].nif_professor as estades_nif_professor, [estades_t].curs as [estades_curs] FROM estades_t WHERE estades_t.[codi] = ?;"

/* codi, estat, data, comentaris */
const val insertSeguimentQuery: String = "INSERT INTO seguiment_estades_t (codi, estat, comentaris) VALUES (?, ?, ?)"

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

const val findAllAdmesosSenseEstadaQuery =
        "SELECT admesos_t.nif AS admesos_nif, professors_t.tractament AS professors_tractament, professors_t.nom AS professors_nom, professors_t.cognom_1 AS professors_cognom_1, professors_t.cognom_2 AS professors_cognom_2, professors_t.familia AS professors_familia, professors_t.especialitat AS professors_especialitat, admesos_t.email AS admesos_email, professors_t.sexe AS professors_sexe, professors_t.centre AS professors_centre, professors_t.municipi AS professors_municipi, professors_t.delegacio_territorial AS professors_delegacio_territorial, professors_t.telefon AS professors_telefon, admesos_t.baixa AS admesos_baixa, estades_t.codi AS estades_codi\n" +
                "FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor) LEFT JOIN professors_t ON admesos_t.nif = professors_t.nif\n" +
                "WHERE (((admesos_t.baixa)=False) AND ((estades_t.codi) Is Null) AND ((admesos_t.curs) = ?))\n" +
                "ORDER BY admesos_t.nif;\n"

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

/* Totes les famílies sense repetits del les estades pendents */
const val allFamiliesFromAdmesosQuery =
        "SELECT DISTINCT professors_t.familia AS professors_familia\n" +
                "FROM admesos_t INNER JOIN professors_t ON admesos_t.nif = professors_t.nif\n" +
                "WHERE professors_t.nif IN\n" +
                "(SELECT admesos_t.nif FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor)  WHERE estades_t.codi IS NULL)\n" +
                "ORDER BY professors_t.familia;\n"

const val allFamiliesFromProfessorsQuery =
        """SELECT DISTINCT professors_t.familia AS professors_familia FROM professors_t;"""

const val allDocentsFromProfessorsPerFamiliaQuery =
        """SELECT iif(professors_t.sexe = 'H', 'Benvolgut ', 'Benvolguda ') & professors_t.nom AS [professors_nom_amb_tractament], professors_t.especialitat AS [professors_especialitat], professors_t.email AS [professors_email], professors_t.c_municipi AS [professors_cp] FROM professors_t WHERE professors_t.familia = ?;"""

const val allDocentsFromProfessorsDeTotesLesFamiliaQuery =
        """SELECT iif(professors_t.sexe = 'H', 'Benvolgut ', 'Benvolguda ') & professors_t.nom AS [professors_nom_amb_tractament], professors_t.especialitat AS [professors_especialitat], professors_t.email AS [professors_email], professors_t.c_municipi AS [professors_cp] FROM professors_t;"""

// TODO("Finish up")
const val allDirectorsQuery =
    """SELECT ;"""

// TODO("Finish up")
const val allDirectorsPerMunicipiQuery =
    """SELECT ;"""


const val allEstadesFetesYEnCursQuery =
        """SELECT estades_t.codi AS estades_codi, estades_t.nif_professor AS estades_nif_professor, professors_t.cognom_1 AS professors_cognom1, professors_t.cognom_2 AS professors_cognom2, professors_t.nom AS professors_nom, professors_t.familia AS professors_familia, professors_t.centre AS professors_centre, professors_t.destinacio AS professors_destinacio, professors_t.c_especialitat AS professors_codi_especialitat ,estades_t.nom_empresa AS estades_nom_empresa, estades_t.municipi_empresa AS estades_municipi_empresa, estades_t.data_inici AS estades_data_inici, estades_t.data_final AS estades_data_final
FROM estades_t INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif
WHERE estades_t.curs = ?
ORDER BY professors_t.familia, professors_t.centre;
"""

/* Totes les estades pendents per fer */
const val estadesPendentsPerFamiliaQuery =
        "SELECT admesos_t.nif AS professors_nif, professors_t.tractament AS professors_tractament, professors_t.noms AS professors_nom, professors_t.telefon AS professors_telefon, professors_t.email AS professors_email, professors_t.especialitat AS professors_especialitat, centres_t.NOM_Municipi AS centres_municipi, centres_t.NOM_Centre AS centres_nom\n" +
                "FROM (admesos_t INNER JOIN professors_t ON admesos_t.nif = professors_t.nif) INNER JOIN centres_t ON professors_t.c_centre = centres_t.C_Centre\n" +
                "WHERE (((professors_t.familia)= ?) AND ((professors_t.nif) In (SELECT admesos_t.nif FROM (admesos_t LEFT JOIN estades_t ON admesos_t.nif = estades_t.nif_professor) \n" +
                "WHERE admesos_t.baixa = FALSE AND estades_t.codi IS NULL)))\n" +
                "ORDER BY professors_t.especialitat, centres_t.NOM_Municipi, centres_t.NOM_Centre;"

const val baremQuery =
        "SELECT barem_t.Id as barem_id, barem_t.nif as barem_nif, barem_t.nom as barem_nom, barem_t.email as barem_email, barem_t.curs as barem_curs, barem_t.privat as barem_privat, barem_t.nou as barem_cicle_nou, barem_t.dual as barem_dual, barem_t.grup as barem_grup, barem_t.interi as barem_interi, barem_t.repetidor as barem_repetidor, barem_t.en_espera as barem_en_espera, barem_t.nota_projecte as barem_nota_projecte, barem_t.nota_antiguitat as barem_nota_antiguitat, barem_t.nota_formacio as barem_nota_formacio, barem_t.nota_treballs_desenvolupats as barem_nota_treball_desenvolupats, barem_t.nota_altres_titulacions as barem_nota_altres_titulacions, barem_t.nota_catedratic as barem_nota_catedratic, barem_t.codi_grup as barem_codi_grup, barem_t.nota_individual as barem_nota_individual, barem_t.nota_grup as barem_nota_grup, barem_t.comentaris as barem_comentaris\n" +
                "FROM barem_t ORDER BY barem_t.nota_projecte;"

/*
* admesos_t.[nif] as [admesos_nif], admesos_t.nom AS [admesos_nom], admesos_t.[email] as [admesos_email], admesos_t.[curs] as [admesos_curs], admesos_t.[baixa] as [admesos_baixa] FROM admesos_t;"
* */
const val insertAdmesQuery: String =
        """INSERT INTO admesos_t (nif, nom, email, curs, baixa) VALUES (?,?,?,?,?)"""

/*
*
* TODO("Cal notificar els directors de cada centre una relació de docents que han sol·licitat una estada B abans de tancar la llista provisional")
*
* */
const val relacioSolicitudsEstadesPerCentreQuery =
        "SELECT centres_t.NOM_Centre, centres_t.Correu_electronic, directors_t.[tractament (Sr_Sra)], directors_t.Nom, directors_t.Cognoms, professors_t.tractament, candidats_t.nom, professors_t.familia, professors_t.especialitat\n" +
                "FROM (candidats_t INNER JOIN professors_t ON candidats_t.nif = professors_t.nif) INNER JOIN (centres_t INNER JOIN directors_t ON centres_t.C_Centre = directors_t.UBIC_CENT_LAB_C) ON professors_t.c_centre = centres_t.C_Centre\n" +
                "ORDER BY centres_t.NOM_Centre;\n"

/* Cerca per nom docent i nom empresa */
const val estadesPerNomQuery =
        "SELECT estades_t.*, professors_t.noms FROM estades_t INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif WHERE professors_t.noms like '*GA*' OR  estades_t.nom_empresa like '*GA*';"

const val estadesEnCursQuery =
        "SELECT estades_t.codi AS estades_codi, estades_t.nom_empresa AS estades_nom_empresa, estades_t.direccio_empresa AS estades_direccio_empresa, estades_t.codi_postal_empresa AS estades_codi_postal_empresa, estades_t.municipi_empresa AS estades_municipi_empresa, estades_t.contacte_nom AS estades_contacte_nom, estades_t.contacte_carrec AS estades_contacte_carrec, estades_t.contacte_telefon as estades_contacte_telefon, estades_t.contacte_email AS estades_contacte_email, estades_t.data_inici AS estades_data_inici, estades_t.data_final AS estades_data_final, estades_t.nif_professor AS estades_nif_professor, professors_t.tractament AS professors_tractament, professors_t.nom AS professors_nom, professors_t.cognom_1 AS professors_cognom1, professors_t.cognom_2 AS professors_cognom2 , professors_t.sexe AS professors_sexe, professors_t.email AS professors_email, professors_t.telefon AS professors_telefon, professors_t.especialitat AS professors_especialitat, professors_t.familia AS professors_familia, professors_t.centre AS professors_centre, professors_t.municipi AS professors_municipi, professors_t.delegacio_territorial AS professors_delegacio_territorial\n" +
                "FROM estades_t INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif\n" +
                "WHERE (((estades_t.data_inici)<=Date()) AND ((estades_t.data_final)>=Date()) AND ((estades_t.curs)= ?));"

const val allVisitesQuery =
        """SELECT visites_t.id AS visites_id, visites_t.estades_codi AS visites_estades_codi, visites_t.curs AS visites_curs, visites_t.tipus AS visites_tipus, visites_t.data AS visites_data, visites_t.hora AS visites_hora, visites_t.comentaris AS visites_comentaris
            FROM visites_t
            WHERE visites_t.curs = ?;"""

const val allVisitesBetweenDatesQuery =
        """SELECT visites_t.id AS visites_id, visites_t.estades_codi AS visites_estades_codi, visites_t.tipus AS visites_tipus, visites_t.data AS visites_data, visites_t.hora AS visistes_hora, visites_t.comentaris AS visites_comentaris
            FROM visites_t
            WHERE visites_t.data BETWEEN ? AND ?;"""

const val allEmpresesLongQuery =
        """SELECT empreses_t.id AS empreses_id, empreses_t.empresa_nif AS empreses_nif, empreses_t.empresa_nom AS empreses_nom, empreses_t.empresa_direccio AS empreses_direccio, empreses_t.empresa_cp AS empreses_cp, empreses_t.empresa_municipi AS empreses_municipi, empreses_t.empresa_telefon AS empreses_telefon, empreses_t.empresa_email AS empreses_email, empreses_t.empresa_persona_contacte_tracte AS empreses_pc_tracte, empreses_t.empresa_persona_contacte_nom AS empreses_pc_nom, empreses_t.empresa_persona_contacte_carrec AS empreses_pc_carrec, empreses_t.empresa_persona_contacte_telefon AS empreses_pc_telefon FROM empreses_t;"""


/* All empreses query Tiple<String, String, String> */
const val allEmpresesQuery =
        """SELECT empreses_t.empresa_email AS [empreses_email], empreses_t.empresa_persona_contacte_tracte AS [empreses_pc_tracte], empreses_t.empresa_persona_contacte_nom AS [empreses_pc_nom] FROM empreses_t WHERE (((empreses_t.empresa_persona_contacte_nom) Is Not Null));"""

/* TODO("Distinct municipis from empreses_t") */
const val allMunicipisFromEmpresesQuery =
        """SELECT DISTINCT empreses_t.empresa_municipi AS [empreses_municipi] FROM empreses_t;"""

const val allEmpresesByMunicipiQuery =
        """SELECT empreses_t.empresa_email AS [empreses_email], empreses_t.empresa_persona_contacte_tracte AS [empreses_pc_tracte], empreses_t.empresa_persona_contacte_nom AS [empreses_pc_nom] FROM empreses_t WHERE (((empreses_t.empresa_persona_contacte_nom) Is Not Null) AND empreses_t.municipi = ?);"""

const val insertVisitaQuery =
        """INSERT INTO visites_t (estades_codi, curs, tipus, data, hora, comentaris) VALUES (?, ?, ?, ?, ?, ?);"""

const val updateVisitaQuery =
        """UPDATE visites_t SET estades_codi = ?, curs = ?, tipus = ?, data = ?, hora = ?, comentaris = ? WHERE id = ?"""

const val insertSeguimentEmpresa =
        """INSERT INTO seguiment_empreses_t (empresa_id, data, comentaris) values (?, ?, ?)"""

const val allSeguimentEmpresesByIdEmpresa =
        """SELECT seguiment_empreses_t.id AS [seguiment_empreses_id], seguiment_empreses_t.empresa_id AS [seguiment_empreses_empresa_id], seguiment_empreses_t.data AS [seguiment_empreses_data], seguiment_empreses_t.comentaris AS [seguiment_empreses_comentaris] FROM seguiment_empreses_t WHERE seguiment_empreses_t.empresa_id = ?;"""


const val getFortecoQuery =
        """SELECT forteco_cursos_t.codi AS [codi_curs], forteco_cursos_t.nom AS [nom_curs], forteco_cursos_t.empresa AS [nom_empresa], forteco_cursos_t.data_inici AS [data_inici], forteco_cursos_t.data_final AS [data_final],  forteco_cursos_t.hora_inici AS [hora_inici], forteco_cursos_t.hora_final AS [hora_final], forteco_docents_t.nif AS [nif_docent], professors_t.noms as [noms_docent], professors_t.email AS [email_docent], professors_t.cos AS [cos_docent], professors_t.centre AS [nom_centre], professors_t.delegacio_territorial AS [nom_delegacio], professors_t.especialitat AS [nom_especialitat]
FROM (forteco_cursos_t INNER JOIN forteco_docents_t ON forteco_cursos_t.codi = forteco_docents_t.codi_curs) LEFT JOIN professors_t ON forteco_docents_t.nif = professors_t.nif
WHERE (((forteco_cursos_t.curs)= ?))
ORDER BY forteco_cursos_t.codi;"""

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
    fun isDocentAdmes(nif: String): Boolean {
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
            errorNotification(Utils.APP_TITLE, "El/La docent amb NIF $nif no té una estada concedidad")
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
            // cleanScreen()
            if (GesticusOs.renameForm(
                            nif,
                            registre.estada!!.numeroEstada,
                            registre.estada!!.tipusEstada
                    )
            ) {
                infoNotification(
                        Utils.APP_TITLE,
                        "S'ha modificat el nom de la sol·licitud '${nif}.pdf' correctament"
                )
            } else {
                errorNotification(Utils.APP_TITLE, "La sol·licitud '${nif}.pdf' no existeix")
            }
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
            infoNotification(Utils.APP_TITLE, "L'estada de $nif ha estat modificada correctament")
            true
        } catch (error: Exception) {
            errorNotification(Utils.APP_TITLE, error.message)
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
                                "Estada ${registre.estada?.numeroEstada} afegida correctament. Vols enviar un correu de confirmació a ${registre.docent?.nom}?"
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
                                                Utils.APP_TITLE,
                                                "S'ha enviat un correu de confirmació d'estada $numeroEstada registrada a ${registre.docent?.nom}"
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
                        runLater {
                            infoNotification(
                                    Utils.APP_TITLE,
                                    "S'ha enviat un correu de confirmació d'estada $numeroEstada iniciada a ${registre.docent?.nom}"
                            )
                        }

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
                        runLater {
                            infoNotification(
                                    Utils.APP_TITLE,
                                    "S'ha enviat un correu de confirmació d'estada $numeroEstada acabada a ${registre.docent?.nom}"
                            )
                        }

                    }
                    EstatsSeguimentEstadaEnum.BAIXA -> {

                    }
                    EstatsSeguimentEstadaEnum.TANCADA -> {
                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                SUBJECT_GENERAL,
                                BODY_TANCADA
                                        .replace("?1", emailAndTracte?.second ?: "Benvolgut/da,")
                                        .replace("?2", numeroEstada),
                                listOf(),
                                listOf<String>(CORREU_LOCAL1, emailAndTracte!!.first)
                        )
                        infoNotification(
                                Utils.APP_TITLE,
                                "S'ha enviat un correu de confirmació d'estada número $numeroEstada tancada a ${registre.docent?.nom}"
                        )
                    }
                    EstatsSeguimentEstadaEnum.RENUNCIADA -> {
                        val docent = registre.docent?.nom!!
                        val delSr = if (docent.startsWith("Sr.")) "del $docent" else "de la $docent"
                        val institut = registre.centre?.nom!!
                        val numEstada = registre.estada?.numeroEstada!!
                        val empresa = registre.empresa?.identificacio?.nom!!
                        val municipi = registre.empresa?.identificacio?.municipi!!
                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                SUBJECT_GENERAL,
                                BODY_RENUNCIA_A_TOTHOM
                                        .replace("?1", delSr)
                                        .replace("?2", nif)
                                        .replace("?3", institut)
                                        .replace("?4", numEstada)
                                        .replace("?5", empresa)
                                        .replace("?6", municipi),
                                listOf(),
                                listOf<String>(
                                        CORREU_LOCAL1,
                                        registre.docent?.email!!,
                                        registre.centre?.email!!,
                                        registre.empresa?.personaDeContacte?.email!!,
                                        registre.sstt?.emailCRHD!!
                                )
                        )
                        infoNotification(
                                Utils.APP_TITLE,
                                "S'ha enviat un correu de renùncia voluntària de l'estada número $numeroEstada a tots els agents implicats"
                        )
                    }
                    else -> {
                    }
                }
            } else {
                runLater {
                    errorNotification(APP_TITLE, "l'estada ${numeroEstada} no s'ha afegit correctament")
                }
            }

            true

        } catch (error: Exception) {
            runLater {
                errorNotification(APP_TITLE, error.message)
            }
            return false
        } finally {
            seguimentSts.closeOnCompletion()
        }
    }

    fun insertEstatDeEstadaDocumentada(
            numeroEstada: String,
            estat: EstatsSeguimentEstadaEnum,
            comentaris: String,
            hores: Int
    ): Boolean {

        if (!existeixNumeroDeEstada(numeroEstada)) {
            // Alert(Alert.AlertType.ERROR, "No existeix cap estada amb número $numeroEstada").showAndWait()
            return false
        }
        val registre = findRegistreByCodiEstada(numeroEstada)
        val nif = registre?.docent?.nif!!
        val emailAndTracte = findEmailAndTracteByNif(nif)

        when (estat) {
            /* Un cop documentada cal anotar el nombre d'hores certificades reals */
            EstatsSeguimentEstadaEnum.DOCUMENTADA -> {

                val seguimentSts = conn.prepareStatement(insertSeguimentQuery)
                seguimentSts.setString(1, numeroEstada)
                seguimentSts.setString(2, estat.name)
                seguimentSts.setString(3, comentaris)

                val count = seguimentSts.executeUpdate()
                if (count == 1) {
                    val estadaSts = conn.prepareStatement(updateHoresEstadesQuery)
                    estadaSts.setInt(1, hores)
                    estadaSts.setString(2, numeroEstada)
                    val regs = estadaSts.executeUpdate()
                    if (regs == 1) {
                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                SUBJECT_GENERAL,
                                BODY_DOCUMENTADA
                                        .replace(
                                                "?1", emailAndTracte?.second
                                                ?: "Benvolgut/da,"
                                        )
                                        .replace("?2", numeroEstada)
                                        .replace("?3", hores.toString())

                                ,
                                listOf(),
                                listOf<String>(CORREU_LOCAL1, emailAndTracte!!.first)
                        )

                        Alert(
                                Alert.AlertType.CONFIRMATION,
                                "S'ha enviat un correu de confirmació d'estada documentada número $numeroEstada a ${registre.docent?.nom}. Vols lliurar una còpia de la carta d'agraïment a l'empresa?"
                        )
                                .showAndWait()
                                .ifPresent {
                                    if (it == ButtonType.YES || it == ButtonType.OK) {
                                        val filename = GesticusReports.createCartaAgraiment(registre)
                                        val nomAmbTractament = registre.docent?.nom!!
                                        val docent = if (nomAmbTractament.startsWith("Sr.")) "el $nomAmbTractament"
                                        else if (nomAmbTractament.startsWith("Sra.")) "la $nomAmbTractament"
                                        else "el/la $nomAmbTractament"
                                        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                                SUBJECT_GENERAL,
                                                BODY_AGRAIMENT
                                                        .replace("?1", registre.empresa?.personaDeContacte?.nom!!)
                                                        .replace("?2", docent)
                                                ,
                                                listOf(filename!!),
                                                listOf(registre.empresa?.personaDeContacte?.email!!)
                                        )
                                    }
                                }

                        infoNotification(
                                Utils.APP_TITLE,
                                "S'ha enviat una carta d'agraïment de l'estada $numeroEstada a ${registre.empresa?.personaDeContacte?.nom} correctament"
                        )
                    }

                }


            }
            else -> {
            }

        }
        return true
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

        val estades = mutableListOf<EstadaQuery>()
        try {
            val statement = conn.prepareStatement(estadesByNifQuery)
            statement.setString(1, nif)
            val rs: ResultSet = statement.executeQuery()

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
        } catch (error: Exception) {
            errorNotification(Utils.APP_TITLE, error.message)
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
                        summary.add(
                                Summary(
                                        numeroEstada,
                                        professorAmbTractament,
                                        professorEmail,
                                        nomEmpresa,
                                        dataInici,
                                        dataFinal,
                                        darrerEstat.name,
                                        "Registrada però no comunicada"
                                )
                        )
                    }
                    /* Estada Acabada però no ha lliurat la documentació */
                    EstatsSeguimentEstadaEnum.ACABADA -> {
                        summary.add(
                                Summary(
                                        numeroEstada,
                                        professorAmbTractament,
                                        professorEmail,
                                        nomEmpresa,
                                        dataInici,
                                        dataFinal,
                                        darrerEstat.name,
                                        "Acabada però no documentada"
                                )
                        )
                    }
                    /* Estada Documentada però no tancada al GTAF */
                    EstatsSeguimentEstadaEnum.DOCUMENTADA -> {
                        summary.add(
                                Summary(
                                        numeroEstada,
                                        professorAmbTractament,
                                        professorEmail,
                                        nomEmpresa,
                                        dataInici,
                                        dataFinal,
                                        darrerEstat.name,
                                        "Documentada però no tancada"
                                )
                        )
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

    fun getAllEstades(): List<EstadaSearch> {

        val allEstades = conn.prepareStatement(allEstadesQuery)
        allEstades.setString(1, currentCourseYear())
        val allEstadesResultSet = allEstades.executeQuery()
        val estades = mutableListOf<EstadaSearch>()
        while (allEstadesResultSet.next()) {
            with(allEstadesResultSet) {
                estades.add(
                        EstadaSearch(
                                getString("estades_codi"),
                                getString("estades_nom_empresa"),
                                getString("estades_curs"),
                                getDate("estades_data_inici").toCatalanDateFormat(),
                                getDate("estades_data_final").toCatalanDateFormat(),
                                getString("professors_nom_amb_tractament"),
                                getString("estades_nif_professor"),
                                getString("professors_email")
                        )
                )
            }
        }
        allEstades.closeOnCompletion()
        return estades
    }


    /*
    * Actualitza l'estat de les gestionades de COMUNICADA a INICIADA (generalment dilluns)
    * i d'INICIADA a ACABADA (generalment divendres)
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
//                    val professorAmbTractament = allEstadesResultSet.getString("professors_nom_amb_tractament")
//                    val professorEmail = allEstadesResultSet.getString("professors_email")
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
                                insertSeguimentDeEstada(
                                        numeroEstada,
                                        EstatsSeguimentEstadaEnum.INICIADA,
                                        "Estada Iniciada"
                                )
                            }
                        }
                        /* D'INICIADA a ACABADA*/
                        EstatsSeguimentEstadaEnum.INICIADA -> {
                            if (avui.after(dataFinal)) {
                                // set estat ACABADA
                                insertSeguimentDeEstada(
                                        numeroEstada,
                                        EstatsSeguimentEstadaEnum.ACABADA,
                                        "Estada acabada"
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
            runLater {
                errorNotification(Utils.APP_TITLE, error.message)
            }

        }
    }

    /*
            * Aquest mètode lliura un correu a aquells que ja han acabat l'estada fa més d'un mes
            * però encara no l'han documentada. Aixo hi ha un view específic.
            * */
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
//                    val dataInici = allEstadesResultSet.getDate("estades_data_inici")
                    val dataFinal = allEstadesResultSet.getDate("estades_data_final")
//                    val avui = Date()
                    val darrerEstat =
                            EstatsSeguimentEstadaEnum.valueOf(lastSeguimentFromEstada.getString("seguiment_estat"))
                    when (darrerEstat) {
                        // Esta acabada i un mes després encara no ha lliurat la documentació
                        EstatsSeguimentEstadaEnum.ACABADA -> {

                            val inOneMonth = dataFinal.toLocalDate().plus(1, ChronoUnit.MONTHS)
                            if (LocalDate.now().isAfter(inOneMonth)) {
                                infoNotification(
                                        Utils.APP_TITLE,
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
            runLater {
                errorNotification(Utils.APP_TITLE, error.message)
            }

        }

    }

    /*
    * Aquest mètode genera un fitxer CSV a temporal amb totes les entrades de estades_t
    * ja documentades per tal de fer una càrrega massiva
    * */
    fun generateCSVFileStatusDocumentada(): Unit {

        val FILE_NAME = "${PATH_TO_TEMPORAL}estades-documentades-${currentCourseYear()}.csv"

        var fileWriter = FileWriter(FILE_NAME)
        var csvPrinter = CSVPrinter(
                fileWriter,
                CSVFormat
                        .EXCEL
                        .withIgnoreEmptyLines()
                        .withRecordSeparator("\n")
                        .withDelimiter(';')
                        .withQuote('"')
                        .withHeader(
                                "CODI_ANY",
                                "CODI_ACTIVITAT",
                                "CODI_PERSONA",
                                "NOM_ACTIVITAT",
                                "NUM_HORES_PREVISTES",
                                "DATA_INICI",
                                "DATA_FINAL"
                        )
        )

        try {
            val allEstades = conn.prepareStatement(allEstadesCSVQuery)
            allEstades.setString(1, currentCourseYear())
            val allEstadesResultSet = allEstades.executeQuery()
            while (allEstadesResultSet.next()) {
                val numeroEstada = allEstadesResultSet.getString("estades_codi")
                val seguiments = conn.prepareStatement(lastSeguimentForCodiEstadaQuery)
                seguiments.setString(1, numeroEstada)
                val lastSeguimentFromEstada = seguiments.executeQuery()
                if (lastSeguimentFromEstada.next()) {
//                    val professorNoms = allEstadesResultSet.getString("professors_noms")
//                    val professorEmail = allEstadesResultSet.getString("professors_email")
                    val professorNIF = allEstadesResultSet.getString("professors_nif")
                    val nomActivitat = allEstadesResultSet.getString("estades_nom_empresa")
                    val horesCertificades = allEstadesResultSet.getInt("estades_hores_certificades")
                    val dataInici = allEstadesResultSet.getDate("estades_data_inici")
                    val dataFinal = allEstadesResultSet.getDate("estades_data_final")
                    val darrerEstat =
                            EstatsSeguimentEstadaEnum.valueOf(lastSeguimentFromEstada.getString("seguiment_estat"))
                    when (darrerEstat) {
                        EstatsSeguimentEstadaEnum.DOCUMENTADA -> {

                            val data = Arrays.asList(
                                    "${currentCourseYear()}-${nextCourseYear()}",
                                    "${numeroEstada.substring(0, 10)}",
                                    professorNIF,
                                    "${nomActivitat}. Estada formativa de tipus B",
                                    horesCertificades + 5,
                                    dataInici.toCatalanDateFormat(),
                                    dataFinal.toCatalanDateFormat()
                            )
                            //println("$numeroEstada $professorNoms $professorEmail $dataInici $dataFinal")
                            csvPrinter.printRecord(data)
                        }
                        /* Do nothing */
                        else -> {

                        }
                    }
                }
            }
            allEstades.closeOnCompletion()
            fileWriter.flush()
            fileWriter.close()
            runLater {
                infoNotification(Utils.APP_TITLE, "$FILE_NAME creat correctament")
            }

        } catch (error: java.lang.Exception) {
            runLater {
                errorNotification(Utils.APP_TITLE, error.message)
            }

        }

    }

    /*
    * Aquest mètode genera un fitxer CSV a temporal amb totes les entrades de estades_t
    * per tal de fer una càrrega massiva
    * */
    fun generateCSVFileStatusAll(): Unit {

        val FILE_NAME = "${PATH_TO_TEMPORAL}estades-all-${currentCourseYear()}.csv"

        var fileWriter = FileWriter(FILE_NAME)
        var csvPrinter = CSVPrinter(
                fileWriter,
                CSVFormat
                        .EXCEL
                        .withIgnoreEmptyLines()
                        .withRecordSeparator("\n")
                        .withDelimiter(';')
                        .withQuote('"')
                        .withHeader(
                                "CODI_ANY",
                                "CODI_ACTIVITAT",
                                "CODI_PERSONA",
                                "NOM_ACTIVITAT",
                                "NUM_HORES_PREVISTES",
                                "DATA_INICI",
                                "DATA_FINAL"
                        )
        )

        try {
            val allEstades = conn.prepareStatement(allEstadesCSVQuery)
            allEstades.setString(1, currentCourseYear())
            val allEstadesResultSet = allEstades.executeQuery()
            while (allEstadesResultSet.next()) {
                val numeroEstada = allEstadesResultSet.getString("estades_codi")
                val seguiments = conn.prepareStatement(lastSeguimentForCodiEstadaQuery)
                seguiments.setString(1, numeroEstada)
                val lastSeguimentFromEstada = seguiments.executeQuery()
                if (lastSeguimentFromEstada.next()) {
//                    val professorNoms = allEstadesResultSet.getString("professors_noms")
//                    val professorEmail = allEstadesResultSet.getString("professors_email")
                    val professorNIF = allEstadesResultSet.getString("professors_nif")
                    val nomActivitat = allEstadesResultSet.getString("estades_nom_empresa")
                    val horesCertificades = allEstadesResultSet.getInt("estades_hores_certificades")
                    val dataInici = allEstadesResultSet.getDate("estades_data_inici")
                    val dataFinal = allEstadesResultSet.getDate("estades_data_final")

                    val data = Arrays.asList(
                            "${currentCourseYear()}-${nextCourseYear()}",
                            "${numeroEstada.substring(0, 10)}",
                            professorNIF,
                            "${nomActivitat}. Estada formativa de tipus B",
                            horesCertificades + 5,
                            dataInici.toCatalanDateFormat(),
                            dataFinal.toCatalanDateFormat()
                    )
                    //println("$numeroEstada $professorNoms $professorEmail $dataInici $dataFinal")
                    csvPrinter.printRecord(data)
                }
            }
            allEstades.closeOnCompletion()
            fileWriter.flush()
            fileWriter.close()
            runLater {
                infoNotification(Utils.APP_TITLE, "$FILE_NAME creat correctament")
            }

        } catch (error: java.lang.Exception) {
            runLater {
                errorNotification(Utils.APP_TITLE, error.message)
            }

        }

    }

    /*
  * Aquest mètode genera un fitxer CSV a temporal amb totes les estades
  * i adreces per a google maps
  * */
    fun generateCSVFileEstadesGoogleMaps(): Unit {

        val FILE_NAME = "${PATH_TO_TEMPORAL}estades-googlemaps.csv"

        var fileWriter = FileWriter(FILE_NAME)
        var csvPrinter = CSVPrinter(
                fileWriter,
                CSVFormat
                        .EXCEL
                        .withIgnoreEmptyLines()
                        .withRecordSeparator("\n")
                        .withDelimiter(',')
                        .withQuote('"')
                        //.withSkipHeaderRecord()
                        .withHeader("NOM EMPRESA", "DIRECCIO", "CODI POSTAL", "MUNICIPI")
        )

        try {
            val allEstades = conn.prepareStatement(allEstadesCSVQuery)
            allEstades.setString(1, currentCourseYear())
            val allEstadesResultSet = allEstades.executeQuery()
            while (allEstadesResultSet.next()) {
                val nomEmpresa = allEstadesResultSet.getString("estades_nom_empresa")
                val direccioEmpresa = allEstadesResultSet.getString("estades_direccio_empresa")
                val codiPostalEmpresa = allEstadesResultSet.getString("estades_codi_postal_empresa")
                val municipiEmpresa = allEstadesResultSet.getString("estades_municipi_empresa")

                val data = Arrays.asList(nomEmpresa, direccioEmpresa, codiPostalEmpresa, municipiEmpresa)
                csvPrinter.printRecord(data)
            }
            allEstades.closeOnCompletion()
            fileWriter.flush()
            fileWriter.close()
            runLater {
                infoNotification(Utils.APP_TITLE, "$FILE_NAME creat correctament")
            }

        } catch (error: java.lang.Exception) {
            runLater {
                errorNotification(Utils.APP_TITLE, error.message)
            }

        }

    }


    /*
    * Baixa voluntària
    * */
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
                errorNotification(Utils.APP_TITLE, "El registre amb NIF $nif no es troba")
                return
            }
        } catch (error: java.lang.Exception) {
            errorNotification(Utils.APP_TITLE, error.message)
            return
        }

        /* Ara efectivament la passem a alta/baixa */
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
                infoNotification(
                        "Gèsticus",
                        "La sol·licitud d'estada amb NIF $nif ha estat donat $altaBaixa correctament."
                )
                if (value) {
                    val nom = registre?.docent?.nom
                    val al = if (nom!!.startsWith("Sr.")) "al" else if (nom.startsWith("Sra.")) "a la" else "a"
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
                                            BODY_BAIXA_VOLUNTARIA.replace("?1", emailTracte?.second.orEmpty()),
                                            listOf(),
                                            listOf<String>(CORREU_LOCAL1, emailTracte?.first.orEmpty())
                                    )
                                }
                            }

                }
            } else {
                errorNotification(Utils.APP_TITLE, "No s'ha trobat el registre $nif a la taula 'admesos_t'")
            }
        } catch (error: SQLException) {
            errorNotification(Utils.APP_TITLE, "No s'ha trobat el registre $nif a la taula 'admesos_t'")
        }
    }

    private fun findAllPendingAdmesos(): List<CollectiuPendent> {
        val collectiu = arrayListOf<CollectiuPendent>()
        val AllAdmesosSenseEstadaStatement = conn.prepareStatement(findAllAdmesosSenseEstadaQuery)
        AllAdmesosSenseEstadaStatement.setString(1, currentCourseYear())
        val result = AllAdmesosSenseEstadaStatement.executeQuery()
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
        AllAdmesosSenseEstadaStatement.closeOnCompletion()
        return collectiu
    }

    fun doBaixaObligatoria() {

        val collectiu = findAllPendingAdmesos()
        collectiu.forEach {
            val setBaixaStatement: PreparedStatement = conn.prepareStatement(admesosSetBaixaToTrueFalseQuery)
            setBaixaStatement.setBoolean(1, true)
            setBaixaStatement.setString(2, it.nif)
            setBaixaStatement.setString(3, currentCourseYear())
            val count = setBaixaStatement.executeUpdate()
            if (count == 1) {
                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                        SUBJECT_GENERAL,
                        BODY_BAIXA_OBLIGATORIA.replace("?1", "${it.tractament} ${it.nom}"),
                        listOf(),
                        listOf(it.email)
                )
                information(APP_TITLE, "${it.nom} ha estat donat de baixa correctament.")
            }
        }
    }


    /* Aquest mètode revoca una estada concedida: informa docent, centre, empresa i ssttt */
    fun renunciaEstada(registre: Registre): Boolean {

        //val nif = registre.docent?.nif!!
        doBaixa(registre.estada?.numeroEstada!!, true)

        insertSeguimentDeEstada(
                registre.estada?.numeroEstada!!,
                EstatsSeguimentEstadaEnum.RENUNCIADA,
                "Renùncia voluntària"
        )

        return true
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
        val countTotalEstadesPerCentreStatement =
                conn.prepareStatement(countTotalEstadesNoGestionadesPerCentreQuery)
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
        val countTotalEstadesPerFamiliaStatement =
                conn.prepareStatement(countTotalEstadesNoGestionadesPerFamiliaQuery)
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
            val countTotalEstadesPerSSTTStatement =
                    conn.prepareStatement(countTotalEstadesNoGestionadesPerSSTTQuery)
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
            estadesPendents.add(
                    EstadaPendent(
                            result.getString("professors_nif"),
                            result.getString("professors_tractament"),
                            result.getString("professors_nom"),
                            result.getString("professors_telefon"),
                            result.getString("professors_email"),
                            result.getString("professors_especialitat"),
                            result.getString("centres_municipi"),
                            result.getString("centres_nom")
                    )
            )
        }
        return estadesPendents
    }

    /*
    * "SELECT
    * estades_t.codi AS estades_codi,
    * estades_t.nom_empresa AS estades_nom_empresa,
    * estades_t.direccio_empresa AS estades_direccio_empresa,
    * estades_t.codi_postal_empresa AS estades_codi_postal_empresa,
    * estades_t.municipi_empresa AS estades_municipi_empresa,
    * estades_t.contacte_nom AS estades_contacte_nom,
    * estades_t.contacte_carrec AS estades_contacte_carrec,
    * estades_t.contacte_telefon as estades_contacte_telefon,
    * estades_t.contacte_email AS estades_contacte_email,
    * estades_t.data_inici AS estades_data_inici,
    * estades_t.data_final AS estades_data_final,
    * estades_t.nif_professor AS estades_nif_professor,
    * professors_t.tractament AS professors_tractament,
    * professors_t.nom AS professors_nom,
    * professors_t.cognom_1 AS professors_cognom1,
    * professors_t.cognom_2 AS professors_cognom2 ,
    * professors_t.sexe AS professors_sexe,
    * professors_t.email AS professors_email,
    * professors_t.telefon AS professors_telefon,
    * professors_t.especialitat AS professors_especialitat,
    * professors_t.familia AS professors_familia,
    * professors_t.centre AS professors_centre,
    * professors_t.municipi AS professors_municipi,
    * professors_t.delegacio_territorial AS professors_delegacio_territorial
    * FROM estades_t INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif
    * WHERE (((estades_t.data_inici)<=Date()) AND ((estades_t.data_final)>=Date()) AND ((estades_t.curs)= ?));"
    *
    * */
    fun getEstadesEnCurs(): List<EstadaEnCurs> {
        val estadesEnCursStatement = conn.prepareStatement(estadesEnCursQuery)
        estadesEnCursStatement.setString(1, currentCourseYear())
        val result = estadesEnCursStatement.executeQuery()
        val estadesEnCurs = mutableListOf<EstadaEnCurs>()
        while (result.next()) {
            with(result) {
                estadesEnCurs.add(
                        EstadaEnCurs(
                                getString("estades_codi"),
                                getString("estades_nom_empresa"),
                                getString("estades_direccio_empresa"),
                                getString("estades_codi_postal_empresa"),
                                getString("estades_municipi_empresa"),
                                getString("estades_contacte_nom"),
                                getString("estades_contacte_carrec"),
                                getString("estades_contacte_telefon"),
                                getString("estades_contacte_email"),
                                getString("estades_data_inici"),
                                getString("estades_data_final"),
                                getString("estades_nif_professor"),
                                getString("professors_tractament"),
                                getString("professors_nom"),
                                getString("professors_cognom1"),
                                getString("professors_cognom2"),
                                getString("professors_sexe"),
                                getString("professors_email"),
                                getString("professors_telefon"),
                                getString("professors_especialitat"),
                                getString("professors_familia"),
                                getString("professors_centre"),
                                getString("professors_municipi"),
                                getString("professors_delegacio_territorial")
                        )
                )
            }

        }
        return estadesEnCurs
    }


    /*
    * baremQuery
    *
    * SELECT
    * barem_t.Id as barem_id,
    * barem_t.nif as barem_nif,
    * barem_t.nom as barem_nom,
    * barem_t.email as barem_email,
    * barem_t.curs as barem_curs,
    * barem_t.privat as barem_privat,
    * barem_t.nou as barem_cicle_nou,
    * barem_t.dual as barem_dual,
    * barem_t.grup as barem_grup,
    * barem_t.interi as barem_interi,
    * barem_t.repetidor as barem_repetidor,
    * barem_t.en_espera as barem_en_espera,
    * barem_t.nota_projecte as barem_nota_projecte,
    * barem_t.nota_antiguitat as barem_nota_antiguitat,
    * barem_t.nota_formacio as barem_nota_formacio,
    * barem_t.nota_treballs_desenvolupats as barem_nota_treball_desenvolupats,
    * barem_t.nota_altres_titulacions as barem_nota_altres_titulacions,
    * barem_t.nota_catedratic as barem_nota_catedratic,
    * barem_t.codi_grup as barem_codi_grup,
    * barem_t.nota_individual as barem_nota_individual,
    * barem_t.nota_grup as barem_nota_grup,
    * barem_t.comentaris as barem_comentaris
                "FROM barem_t;
    *
    *
    * */
    fun getBarem(): List<Barem> {
        val baremStatement = conn.prepareStatement(baremQuery)
        val result = baremStatement.executeQuery()
        val barem = mutableListOf<Barem>()
        while (result.next()) {
            with(result) {
                barem.add(
                        Barem(
                                getLong("barem_id"),
                                getString("barem_nif"),
                                getString("barem_nom"),
                                getString("barem_email"),
                                getString("barem_curs"),
                                getBoolean("barem_privat"),
                                getBoolean("barem_cicle_nou"),
                                getBoolean("barem_dual"),
                                getBoolean("barem_grup"),
                                getBoolean("barem_interi"),
                                getBoolean("barem_repetidor"),
                                getBoolean("barem_en_espera"),
                                getDouble("barem_nota_projecte"),
                                getDouble("barem_nota_antiguitat"),
                                getDouble("barem_nota_formacio"),
                                getDouble("barem_nota_treball_desenvolupats"),
                                getDouble("barem_nota_altres_titulacions"),
                                getDouble("barem_nota_catedratic"),
                                getString("barem_codi_grup"),
                                getDouble("barem_nota_individual"),
                                getDouble("barem_nota_grup"),
                                getString("barem_comentaris")
                        )
                )
            }

        }
        return barem
    }

    fun doMemoria() {
        infoNotification(Utils.APP_TITLE, "En procés")
    }

    fun doLlistatPendentsPerFamilies(): Boolean {
        val allFamiliesStatement = conn.prepareStatement(allFamiliesFromAdmesosQuery)
        val result = allFamiliesStatement.executeQuery()
        while (result.next()) {
            val familia = result.getString("professors_familia")
            GesticusReports.createCartaPendentsFamiliaHTML(familia, docentsPendentsPerFamilia(familia))
        }
        allFamiliesStatement.closeOnCompletion()
        return true
    }

    /*
    * SELECT
    * estades_t.codi AS estades_codi,
    * estades_t.nif_professor AS estades_nif_professor,
    * professors_t.cognom_1 AS professors_cognom1,
    * professors_t.cognom_2 AS professors_cognom2,
    * professors_t.nom AS professors_nom,
    * professors_t.familia AS professors_familia,
    * professors_t.centre AS professors_centre,
    * estades_t.nom_empresa AS estades_nom_empresa,
    * estades_t.municipi_empresa AS estades_municipi_empresa,
    * estades_t.data_inici AS estades_data_inici,
    * estades_t.data_final AS estades_data_final
    * FROM estades_t INNER JOIN professors_t ON estades_t.nif_professor = professors_t.nif
    * ORDER BY professors_t.familia, professors_t.centre;
    * */
    fun doLlistatEstadesFetesPerFamilies(): Boolean {
        val allEstadesStatement = conn.prepareStatement(allEstadesFetesYEnCursQuery)
        allEstadesStatement.setString(1, currentCourseYear())
        val result = allEstadesStatement.executeQuery()
        val allEstades = mutableListOf<AllEstades>()
        while (result.next()) {
            val estada = AllEstades(
                    result.getString("estades_codi"),
                    result.getString("estades_nif_professor"),
                    result.getString("professors_cognom1"),
                    result.getString("professors_cognom2"),
                    result.getString("professors_nom"),
                    result.getString("professors_familia"),
                    result.getString("professors_centre"),
                    result.getString("professors_destinacio"),
                    result.getString("professors_codi_especialitat"),
                    result.getString("estades_nom_empresa"),
                    result.getString("estades_municipi_empresa"),
                    result.getString("estades_data_inici"),
                    result.getString("estades_data_final")
            )
            allEstades.add(estada)
        }
        GesticusReports.createInformeAllEstades(allEstades)
        allEstadesStatement.closeOnCompletion()
        return true
    }

    /* Aquest métode envia un email de recordatori a tothom que encara no ha lliurar la seva sol·licitud */
    fun sendRecordatoriPendentsATothom(data: String): Boolean {

        val allFamiliesStatement = conn.prepareStatement(allFamiliesFromAdmesosQuery)
        val result = allFamiliesStatement.executeQuery()
        while (result.next()) {
            val familia = result.getString("professors_familia")
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
                        listOf(it.professorsEmail)
                )
            }
        }
        allFamiliesStatement.closeOnCompletion()
        return true
    }

    /* nif, nom, email, curs, baixa */
    fun insertDocentAAdmesos(docent: Docent): Boolean {

        val insertAdmesosStatement = conn.prepareStatement(insertAdmesQuery)
        with(insertAdmesosStatement) {
            with(docent) {
                setString(1, nif)
                setString(2, nom)
                setString(3, email)
                setString(4, currentCourseYear())
                setBoolean(5, false)
            }
        }
        val result = insertAdmesosStatement.executeUpdate()
        insertAdmesosStatement.closeOnCompletion()
        return result == 1
    }

    /*
    * SELECT visites_t.id AS visites_id, visites_t.estades_codi AS visites_estades_codi, visites_t.curs AS visites_curs, visites_t.tipus AS visites_tipus, visites_t.data AS visites_data, visites_t.hora AS visistes_hora, visites_t.comentaris AS visites_comentaris
            FROM visites_t
            WHERE visites_t.curs = ?;
    * */
    fun getVisites(): MutableList<Visita> {

        val allVisitesStatement = conn.prepareStatement(allVisitesQuery)
        allVisitesStatement.setString(1, currentCourseYear())
        val result = allVisitesStatement.executeQuery()
        val visites = mutableListOf<Visita>()
        while (result.next()) {
            with(result) {
                val visita = Visita(
                        getLong("visites_id"),
                        getString("visites_estades_codi"),
                        getString("visites_curs"),
                        getString("visites_tipus"),
                        getDate("visites_data").toLocalDate(),
                        getString("visites_hora"),
                        getString("visites_comentaris")
                )
                visites.add(visita)
            }
        }
        return visites
    }

    /*
    *
    * INSERT INTO visites_t (estades_codi, curs, tipus, data, hora, comentaris) VALUES (?, ?, ?, ?, ?, ?)
    *
    * */
    fun saveVisita(visita: Visita): Boolean {

        val insertVisitaStatement = conn.prepareStatement(insertVisitaQuery)
        with(insertVisitaStatement) {
            with(visita) {
                setString(1, estadesCodi)
                setString(2, curs)
                setString(3, tipus)
                setDate(4, java.sql.Date.valueOf(data))
                setString(5, hora.toString())
                setString(6, comentaris)
            }
        }
        val result = insertVisitaStatement.executeUpdate()
        insertVisitaStatement.closeOnCompletion()
        return result == 1

    }

    /*
    * UPDATE visites_t SET estades_codi = ?, curs = ?, tipus = ?, data = ?, hora = ?, comentaris = ? WHERE id = ?
    * */
    fun updateVisita(visita: Visita): Boolean {
        val updateVisitaStatement = conn.prepareStatement(updateVisitaQuery)
        with(updateVisitaStatement) {
            with(visita) {
                setString(1, estadesCodi)
                setString(2, curs)
                setString(3, tipus)
                setDate(4, java.sql.Date.valueOf(data))
                setString(5, hora)
                setString(6, comentaris)
                setLong(7, id)
            }
        }
        val result = updateVisitaStatement.executeUpdate()
        updateVisitaStatement.closeOnCompletion()
        return result == 1
    }

    fun generaInformeVisites(): Unit {

        val visites = getVisites()
        GesticusReports.generaInformeVisites(visites)

    }

    fun getFamilies(): List<String> {
        val allFamiliesStatement = conn.createStatement()
        val result = allFamiliesStatement.executeQuery(allFamiliesFromProfessorsQuery)
        val families = mutableListOf<String>()
        while (result.next()) {
            val familia = result.getString("professors_familia")
            families.add(familia)
        }
        return families
    }

    /*
    * Aquest metode retorna un triplete nom de docent amb tractament, especialitat i email
    * */
    private fun makeTripleEmailTractamentEspecialitatPerDocent(result: ResultSet): Triple<String, String, String> {
        val nom_amb_tractament = result.getString("professors_nom_amb_tractament")
        val especialitat = result.getString("professors_especialitat")
        val email = result.getString("professors_email")
        val triple = Triple(email, nom_amb_tractament, especialitat)
        return triple
    }

    /*
    * Aquest mètode retorna una parella nom de director amb tractament i email
    * */
    private fun makePairEmailTractamentEspecialitatPerDirector(result: ResultSet): Pair<String, String> {
        val nom_amb_tractament = result.getString("directors_nom_amb_tractament")
        val email = result.getString("directors_email")
        val pair = email to nom_amb_tractament
        return pair
    }

    /* return type List<Triple<email, tractament, especialitat>> de docents */
    fun getDocentsPerFamiliaYMunicipi(familia: String, municipi: String = "TOTHOM"): List<Triple<String, String, String>> {
        var allDocentsFromFamilia: PreparedStatement
        if (familia == "TOTHOM") {
            allDocentsFromFamilia = conn.prepareStatement(allDocentsFromProfessorsDeTotesLesFamiliaQuery)
        } else {
            allDocentsFromFamilia = conn.prepareStatement(allDocentsFromProfessorsPerFamiliaQuery)
            allDocentsFromFamilia.setString(1, familia)
        }
        val result: ResultSet = allDocentsFromFamilia.executeQuery()
        val docents = mutableListOf<Triple<String, String, String>>()
        while (result.next()) {
            if (municipi == "TOTHOM") {
                docents.add(makeTripleEmailTractamentEspecialitatPerDocent(result))
            } else {
                val cp = result.getString("professors_cp")
                when (municipi) {
                    "Barcelona" -> {
                        if (cp.substring(0, 2) == "08") {
                            docents.add(makeTripleEmailTractamentEspecialitatPerDocent(result))
                        }
                    }
                    "Tarragona" -> {
                        if (cp.substring(0, 2) == "43") {
                            docents.add(makeTripleEmailTractamentEspecialitatPerDocent(result))
                        }
                    }
                    "Lleida" -> {
                        if (cp.substring(0, 2) == "25") {
                            docents.add(makeTripleEmailTractamentEspecialitatPerDocent(result))
                        }
                    }
                    "Girona" -> {
                        if (cp.substring(0, 2) == "17") {
                            docents.add(makeTripleEmailTractamentEspecialitatPerDocent(result))
                        }
                    }
                }
            }
        }
        return docents
    }

    /* return type List<Pair<email, tractament>> de docents */
    fun getDirectorsPerMunicipi(municipi: String = "TOTHOM"): List<Pair<String, String>> {
        var allDirectorsFromMunicipi: PreparedStatement
        if (municipi == "TOTHOM") {
            allDirectorsFromMunicipi = conn.prepareStatement(allDirectorsQuery)
        } else {
            allDirectorsFromMunicipi = conn.prepareStatement(allDirectorsPerMunicipiQuery)
            allDirectorsFromMunicipi.setString(1, municipi)
        }
        val result: ResultSet = allDirectorsFromMunicipi.executeQuery()
        val directors = mutableListOf<Pair<String, String>>()
        while (result.next()) {
            if (municipi == "TOTHOM") {
                directors.add(makePairEmailTractamentEspecialitatPerDirector(result))
            } else {
                val cp = result.getString("professors_cp")
                when (municipi) {
                    "Barcelona" -> {
                        if (cp.substring(0, 2) == "08") {
                            directors.add(makePairEmailTractamentEspecialitatPerDirector(result))
                        }
                    }
                    "Tarragona" -> {
                        if (cp.substring(0, 2) == "43") {
                            directors.add(makePairEmailTractamentEspecialitatPerDirector(result))
                        }
                    }
                    "Lleida" -> {
                        if (cp.substring(0, 2) == "25") {
                            directors.add(makePairEmailTractamentEspecialitatPerDirector(result))
                        }
                    }
                    "Girona" -> {
                        if (cp.substring(0, 2) == "17") {
                            directors.add(makePairEmailTractamentEspecialitatPerDirector(result))
                        }
                    }
                }
            }
        }
        return directors
    }

    /*
    *
    * Triple: email, nom_amb_tractament, especialitat
    *
    * */
    fun sendEmailADocents(email: Email) {
        val professors: List<Triple<String, String, String>> = getDocentsPerFamiliaYMunicipi(email.pera, email.territori)
        //GesticusMailUserAgent.openConnection()
        professors.forEach {
            val cos = email.cos.replace("?1", it.second).replace("?2", it.third)
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(email.motiu, cos, emptyList(), listOf(it.first))
        }
        //GesticusMailUserAgent.closeConnection()
    }

    /*
    * TODO
    * Triple: email, nom_amb_tractament, especialitat
    *
    * */
    fun sendEmailADirectors(email: Email) {
        val directors: List<Triple<String, String, String>> = getDocentsPerFamiliaYMunicipi(email.pera, email.territori)
        //GesticusMailUserAgent.openConnection()
        directors.forEach {
            val cos = email.cos.replace("?1", it.second).replace("?2", it.third)
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(email.motiu, cos, emptyList(), listOf(it.first))
        }
        //GesticusMailUserAgent.closeConnection()
    }
    /*allEmpresesLongQuery
    *
    * empreses_t.id AS empreses_id,
    * empreses_t.empresa_nif AS empreses_nif,
    * empreses_t.empresa_nom AS empreses_nom,
    * empreses_t.empresa_direccio AS empreses_direccio,
    * empreses_t.empresa_cp AS empreses_cp,
    * empreses_t.empresa_municipi AS empreses_municipi,
    * empreses_t.empresa_telefon AS empreses_telefon,
    * empreses_t.empresa_email AS empreses_email,
    * empreses_t.empresa_persona_contacte_tracte AS empreses_pc_tracte,
    * empreses_t.empresa_persona_contacte_nom AS empreses_pc_nom,
    * empreses_t.empresa_persona_contacte_carrec AS empreses_pc_carrec,
    * empreses_t.empresa_persona_contacte_telefon AS empreses_pc_telefon
    *
    * */
    fun getAllLongEmpreses(): List<EmpresaBean> {

        val empreses = mutableListOf<EmpresaBean>()
        val allEmpresesStatement = conn.createStatement()
        val result = allEmpresesStatement.executeQuery(allEmpresesLongQuery)
        while (result.next()) {
            with(result) {
                val id = getInt("empreses_id")
                val nif = getString("empreses_nif")
                val nom = getString("empreses_nom")
                val direccio = getString("empreses_direccio")
                val cp = getString("empreses_cp")
                val municipi = getString("empreses_municipi")
                val telefon = getString("empreses_telefon")
                val email = getString("empreses_email")
                val pcTracte = getString("empreses_pc_tracte")
                val pcNom = getString("empreses_pc_nom")
                val pcCarrec = getString("empreses_pc_carrec")
                val pcTelefon = getString("empreses_pc_telefon")
                val empresaBean = EmpresaBean(
                        id,
                        nif,
                        nom,
                        direccio,
                        cp,
                        municipi,
                        telefon,
                        email,
                        pcTracte,
                        pcNom,
                        pcCarrec,
                        pcTelefon
                )
                empresaBean.seguiments = getallSeguimentEmpresesByIdEmpresa(id)
                empreses.add(empresaBean)
            }
        }
        return empreses
    }


    /*
    * allEmpresesQuery
    *
    * first is empreses_email
    * second is empreses_pc_tracte
    * third is empreses_pc_nom
    *
    *  */
    fun getAllEmpreses(): List<Triple<String, String, String>> {

        val empreses = mutableListOf<Triple<String, String, String>>()
        val allEmpresesStatement = conn.createStatement()
        val result = allEmpresesStatement.executeQuery(allEmpresesQuery)
        while (result.next()) {
            with(result) {
                val email = getString("empreses_email")
                val tractament = getString("empreses_pc_tracte")
                val nom = getString("empreses_pc_nom")
                empreses.add(Triple(email, tractament, nom))
            }
        }
        return empreses
    }

    /*
     * allMunicipisFromEmpresesQuery
     *
     *  */
    fun getAllMunicipisFromEmpreses(): List<String> {

        val municipis = mutableListOf<String>()
        val allMunicipisStatement = conn.createStatement()
        val result = allMunicipisStatement.executeQuery(allMunicipisFromEmpresesQuery)
        while (result.next()) {
            with(result) {
                val municipi = getString("empreses_municipi")
                municipis.add(municipi)
            }
        }
        return municipis
    }

    /* allEmpresesByMunicipiQuery */
    fun getAllEmpresesByMunicipi(municipi: String): List<Triple<String, String, String>> {

        val empreses = mutableListOf<Triple<String, String, String>>()
        val allEmpresesByMunicipiStatement = conn.prepareStatement(allEmpresesByMunicipiQuery)
        allEmpresesByMunicipiStatement.setString(1, municipi)
        val result = allEmpresesByMunicipiStatement.executeQuery()
        while (result.next()) {
            with(result) {
                val email = getString("empreses_email")
                val tractament = getString("empreses_pc_tracte")
                val nom = getString("empreses_pc_nom")
                empreses.add(Triple(email, tractament, nom))
            }
        }
        return empreses
    }

    /*
    * const val insertSeguimentEmpresa =
        """INSERT INTO seguiment_empreses_t (empresa_id, data, comentaris) values (?, ?, ?)"""

const val allSeguimentEmpresesByIdEmpresa =
        """SELECT seguiment_empreses_t.id AS [seguiment_empreses_id], seguiment_empreses_t.empresa_id AS [seguiment_empreses_empresa_id], seguiment_empreses_t.data AS [seguiment_empreses_data], seguiment_empreses_t.comentaris AS [seguiment_empreses_comentaris] FROM seguiment_empreses_t WHERE seguiment_empreses_t.empresa_id = ?;"""

    * */
    fun insertSeguimentEmpresa(empresaId: Int, comentaris: String): Boolean {

        val insertSeguimentEmpresaStatement = conn.prepareStatement(insertSeguimentEmpresa)
        with(insertSeguimentEmpresaStatement) {
            setInt(1, empresaId)
            setDate(2, java.sql.Date(System.currentTimeMillis()))
            setString(3, comentaris)
        }
        val result = insertSeguimentEmpresaStatement.executeUpdate()
        insertSeguimentEmpresaStatement.closeOnCompletion()
        return result == 1
    }

    fun getallSeguimentEmpresesByIdEmpresa(empresaId: Int): List<EmpresaSeguimentBean> {
        val empreses = mutableListOf<EmpresaSeguimentBean>()
        val allSeguimentEmpresesByIdEmpresaStatement = conn.prepareStatement(allSeguimentEmpresesByIdEmpresa)
        allSeguimentEmpresesByIdEmpresaStatement.setInt(1, empresaId)
        val result = allSeguimentEmpresesByIdEmpresaStatement.executeQuery()
        while (result.next()) {
            with(result) {
                val id = getInt("seguiment_empreses_id")
                val empresaRef = getInt("seguiment_empreses_empresa_id")
                val data = getDate("seguiment_empreses_data")
                val comentaris = getString("seguiment_empreses_comentaris")
                val empresa = EmpresaSeguimentBean(id, empresaRef, data, comentaris)
                empreses.add(empresa)
            }
        }
        return empreses
    }

    fun findSollicitantByNIF(nif: String): Sollicitant? {

        var sollicitant: Sollicitant? = null

        val allEmpresesByMunicipiStatement = conn.prepareStatement(findSollicitantsByNif)
        allEmpresesByMunicipiStatement.setString(1, nif)
        val result = allEmpresesByMunicipiStatement.executeQuery()
        if (result.next()) {
            with(result) {
                val nom = getString("professors_nom")
                val email = getString("professors_email")
                val cos = getString("professors_cos")
                val centre = getString("professors_centre")
                val unitat = getString("professors_delegacio")
                sollicitant = Sollicitant(nom, email, cos, centre, unitat)
            }
        }

        return sollicitant
    }

    /* This method creates a new directory where to copy all stuff */
    private fun creaDirectori(): String {
        val transaction = Integer.toHexString(System.currentTimeMillis().toInt())
        val newDir = "${PATH_TO_TEMPORAL}forteco_${transaction}"
        File(newDir).mkdir()
        return newDir
    }

    private fun copyInforme(where: String) {
        GesticusOs.copyFile(PATH_TO_DESPESES_INFORME_FORTECO, where)
    }

    private fun copyGraella(where: String) {
        GesticusOs.copyFile(PATH_TO_DESPESES_GRAELLA_FORTECO, where)
    }

    private fun creaZip(directori: String, destinacio: String) {
        GesticusOs.zipDirectory(directori, destinacio)
    }

    private fun lliuraZip(to: String, file: String) {
        GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                SUBJECT_GENERAL,
                BODY_AUTORITZACIO_DESPESES_FORTECO,
                listOf(file),
                listOf(to)
        )
    }

    /*
    *
    * SELECT forteco_cursos_t.codi AS [codi_curs],
    * forteco_cursos_t.nom AS [nom_curs],
    * forteco_cursos_t.empresa AS [nom_empresa],
    * forteco_cursos_t.data_inici AS [data_inici],
    * forteco_cursos_t.data_final AS [data_final],
    * forteco_cursos_t.hora_inici AS [hora_inici],
    * forteco_cursos_t.hora_final AS [hora_final],
    * forteco_docents_t.nif AS [nif_docent],
    * professors_t.noms as [noms_docent],
    * professors_t.email AS [email_docent],
    * professors_t.cos AS [cos_docent],
    * professors_t.centre AS [nom_centre],
    * professors_t.delegacio_territorial AS [nom_delegacio],
    * professors_t.especialitat AS [nom_especialitat]
    *
    * */
    private fun getAllForteco(): List<FortecoBean> {
        val fortecos = mutableListOf<FortecoBean>()
        val allFortecoStatement = conn.prepareStatement(getFortecoQuery)
        allFortecoStatement.setString(1, currentCourseYear())
        val result = allFortecoStatement.executeQuery()
        while (result.next()) {
            with(result) {
                val codiCurs = getString("codi_curs")
                val nomCurs = getString("nom_curs")
                val nomEmpresa = getString("nom_empresa")
                val dataInici = getDate("data_inici")
                val dataFinal = getDate("data_final")
                val horaInici = getString("hora_inici")
                val horaFinal = getString("hora_final")
                val nifDocent = getString("nif_docent")
                val nomsDocent = getString("noms_docent")
                val emailDocent = getString("email_docent")
                val cosDocent = getString("cos_docent")
                val nomCentre = getString("nom_centre")
                val nomDelegacio = getString("nom_delegacio")
                val nomEspecialitat = getString("nom_especialitat")
                val forteco = FortecoBean(
                        codiCurs,
                        nomCurs,
                        nomEmpresa,
                        dataInici.toLocalDate(),
                        dataFinal.toLocalDate(),
                        horaInici,
                        horaFinal,
                        nifDocent,
                        nomsDocent,
                        emailDocent,
                        cosDocent,
                        nomCentre,
                        nomDelegacio,
                        nomEspecialitat
                )
                fortecos.add(forteco)
            }
        }
        return fortecos
    }


    /*
    * This method will:
    * - Create a new directory
    * - Copy doc to new directory
    * - For each entry in forteco_docents_t from 2018 do
    *   - Create a PDF file
    *   - Copy to new directory
    * - Zip new directory
    * - Send zip to contact
    * */
    fun doForteco() {
        if (Files.notExists(Paths.get(PATH_TO_DESPESES_PROPOSTA_FORTECO))) {
            information(APP_TITLE, "La ruta $PATH_TO_DESPESES_INFORME_FORTECO no existeix")
            return
        }
        if (Files.notExists(Paths.get(PATH_TO_DESPESES_PROPOSTA_FORTECO))) {
            information(APP_TITLE, "La ruta $PATH_TO_DESPESES_PROPOSTA_FORTECO no existeix")
            return
        }
        if (Files.notExists(Paths.get(PATH_TO_DESPESES_GRAELLA_FORTECO))) {
            information(APP_TITLE, "La ruta $PATH_TO_DESPESES_GRAELLA_FORTECO no existeix")
            return
        }
        val dir = creaDirectori()
        copyInforme("${dir}\\informe_forteco.doc")
        copyGraella("${dir}\\graella_forteco.pdf")

        // process query
        val fortecos = getAllForteco()
        fortecos.forEach {
            GesticusPdf.creaSollicitudsDespesaFortecoPdf(File(PATH_TO_DESPESES_PROPOSTA_FORTECO), it, dir)
        }
        creaZip(dir, "${dir}.zip")
//        lliuraZip(CORREU_RESPONSABLE_FORMACIO, "${dir}.zip")
        lliuraZip("fpestades@xtec.cat", "${dir}.zip")
    }

    /* TODO("llei proteccio de dades: 39164k-jmv") */
    private fun escriuInformeHTML() {}

    fun close(): Unit {
        writeToLog("${LocalDate.now()} Closing connection.")
        conn.close()
    }

    /*
    *
    * TODO("Pendent")
    *
    * allEstadesQuery
    * [estades_t].codi as estades_codi,
    * estades_t.nom_empresa AS estades_nom_empresa,
    * [estades_t].curs as [estades_curs],
    * [estades_t].data_inici as [estades_data_inici],
    * [estades_t].data_final as [estades_data_final],
    * iif(professors_t.sexe = 'H', 'Sr. ', 'Sra. ') & professors_t.nom & ' ' & professors_t.cognom_1 & ' ' & professors_t.cognom_2 as [professors_nom_amb_tractament],
    * estades_t.nif_professor AS estades_nif_professor,
    * professors_t.email AS professors_email
    * */
    fun doEnquestes() {

        val allEstades = conn.prepareStatement(allEstadesQuery)
        allEstades.setString(1, currentCourseYear())
        val allEstadesResultSet = allEstades.executeQuery()
        while (allEstadesResultSet.next()) {
            val docentAmbTractament = allEstadesResultSet.getString("professors_nom_amb_tractament")
            val numeroEstada = allEstadesResultSet.getString("estades_codi")
            val email = allEstadesResultSet.getString("professors_email")
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_ENQUESTA,
                    BODY_ENQUESTA_ESTADES.replace("?1", docentAmbTractament),
                    listOf(),
                    listOf(email))

        }
        allEstades.closeOnCompletion()
    }

}
