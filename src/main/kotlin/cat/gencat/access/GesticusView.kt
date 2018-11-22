package cat.gencat.access

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.io.IOException
import java.io.File
import javafx.stage.FileChooser



class GesticusView : View("Gèsticus v. 2.0") {

    override val root: BorderPane by fxml()

    val controller: GesticusController by inject()

    // Menu Database
    val databaseMenuItemCerca: MenuItem by fxid("database_menuitem_cerca")
    val databaseMenuItemRecarregaPdf: MenuItem by fxid("database_menuitem_recarrega_pdf")
    val databaseMenuItemTanca: MenuItem by fxid("database_menuitem_tanca")
    // Menu Comunicats
    val comunicatsMenuItemCorreuCentre: MenuItem by fxid("comunicats_menuitem_correu_centre")
    val comunicatsMenuItemCorreuEmpresa: MenuItem by fxid("comunicats_menuitem_correu_empresa")
    val comunicatsMenuItemCartaCentre: MenuItem by fxid("comunicats_menuitem_carta_centre")
    val comunicatsMenuItemCartaEmpresa: MenuItem by fxid("comunicats_menuitem_carta_empresa")
    // Menu Eines
    val einesMenuItemPreferencies: MenuItem by fxid("eines_menuitem_preferencies")
    val einesMenuItemLlistat: MenuItem by fxid("eines_menuitem_llistat")
    val einesRadioMenuItemModeExpert: RadioMenuItem by fxid("eines_radiomenuitem_mode_expert")
    // Menu Ajuda
    val ajudaMenuItemUs: MenuItem by fxid("ajuda_menuitem_us")
    val ajudaMenuItemSobreNosaltres: MenuItem by fxid("ajuda_menuitem_sobre_nosaltres")

    // Estada
    val estadaTextFieldNumeroEstada: TextField by fxid("estada_textfield_numero_estada")
    val estadaComboBoxTipusEstada: ComboBox<String> by fxid("estada_combobox_tipus_estada")
    val estadaDatePickerDataInici: DatePicker by fxid("estada_datepicker_data_inici")
    val estadaDatePickerDataFinal: DatePicker by fxid("estada_datepicker_data_final")
    val estadaTextFieldDescripcio: TextField by fxid("estada_textfield_descripcio")
    val estadaTextFieldComentaris: TextField by fxid("estada_textfield_comentaris")

    // Empresa
    val empresaIdentificacioTextFieldNif: TextField by fxid("empresa_identificacio_textfield_nif")
    val empresaIdentificacioTextFieldNom: TextField by fxid("empresa_identificacio_textfield_nom")
    val empresaIdentificacioTextFieldDireccio: TextField by fxid("empresa_identificacio_textfield_direccio")
    val empresaIdentificacioTextFieldCodiPostal: TextField by fxid("empresa_identificacio_textfield_codi_postal")
    val empresaIdentificacioTextFieldMunicipi: TextField by fxid("empresa_identificacio_textfield_municipi")
    val empresaPersonaContacteTextFieldNom: TextField by fxid("empresa_persona_contacte_textfield_nom")
    val empresaPersonaContacteTextFieldCarrec: TextField by fxid("empresa_persona_contacte_textfield_carrec")
    val empresaPersonaContacteTextFieldTelefon: TextField by fxid("empresa_persona_contacte_textfield_telefon")
    val empresaPersonaContacteTextFieldEmail: TextField by fxid("empresa_persona_contacte_textfield_email")
    val empresaTutorTextFieldNom: TextField by fxid("empresa_tutor_textfield_nom")
    val empresaTutorTextFieldCarrec: TextField by fxid("empresa_tutor_textfield_carrec")
    val empresaTutorTextFieldTelefon: TextField by fxid("empresa_tutor_textfield_telefon")
    val empresaTutorTextFieldEmail: TextField by fxid("empresa_tutor_textfield_email")

    // Docent
    val docentTextFieldDni: TextField by fxid("docent_textfield_dni")
    val docentTextFieldNom: TextField by fxid("docent_textfield_nom")
    val docentTextFieldDestinacio: TextField by fxid("docent_textfield_destinacio")
    val docentTextFieldEspecialitat: TextField by fxid("docent_textfield_expecialitat")
    val docentTextFieldEmail: TextField by fxid("docent_textfield_email")
    val docentTextFieldTelefon: TextField by fxid("docent_textfield_telefon")

    // Centre
    val centreTextFieldCodi: TextField by fxid("centre_textfield_codi")
    val centreTextFieldNom: TextField by fxid("centre_textfield_nom")
    val centreTextFieldMunicipi: TextField by fxid("centre_textfield_municipi")
    val centreTextFieldDirector: TextField by fxid("centre_textfield_director")
    val centreTextFieldTelefon: TextField by fxid("centre_textfield_telefon")
    val centreTextFieldEmail: TextField by fxid("centre_textfield_email")

    // SSTT
    val ssttTextFieldCodi: TextField by fxid("sstt_textfield_codi")
    val ssttTextFieldNom: TextField by fxid("sstt_textfield_nom")
    val ssttTextFieldMunicipi: TextField by fxid("sstt_textfield_municipi")
    val ssttTextFieldCoordinador: TextField by fxid("sstt_textfield_coordinador")
    val ssttTextFieldTelefon: TextField by fxid("sstt_textfield_telefon")
    val ssttTextFieldEmail: TextField by fxid("sstt_textfield_email")

    val accordion: Accordion by fxid("accordion")
    val titledPaneEstada: TitledPane by fxid("tp_estada")
    val titledPaneEmpresa: TitledPane by fxid("tp_empresa")
    val titledPaneDocent: TitledPane by fxid("tp_docent")
    val titledPaneCentre: TitledPane by fxid("tp_centre")
    val titledPaneSSTT: TitledPane by fxid("tp_sstt")

    // Toolbar
    val toolbarButtonCerca: Button by fxid("button_cerca")

    // ButtonBar
    val buttonBarButtonDesa: Button by fxid("button_desa")

    init {

        with(root) {
        }

        doSetup()

    } // init ends

    private fun doSetup() {
        controller.preLoadData()

        // Menu Database
        databaseMenuItemCerca.setOnAction { }
        databaseMenuItemRecarregaPdf.setOnAction { recarregaPdf() }
        databaseMenuItemTanca.setOnAction { controller.menuTanca() }

        // Menu Comunicats
        comunicatsMenuItemCorreuCentre.setOnAction { }
        comunicatsMenuItemCorreuEmpresa.setOnAction { }
        comunicatsMenuItemCartaCentre.setOnAction { }
        comunicatsMenuItemCartaEmpresa.setOnAction { }

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
        estadaComboBoxTipusEstada.items.addAll("Tipus A", "Tipus B")
        // Check it should be Monday and set estadaDatePickerDataFinal to second next Friday
        estadaDatePickerDataInici.setOnAction { }
        estadaDatePickerDataFinal.setOnAction { }

        // Docent
        docentTextFieldDni.setOnAction { findDataByDocentId(docentTextFieldDni.text) }

        accordion.expandedPane = titledPaneDocent
        Platform.runLater {
            docentTextFieldDni.requestFocus()
        }
    }

    private fun findDataByDocentId(nif: String): Unit {
        val registre: Registre? = controller.findDataByDocentId(docentTextFieldDni.text)
        display(registre?.estada)
        display(registre?.empresa)
        display(registre?.docent)
        display(registre?.centre)
        display(registre?.sstt)
        if (registre != null) {
            Alert(Alert.AlertType.INFORMATION, "S'ha carregat el/la docent ${registre?.docent?.nom} correctament.").show()
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
        fileChooser.initialDirectory = File(pathToPdfs)
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Estades", "*.pdf"),
                FileChooser.ExtensionFilter("All Files", "*.*"))
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
            ssttTextFieldCoordinador.text = coordinador
            ssttTextFieldTelefon.text = telefon
            ssttTextFieldEmail.text = email
        }
    }

    @Throws(IOException::class)
    fun createPdf(dest: String) {
        //Initialize PDF writer
        val writer = PdfWriter(dest)
        //Initialize PDF document
        val pdf = PdfDocument(writer)
        // Initialize document
        val document = Document(pdf)
        //Add paragraph to the document
        document.add(Paragraph("Hello World!"))
        //Close document
        document.close()
    }
}
