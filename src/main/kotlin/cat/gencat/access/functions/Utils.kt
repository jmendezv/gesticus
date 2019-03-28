package cat.gencat.access.functions

//import com.sun.javafx.util.Utils
import cat.gencat.access.reports.SUBDIRECCIO_LINIA_0
import cat.gencat.access.reports.TECNIC_DOCENT_CARREC_0
import de.jensd.fx.glyphs.GlyphIcon
import de.jensd.fx.glyphs.GlyphIcons
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.util.Duration
import org.controlsfx.control.Notifications
import org.controlsfx.control.action.Action
import org.jasypt.util.text.BasicTextEncryptor
import tornadofx.*
import tornadofx.FX.Companion.messages
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors.callable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

const val MILLISECONDS_IN_A_DAY = 1000 * 60 * 60 * 24

const val CORREU_LOCAL1: String = "fpestades@xtec.cat"

const val SUBJECT_GENERAL: String = "Comunicat Estades Formatives"

const val FUNCIONARI_NOM = "Pep Méndez"

const val BODY_RESPONSABLE: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Bon dia Sonia,</p><br><p>Adjunt trobaràs un fitxer relatiu a una estada formativa en empresa de tipus B (amb substitució).</p><p>Aquest document un cop signat per la sub-directora, cal registrar-lo i enviar-lo per correu ordinari.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_DOCENT: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Us ha estat concedida una estada formativa en empresa de tipus B (<strong>amb substitució</strong>).</p><p>Si us plau, consulteu l'apartat: &quot;<em>Documentació a presentar al finalitzar l'estada</em>&quot; en aquest <a href='http://xtec.gencat.cat/ca/formacio/formaciocollectiusespecifics/formacio_professional/estades/' target='_blank'>enllaç</a>, per tal de procedir al tancament un cop finalitzada.</p><p>Trobareu els detalls de la vostra estada en el document adjunt.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_RECORDATORI_ESTADA_ACABADA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Segons consta en els nostres arxius, el ?2 va acabar la vostra estada formativa en empresa número ?3.</p><p>Recordeu que teniu un mes de temps des de la data de finalització per tal de lliurar a l'adreça fpestades@xtec.cat la documentació que detallem a continuació, a fi de que pugui ser reconeguda com una activitat formativa.</p><p><ul><li>Certificat expedit per l'empresa, on es faci constar el número d'estada, el vostre NIF i nom, les dates d'inici i final així com les hores realitzades segons el model que trobareu a la guía, </li><li>Memòria d'entre 5 i 10 fulls</li><li>Enquesta</li></ul></p><p>Si us plau, consulteu l'apartat: &quot;<em>Documentació a presentar al finalitzar l'estada</em>&quot; en aquest <a href='http://xtec.gencat.cat/ca/formacio/formaciocollectiusespecifics/formacio_professional/estades/' target='_blank'>enllaç</a>, per tal de procedir al tancament un cop finalitzada.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_RECORDATORI_ESTADA_PENDENT: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Segons consta en els nostres arxius, teniu concedida una estada formativa en empresa de tipus B de la família ?2, especialitat ?3 per a l'actual convocatòria ?4.</p><p>L'objectiu d'aquest missatge és el de recordar-vos que el ?5 s'exhaureix el termini de lliurament de les sol·licituds.</p><p>A més a més, cal que tingueu en compte que a l'abril i al maig hi ha algunes setmanes amb vacances, festius i ponts que no són hàbils a l'hora de demanar una estada formativa de tipus B.</p><p>És per aquest motiu que us demanem que ens digueu, en resposta a aquest mateix correu, en quin estat es troba la vostra estada? particularment, si esteu fent alguna gestió personal orientada a trobar una empresa on dur-la a terme.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_CENTRE: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Ha estat concedida una estada formativa en empresa de tipus B (<strong>amb substitució</strong>) ?2, ?3 d'aquest Centre.</p><p>Trobareu els detalls de l'estada en el document adjunt.</p><p>Properament rebreu una carta per correu ordinari, signada per la $SUBDIRECCIO_LINIA_0 i amb registre de sortida d'aquest Subdirecció.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_EMPRESA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Ha estat concedida una estada formativa ?2, ?3 de Formació Professional, en la vostra entitat.</p><p>Trobareu els detalls de l'estada en el document adjunt.</p><p>Properament rebreu una carta per correu ordinari, signada per la $SUBDIRECCIO_LINIA_0 i amb registre de sortida del Departament d'Educació.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_SSTT: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Bon dia,</p><br><p>Hem concedit una estada formativa en empresa de tipus B (<strong>amb substitució</strong>) ?1, ?2 del ?3.<p><strong>Confiem que sabreu gestionar-la tan aviat com sigui possible donat que d'això depen que es pugui dur a terme</strong>.</p><p>Trobareu els detalls de l'estada en el document adjunt.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/* Carta de certificació sota demanda */
const val BODY_TUTOR: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1,</p><br><p>Volem reconèixer la tasca que ?2 ha fet durant una estada formativa en empresa ?3 de Formació Professional.</p><p>Trobareu els detalls de l'estada en el document adjunt i properament rebreu una còpia per correu ordinari signada per la $SUBDIRECCIO_LINIA_0 i amb registre de sortida d'aquesta Subdirecció.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/* Carta d'agraïment que s'envia quan una estada esta documentada */
const val BODY_AGRAIMENT: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1,</p><br><p>Volem agrair-vos la participació en l'estada de formació que ?2 ha realitzat a la vostra seu.</p><p>Properament rebreu una carta per correu ordinari, signada per la $SUBDIRECCIO_LINIA_0 i amb registre de sortida del Departament d'Educació.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_ALTA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Bona feina!!!</p><p>Hem rebut la vostra sol·licitud d'estada formativa de tipus B correctament.</p><p>Aquesta estada té assignat el número ?2; recordeu aquest codi donat que el necessitareu més endavant perquè ha de constar al certiticat d'empresa.</p><p>Si no rebeu confirmació via correu electrònic de la seva gestió administrativa durant les properes hores, us heu de posar en contacte amb nosaltres immediatament.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_BAIXA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>En resposta a la vostra petició de baixa voluntària, hem procedit a donar de baixa l'estada formativa en empresa de tipus B que us haviem concedit.</p><p>Aquesta gestió no té cap efecte administratiu i podreu concursar en futures convocatòries sense cap mena de penalització.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/* Baixa d'una estada que ja esta tramitada, comunicat al docent */
const val BODY_RENUNCIA_A_TOTHOM: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Bon dia,</p><br><p>En resposta a la petició de baixa voluntària ?1 amb NIF ?2, de l'institut ?3, hem procedit a donar de baixa la vostra estada formativa de tipus B (amb substitució) número ?4 que us havíem concedit a l’empresa ?5 de ?6.</p><p>Us transmeto aquest fet perquè ho tingueu en compte per si se’n poguessin derivar altres accions.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/* Baixa d'una estada que ja esta tramitada, comunicat al docent */
const val BODY_RENUNCIA_DOCENT: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>En resposta a la vostra petició de baixa voluntària, hem procedit a donar de baixa l'estada formativa en empresa de tipus B que us haviem concedit.</p><p>Aquesta gestió no té cap efecte administratiu i podreu concursar en futures convocatòries sense cap mena de penalització.</p><p>Des d'el Departament, hem procedit a comunicar la renuncia a l'empresa, al vostre centre i als Serveis Territorials, però cal que la direcció del centre ho comuniqui als Serveis Territorials o Consorci corresponent i que us poseu en contacte amb l'empresa per tal de fer arribar aquest canvi a la persona o persones de contacte que considereu oportú.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/* Baixa d'una estada que ja esta tramitada, comunicat al centre */
const val BODY_RENUNCIA_CENTRE: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>de tipus B quee us haviem concedit.</p><p>.</p><p>Des d'el Departament, hem procedit a comunicar la renuncia a l'empresa, i als Serveis Territorials Consorci corresponent, però cal que la direcció del centre ho comuniqui als Serveis Territorials i que us poseu en contacte amb l'empresa per tal de fer arribar aquest canvi a la persona o persones de contacte que considereu oportú.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/* Baixa d'una estada que ja esta tramitada, comunicat al centre */
const val BODY_RENUNCIA_EMPRESA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>de tipus B quee us haviem concedit.</p><p>.</p><p>Des d'el Departament, hem procedit a comunicar la renuncia a l'empresa, i als Serveis Territorials Consorci corresponent, però cal que la direcció del centre ho comuniqui als Serveis Territorials i que us poseu en contacte amb l'empresa per tal de fer arribar aquest canvi a la persona o persones de contacte que considereu oportú.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/* Baixa d'una estada que ja esta tramitada, comunicat al centre */
const val BODY_RENUNCIA_SSTT: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>de tipus B quee us haviem concedit.</p><p>.</p><p>Des d'el Departament, hem procedit a comunicar la renuncia a l'empresa, i als Serveis Territorials Consorci corresponent, però cal que la direcció del centre ho comuniqui als Serveis Territorials i que us poseu en contacte amb l'empresa per tal de fer arribar aquest canvi a la persona o persones de contacte que considereu oportú.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_INICIADA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Segons consta en els nostres arxius, heu començat o properament començareu la vostra estada formativa en empresa de tipus B número ?2.</p><p>Recordeu que la vostra estada esta condicionada a que el/la substitut/a prengui possessió de la vostra plaça.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_ACABADA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Segons consta en els nostres arxius, heu acabat o properament acabareu la vostra estada formativa en empresa de tipus B número ?2.</p><p>Recordeu que teniu un mes de temps des de la data de finalització per tal de lliurar a l'adreça fpestades@xtec.cat la documentació que detallem a continuació, a fi de que pugui ser reconeguda com una activitat formativa.</p><p><ul><li>Certificat expedit per l'empresa</li><li>Memòria d'entre 5 i 10 fulls</li><li>Enquesta</li></ul></p><p>Si us plau, consulteu l'apartat: &quot;<em>Documentació a presentar al finalitzar l'estada (A o B)</em>&quot; en aquest <a href='http://xtec.gencat.cat/ca/formacio/formaciocollectiusespecifics/formacio_professional/estades/' target='_blank'>enllaç</a>, per tal de procedir al tancament un cop finalitzada.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_DOCUMENTADA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Segons consta en els nostres arxius, hem rebut la documentació relativa a la vostra estada formativa en empresa de tipus B número ?2 de ?3 hores.</p>Properament, procedirem al seu tancament.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_TANCADA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Hem procedit al tancament de la vostra estada formativa en empresa de tipus B número ?2.</p><p>Properament, podreu consultar el seu reconeixement en aquest <a href='http://xtec.gencat.cat/ca/formacio/' target='_blank'>enllaç</a>.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/* Col·lectiu sanitàries */
const val BODY_COLLECTIU: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>Estem fent gestions de cara a poder donar resposta a la vostra estada formativa de tipus B (amb substitució) de la família ?2 i especialitat ?3 de la convocatòria actual ?4.</p><p>És per aquest motiu que us demano que respongueu a aquest correu, enumerant tres activitats per ordre de més a meny preferència que voldrieu fer i que poden ser diferents de les que vàreu esmentar en la vostra sol·licitud inicial.</p><p>Finalment, també cal que ens indiqueu si estaríeu d'acord en transformar la vostra estada de tipus B en una estada de tipus A (sense substitució) a realitzar durant els mesos de juny/juliol, en el benestès que no sempre serà possible trobar una plaça en la modalitat B per a cada docent durant el període escolar.</p></br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_SUMMARY: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1,</p><br><p>Segons consta en els nostres arxius, fa ?2 dies que vàreu acabar la vostra estada número ?3 a ?4. Esperem que hagi estat una bona experiència.</p><p>Si us plau, consulteu l'apartat: &quot;<em>Documentació a presentar al finalitzar l'estada</em>&quot; en aquest <a href='http://xtec.gencat.cat/ca/formacio/formaciocollectiusespecifics/formacio_professional/estades/' target='_blank'>enllaç</a>, per tal de procedir al seu tancament, abans de 30 dies naturals.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_ESTADA_EN_CURS: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Bon dia,</p><br><p>Segons consta en els nostres arxius, actualment s'esta desenvolupant l'estada formativa número ?1 a l'empresa o institució ?2 amb seu a ?3.</p><p>És per aquest motiu i en compliment de la normativa vigent, que us demanem la oportunitat d'assistir en qualitat d'observador durant un mati, en la data que acordem.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"


const val BODY_ESTADA_NO_CONCEDIDA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1,</p><br><p>Segons consta en els nostres arxius, no teniu concedida cap estada formativa en empresa de tipus B per a la convocatòria actual ?2.</p>Si enteneu que es tracta d'un error administratiu cal que ens ho comuniqueu immediatament, i si voleu proposar-ne una, cal que ho feu a través de la direcció del vostre centre.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/*
* TODO("Relació directors dels candidats a estada B")
*
* ?1 Benvolgut/Benvolguda
* ?2 nom de centre
* ?3 20xx-20xx
* ?4 <ol><li>Sr. x de la familia x especialitat y</li></ol>
*
* */
const val BODY_DIRECTOR: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>?1</p><br><p>A continuació detallem una llista de docents del vostre centre, ?2, que han sol·licitat una estada formativa de tipus B (amb substitut) per la convocatòria actual ?3.</p>?4<br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>$TECNIC_DOCENT_CARREC_0</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"


/* TODO("Review") */
const val BODY_LLISTAT_PROVISIONAL: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Adjunt trobareu la llista provisional d'admesos/exclosos de la convocatoria actual d'estades formatives en empresa tipus B (<strong>amb substitució</strong>).</p><p>S'obre un termini de 10 dies naturals, a comptar des de l'endemà de la publicació de les llistes, per tal de fer les al·legacions i esmenes que considereu oportunes.</p><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/* TODO("Review") */
const val BODY_LLISTAT_DEFINITIU: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Adjunt trobareu la llista definitiva d'admesos/exclosos de la convocatoria actual d'gestionades formatives en empresa tipus B (<strong>amb substitució</strong>).</p><p>S'obre el termini de lliurament de sol·licituds, degudament emplenades per tal de facilitar el tractament informàtic, que finalitzarà el proper <b>30 d'abril</b>.</p><p>Cal que tingueu en compte el següent:</p><ul><li>Més enllà del <b>21 de juny</b> els Serveis Territorials/Consorce de Barcelona no acostumen a enviar substituts/es.</li><li>Les substitucions d'un mateix Centre i d'una mateixa família han de ser consecutives.</li><li>La durada de les estades és de dues setmanes: de dilluns a divendres de la següent setmana, i sense dies festius a banda del cap de setmana.</li><li>Les estades formatives de tipus B estan condicionades a què el/la substitut/a hagi acceptat el nomenament i prengui possessió de la vostra plaça.</li><li>Convé que la Direcció reclami el/la substitut/a als SSTT corresponents.</li><li>Si no teniu empresa, en alguns casos, el propi Departament en pot proporcionar una, però recomanen que feu les gestions pertinents de forma individual.</li><li>Les sol·licituds del col·lectiu Sanitari, si no teniu cap persona de contacte, es gestionen des d'el Departament. <i>En rebreu més informació properament</i>.</li><li>En acabar l'estada, disposeu d'un mes per tal de lliurar la documentació de tancament: certificat d'empresa en paper oficial i amb el corresponent segell, memòria, i imprès d'avaluació.</li><li>Si per qualsevol motiu no podeu dur a terme la vostra estada, cal que comuniqueu la baixa voluntària tan aviat com sigui possible, per tal de poder assignar-la a una altre persona. Recordeu que la baixa voluntària no te cap penalització administrativa.</li></ul><p>Trobareu tota la informació necessària en aquest enllaç:</p><a href='http://xtec.gencat.cat/ca/formacio/formaciocollectiusespecifics/formacio_professional/gestionades/'>Estades formatives del professorat a les empreses i institucions</a><br><p>Ben Cordialment,</p><p>$FUNCIONARI_NOM</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"


const val PATH_TO_BASE = "H:\\Mendez\\gesticusv2\\"
const val PATH_TO_REPORTS = "${PATH_TO_BASE}reports"
const val PATH_TO_LLISTATS = "${PATH_TO_BASE}llistats"
const val PATH_TO_DB: String = "${PATH_TO_BASE}bd\\gesticus.accdb"
const val PATH_TO_FORMS: String = "${PATH_TO_BASE}forms\\"
const val PATH_TO_TEMPORAL = "${PATH_TO_BASE}temporal\\"
const val PATH_TO_HELP = "${PATH_TO_BASE}help\\"
const val PATH_TO_CONFIG = "${PATH_TO_BASE}config\\"
const val PATH_TO_LOG = "${PATH_TO_BASE}log\\log.txt"
//const val PATH_TO_LLISTAT_PROVISIONAL = "${PATH_TO_BASE}gesticusv2\\temporal\\resolucio_provisional_estades_tipus_b_2018.xlsx"
//const val PATH_TO_LLISTAT_DEFINITIU = "${PATH_TO_BASE}gesticusv2\\temporal\\resolucio_definitiva_estades_tipus_b_2018.xlsx"
const val PATH_TO_LOGO_HTML = "file:///H:/Mendez/gesticusv2/logos/logo_bn.jpg"
const val PATH_TO_LOGO_CERTIFICAT_HTML = "file:///H:/Mendez/gesticusv2/logos/logo_original.jpg"
const val PATH_TO_LOGO = "${PATH_TO_BASE}logos\\logo_bn.jpg"
const val PATH_TO_ICONS = "${PATH_TO_BASE}icons\\"

const val PATH_TO_COPY = "${PATH_TO_BASE}historic\\"

// config

const val CONFIG_KEY_LAST_TIME_OPEN = "last_time_open"

/* Form A */
const val FORM_A_FIELD_NOM_EMPRESA = "nom i cognoms.1"
const val FORM_A_FIELD_NOM_DOCENT = "nom i cognoms.0.0"
const val FORM_A_FIELD_MOBIL_DOCENT = "nom i cognoms.0.1"
const val FORM_A_FIELD_EMAIL_DOCENT = "nom i cognoms.0.2"
const val FORM_A_FIELD_NIF_DOCENT = "nom i cognoms.0.3"
// Aquest checkbox val On si esta seleccionat i Off si no ho esta
const val FORM_A_FIELD_TE_EMPRESA =
        "S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.0"
// Aquest checkbox val On si esta seleccionat i Off si no ho esta
const val FORM_A_FIELD_NO_TE_EMPRESA =
        "S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.1"
const val FORM_A_FIELD_NIF_EMPRESA = "CIF"
const val FORM_A_FIELD_DIRECCIO_EMPRESA = "adreça.0.0"
const val FORM_A_FIELD_EMAIL_EMPRESA = "adreça.1.0.0"
const val FORM_A_FIELD_MUNICIPI_EMPRESA = "municipi"
const val FORM_A_FIELD_CP_EMPRESA = "cp empresa"
const val FORM_A_FIELD_TELEFON_EMPRESA = "telèfon.0"
const val FORM_A_FIELD_TELEFON_PERSONA_DE_CONTACTE_EMPRESA = "telèfon.1"
const val FORM_A_FIELD_TELEFON_TUTOR_EMPRESA = "telèfon.2"
const val FORM_A_FIELD_NOM_CONTACTE_EMPRESA = "nom contacte"
const val FORM_A_FIELD_CARREC_CONTACTE_EMPRESA = "càrrec"
const val FORM_A_FIELD_NOM_TUTOR_EMPRESA = "nom tutor"
const val FORM_A_FIELD_CARREC_TUTOR_EMPRESA = "càrrec tutor"
const val FORM_A_FIELD_DURADA_HORES_ESTADA = "durada hores.0"
const val FORM_A_FIELD_DATA_INICI_ESTADA = "inici.0.0"
const val FORM_A_FIELD_DATA_FI_ESTADA = "fi"
const val FORM_A_FIELD_HORA_INICI_MATI_ESTADA = "hores1.0"
const val FORM_A_FIELD_HORA_FINAL_MATI_ESTADA = "hores1.1"
const val FORM_A_FIELD_HORA_INICI_TARDA_ESTADA = "hores1.2"
const val FORM_A_FIELD_HORA_FINAL_TARDA_ESTADA = "hores1.3"
const val FORM_A_FIELD_SECTOR_EMPRESA = "sector.0"
const val FORM_A_FIELD_TIPUS_EMPRESA = "tipus"
// Aquest option val Opción1 si esta seleccionat el Sí i Opción2 si esta selcctionat el No
const val FORM_A_FIELD_FP_DUAL_ESTADA = "Group1"
const val FORM_A_FIELD_OBJECTIUS_ESTADA = "objectius"
const val FORM_A_FIELD_ACTIVITATZ_ESTADA = "activitats"
const val FORM_A_FIELD_CODI_CENTRE_ESTADA = "codi_centre"

/* Form B */
const val FORM_B_FIELD_NOM_EMPRESA = "nom i cognoms.1"
const val FORM_B_FIELD_NOM_DOCENT = "nom i cognoms.0.0"
const val FORM_B_FIELD_MOBIL_DOCENT = "nom i cognoms.0.1"
const val FORM_B_FIELD_EMAIL_DOCENT = "nom i cognoms.0.2"
const val FORM_B_FIELD_NIF_DOCENT = "nom i cognoms.0.3"
// Aquest checkbox val On si esta seleccionat i Off si no ho esta
const val FORM_B_FIELD_TE_EMPRESA =
        "S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.0"
// Aquest checkbox val On si esta seleccionat i Off si no ho esta
const val FORM_B_FIELD_NO_TE_EMPRESA =
        "S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.1"
const val FORM_B_FIELD_NIF_EMPRESA = "CIF"
const val FORM_B_FIELD_DIRECCIO_EMPRESA = "adreça.0.0"
const val FORM_B_FIELD_EMAIL_EMPRESA = "adreça.1.0.0"
const val FORM_B_FIELD_MUNICIPI_EMPRESA = "municipi"
const val FORM_B_FIELD_CP_EMPRESA = "cp empresa"
const val FORM_B_FIELD_TELEFON_EMPRESA = "telèfon.0"
const val FORM_B_FIELD_TELEFON_PERSONA_DE_CONTACTE_EMPRESA = "telèfon.1"
const val FORM_B_FIELD_TELEFON_TUTOR_EMPRESA = "telèfon.2"
const val FORM_B_FIELD_NOM_CONTACTE_EMPRESA = "nom contacte"
const val FORM_B_FIELD_CARREC_CONTACTE_EMPRESA = "càrrec"
const val FORM_B_FIELD_NOM_TUTOR_EMPRESA = "nom tutor"
const val FORM_B_FIELD_CARREC_TUTOR_EMPRESA = "càrrec tutor"
const val FORM_B_FIELD_DURADA_HORES_ESTADA = "durada hores.0"
const val FORM_B_FIELD_DATA_INICI_ESTADA = "inici.0.0"
const val FORM_B_FIELD_DATA_FI_ESTADA = "fi"
const val FORM_B_FIELD_HORA_INICI_MATI_ESTADA = "hores1.0"
const val FORM_B_FIELD_HORA_FINAL_MATI_ESTADA = "hores1.1"
const val FORM_B_FIELD_HORA_INICI_TARDA_ESTADA = "hores1.2"
const val FORM_B_FIELD_HORA_FINAL_TARDA_ESTADA = "hores1.3"
const val FORM_B_FIELD_SECTOR_EMPRESA = "sector.0"
const val FORM_B_FIELD_TIPUS_EMPRESA = "tipus"
// Aquest option val Opción1 si esta seleccionat el Sí i Opción2 si esta selcctionat el No
const val FORM_B_FIELD_FP_DUAL_ESTADA = "Group1"
const val FORM_B_FIELD_OBJECTIUS_ESTADA = "objectius"
const val FORM_B_FIELD_ACTIVITATZ_ESTADA = "activitats"
const val FORM_B_FIELD_CODI_CENTRE_ESTADA = "codi_centre"

val NIF_REGEXP = "0\\d{8}[A-Z]".toRegex()

val NIE_REGEXP = "[A-Z]\\d{7}[A-Z]".toRegex()

val CODI_POSTAL_REGEXP = "\\d{5}".toRegex()

class Utils {

    companion object {

        val APP_TITLE: String = messages["app_title"]

        private fun currentYear(): Int {
            val month = LocalDate.now().month.value
            /* Entre setembre i desembre és l'any actual, si no és un any menys */
            return if (month > 8 && month <= 12) LocalDate.now().year else LocalDate.now().year - 1
        }

        fun currentCourseYear(): String = currentYear().toString()

        fun nextCourseYear(): String = (currentYear() + 1).toString()

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
            val terminacions = arrayOf(
                    "T",
                    "R",
                    "W",
                    "A",
                    "G",
                    "M",
                    "Y",
                    "F",
                    "P",
                    "D",
                    "X",
                    "B",
                    "N",
                    "J",
                    "Z",
                    "S",
                    "Q",
                    "V",
                    "H",
                    "L",
                    "C",
                    "K",
                    "E"
            )

            val terminacio = substring(length - 1)

            // DNI 099999999A
            if (matches("0\\d{8}[A-Z]".toRegex())) {
                val modul = Integer.parseInt(substring(1, 9)) % 23
                return terminacio == terminacions[modul]
            }
            // DNI 99999999A
            if (matches("\\d{8}[A-Z]".toRegex())) {
                val modul = Integer.parseInt(substring(0, 8)) % 23
                return terminacio == terminacions[modul]
            }
            // NIE que comença per X9999999A, la X cau
            if (matches("[X]\\d{7}[A-Z]".toRegex())) {
                return true
            }
            // NIE que comença per Y9999999A, la Y es substitueix per 1
            if (matches("[Y]\\d{7}[A-Z]".toRegex())) {
                return true
            }
            // NIE que comença per Z9999999A, la Z es substitueix per 2
            if (matches("[Z]\\d{7}[A-Z]".toRegex())) {
                return true
            }

            return false
        }

        fun String.clean(): String = replace('\u00A0', ' ').trim()

        fun <V, T : ScheduledExecutorService> T.schedule(
                delay: Long,
                unit: TimeUnit = TimeUnit.HOURS,
                action: () -> V
        ): ScheduledFuture<*> {
            return schedule(
                    callable { action() },
                    delay, unit
            )
        }


        fun writeToLog(msg: String): Unit {
            val record = "${LocalDateTime.now().toString()} $msg\n"
            Files.write(Paths.get(PATH_TO_LOG), record.lines(), StandardOpenOption.APPEND)
        }

        /*
    *
    * Valid data formats:
    *
    * 99/99/9999
    * 9/9/99
    * 99-99-9999
    * 9-9-99
    * 99.99.9999
    * 9.9.99
    *
    * */
        fun parseDate(dataStr: String): LocalDate {

            lateinit var data: LocalDate

            if (dataStr.matches("\\d\\d/\\d\\d/[0-9]{4}".toRegex())) {
                data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        ?: LocalDate.now()
            } else if (dataStr.matches("\\d/\\d/[0-9]{2}".toRegex())) {
                data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d/M/yy"))
                        ?: LocalDate.now()
            } else if (dataStr.matches("\\d\\d-\\d\\d-[0-9]{4}".toRegex())) {
                data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        ?: LocalDate.now()
            } else if (dataStr.matches("\\d-\\d-[0-9]{2}".toRegex())) {
                data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d-M-yy"))
                        ?: LocalDate.now()
            } else if (dataStr.matches("\\d\\d\\.\\d\\d\\.[0-9]{4}".toRegex())) {
                data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        ?: LocalDate.now()
            } else if (dataStr.matches("\\d\\.\\d\\.[0-9]{2}".toRegex())) {
                data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("d.M.yy"))
                        ?: LocalDate.now()
            } else {
                data = LocalDate.now()
            }

            return data
        }

        /* gets 0001230600/2018-2019 and returns 0001240600/2018-2019 */
        fun nextEstadaNumber(codi: String): String {
            val nextNumEstada = Integer.parseInt(codi.substring(3, 6)) + 1
            val numberFormat = NumberFormat.getIntegerInstance()
            numberFormat.minimumIntegerDigits = 3
            numberFormat.maximumIntegerDigits = 3
            val newNumEstada = numberFormat.format(nextNumEstada)
            return codi.substring(0, 3) + newNumEstada + codi.substring(6)
        }


        fun isEmailValid(email: String): Boolean {
            return Pattern.compile(
                    "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(email).matches()
        }

        fun Button.icon(icon: GlyphIcons, toolTip: String = "", minButtonWidth: Double = 64.0) {
            graphic = when (icon) {
                is FontAwesomeIcon -> FontAwesomeIconView(icon)
                is MaterialDesignIcon -> MaterialDesignIconView(icon)
                else -> throw IllegalArgumentException("Unknown font family ${icon.fontFamily}")
            }
            with(graphic as GlyphIcon<*>) {
                contentDisplay = ContentDisplay.GRAPHIC_ONLY
                setSize("3em")
                setGlyphSize(32.0)
            }
            minWidth = minButtonWidth
            tooltip = tooltip(toolTip)
        }

        /* From LocalDate to Date*/
        fun asDate(localDate: LocalDate) =
                asDate(localDate.atStartOfDay())

        /* From LocalDateTime to Date */
        fun asDate(localDateTime: LocalDateTime) =
                Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())

        fun asLocalDate(date: Date) =
                Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

        fun asLocalDateTime(date: Date) =
                asLocalDate(date).atStartOfDay()

        internal fun notification(
                title: String?,
                text: String?,
                graphic: Node?,
                position: Pos = Pos.BOTTOM_RIGHT,
                hideAfter: Duration = Duration.seconds(5.0),
                darkStyle: Boolean = false, owner: Any?, vararg action: Action
        ): Notifications {
            val notification = Notifications
                    .create()
                    .title(title ?: "")
                    .text(text ?: "")
                    .graphic(graphic)
                    .position(position)
                    .hideAfter(hideAfter)
                    .action(*action)
            if (owner != null)
                notification.owner(owner)
            if (darkStyle)
                notification.darkStyle()
            return notification
        }

        fun warningNotification(
                title: String?,
                text: String?,
                position: Pos = Pos.BOTTOM_RIGHT,
                hideAfter: Duration = Duration.seconds(5.0),
                darkStyle: Boolean = false, owner: Any? = null, vararg action: Action
        ) {
            notification(title, text, null, position, hideAfter, darkStyle, owner, *action)
                    .showWarning()
        }

        fun infoNotification(
                title: String?,
                text: String?,
                position: Pos = Pos.BOTTOM_RIGHT,
                hideAfter: Duration = Duration.seconds(5.0),
                darkStyle: Boolean = false, owner: Any? = null, vararg action: Action
        ) {
            notification(title, text, null, position, hideAfter, darkStyle, owner, *action)
                    .showInformation()
        }

        fun confirmNotification(
                title: String?,
                text: String?,
                position: Pos = Pos.BOTTOM_RIGHT,
                hideAfter: Duration = Duration.seconds(5.0),
                darkStyle: Boolean = false, owner: Any? = null, vararg action: Action
        ) {
            notification(title, text, null, position, hideAfter, darkStyle, owner, *action)
                    .showConfirm()
        }

        fun errorNotification(
                title: String?,
                text: String?,
                position: Pos = Pos.BOTTOM_RIGHT,
                hideAfter: Duration = Duration.seconds(5.0),
                darkStyle: Boolean = false, owner: Any? = null, vararg action: Action
        ) {
            notification(title, text, null, position, hideAfter, darkStyle, owner, *action)
                    .showError()
        }

        fun customNotification(
                title: String?,
                text: String?,
                graphic: Node,
                position: Pos = Pos.BOTTOM_RIGHT,
                hideAfter: Duration = Duration.seconds(5.0),
                darkStyle: Boolean = false, owner: Any? = null,
                vararg action: Action
        ) {
            notification(title, text, graphic, position, hideAfter, darkStyle, owner, *action)
                    .show()
        }

        /* De COGNOM1 COGNOM2, NOM1 NOM2 a Nom1 Nom2 Cognom1 Cognom2 Apellido2*/
        fun String.nomPropi() =
                toLowerCase()
                        .split(",")
                        .reversed()
                        .joinToString(separator = " ")
                        .trim()
                        .split(" ")
                        .joinToString(separator = " ") {
                            it.capitalize()
                        }

        fun Date.toCatalanFormat() = SimpleDateFormat("dd/MM/yyyy").format(this)

        val formatter = SimpleDateFormat("dd/MM/yyyy")

        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

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
//            System.out.println("Column 'FirstName' has value: ${row["EditableSSTT"]}")
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
*
Fields: 20 Form: 20
'nom i cognoms.1' -> 'IBM Ibérica'
'nom i cognoms.0.0' -> 'Joan Martínez López'
'nom i cognoms.0.1' -> '611909655'
'nom i cognoms.0.2' -> 'jmartinez11@xtec.cat'
'nom i cognoms.0.3' -> '45443789P'
'S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.0' -> 'On'
'S’adjunta l’argumentació de motius per a la inclusió al Projecte de qualitat i millora contínua PQiMC.1' -> 'Off'
'CIF' -> 'B12345678D'
'adreça.0.0' -> 'C/ Intel, 54'
'adreça.1.0.0' -> 'info@ibm.cat'
'municipi' -> 'Barcelona'
'cp empresa' -> '08005'
'telèfon.0' -> '937678899'
'telèfon.1' -> '938765433'
'telèfon.2' -> '932123345'
'nom contacte' -> 'Carles Romero García'
'càrrec' -> 'Director General'
'nom tutor' -> 'Marta Rius Puig'
'càrrec tutor' -> 'Directora RRHH'
'durada hores.0' -> '80'
'inici.0.0' -> '07/01/2019'
'fi' -> '18/01/2019'
'hores1.0' -> '09:00'
'hores1.1' -> '14:00'
'hores1.2' -> '15:00'
'hores1.3' -> '18:00'
'sector.0' -> 'Tecnològic'
'tipus' -> 'Informàtica i comunicacions'
'Group1' -> 'Opción1'
'objectius' -> 'Objectiu número 1.
Objectiu número 2.'
'activitats' -> 'Activitat número 1.
Activitat número 2.'
*
*
*
20437852Y_N_I_MaciasCamposJesus.pdf
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

}
