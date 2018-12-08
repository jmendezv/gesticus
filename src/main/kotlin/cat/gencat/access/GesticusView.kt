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
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.Color
import java.awt.Robot
import java.awt.event.InputEvent
import java.lang.Exception
import java.security.PrivilegedActionException
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

const val APP_TITLE: String = "Gèsticus v. 2.1"

const val pathToHome: String = "D:\\Users\\39164789k\\Desktop\\app_estades\\"

const val MARGIN = 35F
const val FONT_SIZE = 12F
const val FONT_SIZE_FOOT = 10F
const val INTER_LINE = -18F
const val INTER_LINE_FOOT = -15F

const val PDF_OUTPUT_PATH = "D:\\Users\\39164789k\\Desktop\\app_estades\\"

const val USER_NAME = "fpestades@xtec.cat"
const val USER_PASSWORD = "8lMEuDlsEyZUuskwrSeecVKF/1bUDcEk"

const val SECRET_PASSWORD = "secret"

const val PORT_SSL = 465
const val PORT_TLS = 587

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
            createDocentReport()
        }

        buttonBarButtonDesa.setOnAction {
            desa()
        }
    }

    /*
    *
    * TODO("Test")
    * */
    private fun sendEmail(
        subject: String,
        bodyText: String,
        attatchment: String? = null,
        vararg addresses: String
    ): Unit {

        val props = Properties().apply {

            put("mail.debug", "true")
            put("mail.transport.protocol", "smtp")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", PORT_TLS)
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val authenticator = object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(USER_NAME, USER_PASSWORD.decrypt(SECRET_PASSWORD))
            }
        }

        val session = Session.getInstance(props, authenticator)

        val message: MimeMessage = MimeMessage(session)
        message.setFrom(InternetAddress(USER_NAME))
        message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(USER_NAME)
        )
        for (address in addresses) {
            message.setRecipients(
                Message.RecipientType.CC,
                InternetAddress.parse(address)
            )
        }
        message.setSubject(subject);
        val body = MimeBodyPart()
        body.setText(
            "<img src='https://www.vectorlogo.es/wp-content/uploads/2014/12/logo-vector-generalitat-catalunya.jpg'/><i>$bodyText</i>",
            "utf-8",
            "html"
        )
        val multiPart = MimeMultipart()
        multiPart.addBodyPart(body)
        val attachment = MimeBodyPart()
        // Use DataSource with javax 1.3
        // val dataSource = FileDataSource(attatchment)
        // Only javax 1.4+
        attachment.attachFile(attatchment)
        multiPart.addBodyPart(attachment)
        message.setContent(multiPart)
        Transport.send(message)

    }

    /*
    *
    * Informe SSTT
    *
    * */
    private fun createSSTTReport(): Unit {
        val document = PDDocument()
        val catalog = document.documentCatalog
        catalog.language = "cat"
        val documentInfo = document.documentInformation
        documentInfo.author = "Pep Mendez"
        documentInfo.title = "Estada"
        documentInfo.creator = "Creator"
        documentInfo.subject = "Subject"
        documentInfo.creationDate =
                GregorianCalendar(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth)
        documentInfo.keywords = "estades, empresa"
        //val metadata = catalog.metadata
        //val inputStream = metadata.createInputStream()
        val page = PDPage()

        val pageW = page.bBox.width
        val pageH = page.bBox.height

        document.addPage(page)
        val image = PDImageXObject.createFromFile("D:\\Users\\39164789k\\Desktop\\app_estades\\logo_bn.png", document)

        val imageW = image.width.toFloat()
        val imageH = image.height.toFloat()

        val font = PDType1Font.TIMES_ROMAN
        val content: PDPageContentStream = PDPageContentStream(document, page)
        content.drawImage(image, MARGIN, pageH - imageH - MARGIN)
        val registre = gatherDataFromForm()
        content.beginText()
        content.setFont(font, FONT_SIZE)
        content.newLineAtOffset(MARGIN, pageH - imageH - MARGIN * 2)
        content.showText("Benvolgut/da,")
        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("En relació amb les estades formatives de professorat a empreses amb substitució, us trameto les dades i")
        content.newLineAtOffset(0.0f, INTER_LINE)
        content.showText("les dates en què ha estat cocedida.")
        content.newLineAtOffset(0.0f, INTER_LINE - 5)
        content.showText("Us demano que ho tingueu en compte, per tal de poder dur a terme la substitució corresponent.")

        content.newLineAtOffset(20.0F, INTER_LINE * 4)
        content.showText("${registre.docent?.nom} (${registre.docent?.nif})")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText(registre.docent?.telefon)
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText(registre.docent?.email)
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText(registre.docent?.especialitat?.toLowerCase()?.capitalize())
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText(registre.docent?.destinacio)

        content.newLineAtOffset(0.0F, INTER_LINE * 3)
        content.showText("${registre.centre?.nom} (${registre.centre?.codi})")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText(registre.centre?.telefon)
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText(registre.centre?.email)
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText(registre.centre?.municipi)

        content.newLineAtOffset(0.0F, INTER_LINE * 3)
        content.showText("Empresa on farà l'estada: ${registre.empresa?.identificacio?.nom}")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("Persona de contacte: ${registre.empresa?.personaDeContacte?.nom} (${registre.empresa?.personaDeContacte?.telefon})")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("Data d'inici: ${registre.estada?.dataInici}")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("Data de final: ${registre.estada?.dataFinal}")

        content.newLineAtOffset(-20.0F, INTER_LINE * 4)
        content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_FOOT)
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Pep Méndez")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Formació permanent del Professorat d'Ensenyaments Professionals")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Generalitat de Catalunya")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Departament d'Educació")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Direcció General  de Formació Professional Inicial i Ensenyament de Règim Especial")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("T 93 551 69 00 extensió 3218")

        content.endText()
        content.close()
        try {
            val filename = "$PDF_OUTPUT_PATH\\${registre.estada?.numeroEstada?.replace("/", "-")}-sstt.pdf"
            document.save(filename)
            Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, error.message).showAndWait()
        } finally {
            document.close()
        }
    }

    /*
    *
    * Informe Docent
    *
    * */
    private fun createDocentReport(): Unit {

        val document = PDDocument()
        val catalog = document.documentCatalog
        catalog.language = "cat"
        val documentInfo = document.documentInformation
        documentInfo.author = "Pep Mendez"
        documentInfo.title = "Estada"
        documentInfo.creator = "Creator"
        documentInfo.subject = "Subject"
        documentInfo.creationDate =
                GregorianCalendar(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth)
        documentInfo.keywords = "estades, empresa"
        //val metadata = catalog.metadata
        //val inputStream = metadata.createInputStream()
        val page = PDPage()

        val pageW = page.bBox.width
        val pageH = page.bBox.height

        document.addPage(page)
        val image = PDImageXObject.createFromFile("D:\\Users\\39164789k\\Desktop\\app_estades\\logo_bn.png", document)

        val imageW = image.width.toFloat()
        val imageH = image.height.toFloat()

        val font = PDType1Font.TIMES_ROMAN
        val content: PDPageContentStream = PDPageContentStream(document, page)
        content.drawImage(image, MARGIN, pageH - imageH - MARGIN)
        val registre = gatherDataFromForm()
        content.beginText()
        content.setFont(font, FONT_SIZE)
        content.newLineAtOffset(MARGIN, pageH - imageH - MARGIN * 2)
        content.showText("Benvolgut/da,")
        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("Us ha estat concedida l'estada número ${registre.estada?.numeroEstada}. I a tal efecte hem notificat el vostre SSTT")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("amb la següent informació per tal de què gestionin la vostra subsitució:")

        content.newLineAtOffset(20.0F, INTER_LINE * 2)
        content.setFont(PDType1Font.TIMES_BOLD, FONT_SIZE_FOOT)
        content.showText("${registre.docent?.nom} (${registre.docent?.nif})")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText(registre.docent?.telefon)
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText(registre.docent?.email)
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText(registre.docent?.especialitat?.toLowerCase()?.capitalize())
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText(registre.docent?.destinacio)

        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("${registre.centre?.nom} (${registre.centre?.codi})")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText(registre.centre?.telefon)
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText(registre.centre?.email)
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText(registre.centre?.municipi)

        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("Empresa on farà l'estada: ${registre.empresa?.identificacio?.nom}")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Persona de contacte: ${registre.empresa?.personaDeContacte?.nom} (${registre.empresa?.personaDeContacte?.telefon})")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Data d'inici: ${registre.estada?.dataInici}")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Data de final: ${registre.estada?.dataFinal}")

        content.newLineAtOffset(-20.0f, INTER_LINE * 2)
        content.setFont(font, FONT_SIZE)
        content.setNonStrokingColor(Color.RED)
        content.showText("És molt important que comuniqueu qualsevol error al telèfono que trobareu al peu d'aquesta pàgina, i també cal que")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("el vostre Centre comprovi que s'ha produït el nomenament i que reclami la substitució amb la suficient anticipació.")
        // Foot page
        content.newLineAtOffset(0.0F, INTER_LINE * 4)
        content.setNonStrokingColor(Color.BLACK)
        content.showText("Ben cordialment")
        content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_FOOT)
        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("Pep Méndez")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Formació permanent del Professorat d'Ensenyaments Professionals")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Generalitat de Catalunya")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Departament d'Educació")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Direcció General  de Formació Professional Inicial i Ensenyament de Règim Especial")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("T 93 551 69 00 extensió 3218")

        content.endText()
        content.close()

        try {
            val filename = "$PDF_OUTPUT_PATH\\${registre.estada?.numeroEstada?.replace("/", "-")}-docent.pdf"
            document.save(filename)
            Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, error.message).showAndWait()
        } finally {
            document.close()
        }

    }


    /*
    *
    * Carta Director
    *
    * */
    private fun createCartaDirector(): Unit {

        val document = PDDocument()
        val catalog = document.documentCatalog
        catalog.language = "cat"
        val documentInfo = document.documentInformation
        documentInfo.author = "Pep Mendez"
        documentInfo.title = "Estada"
        documentInfo.creator = "Creator"
        documentInfo.subject = "Subject"
        documentInfo.creationDate =
                GregorianCalendar(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth)
        documentInfo.keywords = "estades, empresa"
        //val metadata = catalog.metadata
        //val inputStream = metadata.createInputStream()
        val page = PDPage()

        val pageW = page.bBox.width
        val pageH = page.bBox.height

        document.addPage(page)
        val image = PDImageXObject.createFromFile("D:\\Users\\39164789k\\Desktop\\app_estades\\logo_bn.png", document)

        val imageW = image.width.toFloat()
        val imageH = image.height.toFloat()

        val font = PDType1Font.TIMES_ROMAN
        val content: PDPageContentStream = PDPageContentStream(document, page)
        content.drawImage(image, MARGIN, pageH - imageH - MARGIN)
        val registre = gatherDataFromForm()
        content.beginText()
        content.setFont(font, FONT_SIZE)
        content.newLineAtOffset(MARGIN, pageH - imageH - MARGIN * 2)
        content.showText("Sr./Sra. ${registre.centre?.director},")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("${registre.centre?.nom} (${registre.centre?.codi})")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("${registre.centre?.telefon}")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("${registre.centre?.email}")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("${registre.centre?.municipi}")


        content.newLineAtOffset(0.0f, INTER_LINE * 2)
        content.showText("En relació amb la sol·licitud d'una estada formativa de tipus ${registre.estada?.tipusEstada} de ${registre.docent?.nom}")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("a ${registre.empresa?.identificacio?.nom}  amb seu a ${registre.empresa?.identificacio?.municipi},")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("us comunico que la Direcció General de la Formació Professional Inicial i Ensenyaments de Règim Especial")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("ha resolt autoritzar-la amb el codi d'activitat ${registre.estada?.numeroEstada}.")

        // A
        if (registre.estada?.tipusEstada == "A") {
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Aquesta modalitat d'estada formativa no preveu la substitució del professorat en les seves")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("activitats lectives, això vol dir que ${registre.docent?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("ha d'atendre les seves activitats mentre duri l'estada.")
        }
        // B
        else {
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Aquesta modalitat d'estada formativa preveu la substitució del professorat mentre duri aquesta")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("estada a l’empresa. L’inici estarà condicionat al nomenament i presa de possessió del/de la substitut/a.")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Cal que contacteu amb el vostre Servei Territorial per tot el relacionat amb la substitució.”")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("L'estada formativa no implica cap relació laboral entre ${registre.empresa?.identificacio?.nom} i ${registre.docent?.nom}")
        }

        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("Barcelona a, ${LocalDate.now().toString()}")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("Via Augusta, 202-226")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("Tel. 93 551 69 00")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("http://www.gencat.cat/ensenyament")

        // Foot page
        content.newLineAtOffset(0.0F, INTER_LINE * 4)
        content.setNonStrokingColor(Color.BLACK)
        content.showText("Ben cordialment")
        content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_FOOT)
        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("Pep Méndez")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Formació permanent del Professorat d'Ensenyaments Professionals")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Generalitat de Catalunya")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Departament d'Educació")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Direcció General  de Formació Professional Inicial i Ensenyament de Règim Especial")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("T 93 551 69 00 extensió 3218")

        content.endText()
        content.close()

        try {
            val filename = "$PDF_OUTPUT_PATH\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-director.pdf"
            document.save(filename)
            Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, error.message).showAndWait()
        } finally {
            document.close()
        }

    }

    /*
    *
    * Carta a la empresa
    *
    * TODO("Finish it up")
    *
    * */
    private fun createCartaEmpresa(): Unit {

        val document = PDDocument()
        val catalog = document.documentCatalog
        catalog.language = "cat"
        val documentInfo = document.documentInformation
        documentInfo.author = "Pep Mendez"
        documentInfo.title = "Estada"
        documentInfo.creator = "Creator"
        documentInfo.subject = "Subject"
        documentInfo.creationDate =
                GregorianCalendar(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth)
        documentInfo.keywords = "estades, empresa"
        //val metadata = catalog.metadata
        //val inputStream = metadata.createInputStream()
        val page = PDPage()

        val pageW = page.bBox.width
        val pageH = page.bBox.height

        document.addPage(page)
        val image = PDImageXObject.createFromFile("D:\\Users\\39164789k\\Desktop\\app_estades\\logo_bn.png", document)

        val imageW = image.width.toFloat()
        val imageH = image.height.toFloat()

        val font = PDType1Font.TIMES_ROMAN
        val content: PDPageContentStream = PDPageContentStream(document, page)
        content.drawImage(image, MARGIN, pageH - imageH - MARGIN)
        val registre = gatherDataFromForm()
        content.beginText()
        content.setFont(font, FONT_SIZE)
        content.newLineAtOffset(MARGIN, pageH - imageH - MARGIN * 2)
        content.showText("Benvolgut/da ${registre.empresa?.personaDeContacte?.nom},")
        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("")

        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("Hem rebut una sol·licitud  de ${registre.centre?.director}, director/a del Centre ${registre.centre?.nom}")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("demanant que ${registre.docent?.nom}, professor/a d’aquest Centre,")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("pugui fer una estada de formació a la seva entitat ${registre.empresa?.identificacio?.nom}")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("L’actual model educatiu preveu la col·laboració del sector empresarial i educatiu, per tal d'apropar,")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("cada vegada més, la formació de l’alumnat de cicles formatius a les demandes reals de les empreses i institucions.")
        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("Amb aquest objectiu, i ateses les excel·lents possibilitats de formació que ofereix la vostra entitat us sol·licitem")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("que ${registre.docent?.nom} pugui realitzar aquesta estada, la qual forma part del procés formatiu i està regulada")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("per l'Ordre EDC/458/2005 de 30 de novembre de 2005 i publicada en el DOGC núm. 4525 de 7 de desembre de 2005 i,")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("per tant, no constituiex en cap cas una relació laboral o de serveis entre")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("${registre.empresa?.identificacio?.nom} i ${registre.docent?.nom} professor/a del Departament d’Educació.")

        // Cobertura legal
        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("En relació amb l’assegurança del professorat, us comuniquem que la Generalitat de Catalunya té contractada una")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText(" cobertura pels Departaments, els seus representants, els seus empleats i dependents en l’exercici de les seves")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("funcions o de la seva activitat professional per compte d’aquells, als efectes de garantir les conseqüències")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("econòmiques eventuals derivades de la responsabilitat patrimonial i civil que legalment els hi puguin correspondre.")

        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("La informació relativa a aquesta cobertura d’assegurança la podeu consultar a l’adreça 'http://economia.gencat.cat/',")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("pestanya ‘Àmbits d’actuació’, enllaç ‘Gestió de riscos i assegurances' dins del grup ‘Assegurances’")

        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("Per a qualsevol dubte, podeu posar-vos en contacte amb l’Àrea de Formació de Professorat de Formació Professional,")
        content.newLineAtOffset(0.0F, INTER_LINE)
        content.showText("telèfon 93 551 69 00, extensió 3218).")

        // Foot page
        content.newLineAtOffset(0.0F, INTER_LINE * 4)
        content.setNonStrokingColor(Color.BLACK)
        content.showText("Ben cordialment")
        content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_FOOT)
        content.newLineAtOffset(0.0F, INTER_LINE * 2)
        content.showText("Pep Méndez")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Formació permanent del Professorat d'Ensenyaments Professionals")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Generalitat de Catalunya")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Departament d'Educació")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("Direcció General  de Formació Professional Inicial i Ensenyament de Règim Especial")
        content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
        content.showText("T 93 551 69 00 extensió 3218")

        content.endText()
        content.close()

        try {
            val filename = "$PDF_OUTPUT_PATH\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-empresa.pdf"
            document.save(filename)
            Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
        } catch (error: Exception) {
            Alert(Alert.AlertType.ERROR, error.message).showAndWait()
        } finally {
            document.close()
        }

    }

    private fun desa(): Unit {
        val registre = gatherDataFromForm()
        val estada = registre.estada
        val empresa = registre.empresa
        if (!checkForEmptynessAndCorrectness()) {
            val ret: Boolean = controller.saveEstada(docentTextFieldDni.text, estada!!, empresa!!)
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
            ssttTextFieldEmailCapServeisPersonalDocent.text.trim()
        )
        return Registre(estada, empresa, docent, centre, sstt)
    }

    private fun checkForEmptynessAndCorrectness(): Boolean {
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
        fileChooser.initialDirectory = File(pathToPdfs)
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

}
