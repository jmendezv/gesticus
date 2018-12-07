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
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFontFactory
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

const val APP_TITLE: String = "Gèsticus v. 2.1"

const val pathToHome: String = "D:\\Users\\39164789k\\Desktop\\app_estades\\"

class GesticusView : View(APP_TITLE) {

    override val root: BorderPane by fxml()

    val controller: GesticusController by inject()

    // Menu Database
    val databaseMenuItemCerca: MenuItem by fxid()
    val databaseMenuItemRecarregaPdf: MenuItem by fxid()
    val databaseMenuItemTanca: MenuItem by fxid()
    // Menu Comunicats
    val comunicatsMenuItemCorreuCentre: MenuItem by fxid()
    val comunicatsMenuItemCorreuEmpresa: MenuItem by fxid()
    val comunicatsMenuItemCartaCentre: MenuItem by fxid()
    val comunicatsMenuItemCartaEmpresa: MenuItem by fxid()
    // Menu Eines
    val einesMenuItemPreferencies: MenuItem by fxid()
    val einesMenuItemLlistat: MenuItem by fxid()
    val einesRadioMenuItemModeExpert: RadioMenuItem by fxid()
    // Menu Ajuda
    val ajudaMenuItemUs: MenuItem by fxid()
    val ajudaMenuItemSobreNosaltres: MenuItem by fxid()

    // Toolbar
    val toolbarButtonCerca: Button by fxid()
    val toolbarButtonNeteja: Button by fxid()
    val toolbarButtonPdf: Button by fxid()

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
            val dialog = TextInputDialog("Número d'estada")
            dialog.setTitle(APP_TITLE);
            val result = dialog.showAndWait();
            if (result.isPresent) {
                val codiEstada = result.get()
                if (codiEstada.matches(codiEstadaFormat)) {
                    val registre: Registre? = controller.findRegistreByCodiEstada(codiEstada)
                    if (registre == null) {
                        Alert(Alert.AlertType.ERROR, "La estada $codiEstada no es troba").showAndWait()
                    } else {
                        with(registre) {
                            display(estada)
                            display(empresa)
                            display(docent)
                            display(centre)
                            display(sstt)
                        }
                    }
                } else {
                    Alert(Alert.AlertType.ERROR, "El format del codi d'estada no és correcte").showAndWait()
                }
            }
        }

        toolbarButtonNeteja.setOnAction {
            val alert = Alert(Alert.AlertType.CONFIRMATION, "N'estàs segur?")
            val result = alert.showAndWait()
            if (result.isPresent) {
                if (result.get() == ButtonType.OK) {
                    cleanScreen()
                }
            }
        }

        toolbarButtonPdf.setOnAction {
            val registre = gatherDataFromForm()
            val document = PDDocument()
            val info = document.documentInformation
            info.author = "Pep Mendez"
            info.title = "Estada"
            info.creator = "Creator"
            info.subject = "Subject"
            info.creationDate = GregorianCalendar(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth)
            info.keywords = "estades, empresa"
            val page = PDPage()
            val logo = PDImageXObject.createFromFile(pathToHome + "logo_bn.png", document);
            val stream = PDPageContentStream(document, page)
            println(page.bBox)
            stream.drawImage(logo, 10.0F, 700.0F)
            stream.beginText()
            stream.newLineAtOffset(10.0F, 150.0F)
            stream.setFont(PDFontFactory.createDefaultFont(), 12.0F)
            stream.setLeading(14.5F)
            stream.showText("First line ${registre.docent?.nom}")
            stream.newLine()
            stream.showText("Second line ${registre.docent?.email}")
            stream.endText()
            stream.close()
            document.addPage(page)
            document.save(pathToHome + "estada.pdf")

            document.close()
        }

        buttonBarButtonDesa.setOnAction {
            val estada = Estada(estadaTextFieldNumeroEstada.text,
                    centreTextFieldCodi.text,
                    estadaComboBoxTipusEstada.value,
                    estadaDatePickerDataInici.value,
                    estadaDatePickerDataFinal.value,
                    estadaTextFieldDescripcio.text,
                    estadaTextFieldComentaris.text)
            val identificacio = Identificacio(empresaIdentificacioTextFieldNif.text,
                    empresaIdentificacioTextFieldNom.text,
                    empresaIdentificacioTextFieldDireccio.text,
                    empresaIdentificacioTextFieldCodiPostal.text,
                    empresaIdentificacioTextFieldMunicipi.text)
            val personaDeContacte = PersonaDeContacte(empresaPersonaContacteTextFieldNom.text,
                    empresaPersonaContacteTextFieldCarrec.text,
                    empresaPersonaContacteTextFieldTelefon.text,
                    empresaPersonaContacteTextFieldEmail.text)
            val tutor = Tutor(empresaTutorTextFieldNom.text,
                    empresaTutorTextFieldCarrec.text,
                    empresaTutorTextFieldTelefon.text,
                    empresaTutorTextFieldEmail.text)
            val empresa = Empresa(identificacio, personaDeContacte, tutor)
            if (!checkForEmptynessAndCorrectness()) {
                val ret: Boolean = controller.saveEstada(docentTextFieldDni.text, estada, empresa)
                if (ret) {
                    cleanScreen()
                }
            }
        }
    }

    private fun gatherDataFromForm(): Registre {
        val estada = Estada(
                "estades_codi_estada",
                "centres_codi_centre",
                "estades_tipus_estada",
                LocalDate.now(),
                LocalDate.now(),
                "estades_descripcio",
                "estades_comentaris")
        val identificacio = Identificacio(
                "estades_nif_empresa",
                "estades_nom_empresa",
                "estades_direccio_empresa",
                "estades_codi_postal",
                "estades_direccio_empresa" )
        val contacte = PersonaDeContacte(
                "estades_contacte_nom",
                "estades_contacte_carrec",
                "estades_contacte_telefon",
                "estades_contacte_email")
                val tutor = Tutor(
                "estades_tutor_nom",
                "estades_tutor_carrec",
                "estades_tutor_telefon",
                "estades_tutor_email")
        val empresa = Empresa(identificacio, contacte, tutor)
        val docent = Docent(
                "estades_nif_professor",
                "professors_nom",
                "professors_destinacio",
                "professors_especialitat",
                "professors_email",
                "professors_telefon")
        val centre = Centre(
                "centres_codi_centre",
                "centres_nom_centre",
                "centres_municipi",
                "directors_nom_director",
                "centres_telefon",
                "centres_email_centre")
        val sstt = SSTT(
                "delegacions_codi_delegacio",
                "delegacions_nom_delegacio",
                "delegacions_municipi",
                "delegacions_cap_de_servei",
                "delegacions_telefon_cap_de_servei",
                "delegacions_email_cap_servei")
        return Registre(estada, empresa, docent, centre, sstt)
    }

    private fun checkForEmptynessAndCorrectness(): Boolean {
        if (estadaTextFieldNumeroEstada.text.trim().isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Número d'estada' no pot estar buit").showAndWait()
            return true
        }
        if (!estadaTextFieldNumeroEstada.text.trim().matches(codiEstadaFormat)) {
            Alert(Alert.AlertType.ERROR, "El format del camp 'Número d'estada' no és vàlid: 0009990600/9999-9999").showAndWait()
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
        if (!(estadaDatePickerDataInici.value.dayOfWeek == DayOfWeek.MONDAY && estadaDatePickerDataInici.value.plusDays(11).isEqual(estadaDatePickerDataFinal.value))) {
            Alert(Alert.AlertType.ERROR, "Una estada ha de començar en dilluns i acabar el divendres de la setmana següent").showAndWait()
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
            Alert(Alert.AlertType.ERROR, "El camp 'Codi' de la empresa no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldNom.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Nom' de la empresa no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldMunicipi.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Municipi' de la empresa no pot estar buit").showAndWait()
            return true
        }
        if (centreTextFieldDirector.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Director/a' de la empresa no pot estar buit").showAndWait()
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
            Alert(Alert.AlertType.ERROR, "El camp 'Cap de Servei' del SSTT no pot estar buit").showAndWait()
            return true
        }
        if (ssttTextFieldEmailCapServeisPersonalDocent.text.isNullOrEmpty()) {
            Alert(Alert.AlertType.ERROR, "El camp 'Email del Cap de Serveis' del SSTT no pot estar buit").showAndWait()
            return true
        }
        return false
    }

    private fun cleanScreen() {
        display(Estada())
        display(SSTT())
        display(Centre())
        display(Docent())
        display(Empresa())
    }

    private fun findDataByDocentId(nif: String): Unit {
        val registre: Registre? = controller.findDataByDocentId(docentTextFieldDni.text)

        if (registre != null) {
            display(registre.estada)
            display(registre.empresa)
            display(registre.docent)
            display(registre.centre)
            display(registre.sstt)
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
            ssttTextFieldEmailCapServeisPersonalDocent.text = email
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
