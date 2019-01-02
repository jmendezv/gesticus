package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.db.*
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.*
import cat.gencat.access.os.GesticusOs
import cat.gencat.access.reports.GesticusReports
import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.time.DayOfWeek
import kotlin.concurrent.thread

const val APP_TITLE: String = "Gèsticus v. 2.1"

const val SUBJECT_GENERAL: String = "Comunicat Estades Formatives"

const val BODY_DOCENT: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Us ha estat concedida una estada formativa en empresa de tipus B (<strong>amb substitució</strong>).</p><p>Si us plau, consulteu l'apartat: &quot;<em>Documentació a presentar al finalitzar l'estada</em>&quot; en aquest <a href='http://xtec.gencat.cat/ca/formacio/formaciocollectiusespecifics/formacio_professional/estades/' target='_blank'>enllaç</a>, per tal de procedir al tancament un cop finalitzada.</p><p>Trobareu els detalls de la vostra estada en el document adjunt.</p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_CENTRE: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Ha estat concedida una estada formativa en empresa de tipus B (<strong>amb substitució</strong>) a un/a docent d'aquest Centre.</p><p>Trobareu els detalls de l'estada en el document adjunt.</p><p>Properament rebreu una carta per correu ordinari, signada pel nostre Cap de Servei i amb registre de sortida de la Direcció General.</p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_EMPRESA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Ha estat concedida una estada formativa a un/a professor/a de Formació Professional en la vostra entitat.</p><p>Trobareu els detalls de l'estada en el document adjunt.</p><p>Properament rebreu una carta per correu ordinari, signada pel nostre Cap de Servei i amb registre de sortida de la Direcció General.</p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_SSTT: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Hem concedit una estada formativa en empresa de tipus B (<strong>amb substitució</strong>) a un/a docent d'aquest Servei Territorial.<p>Trobareu els detalls de l'estada en el document adjunt.</p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_TUTOR: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Heu estat tutor/a d'una estada formativa en empresa de tipus B (<strong>amb substitució</strong>) d'un/a professor/a de Formació Professional.</p><p>Trobareu els detalls de l'estada en el document adjunt i properament rebreu una còpia signada pel Cal de Servei.</p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

/* TODO("Review") */
const val BODY_AGRAIMENT: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p></p><p></p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_LLISTAT_PROVISIONAL: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Adjunt trobareu la llista definitiva d'admesos/exclosos de la convocatoria actual d'estades formatives en empresa tipus B (<strong>amb substitució</strong>).</p><p>S'obre el termini de lliurament de sol·licituds, degudament emplenades per tal de facilitar el tractament informàtic, que finalitzarà el proper <b>31 de març</b>.</p><p>Cal que tingueu en compte el següent:</p><ul><li>Més enllà del <b>20 de maig</b> els Serveis Territorials no acostumen a enviar substituts/es.</li><li>Les substitucions d'un mateix Centre i d'una mateixa familia han de ser consecutives.</li><li>La durada de les estades és de dues setmanes: de dilluns a divendres de la següent setmana, i sense dies festius a banda del cap de setmana.</li><li>Les estades tipus B estan condicionades a què el/la substitut/a hagi acceptat el nomenament.</li><li>Convé que la Direcció reclami el/la substitut/a als SSTT corresponents.</li><li>Si no teniu empresa, en alguns casos, el propi Departament en pot proporcionar una, però recomanen que feu les gestions pertinents de forma individual.</li><li>Les sol·licituds del col·lectiu Sanitari, si no teniu cap persona de contacte, es gestionen des d'el Departament. <i>En rebreu més informació properament</i>.</li><li>En acabar l'estada, disposeu d'un mes per tal de lliurar la documentació de tancament: certificat d'empresa en paper oficial i segell, memòria, i imprès d'avaluació.</li><li>Si per qualsevol motiu no podeu dur a terme l'estada, cal que comuniqueu la baixa voluntaria tan aviat como sigui possible, per tal de poder assignar-la a una altre persona. Recordeu que la baixa voluntària no te cap penalització administrativa.</li></ul><p>Trobareu tota la informació necessària en aquest enllaç:</p><a href='http://xtec.gencat.cat/ca/formacio/formaciocollectiusespecifics/formacio_professional/estades/'>Estades formatives del professorat a les empreses i institucions</a><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_LLISTAT_DEFINITIU: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Adjunt trobareu la llista definitiva d'admesos/exclosos de la convocatoria actual d'estades formatives en empresa tipus B (<strong>amb substitució</strong>).</p><p>S'obre el termini de lliurament de sol·licituds, degudament emplenades per tal de facilitar el tractament informàtic, que finalitzarà el proper <b>31 de març</b>.</p><p>Cal que tingueu en compte el següent:</p><ul><li>Més enllà del <b>20 de maig</b> els Serveis Territorials no acostumen a enviar substituts/es.</li><li>Les substitucions d'un mateix Centre i d'una mateixa familia han de ser consecutives.</li><li>La durada de les estades és de dues setmanes: de dilluns a divendres de la següent setmana, i sense dies festius a banda del cap de setmana.</li><li>Les estades tipus B estan condicionades a què el/la substitut/a hagi acceptat el nomenament.</li><li>Convé que la Direcció reclami el/la substitut/a als SSTT corresponents.</li><li>Si no teniu empresa, en alguns casos, el propi Departament en pot proporcionar una, però recomanen que feu les gestions pertinents de forma individual.</li><li>Les sol·licituds del col·lectiu Sanitari, si no teniu cap persona de contacte, es gestionen des d'el Departament. <i>En rebreu més informació properament</i>.</li><li>En acabar l'estada, disposeu d'un mes per tal de lliurar la documentació de tancament: certificat d'empresa en paper oficial i segell, memòria, i imprès d'avaluació.</li><li>Si per qualsevol motiu no podeu dur a terme l'estada, cal que comuniqueu la baixa voluntaria tan aviat como sigui possible, per tal de poder assignar-la a una altre persona. Recordeu que la baixa voluntària no te cap penalització administrativa.</li></ul><p>Trobareu tota la informació necessària en aquest enllaç:</p><a href='http://xtec.gencat.cat/ca/formacio/formaciocollectiusespecifics/formacio_professional/estades/'>Estades formatives del professorat a les empreses i institucions</a><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

class GesticusView : View(APP_TITLE) {

    override val root: BorderPane by fxml()

    val controller: GesticusController by inject()

    // Menu Database
    val databaseMenuItemCerca: MenuItem by fxid()
    val databaseMenuItemSeguiment: MenuItem by fxid()
    val databaseMenuItemNova: MenuItem by fxid()
    val databaseMenuItemObrePdf: MenuItem by fxid()
    val databaseMenuItemTanca: MenuItem by fxid()
    val databaseMenuItemDocumentada: MenuItem by fxid()
    val databaseMenuItemFinalitzada: MenuItem by fxid()
    val databaseMenuItemAlta: MenuItem by fxid()
    val databaseMenuItemBaixa: MenuItem by fxid()
    // Menu Comunicats / Correu
    val comunicatsMenuItemTot: MenuItem by fxid()
    val comunicatsMenuItemCorreuDocent: MenuItem by fxid()
    val comunicatsMenuItemCorreuCentre: MenuItem by fxid()
    val comunicatsMenuItemCorreuEmpresa: MenuItem by fxid()
    val comunicatsMenuItemCorreuServeiTerritorial: MenuItem by fxid()
    val comunicatsMenuItemCorreuCartaAgraiment: MenuItem by fxid()
    val comunicatsMenuItemCorreuCertificatTutor: MenuItem by fxid()
    // Menu Notificacions
    val notificacionsMenuItemLlistaProvisional: MenuItem by fxid()
    val notificacionsMenuItemLlistaDefinitiva: MenuItem by fxid()
    // Menu Eines
    val einesMenuItemPreferencies: MenuItem by fxid()
    val einesMenuItemLlistat: MenuItem by fxid()
    val einesRadioMenuItemModeExpert: RadioMenuItem by fxid()
    // Menu Ajuda
    val ajudaMenuItemUs: MenuItem by fxid()
    val ajudaMenuItemSobreNosaltres: MenuItem by fxid()

    // Toolbar
    val toolbarButtonCerca: Button by fxid()
    val toolbarButtonObre: Button by fxid()
    val toolbarButtonNou: Button by fxid()
    val toolbarButtonTanca: Button by fxid()

    // Estada
    val estadaTextFieldNumeroEstada: TextField by fxid()
    val estadaComboBoxTipusEstada: ComboBox<String> by fxid()
    val estadaDatePickerDataInici: DatePicker by fxid()
    val estadaDatePickerDataFinal: DatePicker by fxid()
    val estadaTextFieldDescripcio: TextField by fxid()
    val estadaTextFieldComentaris: TextField by fxid()

    // Empresa
    val empresaIdentificacioTextFieldNif: TextField by fxid()
    val empresaIdentificacioTextFieldNom: TextField by fxid()
    val empresaIdentificacioTextFieldDireccio: TextField by fxid()
    val empresaIdentificacioTextFieldCodiPostal: TextField by fxid()
    val empresaIdentificacioTextFieldMunicipi: TextField by fxid()
    val empresaPersonaContacteTextFieldNom: TextField by fxid()
    val empresaPersonaContacteTextFieldCarrec: TextField by fxid()
    val empresaPersonaContacteTextFieldTelefon: TextField by fxid()
    val empresaPersonaContacteTextFieldEmail: TextField by fxid()
    val empresaTutorTextFieldNom: TextField by fxid()
    val empresaTutorTextFieldCarrec: TextField by fxid()
    val empresaTutorTextFieldTelefon: TextField by fxid()
    val empresaTutorTextFieldEmail: TextField by fxid()

    // Docent
    val docentTextFieldDni: TextField by fxid()
    val docentTextFieldNom: TextField by fxid()
    val docentTextFieldDestinacio: TextField by fxid()
    val docentTextFieldEspecialitat: TextField by fxid()
    val docentTextFieldEmail: TextField by fxid()
    val docentTextFieldTelefon: TextField by fxid()

    // Centre
    val centreTextFieldCodi: TextField by fxid()
    val centreTextFieldNom: TextField by fxid()
    val centreTextFieldDireccio: TextField by fxid()
    val centreTextFieldCodiPostal: TextField by fxid()
    val centreTextFieldMunicipi: TextField by fxid()
    val centreTextFieldDirector: TextField by fxid()
    val centreTextFieldTelefon: TextField by fxid()
    val centreTextFieldEmail: TextField by fxid()

    // SSTT
    val ssttTextFieldCodi: TextField by fxid()
    val ssttTextFieldNom: TextField by fxid()
    val ssttTextFieldMunicipi: TextField by fxid()
    val ssttTextFieldTelefon: TextField by fxid()
    val ssttTextFieldCapServeisPersonalDocent: TextField by fxid()
    val ssttTextFieldEmailCapServeisPersonalDocent: TextField by fxid()
    val ssttTextFieldEmailCapRecursosHumansDireccio: TextField by fxid()

    // Accordion
    val accordion: Accordion by fxid()
    val titledPaneEstada: TitledPane by fxid()
    val titledPaneEmpresa: TitledPane by fxid()
    val titledPaneDocent: TitledPane by fxid()
    val titledPaneCentre: TitledPane by fxid()
    val titledPaneSSTT: TitledPane by fxid()

    // ButtonBar
    val buttonBarButtonDesa: Button by fxid()

    val codiEstadaFormat = "000\\d{3}0600/\\d{4}-\\d{4}".toRegex()

    init {
        thread {
            controller.preLoadData()
        }
        thread {
            doSetup()
        }
        thread {
            checkEstats()
        }
    } // init ends

    private fun doSetup() {

        // Menu Database
        databaseMenuItemCerca.setOnAction {
            cercaEstadaPerNumeroDeEstada()
        }
        databaseMenuItemSeguiment.setOnAction {
            seguimentEstades()
        }
        databaseMenuItemNova.setOnAction {
            cleanScreen()
        }
        databaseMenuItemObrePdf.setOnAction {
            val registre = getRecordFromPdf()
            display(registre)
        }
        databaseMenuItemDocumentada.setOnAction {
            doDocumentada()
        }
        databaseMenuItemFinalitzada.setOnAction {
            doTancada()
        }
        databaseMenuItemAlta.setOnAction {
            doBaixa(false)
        }
        databaseMenuItemBaixa.setOnAction {
            doBaixa(true)
        }
        databaseMenuItemTanca.setOnAction { controller.menuTanca() }

        comunicatsMenuItemTot.setOnAction {
            sendTotATothom()
        }
        // Menu Comunicats / Correus
        comunicatsMenuItemCorreuDocent.setOnAction {
            sendCartaDocent()
        }
        comunicatsMenuItemCorreuCentre.setOnAction {
            sendCartaCentre()
        }
        comunicatsMenuItemCorreuEmpresa.setOnAction {
            sendCartaEmpresa()
        }
        comunicatsMenuItemCorreuServeiTerritorial.setOnAction {
            sendCartaSSTT()
        }
        comunicatsMenuItemCorreuCartaAgraiment.setOnAction {
            sendCartaAgraiment()
        }
        comunicatsMenuItemCorreuCertificatTutor.setOnAction {
            sendCartaCertificatTutor()
        }


        // Menu Notificacions
        notificacionsMenuItemLlistaProvisional.setOnAction {
            notificaLlistatProvisional()
        }
        notificacionsMenuItemLlistaDefinitiva.setOnAction {
            notificaLlistatDefinitiu()
        }

        // Menu Eines
        einesMenuItemPreferencies.setOnAction { }
        einesRadioMenuItemModeExpert.setOnAction { }
        einesRadioMenuItemModeExpert.setOnAction { }

        // Menu Ajuda
        ajudaMenuItemUs.setOnAction { }
        ajudaMenuItemSobreNosaltres.setOnAction { }

        // Estada
        estadaTextFieldNumeroEstada.setOnAction { }
        estadaTextFieldNumeroEstada.focusedProperty().addListener { obj, oldValue, newValue ->
        }

        estadaComboBoxTipusEstada.selectionModel.selectFirst()

        // Check it should be Monday and set estadaDatePickerDataFinal to second next Friday
        estadaDatePickerDataInici.setOnAction { }
        estadaDatePickerDataFinal.setOnAction { }

        // Docent
        docentTextFieldDni.setOnAction {
            loadDataByDocentIdFromPdf(docentTextFieldDni.text)
        }

        centreTextFieldCodi.setOnAction {
            findCentreAndSSTT(centreTextFieldCodi.text)
        }

        ssttTextFieldCodi.setOnAction {
            findSSTT(ssttTextFieldCodi.text)
        }

        accordion.expandedPane = titledPaneDocent

        Platform.runLater {
            docentTextFieldDni.requestFocus()
        }

        toolbarButtonCerca.setOnAction {
            cercaEstadaPerNumeroDeEstada()
        }

        toolbarButtonObre.setOnAction {
            val registre = getRecordFromPdf()
            display(registre)
        }

        toolbarButtonNou.setOnAction {
            cleanScreen()
        }


        toolbarButtonTanca.setOnAction {
            controller.menuTanca()
        }

        buttonBarButtonDesa.setOnAction {
            desaEstadaBd()
        }

    }

    fun checkEstats() {
        // Loop through each estada and change status accordingly:
        controller.checkEstats()
    }

    private fun findCentreAndSSTT(codiCentre: String) : Unit {
        val centreAndSSTT: Pair<Centre, SSTT> = controller.findCentreAndSSTT(codiCentre)
        display(centreAndSSTT.first)
        display(centreAndSSTT.second)
    }

    private fun findSSTT(codiSSTT: String) : Unit {
        val sstt: SSTT = controller.findSSTT(codiSSTT)
        display(sstt)
    }

    /* Carrega un view amb dos tableview relacionats: estades/estats  */
    private fun seguimentEstades(): Unit {
        val dialog = TextInputDialog("NIF (099999999A/A9999999A)")
        dialog.setTitle(APP_TITLE);
        val result = dialog.showAndWait();
        if (result.isPresent) {
            /* if nif is null should display all estades */
            val nif = result.get()
            if (nif.matches(NIF_REGEXP) || nif.matches(NIE_REGEXP)) {
                //find<SeguimentEstades>(Pair("nif", nif)).openModal()
                find<SeguimentEstades>(mapOf(SeguimentEstades::nif to nif)).openModal()
            }
        } else {
            Alert(Alert.AlertType.INFORMATION, "El NIF no és un NIF vàlid").showAndWait()
        }
    }

    /*
    * Aquest mètodo cerca una estada per numero d'estada
    * que és un camp clau 0003730600/2018-2019
    *
    * */
    private fun cercaEstadaPerNumeroDeEstada() {
        val dialog = TextInputDialog("Número d'estada")
        dialog.setTitle(APP_TITLE);
        val result = dialog.showAndWait();
        if (result.isPresent) {
            var codiEstada = result.get()
            if (codiEstada.matches("\\d{3}".toRegex())) {
                codiEstada = "000${codiEstada}0600/${currentCourseYear()}-${nextCourseYear()}"
            }
            if (codiEstada.matches(codiEstadaFormat)) {
                val registre: Registre? = controller.findRegistreByCodiEstada(codiEstada)
                if (registre == null) {
                    Alert(Alert.AlertType.ERROR, "La estada $codiEstada no es troba").showAndWait()
                } else {
                    display(registre)
                }
            } else {
                Alert(Alert.AlertType.ERROR, "El format del codi d'estada ${codiEstada} no és correcte").showAndWait()
            }
        }
    }

    /* TODO("Refer la constant BODY_LLISTAT_PROVISIONAL") */
    private fun notificaLlistatProvisional() {
        val candidats = controller.queryCandidats()
        if (candidats.size > 0) {
            val fileChooser = FileChooser()
            fileChooser.apply {
                title = "Gesticus: Llistat provisinal"
                initialDirectory = File(PATH_TO_TEMPORAL)
                extensionFilters.add(FileChooser.ExtensionFilter("Fulls de càlcul", "*.xls*"))
                extensionFilters.add(FileChooser.ExtensionFilter("Tots els documents", "*.*"))
            }
            val selectedFile = fileChooser.showOpenDialog(this.currentWindow)
            if (selectedFile != null) {
                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                        SUBJECT_GENERAL,
                        BODY_LLISTAT_PROVISIONAL,
                        selectedFile.absolutePath,
                        candidats
                )
            }
        } else {
            Alert(Alert.AlertType.ERROR, "No hi ha docents a la taula candidats").showAndWait()
        }

    }

    private fun notificaLlistatDefinitiu() {
        val candidats = controller.queryCandidats()
        if (candidats.size > 0) {
            val fileChooser = FileChooser()
            fileChooser.apply {
                title = "Gesticus: Llistat definitiu"
                initialDirectory = File(PATH_TO_TEMPORAL)
                extensionFilters.add(FileChooser.ExtensionFilter("Fulls de càlcul", "*.xls*"))
                extensionFilters.add(FileChooser.ExtensionFilter("Tots els documents", "*.*"))
            }
            val selectedFile = fileChooser.showOpenDialog(this.currentWindow)
            if (selectedFile != null) {
                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                        SUBJECT_GENERAL,
                        BODY_LLISTAT_DEFINITIU,
                        selectedFile.absolutePath,
                        candidats
                )
            }
        } else {
            Alert(Alert.AlertType.ERROR, "No hi ha docents a la taula candidats").showAndWait()
        }
    }

    /* Sends everything according to type estada: carta docent, empresa, centre i si és tipus B també a sstt */
    private fun sendTotATothom() {
        if (checkForEmptyOrNull()) return
        val resp = Alert(Alert.AlertType.CONFIRMATION, "Estas segur que vols notificar totes les entitats?").showAndWait()
        if (resp.isPresent) {
            if (resp.get() == ButtonType.YES) {
                sendCartaDocent(false)
                sendCartaCentre(false)
                sendCartaEmpresa(false)
                if (estadaComboBoxTipusEstada.value == "B") {
                    sendCartaSSTT(false)
                }
                Alert(Alert.AlertType.INFORMATION, "S'ha notificat l'estada a totes les entitats segons tipus d'estada").showAndWait()
            }
        }
    }

    /* Sends carta to Docent */
    private fun sendCartaDocent(notifyOk: Boolean = true) {

        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val filename = GesticusReports.createCartaDocentPDF(registre)

        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_DOCENT,
                    filename,
                    listOf(registre.docent?.email!!))
            controller.insertEstatDeEstada(registre.estada?.numeroEstada!!, EstatsSeguimentEstada.COMUNICADA, "Estada comunicada al/a la docent")
            GesticusOs.copyReport(filename)
            val msg = "S'ha enviat el fitxer $filename correctament"
            writeToLog(msg)
            if (notifyOk)
                Alert(Alert.AlertType.INFORMATION, msg).showAndWait()
        } else {
            val msg = "No es troba la carta del docent ${registre.docent?.nif}"
            writeToLog(msg)
            Alert(Alert.AlertType.ERROR, msg)
        }
    }

    /* Sends carta al Centre i docent */
    private fun sendCartaCentre(notifyOk: Boolean = true) {

        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val filename = GesticusReports.createCartaCentre(registre)

        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_CENTRE,
                    filename,
                    listOf(registre.centre?.email!!, registre.docent?.email!!))
            controller.insertEstatDeEstada(registre.estada?.numeroEstada!!, EstatsSeguimentEstada.COMUNICADA, "Estada comunicada al Centre")
            GesticusOs.copyReport(filename)
            val msg = "S'ha enviat el fitxer $filename correctament"
            writeToLog(msg)
            if (notifyOk)
                Alert(Alert.AlertType.INFORMATION, msg).showAndWait()
        } else {
            val msg = "No es troba la carta pel Centre del docent ${registre.docent?.nif}"
            writeToLog(msg)
            Alert(Alert.AlertType.ERROR, msg)
        }
    }

    /* Sends carta to empresa and docent */
    private fun sendCartaEmpresa(notifyOk: Boolean = true) {

        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val filename = GesticusReports.createCartaEmpresa(registre)
        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_EMPRESA,
                    filename,
                    listOf(registre.empresa?.personaDeContacte?.email!!, registre.docent?.email!!))
            controller.insertEstatDeEstada(registre.estada?.numeroEstada!!, EstatsSeguimentEstada.COMUNICADA, "Estada comunicada a l'empresa")
            GesticusOs.copyReport(filename)
            val msg = "S'ha enviat el fitxer $filename correctament"
            writeToLog(msg)
            if (notifyOk)
                Alert(Alert.AlertType.INFORMATION, msg).showAndWait()
        } else {
            val msg = "No es troba la carta d'empresa del docent ${registre.docent?.nif}"
            writeToLog(msg)
            Alert(Alert.AlertType.ERROR, msg)
        }
    }

    /* Sends two letters to SSTT */
    private fun sendCartaSSTT(notifyOk: Boolean = true) {

        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val filename = GesticusReports.createCartaSSTTPDF(registre)
        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_SSTT,
                    filename,
                    listOf(registre.sstt?.emailCSPD!!, registre.sstt?.emailCRHD!!))
            controller.insertEstatDeEstada(registre.estada?.numeroEstada!!, EstatsSeguimentEstada.COMUNICADA, "Estada comunicada al SSTT")
            GesticusOs.copyReport(filename)
            val msg = "S'ha enviat el fitxer $filename correctament"
            writeToLog(msg)
            if (notifyOk)
                Alert(Alert.AlertType.INFORMATION, msg).showAndWait()
        } else {
            val msg = "No es troba la carta de SSTT del docent ${registre.docent?.nif}"
            writeToLog(msg)
            Alert(Alert.AlertType.ERROR, msg)
        }
    }

    /* Sends carta d'agraïment to empresa */
    private fun sendCartaAgraiment(notifyOk: Boolean = true) {

        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val filename = GesticusReports.createCartaAgraimentPDF(registre)

        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_AGRAIMENT,
                    filename,
                    listOf(registre.empresa?.personaDeContacte?.email!!))
            val msg = "S'ha enviat el fitxer $filename correctament"
            controller.insertEstatDeEstada(registre.estada?.numeroEstada!!, EstatsSeguimentEstada.COMUNICADA, "Enviada carta d'agraïment")
            GesticusOs.copyReport(filename)
            writeToLog(msg)
            if (notifyOk)
                Alert(Alert.AlertType.INFORMATION, msg).showAndWait()
        } else {
            val msg = "No es troba la carta d'agraïment del docent ${registre.docent?.nif}"
            writeToLog(msg)
            Alert(Alert.AlertType.ERROR, msg)
        }
    }

    /* TODO("Check out") */
    private fun sendCartaCertificatTutor(notifyOk: Boolean = true) {

        if (checkForEmptyOrNull()) return

        val view = TutorCertificationView()

        view.openModal(block = true, owner = this.currentWindow, resizable = false, escapeClosesWindow = false)

        if (view.model.item == null) {
            return
        }

        val filename: String?

        try {
            val hores = view.model.hores.value.toInt()
            val dni = view.model.dni.value

            if (dni.isValidDniNie()) {
                val registre = gatherDataFromForm()
                filename = GesticusReports.createCartaCertificatTutorPDF(registre, hores, dni)
                if (filename != null) {
                    GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                            SUBJECT_GENERAL,
                            BODY_TUTOR,
                            filename,
                            listOf(registre.centre?.email!!, registre.docent?.email!!))
                    controller.insertEstatDeEstada(registre.estada?.numeroEstada!!, EstatsSeguimentEstada.COMUNICADA, "Enviada carta de certificació al/a la tutor/a")
                    GesticusOs.copyReport(filename)
                    val msg = "S'ha enviat el fitxer $filename correctament"
                    writeToLog(msg)
                    if (notifyOk)
                        Alert(Alert.AlertType.INFORMATION, msg).showAndWait()
                } else {
                    val msg = "No es troba la carta de certificació pel tutor del docent ${registre.docent?.nif}"
                    writeToLog(msg)
                    Alert(Alert.AlertType.ERROR, msg)
                }
            } else {
                Alert(Alert.AlertType.ERROR, "El DNI/NIE $dni no té un format vàlid").showAndWait()
            }
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, "El camp 'hores' és un camp numèric").show()
        }

    }

    /* Guard o actualiza estada en la bd */
    private fun desaEstadaBd(): Unit {

        if (!checkForEmptyOrNull()) {
            val registre = gatherDataFromForm()
            val ret: Boolean = controller.saveEstada(docentTextFieldDni.text, registre.estada!!, registre.empresa!!)
            if (ret) {
                // cleanScreen()
                GesticusOs.renameForm(docentTextFieldDni.text, registre.estada!!.numeroEstada, registre.estada!!.tipusEstada)
            }
        }
    }

    /* Recull les dades dels formularis en objectes */
    private fun gatherDataFromForm(): Registre {
        val estada = Estada(
                estadaTextFieldNumeroEstada.text.trim(),
                centreTextFieldCodi.text.trim(),
                estadaComboBoxTipusEstada.value,
                estadaDatePickerDataInici.value,
                estadaDatePickerDataFinal.value,
                estadaTextFieldDescripcio.text.trim(),
                estadaTextFieldComentaris.text.trim()
        )
        val identificacio = Identificacio(
                empresaIdentificacioTextFieldNif.text.trim(),
                empresaIdentificacioTextFieldNom.text.trim(),
                empresaIdentificacioTextFieldDireccio.text.trim(),
                empresaIdentificacioTextFieldCodiPostal.text.trim(),
                empresaIdentificacioTextFieldMunicipi.text.trim()
        )
        val personaDeContacte = PersonaDeContacte(
                empresaPersonaContacteTextFieldNom.text.trim(),
                empresaPersonaContacteTextFieldCarrec.text.trim(),
                empresaPersonaContacteTextFieldTelefon.text.trim(),
                empresaPersonaContacteTextFieldEmail.text.trim()
        )
        val tutor = Tutor(
                empresaTutorTextFieldNom.text.trim(),
                empresaTutorTextFieldCarrec.text.trim(),
                empresaTutorTextFieldTelefon.text.trim(),
                empresaTutorTextFieldEmail.text.trim()
        )
        val empresa = Empresa(identificacio, personaDeContacte, tutor)
        val docent = Docent(
                docentTextFieldDni.text.trim(),
                docentTextFieldNom.text.trim(),
                docentTextFieldDestinacio.text.trim(),
                docentTextFieldEspecialitat.text.trim(),
                docentTextFieldEmail.text.trim(),
                docentTextFieldTelefon.text.trim()
        )
        val centre = Centre(
                centreTextFieldCodi.text.trim(),
                centreTextFieldNom.text.trim(),
                centreTextFieldDireccio.text.trim(),
                centreTextFieldCodiPostal.text.trim(),
                centreTextFieldMunicipi.text.trim(),
                centreTextFieldDirector.text.trim(),
                centreTextFieldTelefon.text.trim(),
                centreTextFieldEmail.text.trim()
        )
        val sstt = SSTT(
                ssttTextFieldCodi.text.trim(),
                ssttTextFieldNom.text.trim(),
                ssttTextFieldMunicipi.text.trim(),
                ssttTextFieldCapServeisPersonalDocent.text.trim(),
                ssttTextFieldTelefon.text.trim(),
                ssttTextFieldEmailCapServeisPersonalDocent.text.trim(),
                ssttTextFieldEmailCapRecursosHumansDireccio.text.trim()
        )
        return Registre(estada, empresa, docent, centre, sstt)
    }

    /* Valida dades */
    private fun checkForEmptyOrNull(): Boolean {

        if (estadaTextFieldNumeroEstada.text.trim().isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Número d'estada' no pot estar buit").showAndWait()
            return true
        }
        if (!estadaTextFieldNumeroEstada.text.trim().matches(codiEstadaFormat)) {
            Alert(
                    Alert.AlertType.ERROR,
                    "El format del camp 'Número d'estada' no és vàlid: 0009990600/9999-9999"
            ).showAndWait()
            return true
        }
        if (estadaComboBoxTipusEstada.value.trim().isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Tipus d'estada' no pot estar buit").showAndWait()
            return true
        }
        if (estadaDatePickerDataInici.value == null) {
            Alert(Alert.AlertType.ERROR, "El camp 'Data d'inici' no pot estar buit").showAndWait()
            return true
        }
        if (estadaDatePickerDataFinal.value == null) {
            Alert(Alert.AlertType.ERROR, "El camp 'Data final' no pot estar buit").showAndWait()
            return true
        }
        if (!(estadaDatePickerDataInici.value.dayOfWeek == DayOfWeek.MONDAY && estadaDatePickerDataInici.value.plusDays(
                        11
                ).isEqual(estadaDatePickerDataFinal.value))
        ) {
            Alert(
                    Alert.AlertType.ERROR,
                    "Una estada ha de començar en dilluns i acabar el divendres de la setmana següent"
            ).showAndWait()
            return true
        }
        if (estadaComboBoxTipusEstada.value != "A" && estadaComboBoxTipusEstada.value != "B") {
            Alert(Alert.AlertType.ERROR, "Una estada ha de ser de tipus A o B").showAndWait()
            return true
        }
        if (empresaIdentificacioTextFieldNif.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'NIF' d'empresa no pot estar buit").showAndWait()
            return true
        }
        if (empresaIdentificacioTextFieldNom.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Nom' d'empresa no pot estar buit").showAndWait()
            return true
        }
        if (empresaIdentificacioTextFieldDireccio.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Direcció d'empresa no pot estar buit").showAndWait()
            return true
        }
        if (empresaIdentificacioTextFieldMunicipi.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Municipi' no pot estar buit").showAndWait()
            return true
        }
        if (empresaIdentificacioTextFieldCodiPostal.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Codi postal' d'empresa no pot estar buit").showAndWait()
            return true
        }
        if (empresaPersonaContacteTextFieldNom.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Nom' de la persona de contacte no pot estar buit").showAndWait()
            return true
        }
        if (empresaPersonaContacteTextFieldCarrec.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Càrrec' de la persona de contacte no pot estar buit").showAndWait()
            return true
        }
        if (empresaPersonaContacteTextFieldEmail.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Email' de la persona de contacte no pot estar buit").showAndWait()
            return true
        }
        if (empresaPersonaContacteTextFieldTelefon.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Telèfon' de la persona de contacte no pot estar buit").showAndWait()
            return true
        }
        if (empresaTutorTextFieldNom.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Nom' del tutor/a no pot estar buit").showAndWait()
            return true
        }
        if (empresaTutorTextFieldCarrec.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Càrrec' del tutor/a no pot estar buit").showAndWait()
            return true
        }
        if (empresaTutorTextFieldEmail.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Email' del tutor/a no pot estar buit").showAndWait()
            return true
        }
        if (empresaTutorTextFieldTelefon.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Telèfon' del tutor/a no pot estar buit").showAndWait()
            return true
        }
        if (docentTextFieldDni.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'DNI' del/la docent no pot estar buit").showAndWait()
            return true
        }
        if (docentTextFieldNom.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Nom' del/la docent no pot estar buit").showAndWait()
            return true
        }
        if (docentTextFieldDestinacio.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Destinació' del/la docent no pot estar buit").showAndWait()
            return true
        }
        if (docentTextFieldEspecialitat.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Especialitat' del/la docent no pot estar buit").showAndWait()
            return true
        }
        if (docentTextFieldEmail.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Email' del/la docent no pot estar buit").showAndWait()
            return true
        }
        if (docentTextFieldTelefon.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Telèfon' del/la docent no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldCodi.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Codi' del Centre no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldNom.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Nom' del Centre no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldDireccio.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Direcció' del Centre no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldCodiPostal.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Codi postal' del Centre no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldMunicipi.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Municipi' del Centre no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldDirector.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Director/a' del Centre no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldTelefon.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Teléfon' de la empresa no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldEmail.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Email' de la empresa no pot estar buit").showAndWait()
            return true
        }
        if (ssttTextFieldCodi.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Codi' del SSTT no pot estar buit").showAndWait()
            return true
        }
        if (ssttTextFieldNom.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Nom' del SSTT no pot estar buit").showAndWait()
            return true
        }
        if (ssttTextFieldMunicipi.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Municipi' del SSTT no pot estar buit").showAndWait()
            return true
        }
        if (ssttTextFieldTelefon.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Telefon' del SSTT no pot estar buit").showAndWait()
            return true
        }
        if (ssttTextFieldCapServeisPersonalDocent.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Cap de Servei de Personal Docent' del SSTT no pot estar buit").showAndWait()
            return true
        }
        if (ssttTextFieldEmailCapServeisPersonalDocent.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Email del Cap de Serveis de Personal' del SSTT no pot estar buit").showAndWait()
            return true
        }
        if (ssttTextFieldEmailCapRecursosHumansDireccio.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Email del Cap de Recursos Humans i Direcció' del SSTT no pot estar buit").showAndWait()
            return true
        }
        return false
    }

    private fun cleanScreen() {

        val alert = Alert(Alert.AlertType.CONFIRMATION, "N'estàs segur?")
        val result = alert.showAndWait()
        if (result.isPresent) {
            if (result.get() == ButtonType.OK) {
                display(Estada())
                display(SSTT())
                display(Centre())
                display(Docent())
                display(Empresa())
            }
        }
    }

    /* This methods adds DOCUMENTADA state to this estada and sends email */
    private fun doDocumentada() {
        val registre = gatherDataFromForm()
        controller.insertEstatDeEstada(registre.estada?.numeroEstada!!, EstatsSeguimentEstada.DOCUMENTADA, "L'estada ha estat documentada correctament")
    }

    /* This methods adds FINALITZADA state to this estada and sends email */
    private fun doTancada() {
        val registre = gatherDataFromForm()
        controller.insertEstatDeEstada(registre.estada?.numeroEstada!!, EstatsSeguimentEstada.TANCADA, "L'estada ja esta tancada al GTAF")
    }

    /* Aquest mètode posa admesos_t.baixa a true/false */
    private fun doBaixa(value: Boolean): Unit {
        val dialog = TextInputDialog("NIF")
        dialog.setTitle(APP_TITLE);
        val result = dialog.showAndWait();
        if (result.isPresent) {
            var nif = result.get()
            controller.doBaixa(nif, value)
        }
    }

    /* Aquest mètode troba les dades relatives a una estada des d'una sol·licitud pdf */
    private fun loadDataByDocentIdFromPdf(nif: String): Unit {

        val registre: Registre? = controller.loadDataByDocentIdFromPdf(nif)

        if (registre != null) {
            display(registre)
            Alert(Alert.AlertType.INFORMATION,
                    "S'ha carregat el/la docent ${registre.docent?.nom} correctament."
            ).show()
            accordion.expandedPane = titledPaneEstada
            estadaTextFieldNumeroEstada.requestFocus()
        } else {
            Alert(Alert.AlertType.ERROR, "No s'ha trobat el/la docent amb DNI ${docentTextFieldDni.text}.").show()
            accordion.expandedPane = titledPaneDocent
            Platform.runLater {
                docentTextFieldDni.requestFocus()
            }
        }
    }

    /* This method loads a pdf form choosen from user and displays estada and empresa */
    private fun recarregaPdf() {
        val fileChooser = FileChooser()
        fileChooser.title = "Obre Estada"

        fileChooser.initialDirectory = File(PATH_TO_FORMS + currentCourseYear())
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Estades", "*.pdf"),
                FileChooser.ExtensionFilter("All Files", "*.*")
        )
        val selectedFile = fileChooser.showOpenDialog(this.currentWindow)
        if (selectedFile != null) {
            val estadaEmpresa: Pair<Estada, Empresa>? = controller.reloadPdf(selectedFile)
            if (estadaEmpresa != null) {
                display(estadaEmpresa.first)
                display(estadaEmpresa.second)
            } else {
                Alert(Alert.AlertType.ERROR, "Error de lectura del pdf").show()
            }
        }
    }

    /*
    * This method returns a registre from a pdf form by getting its estada and empresa
    * data, as well as inferring docent, centre and sstt from the nif included in it
    * via the db
    * */
    private fun getRecordFromPdf(): Registre? {
        val fileChooser = FileChooser()
        fileChooser.title = "Obre Estada"

        fileChooser.initialDirectory = File(PATH_TO_FORMS + currentCourseYear())
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Estades", "*.pdf"),
                FileChooser.ExtensionFilter("All Files", "*.*")
        )
        val selectedFile = fileChooser.showOpenDialog(this.currentWindow)
        var registre: Registre? = null
        if (selectedFile != null) {
            val estadaEmpresa: Pair<Estada, Empresa>? = controller.reloadPdf(selectedFile)
            registre = controller.getRegistreFromPdf(selectedFile)
            registre?.estada = estadaEmpresa?.first
            registre?.empresa = estadaEmpresa?.second
        }

        return registre
    }

    /* This method displays a registre */
    private fun display(registre: Registre?) {
        registre?.apply {
            display(estada)
            display(empresa)
            display(docent)
            display(centre)
            display(sstt)
        }
    }

    /* This methodo displays an estada */
    private fun display(estada: Estada?) {
        estada?.apply {
            estadaTextFieldNumeroEstada.text = numeroEstada
            estadaComboBoxTipusEstada.value = tipusEstada
            estadaDatePickerDataInici.value = dataInici
            estadaDatePickerDataFinal.value = dataFinal
            estadaTextFieldDescripcio.text = descripcio
            estadaTextFieldComentaris.text = comentaris
        }
    }

    /* This method displays an empresa */
    private fun display(empresa: Empresa?) {
        empresa?.apply {
            empresaIdentificacioTextFieldNif.text = identificacio.nif
            empresaIdentificacioTextFieldNom.text = identificacio.nom
            empresaIdentificacioTextFieldDireccio.text = identificacio.direccio
            empresaIdentificacioTextFieldCodiPostal.text = identificacio.cp
            empresaIdentificacioTextFieldMunicipi.text = identificacio.municipi
            empresaPersonaContacteTextFieldNom.text = personaDeContacte.nom
            empresaPersonaContacteTextFieldCarrec.text = personaDeContacte.carrec
            empresaPersonaContacteTextFieldTelefon.text = personaDeContacte.telefon
            empresaPersonaContacteTextFieldEmail.text = personaDeContacte.email
            empresaTutorTextFieldNom.text = tutor.nom
            empresaTutorTextFieldCarrec.text = tutor.carrec
            empresaTutorTextFieldTelefon.text = tutor.telefon
            empresaTutorTextFieldEmail.text = tutor.email
        }
    }

    /* This method displays a docent */
    private fun display(docent: Docent?) {
        docent?.run {
            docentTextFieldDni.text = docent.nif
            docentTextFieldNom.text = nom
            docentTextFieldDestinacio.text = destinacio
            docentTextFieldEmail.text = email
            docentTextFieldEspecialitat.text = especialitat
            docentTextFieldTelefon.text = telefon
        }
    }

    /* This method displays a centre */
    private fun display(centre: Centre?) {
        centre?.run {
            centreTextFieldCodi.text = codi
            centreTextFieldNom.text = nom
            centreTextFieldDireccio.text = direccio
            centreTextFieldCodiPostal.text = cp
            centreTextFieldMunicipi.text = municipi
            centreTextFieldDirector.text = director
            centreTextFieldTelefon.text = telefon
            centreTextFieldEmail.text = email
        }
    }

    /* This method displays a sstt */
    private fun display(sstt: SSTT?) {
        sstt?.run {
            ssttTextFieldCodi.text = codi
            ssttTextFieldNom.text = nom
            ssttTextFieldMunicipi.text = municipi
            ssttTextFieldCapServeisPersonalDocent.text = coordinador
            ssttTextFieldTelefon.text = telefon
            ssttTextFieldEmailCapServeisPersonalDocent.text = emailCSPD
            ssttTextFieldEmailCapRecursosHumansDireccio.text = emailCRHD
        }
    }

}
