package cat.gencat.access.views


import cat.gencat.access.controllers.GesticusController
import cat.gencat.access.db.*
import cat.gencat.access.email.GesticusMailUserAgent
import cat.gencat.access.functions.*
import cat.gencat.access.functions.Utils.Companion.currentCourseYear
import cat.gencat.access.functions.Utils.Companion.errorNotification
import cat.gencat.access.functions.Utils.Companion.icon
import cat.gencat.access.functions.Utils.Companion.infoNotification
import cat.gencat.access.functions.Utils.Companion.isEmailValid
import cat.gencat.access.functions.Utils.Companion.isValidDniNie
import cat.gencat.access.functions.Utils.Companion.nextCourseYear
import cat.gencat.access.functions.Utils.Companion.warningNotification
import cat.gencat.access.functions.Utils.Companion.writeToLog
import cat.gencat.access.reports.GesticusReports
import com.dlsc.preferencesfx.PreferencesFx
import com.dlsc.preferencesfx.model.Category
import com.dlsc.preferencesfx.model.Setting
import com.example.demo.view.AdmesosEditorView
import com.example.demo.view.SSTTEditorView
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.util.Duration
import tornadofx.*
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate


class GesticusView : View(APP_TITLE) {

    override val root: BorderPane by fxml()

    val controller: GesticusController by inject()

    // Menu Database
    val databaseMenuItemCerca: MenuItem by fxid()
    val databaseMenuItemCercaPerNom: MenuItem by fxid()
    val databaseMenuItemSeguiment: MenuItem by fxid()
    val databaseMenuItemNova: MenuItem by fxid()
    val databaseMenuItemObreEAPdf: MenuItem by fxid()
    val databaseMenuItemObreEBPdf: MenuItem by fxid()
    val databaseMenuItemTanca: MenuItem by fxid()
    val databaseMenuItemDocumentada: MenuItem by fxid()
    val databaseMenuItemFinalitzada: MenuItem by fxid()
    val databaseMenuItemAlta: MenuItem by fxid()
    val databaseMenuItemBaixa: MenuItem by fxid()
    // Menu Edit
    val editMenuItemAdmesos: MenuItem by fxid()
    val editMenuItemSSTT: MenuItem by fxid()
    // Menu Comunicats / Correu
    val comunicatsMenuItemTot: MenuItem by fxid()
    val comunicatsMenuItemPrintAll: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaCentre: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaDocent: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaEmpresa: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaEmpresaCastella: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaEmpresaAngles: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaAgraiment: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaAgraimentCastella: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaAgraimentAngles: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaCertificacio: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaCertificacioCastella: MenuItem by fxid()
    val comunicatsMenuItemPrintCartaCertificacioAngles: MenuItem by fxid()
    val comunicatsMenuItemCorreuDocent: MenuItem by fxid()
    val comunicatsMenuItemCorreuCentre: MenuItem by fxid()
    val comunicatsMenuItemCorreuEmpresa: MenuItem by fxid()
    val comunicatsMenuItemCorreuServeiTerritorial: MenuItem by fxid()
    val comunicatsMenuItemCorreuCartaAgraiment: MenuItem by fxid()
    val comunicatsMenuItemCorreuCertificatTutor: MenuItem by fxid()
    // Menu Notificacions
    /* Tenen un estada concedida però encara no han lliurat cap sol·licitud */
    val notificacionsMenuItemEstadesPendents: MenuItem by fxid()
    /* Estada acabada però falta documentació, per a fer recordatoris periòdics */
    val notificacionsMenuItemEstatAcabada: MenuItem by fxid()
    /* Missatge de què estem fent tot el possible per a trobar emmpresa, pensada per a sanitaris */
    val notificacionsMenuItemCollectius: MenuItem by fxid()
    // Menu Estadístiques
    val estadistiquesMenuItemProgress: MenuItem by fxid()
    val estadistiquesMenuItemEstadesPerCentre: MenuItem by fxid()
    val estadistiquesMenuItemEstadesPerFamilia: MenuItem by fxid()
    val estadistiquesMenuItemEstadesPerSSTT: MenuItem by fxid()
    val estadistiquesMenuItemEstadesPerCos: MenuItem by fxid()
    val estadistiquesMenuItemEstadesPerSexe: MenuItem by fxid()
    val estadistiquesMenuItemEstadesNoGestionadesPerCentre: MenuItem by fxid()
    val estadistiquesMenuItemEstadesNoGestionadesPerFamilia: MenuItem by fxid()
    val estadistiquesMenuItemEstadesNoGestionadesPerSSTT: MenuItem by fxid()
    val estadistiquesMenuItemEstadesNoGestionadesPerCos: MenuItem by fxid()
    val estadistiquesMenuItemEstadesNoGestionadesPerSexe: MenuItem by fxid()
    val estadistiquesMenuItemLlistatPendentsPerFamilia: MenuItem by fxid()

    // Menu Eines
    val einesMenuItemPreferencies: MenuItem by fxid()
    val einesMenuItemLlistatGeneral: MenuItem by fxid()
    val einesMenuItemObrePdf: MenuItem by fxid()
    val einesMenuItemGeneraCSVEstadesDocumentades: MenuItem by fxid()
    val einesMenuItemBarema: MenuItem by fxid()

    // Menu Ajuda
    val ajudaMenuItemUs: MenuItem by fxid()
    val ajudaMenuItemSobreNosaltres: MenuItem by fxid()

    // Toolbar
    val toolbarButtonCerca: Button by fxid()
    val toolbarButtonCercaPerNom: Button by fxid()
    val toolbarButtonSeguiment: Button by fxid()
    //    val toolbarButtonObreEA: Button by fxid()
    val toolbarButtonObreEB: Button by fxid()
    val toolbarButtonComunicaATohom: Button by fxid()
    val toolbarButtonNou: Button by fxid()
    val toolbarButtonProgres: Button by fxid()
    val toolbarButtonEstats: Button by fxid()
    val toolbarButtonPreferencies: Button by fxid()
    val toolbarButtonAboutUs: Button by fxid()
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
    val buttonProgressIndicator: Button by fxid()
    val buttonBarButtonDesa: Button by fxid()

    val codiEstadaFormat = "000\\d{3}0600/\\d{4}-\\d{4}".toRegex()

    init {
        doSetup()
        buttonProgressIndicator.runAsyncWithProgress {
            buttonProgressIndicator.isVisible = true
            controller.preLoadData()
            buttonProgressIndicator.isVisible = false
            runLater {
                infoNotification(APP_TITLE, "Gèsticus s'ha carregat correctament.", position = Pos.CENTER, owner = this.currentWindow, hideAfter = Duration.seconds(1.5))
//                Alert(Alert.AlertType.ERROR).apply {
//                    title = APP_TITLE
//                    isResizable = false
//                    headerText = "Checking status"
//                    dialogPane.content = VBox().apply {
//                        title = "Second title"
//                        add(label("A Label") {})
//                        add(textarea {
//                            contentText = "dafgfdg"
//                        })
//                    }
//                    showAndWait()
//                }
                checkStatusUpdateBd()
            }
        }


    } // init ends

    private fun doSetup() {

        // Menu Database
        databaseMenuItemCerca.setOnAction {
            cercaEstadaPerNumeroDeEstadaNif()
        }
        databaseMenuItemCercaPerNom.setOnAction {
            cercaEstadaPerNom()
        }
        databaseMenuItemSeguiment.setOnAction {
            seguimentEstades()
        }
        databaseMenuItemNova.setOnAction {
            cleanScreen()
        }
        databaseMenuItemObreEAPdf.setOnAction {
            val registre = getRecordFromPdf("A")
            display(registre)
        }
        databaseMenuItemObreEBPdf.setOnAction {
            val registre = getRecordFromPdf("B")
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

        editMenuItemAdmesos.setOnAction {
            find(AdmesosEditorView::class).openModal()
        }

        editMenuItemSSTT.setOnAction {
            find(SSTTEditorView::class).openModal()
        }

        comunicatsMenuItemTot.setOnAction {
            sendTotATothom()
        }

        comunicatsMenuItemPrintAll.setOnAction {
            if (checkForEmptyOrNull())
                return@setOnAction
            Alert(Alert.AlertType.CONFIRMATION, "Estas segur que vols generar totes les cartes?")
                    .showAndWait()
                    .ifPresent {
                        if (it == ButtonType.OK) {
                            buttonProgressIndicator.runAsyncWithProgress {
                                buttonProgressIndicator.isVisible = true
                                printAll()
                                buttonProgressIndicator.isVisible = false
                            }
                        }
                        infoNotification(APP_TITLE, "S'han creat totes les cartes correctament")
                    }
        }

        comunicatsMenuItemPrintCartaCentre.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = GesticusReports.createCartaCentre(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }
            }
        }

        comunicatsMenuItemPrintCartaDocent.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = GesticusReports.createCartaDocentPDF(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }
            }
        }

        comunicatsMenuItemPrintCartaEmpresa.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = GesticusReports.createCartaEmpresa(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }
            }
        }

        comunicatsMenuItemPrintCartaEmpresaCastella.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = GesticusReports.createCartaEmpresaCastellaHTML(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }
            }
        }

        comunicatsMenuItemPrintCartaEmpresaAngles.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = GesticusReports.createCartaEmpresaAnglesHTML(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }
            }
        }

        comunicatsMenuItemPrintCartaAgraiment.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = GesticusReports.createCartaAgraiment(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }
            }
        }

        comunicatsMenuItemPrintCartaAgraimentCastella.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = GesticusReports.createCartaAgraimentCastellaHTML(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }
            }
        }

        comunicatsMenuItemPrintCartaAgraimentAngles.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = GesticusReports.createCartaAgraimentAnglesHTML(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }
            }
        }

        comunicatsMenuItemPrintCartaCertificacio.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = createCartaCertificatTutor(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }

            }
        }

        comunicatsMenuItemPrintCartaCertificacioCastella.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = createCartaCertificatTutorCastella(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }

            }
        }

        comunicatsMenuItemPrintCartaCertificacioAngles.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            val filename = createCartaCertificatTutorAngles(registre)
            filename?.run {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = APP_TITLE
                    contentText = "Sha creat la carta $filename correctament"
                    showAndWait()
                }

            }
        }

        // Menu Comunicats / Correus
        comunicatsMenuItemCorreuDocent.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                sendCartaDocent(registre)
                buttonProgressIndicator.isVisible = false
            }
        }
        comunicatsMenuItemCorreuCentre.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                sendCartaCentre(registre)
                buttonProgressIndicator.isVisible = false
            }

        }
        comunicatsMenuItemCorreuEmpresa.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                sendCartaEmpresa(registre)
                buttonProgressIndicator.isVisible = false
            }
        }
        comunicatsMenuItemCorreuServeiTerritorial.setOnAction {
            if (checkForEmptyOrNull()) return@setOnAction
            val registre = gatherDataFromForm()
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                sendCartaSSTT(registre)
                buttonProgressIndicator.isVisible = false
            }
        }
        comunicatsMenuItemCorreuCartaAgraiment.setOnAction {
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                sendCartaAgraiment()
                buttonProgressIndicator.isVisible = false
            }
        }
        comunicatsMenuItemCorreuCertificatTutor.setOnAction {
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                sendCartaCertificatTutor()
                buttonProgressIndicator.isVisible = false
            }
        }

        /*
        * Aquest mètode lliura un correu a admesos_t que encara no tenen estada en
        * progress a estades_t avisant-los que el temps s'acaba
        * */
        notificacionsMenuItemEstadesPendents.setOnAction {
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                sendRecordatoriPendentsPerFamilies()
                buttonProgressIndicator.isVisible = false
            }
        }

        /*
        * Aquest mètode lliura un correu a aquells que ja han acabat l'estada fa més d'un mes
        * però encara no l'han documentada
        * */
        notificacionsMenuItemEstatAcabada.setOnAction {
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                checkStatusAcabadaSendEmail()
                buttonProgressIndicator.isVisible = false
            }
        }

        // Menu Notificacions
        notificacionsMenuItemCollectius.setOnAction {
            val dialog = TextInputDialog("Sanitat")
            dialog.setTitle(APP_TITLE)
            dialog.contentText = "Correu col·lectius"
            dialog
                    .showAndWait()
                    .ifPresent {
                        sendCorreuToColletiuSenseEstada(it)
                    }
        }

        // Menu Estadístiques
        estadistiquesMenuItemProgress.setOnAction {
            find<StatisticsProgressView>().openModal()
        }
        estadistiquesMenuItemEstadesPerCentre.setOnAction {
            find<StatisticsByCentreView>().openModal()
        }

        estadistiquesMenuItemEstadesNoGestionadesPerCentre.setOnAction {
            find<StatisticsByCentreNoGestionadaView>().openModal()
        }

        estadistiquesMenuItemEstadesPerFamilia.setOnAction {
            find<StatisticsByFamiliaView>().openModal()
        }

        estadistiquesMenuItemEstadesNoGestionadesPerFamilia.setOnAction {
            find<StatisticsByFamiliaNoGestionadaView>().openModal()
        }

        estadistiquesMenuItemEstadesPerSSTT.setOnAction {
            find<StatisticsBySSTTView>().openModal()
        }

        estadistiquesMenuItemEstadesNoGestionadesPerSSTT.setOnAction {
            find<StatisticsBySSTTNoGestionadaView>().openModal()
        }

        estadistiquesMenuItemEstadesPerCos.setOnAction {
            find<StatisticsByCosView>().openModal()
        }

        estadistiquesMenuItemEstadesNoGestionadesPerCos.setOnAction {
            find<StatisticsByCosNoGestionadaView>().openModal()
        }

        estadistiquesMenuItemEstadesPerSexe.setOnAction {
            find<StatisticsBySexeView>().openModal()
        }

        estadistiquesMenuItemEstadesNoGestionadesPerSexe.setOnAction {
            find<StatisticsBySexeNoGestionadaView>().openModal()
        }

        estadistiquesMenuItemLlistatPendentsPerFamilia.setOnAction {
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                doLlistatPendentsPerFamilies()
                buttonProgressIndicator.isVisible = false
            }
        }
        // Menu Eines
        einesMenuItemPreferencies.setOnAction {
            showPreferences()
        }
        einesMenuItemLlistatGeneral.setOnAction {
            checkStatusSummary()
        }

        einesMenuItemObrePdf.setOnAction {
            obrePdf()
        }

        einesMenuItemGeneraCSVEstadesDocumentades.setOnAction {
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                generaCSVEstadesDocumentades()
                buttonProgressIndicator.isVisible = false
            }
        }

        einesMenuItemBarema.setOnAction {
            buttonProgressIndicator.runAsyncWithProgress {
                buttonProgressIndicator.isVisible = true
                barema()
                buttonProgressIndicator.isVisible = false
            }
        }

        // Menu Ajuda
        ajudaMenuItemUs.setOnAction {
            find<HelpView>().openModal(block = true, resizable = false, escapeClosesWindow = true)
        }
        ajudaMenuItemSobreNosaltres.setOnAction {
            find<AboutUsView>().openModal(block = true, resizable = false, escapeClosesWindow = true)
        }

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
            loadDataByDocentIdFromPdf(docentTextFieldDni.text, "B")
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

        with(toolbarButtonCerca) {
            icon(FontAwesomeIcon.SEARCH, "Cerca estada per codi/NIF")
            setOnAction {
                cercaEstadaPerNumeroDeEstadaNif()
            }
        }

        with(toolbarButtonCercaPerNom) {
            icon(FontAwesomeIcon.SEARCH_PLUS, "Cerca estada per tot")
            setOnAction {
                cercaEstadaPerNom()
            }
        }

        with(toolbarButtonSeguiment) {
            icon(MaterialDesignIcon.CLIPBOARD_ARROW_DOWN, "Seguiment d'estades")
            setOnAction {
                seguimentEstades()
            }
        }

//        toolbarButtonObreEA.setOnAction {
//            val registre = getRecordFromPdf("A")
//            display(registre)
//        }

        with(toolbarButtonObreEB) {
            icon(FontAwesomeIcon.FOLDER_OPEN, "Obre sol·licitud B")
            setOnAction {
                val registre = getRecordFromPdf("B")
                display(registre)
            }
        }

        with(toolbarButtonComunicaATohom) {
            icon(MaterialDesignIcon.SEND, "Comunica estada a Tothom")
            setOnAction {
                sendTotATothom()
            }
        }

        with(toolbarButtonProgres) {
            icon(FontAwesomeIcon.PIE_CHART, "Progrés estades")
            setOnAction {
                find<StatisticsProgressView>().openModal()
            }
        }

        with(toolbarButtonEstats) {
            icon(FontAwesomeIcon.STACK_OVERFLOW, "Check status")
            setOnAction {
                checkStatusSummary()
            }
        }

        with(toolbarButtonNou) {
            icon(FontAwesomeIcon.USER_PLUS, "Neteja formulari")
            setOnAction {
                cleanScreen()
            }
        }

        with(toolbarButtonPreferencies) {
            icon(FontAwesomeIcon.CHECK_CIRCLE_ALT, "Preferències")
            setOnAction {
                showPreferences()
            }
        }

        with(toolbarButtonAboutUs) {
            icon(FontAwesomeIcon.CREATIVE_COMMONS, "Sobre nosaltres")
            setOnAction {
                find<AboutUsView>().openModal(block = true, resizable = false, escapeClosesWindow = true)
            }
        }

        with(toolbarButtonTanca) {
            icon(MaterialDesignIcon.EXIT_TO_APP, "Tanca Gèsticus")
            setOnAction {
                controller.menuTanca()
            }
        }

        with(buttonBarButtonDesa) {
            icon(FontAwesomeIcon.SAVE, "Desa l'estada")
            setOnAction {
                desaEstadaBd()
            }
        }


    }

    /* PreferencesFX */
    private fun showPreferences() {

        val tecnicNomProperty = SimpleStringProperty("")
        val tecnicCarrecProperty = SimpleStringProperty("")
        val responsableNomProperty = SimpleStringProperty("")
        val responsableCarrecProperty = SimpleStringProperty("")
        val adreçaProperty = SimpleStringProperty("")
        val codiPostalProperty = SimpleStringProperty("")
        val provinciaProperty = SimpleStringProperty("")

        val preferences = PreferencesFx.of(
                GesticusApp::class.java,
                Category.of(
                        "General",
                        com.dlsc.preferencesfx.model.Group.of(
                                "Tècnic",
                                Setting.of("Nom", tecnicNomProperty), // creates a group automatically
                                Setting.of("Càrrec", tecnicCarrecProperty)
                        ),
                        com.dlsc.preferencesfx.model.Group.of(
                                "Responsable",
                                Setting.of("Nom", responsableNomProperty), // creates a group automatically
                                Setting.of("Càrrec", responsableCarrecProperty)
                        ),
                        com.dlsc.preferencesfx.model.Group.of(
                                "Seu",
                                Setting.of("Adreça", adreçaProperty), // creates a group automatically
                                Setting.of("Codi postal", codiPostalProperty),
                                Setting.of("Província", provinciaProperty)
                        )
                        // which contains both settings
                )
        ).persistWindowState(false).persistApplicationState(true)

        tecnicNomProperty.onChange {
            config.set("tecnic_nom", it ?: "default")
            config.save()
        }

        tecnicCarrecProperty.onChange {
            config.set("tecnic_carrec", it ?: "default")
            config.save()
        }

        responsableNomProperty.onChange {
            config.set("responsable_nom", it.toString())
            config.save()
        }

        responsableCarrecProperty.onChange {
            config.set("responsable_carrec", it.toString())
            config.save()
        }

        adreçaProperty.onChange {
            config.set("adreça", it.toString())
            config.save()
        }

        codiPostalProperty.onChange {
            config.set("codi_postal", it.toString())
            config.save()
        }

        provinciaProperty.onChange {
            config.set("provincia", it.toString())
            config.save()
        }

        preferences.show(true)
    }

    fun doLlistatPendentsPerFamilies() {
        if (controller.doLlistatPendentsPerFamilies()) {
            runLater {
                infoNotification(APP_TITLE, "S'han creat els fitxers correctament a $PATH_TO_LLISTATS")
            }
        } else {
            runLater {
                errorNotification(APP_TITLE, "No s'han creat els fitxers correctament a $PATH_TO_LLISTATS")
            }
        }
    }

    /* Cal un sistema per a enregistrar el número d'avisos enviats? */
    fun checkStatusSummary() {
        // Loop through each estada and change status accordingly:
        val summary = controller.checkStatusSummary()
        find<SummaryViewWithTable>("summaries" to summary).openModal()
    }

    fun obrePdf() {
        val fileChooser = FileChooser()
        fileChooser.title = "Obre Estada"

        fileChooser.initialDirectory = File(PATH_TO_REPORTS)
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Estades", "*.pdf"),
                FileChooser.ExtensionFilter("All Files", "*.*")
        )
        val selectedFile = fileChooser.showOpenDialog(this.currentWindow)
        //println(selectedFile.absoluteFile)
        var registre: Registre? = null
        if (selectedFile != null) {
            find<PdfViewer>("path" to selectedFile.absolutePath).openModal()
        }
    }

    fun generaCSVEstadesDocumentades() = controller.generateCSVFileStatusDocumentada()

    fun checkStatusUpdateBd() = controller.checkStatusUpdateBd()

    fun sendRecordatoriPendentsPerFamilies() = controller.sendRecordatoriPendentsPerFamilies()

    fun checkStatusAcabadaSendEmail() = controller.checkStatusAcabadaSendEmail()


    private fun findCentreAndSSTT(codiCentre: String): Unit {
        val centreAndSSTT: Pair<Centre, SSTT> = controller.findCentreAndSSTT(codiCentre)
        display(centreAndSSTT.first)
        display(centreAndSSTT.second)
    }

    private fun findSSTT(codiSSTT: String): Unit {
        val sstt: SSTT = controller.findSSTT(codiSSTT)
        display(sstt)
    }

    /* Carrega un view amb dos tableview relacionats: gestionades/estats  */
    private fun seguimentEstades(): Unit {
        val dialog = TextInputDialog(docentTextFieldDni.text)
        dialog.setTitle(APP_TITLE);
        dialog.contentText = "Seguiment d'estades"
        dialog.showAndWait()
                .ifPresent { nif ->
                    if (nif.matches(NIF_REGEXP) || nif.matches(NIE_REGEXP) || nif.matches("%".toRegex())) {
                        //find<SeguimentEstadesView>(Pair("nif", nif)).openModal()
                        // Always same object.
//                        find<SeguimentEstadesView>(mapOf(SeguimentEstadesView::nif to nif)).openModal()
                        SeguimentEstadesView(nif).openModal()

                    } else {
                        Alert(Alert.AlertType.INFORMATION, "El NIF $nif no és vàlid")
                                .show()
                    }
                }
    }

    /*
    * Aquest mètode cerca una estada per numero d'estada
    * que és un camp clau 0003730600/2018-2019
    *
    * També ha de poder buscar per nom
    *
    * */
    private fun cercaEstadaPerNumeroDeEstadaNif() {
        val dialog = TextInputDialog("Número d'estada/NIF")
        dialog.setTitle(APP_TITLE);
        dialog.contentText = "Cerca d'estada"
        val result = dialog.showAndWait();
        if (result.isPresent) {
            var codiEstada = result.get()
            if (codiEstada.matches("\\d{3}".toRegex())) {
                codiEstada = "000${codiEstada}0600/${currentCourseYear()}-${nextCourseYear()}"
            }
            // 0009990600/YYYY-YYYY
            if (codiEstada.matches(codiEstadaFormat)) {
                val registre: Registre? = controller.findRegistreByCodiEstada(codiEstada)
                if (registre == null) {
                    errorNotification(APP_TITLE, "No s'ha trobat cap estada registrada amb el codi $codiEstada")
                } else {
                    display(registre)
                }
            } else if (codiEstada.matches(NIF_REGEXP) || codiEstada.matches(NIE_REGEXP)) {
                val registre: Registre? = controller.findRegistreByNif(codiEstada)
                if (registre == null) {
                    errorNotification(APP_TITLE, "No s'ha trobat cap estada registrada amb el NIF $codiEstada")
                } else {
                    display(registre)
                }
            } else {
                errorNotification(
                        APP_TITLE,
                        "L'argument de busqueda ${codiEstada} no té un format correcte"
                )
            }
        }
    }

    fun cercaEstadaPerNom() {
        find<SearchByNameView>().openModal()
    }

    /* TODO("Review BODY_LLISTAT_PROVISIONAL") */
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
                        listOf(selectedFile.absolutePath),
                        candidats
                )
            }
        } else {
            infoNotification(APP_TITLE, "No hi ha docents a la taula candidats")
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
                        listOf(selectedFile.absolutePath),
                        candidats
                )
            }
        } else {
            errorNotification(APP_TITLE, "No hi ha docents a la taula candidats")
        }
    }

    /* Sends everything according to type estada: carta docent, empresa, centre i si és tipus B també a sstt */
    private fun sendTotATothom() {
        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        val numEstada = registre.estada?.numeroEstada!!
        if (!controller.existeixNumeroDeEstada(numEstada)) {
            errorNotification(APP_TITLE, "L'estada amb codi $numEstada no existeix!")
            return
        }
        Alert(Alert.AlertType.CONFIRMATION, "Estas segur que vols notificar totes les entitats?")
                .showAndWait()
                .ifPresent {
                    if (it == ButtonType.OK) {
                        buttonProgressIndicator.isVisible = true
                        buttonProgressIndicator.runAsyncWithProgress {
                            sendCartaDocent(registre, false)
                            sendCartaCentre(registre, false)
                            sendCartaEmpresa(registre, false)
                            if (estadaComboBoxTipusEstada.value == "B") {
                                sendCartaSSTT(registre, false)
                            }
                        }
                        buttonProgressIndicator.isVisible = false
                        infoNotification(APP_TITLE, "S'ha notificat l'estada a totes les entitats implicades")
                    }

                }
    }

    private fun printAll(): Unit {

        val registre = gatherDataFromForm()
        GesticusReports.createCartaDocentPDF(registre)
        GesticusReports.createCartaCentre(registre)
        GesticusReports.createCartaEmpresa(registre)
        GesticusReports.createCartaSSTTPDF(registre)
        GesticusReports.createCartaAgraimentPDF(registre)
        GesticusReports.createCartaAgraimentHTML(registre)
        /* Cal executar aquest mètode en el UI thread */
        //createCartaCertificatTutor(registre)

    }

    private fun createCartaCertificatTutor(registre: Registre): String? {

        val view = TutorCertificationView()

        view.openModal(block = true, owner = this.currentWindow, resizable = false, escapeClosesWindow = false)

        if (view.model.item == null) {
            return null
        }

        try {
            val hores = view.model.hores.value.toInt()
            val dni = view.model.dni.value

            if (dni.isValidDniNie()) {
                GesticusReports.createCartaCertificatTutorPDF(registre, hores, dni)
                return GesticusReports.createCartaCertificatTutorHTML(registre, hores, dni)
            } else {
                errorNotification(APP_TITLE, "$dni no és un DNI vàlid")
            }
        } catch (error: Exception) {
            writeToLog("${LocalDate.now()} $error")
        }
        return null
    }

    private fun createCartaCertificatTutorCastella(registre: Registre): String? {

        val view = TutorCertificationView()

        view.openModal(block = true, owner = this.currentWindow, resizable = false, escapeClosesWindow = false)

        if (view.model.item == null) {
            return null
        }

        try {
            val hores = view.model.hores.value.toInt()
            val dni = view.model.dni.value

            if (dni.isValidDniNie()) {
                //GesticusReports.createCartaCertificatTutorPDF(registre, hores, dni)
                return GesticusReports.createCartaCertificatTutorCastellaHTML(registre, hores, dni)
            } else {
                errorNotification(APP_TITLE, "$dni no és un DNI vàlid")
            }
        } catch (error: Exception) {
            writeToLog("${LocalDate.now()} $error")
        }
        return null
    }

    private fun createCartaCertificatTutorAngles(registre: Registre): String? {

        val view = TutorCertificationView()

        view.openModal(block = true, owner = this.currentWindow, resizable = false, escapeClosesWindow = false)

        if (view.model.item == null) {
            return null
        }

        try {
            val hores = view.model.hores.value.toInt()
            val dni = view.model.dni.value

            if (dni.isValidDniNie()) {
                //GesticusReports.createCartaCertificatTutorPDF(registre, hores, dni)
                return GesticusReports.createCartaCertificatTutorAnglesHTML(registre, hores, dni)
            } else {
                errorNotification(APP_TITLE, "$dni no és un DNI vàlid")
            }
        } catch (error: Exception) {
            writeToLog("${LocalDate.now()} $error")
        }
        return null
    }

    /* Sends carta to Docent */
    private fun sendCartaDocent(registre: Registre, notifyOk: Boolean = true) {

        val filename = GesticusReports.createCartaDocentPDF(registre)
        var msg = ""
        val benvolgut = if (registre.docent?.nom!!.startsWith("Sra.")) "Bonvolguda,"
        else if (registre.docent?.nom!!.startsWith("Sr.")) "Benvolgut,"
        else "Benvolgut/da,"

        if (filename != null) {
            if (controller.insertEstatDeEstada(
                            registre.estada?.numeroEstada!!,
                            EstatsSeguimentEstadaEnum.COMUNICADA,
                            "comunicada a ${registre.docent?.nom}"
                    )
            ) {
                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                        SUBJECT_GENERAL,
                        BODY_DOCENT.replace("?1", benvolgut),
                        listOf(filename),
                        listOf(registre.docent?.email!!)
                )
//            GesticusOs.copyReport(filename)
                msg = "S'ha enviat el fitxer $filename correctament"
                if (notifyOk) {
                    runLater {
                        Alert(Alert.AlertType.INFORMATION, msg).showAndWait()
                    }
                }
            }
        } else {
            msg = "No es troba la carta del docent ${registre.docent?.nif}"
            Platform.runLater {
                errorNotification(APP_TITLE, msg)
            }
        }
        writeToLog("${LocalDate.now()} $msg")
    }

    /* Sends carta al Centre i docent */
    private fun sendCartaCentre(registre: Registre, notifyOk: Boolean = true) {

        val filename = GesticusReports.createCartaCentre(registre)
        var msg = ""
        val nom = if (registre.docent?.nom!!.startsWith("Sra.")) "a la ${registre.docent?.nom!!}"
        else if (registre.docent?.nom!!.startsWith("Sr.")) "al ${registre.docent?.nom!!}"
        else "un/a docent"
        val professor = if (registre.docent?.nom!!.startsWith("Sra.")) "professora"
        else if (registre.docent?.nom!!.startsWith("Sr.")) "professor"
        else "professor/a"
        val benvolgut = if (registre.centre?.director!!.startsWith("Sra.")) "Bonvolguda,"
        else if (registre.centre?.director!!.startsWith("Sr.")) "Benvolgut,"
        else "Benvolgut/da,"

        if (filename != null) {
            if (controller.insertEstatDeEstada(
                            registre.estada?.numeroEstada!!, EstatsSeguimentEstadaEnum.COMUNICADA,
                            "comunicada a ${registre.centre?.nom}"
                    )
            ) {
                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                        SUBJECT_GENERAL,
                        BODY_CENTRE
                                .replace("?1", benvolgut)
                                .replace("?2", nom)
                                .replace("?3", professor),
                        listOf(filename),
                        listOf(registre.centre?.email!!, registre.docent?.email!!)
                )
                //            GesticusOs.copyReport(filename)
                msg = "S'ha enviat el fitxer $filename correctament"
                if (notifyOk) {
                    runLater {
                        infoNotification(APP_TITLE, msg)
                    }
                }
            }
        } else {
            msg = "No es troba la carta pel Centre del docent ${registre.docent?.nif}"
            Platform.runLater {
                errorNotification(APP_TITLE, msg)
            }

        }
        writeToLog("${LocalDate.now()} $msg")
    }

    /* Sends carta to empresa and docent */
    private fun sendCartaEmpresa(registre: Registre, notifyOk: Boolean = true) {

        val filename = GesticusReports.createCartaEmpresa(registre)
        var msg = ""
        val empresari = registre.empresa?.personaDeContacte?.nom!!
        val nom = if (registre.docent?.nom!!.startsWith("Sra.")) "a la ${registre.docent?.nom!!}"
        else if (registre.docent?.nom!!.startsWith("Sr.")) "al ${registre.docent?.nom!!}"
        else "un/a docent"
        val professor = if (registre.docent?.nom!!.startsWith("Sra.")) "professora"
        else if (registre.docent?.nom!!.startsWith("Sr.")) "professor"
        else "professor/a"
        val benvolgut = if (empresari.startsWith("Sra.")) "Bonvolguda,"
        else if (empresari.startsWith("Sr.")) "Benvolgut,"
        else "Benvolgut/da,"

        if (filename != null) {
            if (controller.insertEstatDeEstada(
                            registre.estada?.numeroEstada!!, EstatsSeguimentEstadaEnum.COMUNICADA,
                            "comunicada a ${registre.empresa?.identificacio?.nom}"
                    )
            ) {
                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                        SUBJECT_GENERAL,
                        BODY_EMPRESA
                                .replace("?1", benvolgut)
                                .replace("?2", nom)
                                .replace("?3", professor),
                        listOf(filename),
                        listOf(registre.empresa?.personaDeContacte?.email!!, registre.docent?.email!!)
                )

//            GesticusOs.copyReport(filename)
                msg = "S'ha enviat el fitxer $filename correctament"
                if (notifyOk) {
                    runLater {
                        infoNotification(APP_TITLE, msg)
                    }
                }
            }
        } else {
            msg = "No es troba la carta d'empresa del docent ${registre.docent?.nif}"
            Platform.runLater {
                errorNotification(APP_TITLE, msg)
            }
        }
        writeToLog("${LocalDate.now()} $msg")
    }

    /* Sends two letters to SSTT */
    private fun sendCartaSSTT(registre: Registre, notifyOk: Boolean = true) {

        val filename = GesticusReports.createCartaSSTTPDF(registre)
        var msg = ""

        val nom = if (registre.docent?.nom!!.startsWith("Sra.")) "a la ${registre.docent?.nom!!}"
        else if (registre.docent?.nom!!.startsWith("Sr.")) "al ${registre.docent?.nom!!}"
        else "un/a docent"
        val professor = if (registre.docent?.nom!!.startsWith("Sra.")) "professora"
        else if (registre.docent?.nom!!.startsWith("Sr.")) "professor"
        else "professor/a"
        val sstt = registre.sstt?.nom!!

        if (filename != null) {
            GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                    SUBJECT_GENERAL,
                    BODY_SSTT
                            .replace("?1", nom)
                            .replace("?2", professor)
                            .replace("?3", sstt),
                    listOf(filename),
                    listOf(registre.sstt?.emailCSPD!!, registre.sstt?.emailCRHD!!)
            )
            controller.insertEstatDeEstada(
                    registre.estada?.numeroEstada!!, EstatsSeguimentEstadaEnum.COMUNICADA,
                    "comunicada al ${registre.sstt?.nom}"
            )
//            GesticusOs.copyReport(filename)
            msg = "S'ha enviat el fitxer $filename correctament"
            if (notifyOk) {
                runLater {
                    infoNotification(APP_TITLE, msg)
                }
            }
        } else {
            msg = "No es troba la carta de SSTT del docent ${registre.docent?.nif}"
            errorNotification(APP_TITLE, msg)
        }
        writeToLog("${LocalDate.now()} $msg")
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
                    listOf(filename),
                    listOf(registre.empresa?.personaDeContacte?.email!!)
            )
            val msg = "S'ha enviat el fitxer $filename correctament"
            controller.insertEstatDeEstada(
                    registre.estada?.numeroEstada!!, EstatsSeguimentEstadaEnum.COMUNICADA,
                    "comunicada a ${registre?.empresa?.personaDeContacte?.nom}"
            )
//            GesticusOs.copyReport(filename)
            writeToLog("${LocalDate.now()} $msg")
            if (notifyOk) {
                runLater {
                    infoNotification(APP_TITLE, msg)
                }
            }
        } else {
            val msg = "No es troba la carta d'agraïment del docent ${registre.docent?.nif}"
            writeToLog("${LocalDate.now()} $msg")
            errorNotification(APP_TITLE, msg)
        }
    }

    /* Sends carta certificat tutor to responsable empresa */
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
                val tutor = registre.empresa?.tutor?.nom!!
                val elTutor = if (tutor.startsWith("Sr.")) "el $tutor"
                else if (tutor.startsWith("Sra.")) "la $tutor"
                else "el/la $tutor"
                val docent = registre.docent?.nom!!
                val elDocent = if (docent.startsWith("Sr.")) "del $docent, professor"
                else if (docent.startsWith("Sra.")) "de la $docent, professora"
                else "d'el/de la $docent, professor/a"
                filename = GesticusReports.createCartaCertificatTutorPDF(registre, hores, dni)
                if (filename != null) {
                    GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                            SUBJECT_GENERAL,
                            BODY_TUTOR
                                    .replace("?1", registre.empresa?.personaDeContacte?.nom!!)
                                    .replace("?2", elTutor)
                                    .replace("?3", elDocent)
                            ,
                            listOf(filename),
                            listOf(registre.centre?.email!!, registre.docent?.email!!)
                    )
                    controller.insertEstatDeEstada(
                            registre.estada?.numeroEstada!!,
                            EstatsSeguimentEstadaEnum.COMUNICADA,
                            "Enviada carta de certificació al/a la tutor/a"
                    )
//                    GesticusOs.copyReport(filename)
                    val msg = "S'ha enviat el fitxer $filename correctament"
                    writeToLog("${LocalDate.now()} $msg")
                    buttonProgressIndicator.isVisible = false
                    if (notifyOk) {
                        runLater {
                            infoNotification(APP_TITLE, msg)
                        }
                    }
                } else {
                    val msg = "No es troba la carta de certificació pel tutor del docent ${registre.docent?.nif}"
                    writeToLog("${LocalDate.now()} $msg")
                    errorNotification(APP_TITLE, msg)
                }
            } else {
                errorNotification(APP_TITLE, "El DNI/NIE $dni no té un format vàlid")
            }
        } catch (error: Exception) {
            errorNotification(APP_TITLE, "El camp 'hores' és un camp numèric")
        }

    }

    /* Guard o actualiza estada en la bd */
    private fun desaEstadaBd(): Unit {

        if (!checkForEmptyOrNull()) {
            val registre = gatherDataFromForm()
//            val ret: Boolean = controller.saveEstada(docentTextFieldDni.text, registre.estada!!, registre.empresa!!)
            val ret: Boolean = controller.saveEstada(registre)
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
        if (!empresaIdentificacioTextFieldCodiPostal.text.matches(CODI_POSTAL_REGEXP)) {
            Alert(Alert.AlertType.ERROR, "El camp 'Codi postal' d'empresa no es un codi postal vàlid").showAndWait()
            return true
        }
        if (empresaPersonaContacteTextFieldNom.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Nom' de la persona de contacte no pot estar buit").showAndWait()
            return true
        }
        if (!empresaPersonaContacteTextFieldNom.text.startsWith("Sr")) {
            Alert(Alert.AlertType.ERROR, "Manca el tractament del camp 'Nom' de la persona de contacte").showAndWait()
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
        if (!isEmailValid(empresaPersonaContacteTextFieldEmail.text)) {
            Alert(
                    Alert.AlertType.ERROR,
                    "El contingut del camp 'Email' de la persona de contacte no és un email vàlid"
            ).showAndWait()
            return true
        }
        if (empresaPersonaContacteTextFieldTelefon.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Telèfon' de la persona de contacte no pot estar buit").showAndWait()
            return true
        }
        if (empresaTutorTextFieldNom.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Nom' del/de la tutor/a no pot estar buit").showAndWait()
            return true
        }
        if (!empresaTutorTextFieldNom.text.startsWith("Sr")) {
            Alert(Alert.AlertType.ERROR, "Manca el tractament del camp 'Nom' del/de la tutor/a").showAndWait()
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
        if (!isEmailValid(empresaTutorTextFieldEmail.text)) {
            Alert(Alert.AlertType.ERROR, "El contingut del camp 'Email' del tutor/a no és un email vàlid").showAndWait()
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
        if (!docentTextFieldNom.text.startsWith("Sr")) {
            Alert(Alert.AlertType.ERROR, "Manca el tractament del camp 'Nom' del/la docent").showAndWait()
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
        if (!isEmailValid(docentTextFieldEmail.text)) {
            Alert(
                    Alert.AlertType.ERROR,
                    "El contingut del camp 'Email' del/la docent no és un email vàlid"
            ).showAndWait()
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
        if (!centreTextFieldCodiPostal.text.matches(CODI_POSTAL_REGEXP)) {
            Alert(Alert.AlertType.ERROR, "El camp 'Codi postal' del Centre no és un codi postal vàlid").showAndWait()
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
        if (!centreTextFieldDirector.text.startsWith("Sr")) {
            Alert(Alert.AlertType.ERROR, "Manca el tractament del camp 'Director/a' del Centre").showAndWait()
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
        if (!isEmailValid(centreTextFieldEmail.text)) {
            Alert(
                    Alert.AlertType.ERROR,
                    "El contingut del camp 'Email' de la empresa no és un email vàlid"
            ).showAndWait()
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
            Alert(
                    Alert.AlertType.ERROR,
                    "El camp 'Cap de Servei de Personal Docent' del SSTT no pot estar buit"
            ).showAndWait()
            return true
        }
        if (ssttTextFieldEmailCapServeisPersonalDocent.text.isNullOrEmpty()) {
            Alert(
                    Alert.AlertType.ERROR,
                    "El camp 'Email del Cap de Serveis de Personal' del SSTT no pot estar buit"
            ).showAndWait()
            return true
        }
        if (!isEmailValid(ssttTextFieldEmailCapServeisPersonalDocent.text)) {
            Alert(
                    Alert.AlertType.ERROR,
                    "El contingut del camp 'Email' del Cap de Serveix del Personal Docent no és un email vàlid"
            ).showAndWait()
            return true
        }
        if (ssttTextFieldEmailCapRecursosHumansDireccio.text.isNullOrEmpty()) {
            Alert(
                    Alert.AlertType.ERROR,
                    "El camp 'Email del Cap de Recursos Humans i Direcció' del SSTT no pot estar buit"
            ).showAndWait()
            return true
        }
        if (!isEmailValid(ssttTextFieldEmailCapRecursosHumansDireccio.text)) {
            Alert(
                    Alert.AlertType.ERROR,
                    "El contingut del camp 'Email' del Cap de Recursos Humans i Direcció no és un email vàlid"
            ).showAndWait()
            return true
        }
        if (!(estadaDatePickerDataInici.value.dayOfWeek == DayOfWeek.MONDAY && estadaDatePickerDataInici.value.plusDays(
                        11
                ).isEqual(estadaDatePickerDataFinal.value))
        ) {
            warningNotification(APP_TITLE,
                    "Una estada ha de començar en dilluns i acabar el divendres de la setmana següent",
                    hideAfter = Duration.seconds(1.5), position = Pos.CENTER)
        }
        return false
    }

    private fun cleanScreen() {

        val alert = Alert(Alert.AlertType.CONFIRMATION, "Estas a punt de buidar el formulari?")
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
        if (checkForEmptyOrNull()) return
        val registre = gatherDataFromForm()
        controller.insertEstatDeEstada(
                registre.estada?.numeroEstada!!,
                EstatsSeguimentEstadaEnum.DOCUMENTADA,
                "L'estada ha estat documentada correctament el ${LocalDate.now()}"
        )
    }

    /* This methods adds FINALITZADA state to this estada and sends email */
    private fun doTancada() {
        val registre = gatherDataFromForm()
        controller.insertEstatDeEstada(
                registre.estada?.numeroEstada!!,
                EstatsSeguimentEstadaEnum.TANCADA,
                "L'estada ha estat tancada al GTAF el ${LocalDate.now()}"
        )
    }

    /* Aquest mètode posa admesos_t.baixa a true/false també revisa si hi ha una estada en curs */
    private fun doBaixa(value: Boolean): Unit {
        val dialog = TextInputDialog("NIF")
        dialog.setTitle(APP_TITLE)
        dialog.contentText = "Baixa d'estades"
        dialog
                .showAndWait()
                .ifPresent { nif ->
                    if (nif.isValidDniNie()) {
                        controller.doBaixa(nif, value)
                    } else {
                        Alert(Alert.AlertType.ERROR, "El NIF $nif no és vàlid").show()
                    }
                }
    }

    /* Aquest mètode troba les dades relatives a una estada des d'una sol·licitud pdf */
    private fun loadDataByDocentIdFromPdf(nif: String, tipusEstada: String): Unit {

        if (!nif.isValidDniNie()) {
            errorNotification(APP_TITLE, "El NIF $nif no és vàlid")
            return
        }

        val registre: Registre? = controller.loadDataByDocentIdFromPdf(nif, tipusEstada)

        registre?.apply {
            display(registre)
            if (docent?.nom?.trim()?.isNotBlank()!!) {
                val el = if (registre.docent!!.nom.startsWith("Sr.")) "el" else if (registre.docent!!.nom.startsWith("Sra.")) "la" else "el/la"
                infoNotification(APP_TITLE,
                        "El formulari de $el ${registre.docent?.nom} s'ha carregat correctament.")
            } else {
                warningNotification(APP_TITLE,
                        "El registre amb NIF $nif s'ha carregat parcialment.")
            }
            accordion.expandedPane = titledPaneEstada
            estadaTextFieldNumeroEstada.requestFocus()
        }
    }

    /* This method loads a pdf form choosen from user and displays estada and empresa */
    private fun recarregaPdf(tipusEstada: String) {
        val fileChooser = FileChooser()
        fileChooser.title = "Obre Estada"

        fileChooser.initialDirectory = File(PATH_TO_FORMS + currentCourseYear())
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Estades", "*.pdf"),
                FileChooser.ExtensionFilter("All Files", "*.*")
        )
        val selectedFile = fileChooser.showOpenDialog(this.currentWindow)
        if (selectedFile != null) {
            val estadaEmpresa: Pair<Estada, Empresa>? = controller.parsePdf(selectedFile, tipusEstada)
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
    private fun getRecordFromPdf(tipusEstada: String): Registre? {
        val fileChooser = FileChooser()
        fileChooser.title = "Obre Estada"

        fileChooser.initialDirectory = File(PATH_TO_FORMS + currentCourseYear())
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Estades", "*.pdf"),
                FileChooser.ExtensionFilter("All Files", "*.*")
        )
        val selectedFile = fileChooser.showOpenDialog(this.currentWindow)
        //println(selectedFile.absoluteFile)
        var registre: Registre? = null
        if (selectedFile != null) {
            val estadaEmpresa: Pair<Estada, Empresa>? = controller.parsePdf(selectedFile, tipusEstada)
//            println(estadaEmpresa?.first ?: "estada is null")
//            println(estadaEmpresa?.second ?: "empresa is null")
            registre = controller.getRegistreFromPdf(selectedFile, tipusEstada)
            registre?.estada = estadaEmpresa?.first
            registre?.empresa = estadaEmpresa?.second
        }

        return registre
    }

    fun sendCorreuToColletiuSenseEstada(familia: String) {
        val collectiu = controller.findAllColletiuSenseEstada(familia)
        Alert(Alert.AlertType.CONFIRMATION, "Esteu a punt d'enviar ${collectiu?.size} correus, esteu d'acord?")
                .showAndWait()
                .ifPresent {
                    if (it == ButtonType.OK) {
                        buttonProgressIndicator.isVisible = true
                        buttonProgressIndicator.runAsyncWithProgress {
                            collectiu?.forEach { c ->
                                val BODY = BODY_COLLECTIU
                                        .replace("?1", "${c.tractament} ${c.cognom1},")
                                        .replace("?2", c.familia)
                                        .replace("?3", c.especialitat)
                                        .replace("?4", "${currentCourseYear()}-${nextCourseYear()}")
                                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                        SUBJECT_GENERAL,
                                        BODY,
                                        listOf(),
                                        listOf(CORREU_LOCAL1, c.email)
                                )
                            }
                            buttonProgressIndicator.isVisible = false
                            runLater {
                                infoNotification(APP_TITLE, "S'han enviat ${collectiu?.size} correus correctament")
                            }
                        }
                    }
                }

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
            if (nom.startsWith("Sr.")) {
                docentTextFieldDestinacio.text = destinacioMapSr[destinacio]
            } else if (nom.startsWith("Sra.")) {
                docentTextFieldDestinacio.text = destinacioMapSra[destinacio]
            } else {
                docentTextFieldDestinacio.text = destinacioMap[destinacio]
            }

            docentTextFieldEmail.text = email
            docentTextFieldEspecialitat.text = especialitat
            docentTextFieldTelefon.text = telefon

            if (!(nom.isEmpty() || controller.isDocentAdmes(docent.nif))) {
                val el = if (docent.nom.startsWith("Sr.")) "El" else if (docent.nom.startsWith("Sra.")) "La" else "El/La"
                Alert(Alert.AlertType.CONFIRMATION, "$el $nom no té una estada concedida. Vols enviar un missatge informatiu?")
                        .showAndWait()
                        .ifPresent {
                            if (it == ButtonType.OK) {
                                GesticusMailUserAgent.sendBulkEmailWithAttatchment(
                                        SUBJECT_GENERAL,
                                        BODY_ESTADA_NO_CONCEDIDA
                                                .replace("?1", docent.nom)
                                                .replace("?2", "${currentCourseYear()}-${nextCourseYear()}")
                                        ,
                                        listOf(),
                                        listOf(docent.email)
                                )
                                writeToLog("$el ${docent.nom} ha fet una sol·licitud improcedent")
                                infoNotification(APP_TITLE, "S'ha enviat un correu informatiu a $el ${docent.nom} correctament")
                            }

                        }
            }
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

    private fun barema() = controller.barema()

    companion object {
        val destinacioMap = mapOf<String, String>(
                "CS" to "Comissió de Serveis",
                "DD" to "Destinació Definitiva",
                "IN" to "Interí/na",
                "PP" to "Propietari/a Provisional",
                "PS" to "Propietari/a Suprimit"
        )
        val destinacioMapSr = mapOf<String, String>(
                "CS" to "Comissió de Serveis",
                "DD" to "Destinació Definitiva",
                "IN" to "Interí",
                "PP" to "Propietari Provisional",
                "PS" to "Propietari Suprimit"
        )
        val destinacioMapSra = mapOf<String, String>(
                "CS" to "Comissió de Serveis",
                "DD" to "Destinació Definitiva",
                "IN" to "Interina",
                "PP" to "Propietaria Provisional",
                "PS" to "Propietaria Suprimit"
        )

    }

}
