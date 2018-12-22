package cat.gencat.access.views

import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.db.*
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.*
import cat.gencat.access.reports.GesticusReports
import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import tornadofx.View
import java.io.File
import java.lang.Exception
import java.time.DayOfWeek

const val APP_TITLE: String = "Gèsticus v. 2.1"

const val SUBJECT_GENERAL: String = "Comunicat Estades Formatives"

const val BODY_DOCENT: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Us ha estat concedida una estada formativa en empresa de tipus B (<strong>amb substitució</strong>).</p><p>Si us plau, consulteu l'apartat: &quot;<em>Documentació a presentar al finalitzar l'estada</em>&quot; en aquest <a href='http://xtec.gencat.cat/ca/formacio/formaciocollectiusespecifics/formacio_professional/estades/' target='_blank'>enllaç</a>, per tal de procedir al tancament un cop finalitzada.</p><p>Trobareu els detalls de la vostra estada en el document adjunt.</p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_CENTRE: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Ha estat concedida una estada formativa en empresa de tipus B (<strong>amb substitució</strong>) a un/a docent d'aquest Centre.</p><p>Trobareu els detalls en el document adjunt.</p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val BODY_EMPRESA: String =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Ha estat concedida una estada formativa a un/a professor/a de Formació Professional en la vostra entitat.</p><p>Trobareu els detalls de l'estada en el document adjunt.</p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

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
    val databaseMenuItemRecarregaPdf: MenuItem by fxid()
    val databaseMenuItemTanca: MenuItem by fxid()
    // Menu Comunicats / Correu
    val comunicatsMenuItemCorreuDocent: MenuItem by fxid()
    val comunicatsMenuItemCorreuCentre: MenuItem by fxid()
    val comunicatsMenuItemCorreuEmpresa: MenuItem by fxid()
    val comunicatsMenuItemCorreuServeiTerritorial: MenuItem by fxid()
    val comunicatsMenuItemCorreuCartaAgraiment: MenuItem by fxid()
    val comunicatsMenuItemCorreuCertificatTutor: MenuItem by fxid()
    // Menu Comunicats / Cartes
    val comunicatsMenuItemCartaDocent: MenuItem by fxid()
    val comunicatsMenuItemCartaCentre: MenuItem by fxid()
    val comunicatsMenuItemCartaEmpresa: MenuItem by fxid()
    val comunicatsMenuItemCartaServeiTerritorial: MenuItem by fxid()
    val comunicatsMenuItemCartaCartaAgraiment: MenuItem by fxid()
    val comunicatsMenuItemCartaCertificatTutor: MenuItem by fxid()
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

        with(root) {
        }

        doSetup()

    } // init ends

    private fun doSetup() {

        controller.preLoadData()

        // Menu Database
        databaseMenuItemCerca.setOnAction {
            cercaEstada()
        }
        databaseMenuItemRecarregaPdf.setOnAction { recarregaPdf() }
        databaseMenuItemTanca.setOnAction { controller.menuTanca() }

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

        // Menu Comunicats / Cartes
        comunicatsMenuItemCartaDocent.setOnAction { createCartaDocent() }
        comunicatsMenuItemCartaCentre.setOnAction { createCartaCentre() }
        comunicatsMenuItemCartaEmpresa.setOnAction { createCartaEmpresa() }
        comunicatsMenuItemCartaServeiTerritorial.setOnAction { createCartaSSTT() }
        comunicatsMenuItemCartaCartaAgraiment.setOnAction {
            createCartaAgraiment()
        }
        comunicatsMenuItemCartaCertificatTutor.setOnAction {
            createCartaCertificat()
        }

        // Menu Notificacions
        notificacionsMenuItemLlistaProvisional.setOnAction {

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
        docentTextFieldDni.setOnAction { findDataByDocentId(docentTextFieldDni.text) }

        accordion.expandedPane = titledPaneDocent
        Platform.runLater {
            docentTextFieldDni.requestFocus()
        }

        toolbarButtonCerca.setOnAction {
            cercaEstada()
        }

        toolbarButtonNou.setOnAction {
            cleanScreen()
        }


        toolbarButtonTanca.setOnAction {
            controller.menuTanca()
        }

        buttonBarButtonDesa.setOnAction {
            desa()
        }

    }

    private fun cercaEstada() {
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

    /*
    *
    * TODO("Refer la constant BODY_LLISTAT_PROVISIONAL")
    * */
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

    private fun createCartaDocent() {
        if (checkForEmptyOrNull()) return
        GesticusReports.createCartaDocent(gatherDataFromForm())
    }


    private fun createCartaCentre() {
        if (checkForEmptyOrNull()) return
        GesticusReports.createCartaCentre(gatherDataFromForm())
    }

    private fun createCartaEmpresa() {
        if (checkForEmptyOrNull()) return
        GesticusReports.createCartaEmpresa(gatherDataFromForm())
    }

    private fun createCartaSSTT() {
        if (checkForEmptyOrNull()) return
        GesticusReports.createCartaSSTT(gatherDataFromForm())
    }

    private fun createCartaCertificat(): Unit {

        if (checkForEmptyOrNull()) return

        val view = TutorCertificationView()

        view.openModal(block = true, owner = this.currentWindow, resizable = false, escapeClosesWindow = false)

        if (view.model.item == null) {
            return
        }

        try {
            val hores = view.model.hores.value.toInt()
            val dni = view.model.dni.value

            if (dni.isValidDniNie()) {
                GesticusReports.createCartaCertificatTutor(gatherDataFromForm(), hores, dni)
            } else {
                Alert(Alert.AlertType.ERROR, "El DNI/NIE $dni no té un format vàlid").showAndWait()
            }
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, "El camp 'hores' és un camp numèric").show()
        }

    }

    private fun createCartaAgraiment() {
        if (checkForEmptyOrNull()) return
        GesticusReports.createCartaAgraiment(gatherDataFromForm())
    }

    /*
    * */
    private fun sendCartaDocent() {

        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val filename = GesticusReports.createCartaDocent(registre)

        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_DOCENT,
                    filename,
                    listOf("fpestades@xtec.cat", registre.docent?.email!!))
        } else {
            Alert(Alert.AlertType.ERROR, "No es troba la carta del docent ${registre.docent?.nif}")
        }

    }

    /*
    * */
    private fun sendCartaAgraiment() {

        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val filename = GesticusReports.createCartaAgraiment(registre)

        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_AGRAIMENT,
                    filename,
                    listOf("fpestades@xtec.cat", registre.empresa?.personaDeContacte?.email!!))
        } else {
            Alert(Alert.AlertType.ERROR, "No es troba la carta d'agraïment del docent ${registre.docent?.nif}")
        }

    }

    /*
    *
    * */
    private fun sendCartaCentre() {

        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val filename = GesticusReports.createCartaCentre(registre)

        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_CENTRE,
                    filename,
                    listOf("fpestades@xtec.cat", registre.centre?.email!!, registre.docent?.email!!))
         } else {
            Alert(Alert.AlertType.ERROR, "No es troba la carta de Centre del docent ${registre.docent?.nif}")
        }


    }

    /*
    * TODO("Check out")
    * */
    private fun sendCartaCertificatTutor() {

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
                filename = GesticusReports.createCartaCertificatTutor(registre, hores, dni)
                if (filename != null) {
                    GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                            SUBJECT_GENERAL,
                            BODY_TUTOR,
                            filename,
                            listOf("fpestades@xtec.cat", registre.centre?.email!!, registre.docent?.email!!))
                } else {
                    Alert(Alert.AlertType.ERROR, "No es troba la carta de certificacio del tutor del docent ${registre.docent?.nif}")
                }
            } else {
                Alert(Alert.AlertType.ERROR, "El DNI/NIE $dni no té un format vàlid").showAndWait()
            }
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, "El camp 'hores' és un camp numèric").show()
        }

    }

    /*
    * */
    private fun sendCartaEmpresa() {

        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val filename = GesticusReports.createCartaEmpresa(registre)
        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_EMPRESA,
                    filename,
                    listOf("fpestades@xtec.cat", registre.empresa?.personaDeContacte?.email!!, registre.docent?.email!!))
        } else {
            Alert(Alert.AlertType.ERROR, "No es troba la carta d'empresa del docent ${registre.docent?.nif}")
        }

    }

    /*
    *
    * */
    private fun sendCartaSSTT() {

        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val filename = GesticusReports.createCartaSSTT(registre)
        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_SSTT,
                    filename,
                    listOf("fpestades@xtec.cat", registre.sstt?.emailCSPD!!, registre.sstt?.emailCRHD!!))
        } else {
            Alert(Alert.AlertType.ERROR, "No es troba la carta de SSTT del docent ${registre.docent?.nif}")
        }

    }

    private fun desa(): Unit {

        val registre = gatherDataFromForm()

        if (!checkForEmptyOrNull()) {
            val ret: Boolean = controller.saveEstada(docentTextFieldDni.text, registre.estada!!, registre.empresa!!)
            if (ret) {
                // cleanScreen()
            }
        }
    }

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

    private fun findDataByDocentId(nif: String): Unit {
        val registre: Registre? = controller.findDataByDocentId(docentTextFieldDni.text)

        if (registre != null) {
            display(registre.estada)
            display(registre.empresa)
            display(registre.docent)
            display(registre.centre)
            display(registre.sstt)
            Alert(
                    Alert.AlertType.INFORMATION,
                    "S'ha carregat el/la docent ${registre?.docent?.nom} correctament."
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

    private fun display(registre: Registre) {
        with(registre) {
            display(estada)
            display(empresa)
            display(docent)
            display(centre)
            display(sstt)

        }
    }

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
