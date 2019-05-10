package cat.gencat.access.reports

import cat.gencat.access.db.Registre
import cat.gencat.access.functions.*
import cat.gencat.access.functions.Utils.Companion.americanDateTimeFormatter
import cat.gencat.access.functions.Utils.Companion.currentCourseYear
import cat.gencat.access.functions.Utils.Companion.dateTimeFormatter
import cat.gencat.access.functions.Utils.Companion.nextCourseYear
import cat.gencat.access.model.AllEstades
import cat.gencat.access.model.EstadaPendent
import cat.gencat.access.model.Visita
import cat.gencat.access.functions.Utils.Companion.toCatalanDateFormat
import javafx.scene.control.Alert
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.Color
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

const val MARGIN = 35F
const val FONT_SIZE_10 = 10F
const val FONT_SIZE_12 = 12F
const val FONT_SIZE_14 = 14F
const val FONT_SIZE_16 = 16F
const val FONT_SIZE_18 = 18F
const val INTER_LINE = -13F
const val INTER_LINE_FOOT = -11F

const val RESPONSABLE = "Pilar Nus Rey"

//const val RESPONSABLE_EMAIL = "formacioprofessional@gencat.cat"

const val SUBDIRECCIO_LINIA_0 =
        "sub-directora general d'Ordenació de Formació Professional Inicial i Ensenyaments de Règim Especial"

const val SUBDIRECCIO_LINIA_1 = "Sub-directora general d'Ordenació de Formació Professional"
const val SUBDIRECCIO_CASTELLA_LINIA_1 = "Subdirectora General de Ordenación de Formación Profesional"
const val SUBDIRECCIO_ANGLES_LINIA_1 = "Deputy Director for the Curricular Unit of Initial Vocational Studies"
const val SUBDIRECCIO_LINIA_2 = "Inicial i Ensenyaments de Règim Especial"
const val SUBDIRECCIO_CASTELLA_LINIA_2 = "Inicial y Enseñanzas de Régimen Especial"
const val SUBDIRECCIO_ANGLES_LINIA_2 = "(iVET) and Specialised Studies"

const val SUBDIRECCIO_SHORT = "Subdirecció General d'Ordenació de Formació Professional"

const val TECNIC_DOCENT = "Pep Méndez"

const val TECNIC_DOCENT_CARREC_0 =
        "Tècnic docent del Servei de Programes i Projectes de Foment dels Ensenyaments Professionals"
const val TECNIC_DOCENT_CARREC_1 = "Tècnic docent del Servei de Programes i Projectes"
const val TECNIC_DOCENT_CARREC_2 = "de Foment dels Ensenyaments Professionals"

const val LANGUAGE = "CAT"
const val AUTHOR = "Josep Méndez Valverde"
const val TITLE = "Informe Estades Formatives"
const val CREATOR = "Josep Méndez Valverde"
const val SUBJECT = "Estades Formatives"
const val KEYWORDS = "Estades Formacio FP Empresa"

class GesticusReports {

    companion object {

        lateinit var document: PDDocument
        lateinit var content: PDPageContentStream
        lateinit var font: PDType1Font

        var pageH: Float = 0.0F
        var imageH: Float = 0.0F

        /* TODO(" Finish up") */
        val ssttMap = mapOf<String, String>(
                "0308" to "Servei Territorial del Baix Llobregat",
                "0208" to "Servei Territorial de Barcelona Comarques",
                "0608" to "Servei Territorial de Catalunya Central",
                "0117" to "Servei Territorial de Girona",
                "0125" to "Servei Territorial de Lleida",
                "0508" to "Servei Territorial de Maresme - Vallès Oriental",
                "0143" to "Servei Territorial de Tarragona",
                "0243" to "Servei Territorial de Terres de l'Ebre",
                "0408" to "Servei Territorial del Vallès Occidental",
                "0108" to "Consorci d'Educació de Barcelona"
        )


        private fun setupDocumentPDF(): Unit {

            document = PDDocument()
            val catalog = document.documentCatalog
            catalog.language = LANGUAGE
            val documentInfo = document.documentInformation
            documentInfo.author = AUTHOR
            documentInfo.title = TITLE
            documentInfo.creator = CREATOR
            documentInfo.subject = SUBJECT
            documentInfo.creationDate =
                    GregorianCalendar(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth)
            documentInfo.keywords = KEYWORDS
            //val metadata = catalog.metadata
            //val inputStream = metadata.createInputStream()
            val page = PDPage()

            //val pageW = page.bBox.width
            pageH = page.bBox.height

            document.addPage(page)
            val image =
                    PDImageXObject.createFromFile(PATH_TO_LOGO, document)

            //val imageW = image.width.toFloat()
            imageH = image.height.toFloat()

            font = PDType1Font.TIMES_ROMAN
            content = PDPageContentStream(document, page)
            content.drawImage(image, MARGIN, pageH - imageH - MARGIN)

        }

        private fun setFootPageTecnicPDF(content: PDPageContentStream, offset: Int = 5): Unit {

            content.newLineAtOffset(0.0F, INTER_LINE * 2)

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}")
            } else {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}")
            }
            // Foot page
            content.newLineAtOffset(0.0F, INTER_LINE * offset)
            content.setNonStrokingColor(Color.BLACK)
            content.showText("Ben cordialment")
            // content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_10)
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText(TECNIC_DOCENT)
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText(TECNIC_DOCENT_CARREC_1)
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText(TECNIC_DOCENT_CARREC_2)
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Departament d'Educació")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Direcció General  de Formació Professional")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Inicial i Ensenyament de Règim Especial")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("T 93 551 69 00 extensió 3218")

        }

        private fun setFootPageResponsablePDF(content: PDPageContentStream, offset: Int = 5): Unit {

            content.newLineAtOffset(0.0F, INTER_LINE * 2)

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                Companion.content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}")
            } else {
                Companion.content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}")
            }

            // Foot page
            content.newLineAtOffset(0.0F, INTER_LINE * offset)
            content.showText("Atentament,")
            content.newLineAtOffset(0.0F, INTER_LINE * 5)
            content.showText(RESPONSABLE)
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText(SUBDIRECCIO_LINIA_1)
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText(SUBDIRECCIO_LINIA_2)

        }

        private fun setFootPageAddressPDF(content: PDPageContentStream, offset: Int = 5): Unit {
            content.newLineAtOffset(0.0F, INTER_LINE * offset)
            content.setNonStrokingColor(Color.BLACK)
            content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_10)
            content.showText("Via Augusta, 202-226")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("08021 Barcelona")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("Tel. 93 551 69 00")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("http://www.gencat.cat/ensenyament")
        }

        private fun setFootPageResponsableHTML(content: StringBuilder): Unit {

            content.append("<br/>")
            content.append("Atentament,<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")
            content.append("$RESPONSABLE<BR/>")
            content.append("$SUBDIRECCIO_LINIA_1<BR/>")
            content.append("$SUBDIRECCIO_LINIA_2<BR>")

            content.append("<br/>")

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.append("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}")
            } else {
                content.append("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}")
            }
        }

        private fun setFootPageResponsableCastellaHTML(content: StringBuilder): Unit {

            content.append("<br/>")
            content.append("Atentamente,<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")
            content.append("$RESPONSABLE<BR/>")
            content.append("$SUBDIRECCIO_CASTELLA_LINIA_1<BR/>")
            content.append("$SUBDIRECCIO_CASTELLA_LINIA_2<BR>")

            content.append("<br/>")

            content.append("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale("es", "ES")))}")

        }

        private fun setFootPageResponsableAnglesHTML(content: StringBuilder): Unit {

            content.append("<br/>")
            content.append("Yours sincerely,<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")
            content.append("$RESPONSABLE<BR/>")
            content.append("${SUBDIRECCIO_ANGLES_LINIA_1}<BR/>")
            content.append("$SUBDIRECCIO_ANGLES_LINIA_2<BR>")

            content.append("<br/>")

            content.append("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d LLLL yyyy").withLocale(Locale.UK))}")

        }

        /* Informe Docent PDF  */
        fun createCartaDocentPDF(registre: Registre): String? {

            setupDocumentPDF()

            var filename: String? = null

            val docentAmbTractamemt = registre.docent?.nom

            val benvolgut = if (docentAmbTractamemt!!.startsWith("Sr.")) "Benvolgut," else "Benvolguda,"

            val sstt = ssttMap[registre.sstt?.codi]

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN + 30, pageH - imageH - MARGIN * 2)
            content.showText("${benvolgut}")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Us ha estat concedida l'estada número ${registre.estada?.numeroEstada}. I a tal efecte hem notificat el")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${sstt} amb la següent informació per tal de què gestionin la vostra substitució:")

            content.newLineAtOffset(20.0F, INTER_LINE * 2)
            content.setFont(PDType1Font.TIMES_BOLD, FONT_SIZE_10)
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

            // *** U+00A0 ('nbspace') is not available in this font Times-Bold encoding: WinAnsiEncoding

//            val formFont = PDType0Font.load(document, FileInputStream("c:/windows/fonts/somefont.ttf"), false) // check that the font has what you need; ARIALUNI.TTF is good but huge
//            val res = acroForm.getDefaultResources() // could be null, if so, then create it with the setter
//            val fontName = res.add(formFont).getName()
//            val defaultAppearanceString = "/$fontName 0 Tf 0 g" // adjust to replace existing font name
//            textField.setDefaultAppearance(defaultAppearanceString)

            // ***

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Empresa on farà l'estada: ${registre.empresa?.identificacio?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("Persona de contacte: ${registre.empresa?.personaDeContacte?.nom} (${registre.empresa?.personaDeContacte?.telefon})")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("Data d'inici: ${registre.estada?.dataInici}")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("Data de final: ${registre.estada?.dataFinal}")

            content.newLineAtOffset(-20.0f, INTER_LINE * 2)
            content.setFont(font, FONT_SIZE_12)
            content.setNonStrokingColor(Color.RED)
            content.showText("És molt important que comuniqueu qualsevol error al telèfono que trobareu al peu d'aquesta pàgina,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("i també cal que el vostre Centre comprovi que s'ha produït el nomenament i que reclami la substitució")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("amb la suficient anticipació.")
            content.setNonStrokingColor(Color.BLACK)

            // Foot page

            setFootPageTecnicPDF(content, 7)

            content.endText()
            content.close()

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-docent.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaDocentPDF ${error.message}").showAndWait()
            } finally {
                document.close()
            }

            return filename

        }

        /* Informe pel Centre i Docent PDF */
        private fun createCartaCentrePDF(registre: Registre): String? {

            setupDocumentPDF()

            var filename: String? = null

            val docentAmbTractamemt = registre.docent?.nom

            val direAmbTractament = registre.centre?.director

            val benvolgut = if (direAmbTractament!!.startsWith("Sr.")) "Benvolgut,"
            else if (direAmbTractament.startsWith("Sra.")) "Benvolguda,"
            else "Benvolgut/da,"

            val docentSenseTractament = docentAmbTractamemt?.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val elProfessor = if (registre.docent!!.nom.startsWith("Sr.")) "el professor"
            else if (registre.docent!!.nom.startsWith("Sra.")) "la professora"
            else "el/la professor/a"

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN + 30, pageH - imageH - MARGIN * 2)
            content.showText("$direAmbTractament")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.centre?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.centre?.direccio}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.centre?.cp} ${registre.centre?.municipi}")

            content.newLineAtOffset(0.0f, INTER_LINE * 3)
            content.showText(benvolgut)
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("En relació amb la sol·licitud d'una estada formativa de tipus ${registre.estada?.tipusEstada} de $docentSenseTractament")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("a ${registre.empresa?.identificacio?.nom} amb seu a ${registre.empresa?.identificacio?.municipi}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("que es durà a terme entre les dates ${registre.estada?.dataInici?.format(dateTimeFormatter)} i ${registre.estada?.dataFinal?.format(dateTimeFormatter)},")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("us comunico que la Direcció General de Formació Professional Inicial i Ensenyaments de Règim")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Especial ha resolt autoritzar-la amb el codi d'activitat número ${registre.estada?.numeroEstada}.")

            // Estada A
            if (registre.estada?.tipusEstada == "A") {
                content.newLineAtOffset(0.0F, INTER_LINE * 2)
                content.showText("Aquesta modalitat d'estada formativa no preveu la substitució del professorat en les seves")
                content.newLineAtOffset(0.0F, INTER_LINE)
                content.showText("activitats lectives, això vol dir que ${registre.docent?.nom}")
                content.newLineAtOffset(0.0F, INTER_LINE)
                content.showText("ha d'atendre les seves activitats mentre duri l'estada.")
            }
            // Estada B
            else {
                content.newLineAtOffset(0.0F, INTER_LINE * 2)
                content.showText("Aquesta modalitat d'estada formativa preveu la substitució del professorat mentre duri aquesta")
                content.newLineAtOffset(0.0F, INTER_LINE)
                content.showText("estada a l’empresa. L’inici estarà condicionat al nomenament i presa de possessió del/de la substitut/a.")
                content.newLineAtOffset(0.0F, INTER_LINE)
                content.showText("Cal que contacteu amb el vostre Servei Territorial per tot el relacionat amb la substitució.")
                content.newLineAtOffset(0.0F, INTER_LINE * 2)
                content.showText("L'estada formativa no implica cap relació laboral entre la institució i ${elProfessor} que la realitza.")
            }

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació del Professorat")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de Formació Professional (telèfon 935516900, extensió 3218)")

            setFootPageResponsablePDF(content, 2)

            // setFootPageAddressPDF(content, 10)

            content.endText()
            content.close()

            try {
                filename =
                        "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-centre.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCentrePDF ${error.message}").showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        /* Informe pel Centre i Docent PDF */
        private fun createCartaCentrePrivatPDF(registre: Registre): String? {

            setupDocumentPDF()

            var filename: String? = null

            val docentAmbTractamemt = registre.docent?.nom

            val direAmbTractament = registre.centre?.director

            val benvolgut = if (direAmbTractament!!.startsWith("Sr.")) "Benvolgut,"
            else if (direAmbTractament.startsWith("Sra.")) "Benvolguda,"
            else "Benvolgut/da,"

            val docentSenseTractament = docentAmbTractamemt?.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val elProfessor = if (registre.docent!!.nom.startsWith("Sr.")) "el professor"
            else if (registre.docent!!.nom.startsWith("Sra.")) "la professora"
            else "el/la professor/a"

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN + 30, pageH - imageH - MARGIN * 2)
            content.showText("$direAmbTractament")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.centre?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.centre?.direccio}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.centre?.cp} ${registre.centre?.municipi}")

            content.newLineAtOffset(0.0f, INTER_LINE * 3)
            content.showText(benvolgut)
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("En relació amb la sol·licitud d'una estada formativa de tipus ${registre.estada?.tipusEstada} de $docentSenseTractament")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("a ${registre.empresa?.identificacio?.nom} amb seu a ${registre.empresa?.identificacio?.municipi}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("que es durà a terme entre les dates ${registre.estada?.dataInici?.format(dateTimeFormatter)} i ${registre.estada?.dataFinal?.format(dateTimeFormatter)},")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("us comunico que la Direcció General de Formació Professional Inicial i Ensenyaments de Règim")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Especial ha resolt autoritzar-la amb el codi d'activitat número ${registre.estada?.numeroEstada}.")

            // Estada A
            if (registre.estada?.tipusEstada == "A") {
                content.newLineAtOffset(0.0F, INTER_LINE * 2)
                content.showText("Aquesta modalitat d'estada formativa no preveu la substitució del professorat en les seves")
                content.newLineAtOffset(0.0F, INTER_LINE)
                content.showText("activitats lectives, això vol dir que ${registre.docent?.nom}")
                content.newLineAtOffset(0.0F, INTER_LINE)
                content.showText("ha d'atendre les seves activitats mentre duri l'estada.")
            }
            // Estada B
            else {
                content.newLineAtOffset(0.0F, INTER_LINE * 2)
                content.showText("Aquesta modalitat d'estada formativa preveu la substitució del professorat mentre duri aquesta")
                content.newLineAtOffset(0.0F, INTER_LINE)
                content.showText("estada a l’empresa. L’inici estarà condicionat al nomenament i presa de possessió del/de la substitut/a.")
//                content.newLineAtOffset(0.0F, INTER_LINE)
//                content.showText("Cal que contacteu amb el vostre Servei Territorial per tot el relacionat amb la substitució.")
                content.newLineAtOffset(0.0F, INTER_LINE * 2)
                content.showText("L'estada formativa no implica cap relació laboral entre la institució i ${elProfessor} que la realitza.")
            }

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació del Professorat")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de Formació Professional (telèfon 935516900, extensió 3218)")

            setFootPageResponsablePDF(content, 2)

            // setFootPageAddressPDF(content, 10)

            content.endText()
            content.close()

            try {
                filename =
                        "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-centre-privat.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCentrePDF ${error.message}").showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        /* Carta Centre HTML (per signar) i pdf per email */
        fun createCartaCentre(registre: Registre): String? {
            createCartaCentreHTML(registre)
            return createCartaCentrePDF(registre)
        }

        fun createCartaCentrePrivat(registre: Registre): String? {
            createCartaCentrePrivatHTML(registre)
            return createCartaCentrePrivatPDF(registre)
        }

        private fun createCartaEmpresaPDF(registre: Registre): String? {

            setupDocumentPDF()

            var filename: String? = null

            val dire = registre.centre?.director

            val direSenseTractament = dire?.substring(dire.indexOf(" ", 5) + 1)

            val director = if (dire!!.startsWith("Sr.")) "director" else "directora"

            val docent = registre.docent?.nom

            val docentSenseTractament = docent!!.substring(docent.indexOf(" ") + 1)

            val professor = if (docent.startsWith("Sr.")) "professor" else "professora"

            val esmetatProfe = if (docent.startsWith("Sr.")) "l'esmentat professor" else "l'esmentada professora"

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN + 30, pageH - imageH - MARGIN * 3)
            content.showText("${registre.empresa?.identificacio?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("A/A ${registre.empresa?.personaDeContacte?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.identificacio?.direccio}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}")

            content.newLineAtOffset(0.0F, INTER_LINE * 3)
            content.showText("Bon dia,")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Hem rebut una sol·licitud de $direSenseTractament, $director del Centre ${registre.centre?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("demanant que $docentSenseTractament, ${professor} d’aquest Centre, pugui fer")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("una estada de formació a la vostra institució.")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("L’actual model educatiu preveu la col·laboració del sector empresarial i educatiu,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("per tal d'apropar, cada vegada més, la formació de l’alumnat de cicles formatius a")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("les demandes reals de les empreses i institucions.")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Amb aquest objectiu, i ateses les excel·lents possibilitats de formació que ofereix la vostra")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("institució us sol·licitem que ${esmetatProfe} pugui realitzar aquesta estada, la qual forma")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("part del procés formatiu i està regulada per l'Ordre EDC/458/2005 de 30 de novembre de 2005")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("i publicada en el DOGC núm. 4525 de 7 de desembre de 2005 i, per tant, no constituiex en cap cas,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("una relació laboral o de serveis entre ${registre.empresa?.identificacio?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("i ${registre.docent?.nom} ${professor} del Departament d’Educació.")

            // Cobertura legal
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("En relació amb l’assegurança del professorat, us comuniquem que la Generalitat de Catalunya")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("té contractada una cobertura pels Departaments, els seus representants, els seus empleats i")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("dependents en l’exercici de les seves funcions o de la seva activitat professional per compte")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("d’aquells, als efectes de garantir les conseqüències econòmiques eventuals derivades de la")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("responsabilitat patrimonial i civil que legalment els hi puguin correspondre.")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("La informació relativa a aquesta cobertura d’assegurança la podeu consultar a l’adreça")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("'http://economia.gencat.cat/', pestanya ‘Àmbits d’actuació’, enllaç ‘Gestió de riscos i assegurances'")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("dins del grup ‘Assegurances’")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació de Professorat de")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de Formació Professional (telèfon 935516900, extensió 3218).")

            setFootPageResponsablePDF(content, 2)


            // setFootPageTecnicPDF(content)

            content.endText()
            content.close()

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-empresa.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaEmpresaPDF ${error.message}").showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        private fun createCartaEmpresaPrivatPDF(registre: Registre): String? {

            setupDocumentPDF()

            var filename: String? = null

            val dire = registre.centre?.director

            val direSenseTractament = dire?.substring(dire.indexOf(" ", 5) + 1)

            val director = if (dire!!.startsWith("Sr.")) "director" else "directora"

            val docent = registre.docent?.nom

            val docentSenseTractament = docent!!.substring(docent.indexOf(" ") + 1)

            val professor = if (docent.startsWith("Sr.")) "professor" else "professora"

            val esmetatProfe = if (docent.startsWith("Sr.")) "l'esmentat professor" else "l'esmentada professora"

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN + 30, pageH - imageH - MARGIN * 3)
            content.showText("${registre.empresa?.identificacio?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("A/A ${registre.empresa?.personaDeContacte?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.identificacio?.direccio}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}")

            content.newLineAtOffset(0.0F, INTER_LINE * 3)
            content.showText("Bon dia,")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Hem rebut una sol·licitud de $direSenseTractament, $director del Centre ${registre.centre?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("demanant que $docentSenseTractament, ${professor} d’aquest Centre, pugui fer")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("una estada de formació a la vostra institució.")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("L’actual model educatiu preveu la col·laboració del sector empresarial i educatiu,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("per tal d'apropar, cada vegada més, la formació de l’alumnat de cicles formatius a")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("les demandes reals de les empreses i institucions.")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Amb aquest objectiu, i ateses les excel·lents possibilitats de formació que ofereix la vostra")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("institució us sol·licitem que ${esmetatProfe} pugui realitzar aquesta estada, la qual forma")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("part del procés formatiu i està regulada per l'Ordre EDC/458/2005 de 30 de novembre de 2005")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("i publicada en el DOGC núm. 4525 de 7 de desembre de 2005 i, per tant, no constituiex en cap cas,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("una relació laboral o de serveis entre ${registre.empresa?.identificacio?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("i ${registre.docent?.nom} ${professor} del Departament d’Educació.")

            // Cobertura legal
//            content.newLineAtOffset(0.0F, INTER_LINE * 2)
//            content.showText("En relació amb l’assegurança del professorat, us comuniquem que la Generalitat de Catalunya")
//            content.newLineAtOffset(0.0F, INTER_LINE)
//            content.showText("té contractada una cobertura pels Departaments, els seus representants, els seus empleats i")
//            content.newLineAtOffset(0.0F, INTER_LINE)
//            content.showText("dependents en l’exercici de les seves funcions o de la seva activitat professional per compte")
//            content.newLineAtOffset(0.0F, INTER_LINE)
//            content.showText("d’aquells, als efectes de garantir les conseqüències econòmiques eventuals derivades de la")
//            content.newLineAtOffset(0.0F, INTER_LINE)
//            content.showText("responsabilitat patrimonial i civil que legalment els hi puguin correspondre.")
//
//            content.newLineAtOffset(0.0F, INTER_LINE * 2)
//            content.showText("La informació relativa a aquesta cobertura d’assegurança la podeu consultar a l’adreça")
//            content.newLineAtOffset(0.0F, INTER_LINE)
//            content.showText("'http://economia.gencat.cat/', pestanya ‘Àmbits d’actuació’, enllaç ‘Gestió de riscos i assegurances'")
//            content.newLineAtOffset(0.0F, INTER_LINE)
//            content.showText("dins del grup ‘Assegurances’")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació de Professorat de")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de Formació Professional (telèfon 935516900, extensió 3218).")

            setFootPageResponsablePDF(content, 2)


            // setFootPageTecnicPDF(content)

            content.endText()
            content.close()

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-empresa-privat.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaEmpresaPrivadaPDF ${error.message}").showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        fun createCartaEmpresa(registre: Registre): String? {
            createCartaEmpresaHTML(registre)
            return createCartaEmpresaPDF(registre)
        }

        fun createCartaEmpresaPrivat(registre: Registre): String? {
            createCartaEmpresaPrivadaHTML(registre)
            return createCartaEmpresaPrivatPDF(registre)
        }

        fun createCartaAgraiment(registre: Registre): String? {
            createCartaAgraimentHTML(registre)
            return createCartaAgraimentPDF(registre)
        }

        /* Informe SSTT PDF */
        fun createCartaSSTTPDF(registre: Registre): String? {

            var filename: String? = null

            setupDocumentPDF()

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN + 30, pageH - imageH - MARGIN * 2)
            content.showText("Benvolgut/da,")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("En relació amb les estades formatives en empresa amb substitució, del professorat de Formació")
            content.newLineAtOffset(0.0f, INTER_LINE)
            content.showText("Professional, us trameto les dades en què ha estat concedida.")
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


            val dataInici = registre.estada?.dataInici?.format(dateTimeFormatter)
            val dataFinal = registre.estada?.dataFinal?.format(dateTimeFormatter)

            content.newLineAtOffset(0.0F, INTER_LINE * 3)
            content.showText("Empresa on farà l'estada: ${registre.empresa?.identificacio?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Persona de contacte: ${registre.empresa?.personaDeContacte?.nom} (${registre.empresa?.personaDeContacte?.telefon})")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Data d'inici: ${dataInici}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Data de final: ${dataFinal}")

            content.newLineAtOffset(-20.0F, INTER_LINE)
            setFootPageTecnicPDF(content, 7)

            content.endText()
            content.close()
            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-sstt.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaSSTTPDF ${error.message}").showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        /* La carta d'agraïment s'envia un cop ha acabat l'estada al tutor/persona de contacte
        *
        * TODO("Adobe no pot obrir el fitxer")
        *
        * */
        fun createCartaAgraimentPDF(registre: Registre): String? {

            setupDocumentPDF()

            var filename: String? = null

            val docent = registre.docent?.nom

            val docentSenseTractament = docent!!.substring(docent.indexOf(" ") + 1)

            val professor = if (docent.startsWith("Sr.")) "professor" else "professora"

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN, pageH - imageH - MARGIN * 2)
            content.showText("${registre.empresa?.identificacio?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("A/A ${registre.empresa?.personaDeContacte?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.identificacio?.direccio}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}")

            content.newLineAtOffset(0.0f, INTER_LINE * 4)
            content.showText("Bon dia,")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Volem agrair-vos la participació en l'estada de formació que ${docentSenseTractament} ${professor}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("del centre educatiu '${registre.centre?.nom}', de ${registre.centre?.municipi}, ha realitzat a la vostra seu.")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Aquestes accions són de gran importància en l'actual Formació Professional, ja que el contacte directe amb el")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("món laboral, com el que vosaltres heu facilitat, permet actualitzar la formació de base del professorat amb els")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("procediments i tasques que es desenvolupen dia a dia en el món laboral, alhora que possibilita la consolidació")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de la relació del centre amb l'empresa. Tot plegat l’ajudarà a planificar i realitzar la tasca docent d'acord amb")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("els requeriments que les empreses i institucions demanen als seus treballadors actualment.")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Rebeu una cordial salutació,")

            setFootPageResponsablePDF(content, 5)

            content.endText()
            content.close()

            try {
                filename =
                        "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-agraiment.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaAgraimentPDF ${error.message}").showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        /* Aquesta carta s'envia a la persona de contacte de l'empresa sota petició a nom del tutor */
        fun createCartaCertificatTutorPDF(registre: Registre, hores: Int, dniTutor: String): String? {

            setupDocumentPDF()

            var filename: String? = null

            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractament = docentAmbTractamemt?.substring(docentAmbTractamemt.indexOf(" "))

            val professor = if (docentAmbTractamemt!!.startsWith("Sr.")) "professor"
            else if (docentAmbTractamemt.startsWith("Sra.")) "professora"
            else "Sr./Sra."

//            val delLa = if (docentAmbTractamemt!!.startsWith("Sr.")) "del"
//            else if (docentAmbTractamemt.startsWith("Sra.")) "de la"
//            else "del/de la"

            val tutor = registre.empresa?.tutor?.nom
            val elLaTutor = if (tutor!!.startsWith("Sr.")) "el"
            else if (tutor.startsWith("Sra.")) "la"
            else "el/la"


            val numEstada = registre.estada?.numeroEstada
            val pos = numEstada?.indexOf("/", 0) ?: 0
            val anyEscolar = numEstada?.substring(pos + 1, numEstada.length)

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN, pageH - imageH - MARGIN * 2)

            content.newLineAtOffset(0.0F, INTER_LINE * 5)
            content.showText("$RESPONSABLE, sub-directora General d'Ordenació de Formació Professional Inicial i")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Ensenyaments de Règim Especial de la Direcció General de Formació Professional Inicial i")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Ensenyaments de Règim Especial del Departament d'Educació de la Generalitat de Catalunya.")

            content.newLineAtOffset(0.0F, INTER_LINE * 4)
            content.setFont(font, FONT_SIZE_18)
            content.showText("C E R T I F I C O :")

            content.newLineAtOffset(0.0F, INTER_LINE * 3)
            content.setFont(font, FONT_SIZE_12)
            content.showText("Que, segons consta en els nostres arxius, $elLaTutor ${registre.empresa?.tutor?.nom} amb DNI ${dniTutor},")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.tutor?.carrec} de la empresa ${registre.empresa?.identificacio?.nom}, ha realitzat la tutoria d'una estada")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("formativa per a ${docentSenseTractament} ${professor} del Departament d'Educació")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("amb una durada de ${hores} hores, durant el curs escolar ${anyEscolar}.")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("I, perquè així consti, signo el present certificat.")

            setFootPageResponsablePDF(content, 10)

//            content.newLineAtOffset(0.0F, INTER_LINE * 2)
//            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
//                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}")
//            } else {
//                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}")
//            }

            content.endText()
            content.close()

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-tutor.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCertificatTutorPDF ${error.message}").showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        fun setupDocumentHtml(content: java.lang.StringBuilder, title: String): Unit {

            content.append("<!DOCTYPE HTML>")
            content.append("<html>")
            content.append("<head>")
            content.append("<title>Estades Formatives en Empresa: $title</title>")
            content.append("</head>")
            content.append("<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'>")
            content.append("<img src='$PATH_TO_LOGO_HTML'/>")
            content.append("<div style='margin-left:25px; width:95%;' align='justify'>")
            content.append("<br/>")
            content.append("<br/>")

        }

        fun setupDocumentCertificatHtml(content: java.lang.StringBuilder, title: String): Unit {

            content.append("<!DOCTYPE HTML>")
            content.append("<html>")
            content.append("<head>")
            content.append("<title>Estades Formatives en Empresa: $title</title>")
            content.append("</head>")
            content.append("<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'>")
            content.append("<img src='$PATH_TO_LOGO_CERTIFICAT_HTML' width='360' height='80'/>")
            content.append("<div style='margin-left:40px; width:90%;' align='justify'>")
            content.append("<br/>")
            content.append("<br/>")

        }


        /* Aquesta carta s'envia al Centre per correu ordinari signada i amb registre de sortida */
        fun createCartaCentreHTML(registre: Registre): String? {

            var filename: String?

            // dire és Sr. Director ... o Sra. Directora ...
            val dire = registre.centre?.director

            // docentAmbTractament es de la forma Sr. ... o Sra.
            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractamemt = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val benvolgut = if (dire!!.startsWith("Sr.")) "Benvolgut," else "Benvolguda,"

            val elProfessor = if (docentSenseTractamemt.startsWith("Sr.")) "el professor" else "la professora"

            val content = StringBuilder()

            setupDocumentHtml(content, "Carta de Centre")

            content.append("$dire<BR/>")
            content.append("${registre.centre?.nom}<BR/>")
            content.append("${registre.centre?.direccio}<BR/>")
            content.append("${registre.centre?.cp} ${registre.centre?.municipi}<BR/>")

            content.append("<br/>")
            content.append("<br/>")
            content.append("${benvolgut}</br>")

            content.append("<p>En relació amb la sol·licitud d'una estada formativa de tipus ${registre.estada?.tipusEstada} de ${docentSenseTractamemt} a ${registre.empresa?.identificacio?.nom} amb seu a ${registre.empresa?.identificacio?.municipi} que es durà a terme entre les dates ${registre.estada?.dataInici?.format(dateTimeFormatter)} i ${registre.estada?.dataFinal?.format(dateTimeFormatter)}, us comunico que la Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial ha resolt autoritzar-la amb el codi d'activitat número ${registre.estada?.numeroEstada}.</p>")

            // Estada A
            if (registre.estada?.tipusEstada == "A") {
                content.append("<p>Aquesta modalitat d'estada formativa no preveu la substitució del professorat en les seves activitats lectives, això vol dir que ${registre.docent?.nom} ha d'atendre les seves activitats mentre duri l'estada.</p>")
            }
            // Estada B
            else {
                content.append("<p>Aquesta modalitat d'estada formativa preveu la substitució del professorat mentre duri aquesta estada a l’empresa.</p>")
                content.append("<p>L’inici estarà condicionat al nomenament i presa de possessió del/de la substitut/a.</p>")
                content.append("<p>Cal que contacteu amb el vostre Servei Territorial per tot el relacionat amb la substitució.</p>")
                content.append("<p>L'estada formativa no implica cap relació laboral entre la institució i $elProfessor que la realitza.</p>")
            }

            content.append("<p>Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació del Professorat de Formació Professional (telèfon 935516900, extensió 3218).</p>")

            setFootPageResponsableHTML(content)

            content.append("</div>")
            content.append("</body>")
            content.append("</hmtl>")

            try {
                filename =
                        "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-centre.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
                return filename
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCentreHTML ${error.message}").showAndWait()
            } finally {
            }
            return null
        }

        /* Aquesta carta s'envia al Centre privat per correu ordinari signada i amb registre de sortida */
        fun createCartaCentrePrivatHTML(registre: Registre): String? {

            var filename: String?

            // dire és Sr. Director ... o Sra. Directora ...
            val dire = registre.centre?.director

            // docentAmbTractament es de la forma Sr. ... o Sra.
            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractamemt = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val benvolgut = if (dire!!.startsWith("Sr.")) "Benvolgut," else "Benvolguda,"

            val elProfessor = if (docentSenseTractamemt.startsWith("Sr.")) "el professor" else "la professora"

            val content = StringBuilder()

            setupDocumentHtml(content, "Carta de Centre")

            content.append("$dire<BR/>")
            content.append("${registre.centre?.nom}<BR/>")
            content.append("${registre.centre?.direccio}<BR/>")
            content.append("${registre.centre?.cp} ${registre.centre?.municipi}<BR/>")

            content.append("<br/>")
            content.append("<br/>")
            content.append("${benvolgut}</br>")

            content.append("<p>En relació amb la sol·licitud d'una estada formativa de tipus ${registre.estada?.tipusEstada} de ${docentSenseTractamemt} a ${registre.empresa?.identificacio?.nom} amb seu a ${registre.empresa?.identificacio?.municipi} que es durà a terme entre les dates ${registre.estada?.dataInici?.format(dateTimeFormatter)} i ${registre.estada?.dataFinal?.format(dateTimeFormatter)}, us comunico que la Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial ha resolt autoritzar-la amb el codi d'activitat número ${registre.estada?.numeroEstada}.</p>")

            // Estada A
            if (registre.estada?.tipusEstada == "A") {
                content.append("<p>Aquesta modalitat d'estada formativa no preveu la substitució del professorat en les seves activitats lectives, això vol dir que ${registre.docent?.nom} ha d'atendre les seves activitats mentre duri l'estada.</p>")
            }
            // Estada B
            else {
                content.append("<p>Aquesta modalitat d'estada formativa preveu la substitució del professorat mentre duri aquesta estada a l’empresa.</p>")
                content.append("<p>L’inici estarà condicionat al nomenament i presa de possessió del/de la substitut/a.</p>")
                //content.append("<p>Cal que contacteu amb el vostre Servei Territorial per tot el relacionat amb la substitució.</p>")
                content.append("<p>L'estada formativa no implica cap relació laboral entre la institució i $elProfessor que la realitza.</p>")
            }

            content.append("<p>Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació del Professorat de Formació Professional (telèfon 935516900, extensió 3218).</p>")

            setFootPageResponsableHTML(content)

            content.append("</div>")
            content.append("</body>")
            content.append("</hmtl>")

            try {
                filename =
                        "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-centre-privat.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
                return filename
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCentreHTML ${error.message}").showAndWait()
            } finally {
            }
            return null
        }

        /* Create carta empresa HTML (per signar) */
        fun createCartaEmpresaHTML(registre: Registre): String? {

            var filename: String?

            val dire = registre.centre?.director

            val direSenseTractament = dire?.substring(dire.indexOf(" ", 5) + 1)

            val director = if (dire!!.startsWith("Sr.")) "director" else "directora"

            // docentAmbTractament es de la forma Sr. ... o Sra.
            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractament = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val empresari = registre.empresa?.personaDeContacte?.nom!!

            val benvolgut = if (empresari.startsWith("Sra.")) "Bonvolguda,"
            else if (empresari.startsWith("Sr.")) "Benvolgut,"
            else "Bon dia,"


            val content: StringBuilder = StringBuilder()

            setupDocumentHtml(content, "Carta d'Empresa")

            //content.append("<br/>")
            content.append("${registre.empresa?.identificacio?.nom}<BR/>")
            content.append("A/A ${registre.empresa?.personaDeContacte?.nom}<BR/>")
            content.append("${registre.empresa?.identificacio?.direccio}<BR/>")
            content.append("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}<BR/>")
            content.append("<br/>")

            val professor = if (docentAmbTractamemt.startsWith("Sr.")) "professor" else "professora"

            val esmetatProfe =
                    if (docentAmbTractamemt.startsWith("Sr.")) "l'esmentat professor" else "l'esmentada professora"

            content.append("$benvolgut")
            //content.append("<br/>")
            content.append("<p>Hem rebut una sol·licitud de ${direSenseTractament}, ${director} del centre '${registre.centre?.nom}' demanant que ${docentSenseTractament}, ${professor} d’aquest centre, pugui fer una estada de formació a la vostra institució.</p>")
            content.append("<p>L’actual model educatiu preveu la col·laboració del sector empresarial i educatiu, per tal d'apropar, cada vegada més, la formació de l’alumnat de cicles formatius a les demandes reals de les empreses i institucions.</p>")
            content.append("<p>Amb aquest objectiu, i ateses les excel·lents possibilitats de formació que ofereix la vostra institució, us sol·licitem que ${esmetatProfe} pugui realitzar aquesta estada, la qual forma part del procés formatiu i està regulada per l'Ordre EDC/458/2005 de 30 de novembre de 2005 i publicada en el DOGC núm. 4525 de 7 de desembre de 2005 i, per tant, no constituiex en cap cas, una relació laboral o de serveis entre l'entitat '${registre.empresa?.identificacio?.nom}' i ${docentSenseTractament}, ${professor} del Departament d’Educació.</p>")

            // Cobertura legal
            content.append("<p>En relació amb l’assegurança del professorat, us comuniquem que la Generalitat de Catalunya té contractada una cobertura pels Departaments, els seus representants, els seus empleats i dependents en l’exercici de les seves funcions o de la seva activitat professional per compte d’aquells, als efectes de garantir les conseqüències econòmiques eventuals derivades de la responsabilitat patrimonial i civil que legalment els hi puguin correspondre.</p>")

            //content.append("<br/>")
            content.append("<p>La informació relativa a aquesta cobertura d’assegurança la podeu consultar a l’adreça 'http://economia.gencat.cat/', pestanya ‘Àmbits d’actuació’, enllaç ‘Gestió de riscos i assegurances' dins del grup ‘Assegurances’.")

            // Closure
            content.append("<p>Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació de Professorat de Formació Professional (telèfon 935516900, extensió 3218).</p>")

            // Foot page
            setFootPageResponsableHTML(content)

            content.append("</div>")
            content.append("</body>")
            content.append("</html>")

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-empresa.html"

                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
                return filename
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            }

            return null
        }

        /* Create carta empresa HTML (per signar) */
        fun createCartaEmpresaPrivadaHTML(registre: Registre): String? {

            var filename: String?

            val dire = registre.centre?.director

            val direSenseTractament = dire?.substring(dire.indexOf(" ", 5) + 1)

            val director = if (dire!!.startsWith("Sr.")) "director" else "directora"

            // docentAmbTractament es de la forma Sr. ... o Sra.
            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractament = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val empresari = registre.empresa?.personaDeContacte?.nom!!

            val benvolgut = if (empresari.startsWith("Sra.")) "Bonvolguda,"
            else if (empresari.startsWith("Sr.")) "Benvolgut,"
            else "Bon dia,"


            val content: StringBuilder = StringBuilder()

            setupDocumentHtml(content, "Carta d'Empresa")

            //content.append("<br/>")
            content.append("${registre.empresa?.identificacio?.nom}<BR/>")
            content.append("A/A ${registre.empresa?.personaDeContacte?.nom}<BR/>")
            content.append("${registre.empresa?.identificacio?.direccio}<BR/>")
            content.append("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}<BR/>")
            content.append("<br/>")

            val professor = if (docentAmbTractamemt.startsWith("Sr.")) "professor" else "professora"

            val esmetatProfe =
                    if (docentAmbTractamemt.startsWith("Sr.")) "l'esmentat professor" else "l'esmentada professora"

            content.append("$benvolgut")
            //content.append("<br/>")
            content.append("<p>Hem rebut una sol·licitud de ${direSenseTractament}, ${director} del centre '${registre.centre?.nom}' demanant que ${docentSenseTractament}, ${professor} d’aquest centre, pugui fer una estada de formació a la vostra institució.</p>")
            content.append("<p>L’actual model educatiu preveu la col·laboració del sector empresarial i educatiu, per tal d'apropar, cada vegada més, la formació de l’alumnat de cicles formatius a les demandes reals de les empreses i institucions.</p>")
            content.append("<p>Amb aquest objectiu, i ateses les excel·lents possibilitats de formació que ofereix la vostra institució, us sol·licitem que ${esmetatProfe} pugui realitzar aquesta estada, la qual forma part del procés formatiu i està regulada per l'Ordre EDC/458/2005 de 30 de novembre de 2005 i publicada en el DOGC núm. 4525 de 7 de desembre de 2005 i, per tant, no constituiex en cap cas, una relació laboral o de serveis entre l'entitat '${registre.empresa?.identificacio?.nom}' i ${docentSenseTractament}, ${professor} del Departament d’Educació.</p>")

            // Cobertura legal
            //content.append("<p>En relació amb l’assegurança del professorat, us comuniquem que la Generalitat de Catalunya té contractada una cobertura pels Departaments, els seus representants, els seus empleats i dependents en l’exercici de les seves funcions o de la seva activitat professional per compte d’aquells, als efectes de garantir les conseqüències econòmiques eventuals derivades de la responsabilitat patrimonial i civil que legalment els hi puguin correspondre.</p>")

            //content.append("<br/>")
            //content.append("<p>La informació relativa a aquesta cobertura d’assegurança la podeu consultar a l’adreça 'http://economia.gencat.cat/', pestanya ‘Àmbits d’actuació’, enllaç ‘Gestió de riscos i assegurances' dins del grup ‘Assegurances’.")

            // Closure
            content.append("<p>Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació de Professorat de Formació Professional (telèfon 935516900, extensió 3218).</p>")

            // Foot page
            setFootPageResponsableHTML(content)

            content.append("</div>")
            content.append("</body>")
            content.append("</html>")

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-empresa-privat.html"

                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
                return filename
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            }

            return null
        }

        /* Create carta empresa HTML (per signar) */
        fun createCartaEmpresaCastellaHTML(registre: Registre): String? {

            var filename: String?

            val dire = registre.centre?.director

            val direSenseTractament = dire?.substring(dire.indexOf(" ", 5) + 1)

            val director = if (dire!!.startsWith("Sr.")) "director" else "directora"

            // docentAmbTractament es de la forma Sr. ... o Sra.
            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractament = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val empresari = registre.empresa?.personaDeContacte?.nom!!

            val benvolgut = if (empresari.startsWith("Sra.")) "Señora,"
            else if (empresari.startsWith("Sr.")) "Señor,"
            else "Buenos dias,"


            val content: StringBuilder = StringBuilder()

            setupDocumentHtml(content, "Carta d'Empresa")

            //content.append("<br/>")
            content.append("${registre.empresa?.identificacio?.nom}<BR/>")
            content.append("A/A ${registre.empresa?.personaDeContacte?.nom}<BR/>")
            content.append("${registre.empresa?.identificacio?.direccio}<BR/>")
            content.append("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}<BR/>")
            content.append("<br/>")

            val professor = if (docentAmbTractamemt.startsWith("Sr.")) "profesor" else "profesora"

            val esmetatProfe =
                    if (docentAmbTractamemt.startsWith("Sr.")) "el mencionado profesor" else "la mencionada profesora"

            content.append("$benvolgut")
            //content.append("<br/>")
            content.append("<p>Hemos recibido una solicitud de ${direSenseTractament}, ${director} del centro '${registre.centre?.nom}' pidiento que ${docentSenseTractament}, ${professor} de este centro, pueda hacer una estancia de formación en su institución.</p>")
            content.append("<p>El actual modelo educativo preve la colaboración del sector empresarial y educativo, con el fin de acercar, cada vez más, la formación del alumnado de ciclos formativos a los requerimientos reales de las empresas e instituciones.</p>")
            content.append("<p>Con este objetivo, y considerando las excelente posibilidades de formación que ofrece su institución, le solicitamos que ${esmetatProfe} pueda realizar esta estancia, la cual forma parte del proceso formativo y está regulada per la Orden EDC/458/2005 de 30 de noviembre de 2005 y publicada en el DOGC núm. 4525 del 7 de diciembre de 2005 y, por lo tanto, no constituye en ningún caso, una relación laboral o de servicio entre la entidad '${registre.empresa?.identificacio?.nom}' y ${docentSenseTractament}, ${professor} del Departamento de Educación.</p>")

            // Cobertura legal
            content.append("<p>En relación con el seguro del profesorado, le comunicamos que la Generalitat de Catalunya tiene contratada una cobertura para los Departamentos, sus representantes, sus empleados y dependientes en el ejercicio de sus funciones o de su actividad profesional por cuenta de aquellos, a efectos de garantizar las consecuéncias económicas eventuales que pudieran derivarse de la responsabilidad patrimonial y civil que legalmente les corresponda.</p>")

            //content.append("<br/>")
            content.append("<p>La información relativa a esta cobertura del seguro la puede consultar en la dirección ‘http://economia.gencat.cat/ca/ambits-actuacio/assegurances/gestio_de_riscos_i_assegurances/’,")

            // Closure
            content.append("<p>Para resolver cualquier duda, pueden ponerse en contacto con el Área de Formación de Profesorado de Formación Professional (teléfono 935516900, extensión 3218).</p>")

            // Foot page
            setFootPageResponsableCastellaHTML(content)

            content.append("</div>")
            content.append("</body>")
            content.append("</html>")

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-empresa-castella.html"

                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
                return filename
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            }

            return null
        }

        /* Create carta empresa HTML (per signar) */
        fun createCartaEmpresaAnglesHTML(registre: Registre): String? {

            var filename: String?

            val dire = registre.centre?.director

            val direSenseTractament = dire?.substring(dire.indexOf(" ", 5) + 1)

//            val director = if (dire!!.startsWith("Sr.")) "director" else "directora"

            // docentAmbTractament es de la forma Sr. ... o Sra.
            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractament = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

//            val empresari = registre.empresa?.personaDeContacte?.nom!!

            val benvolgut = "Dear ${registre.empresa?.personaDeContacte?.nom},"


            val content: StringBuilder = StringBuilder()

            setupDocumentHtml(content, "Carta d'Empresa")

            //content.append("<br/>")
            content.append("${registre.empresa?.identificacio?.nom}<BR/>")
            content.append("${registre.empresa?.personaDeContacte?.nom}<BR/>")
            content.append("${registre.empresa?.identificacio?.direccio}<BR/>")
            content.append("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}<BR/>")
            content.append("<br/>")

//            val professor = if (docentAmbTractamemt.startsWith("Sr.")) "professor" else "professora"

//            val esmetatProfe =
//                    if (docentAmbTractamemt.startsWith("Sr.")) "l'esmentat professor" else "l'esmentada professora"

            content.append("$benvolgut")
            //content.append("<br/>")
            content.append("<p>We have received a request from ${direSenseTractament}, principal of '${registre.centre?.nom}' asking for ${docentSenseTractament}, teacher of this centre, to join your institution during a two-week training activity.</p>")
            content.append("<p>The current education model welcomes the collaboration between both sectors, educational and corporate, to reduce as much as possible the gap between the curriculum and the real skills required by companies today.</p>")
            content.append("<p>For this reason, and according to the excellent conditions offered by your institution, we respectfully request that the above-mentioned, be allowed to attend the ‘${registre.empresa?.identificacio?.nom}’ for a training period as is regulated by the Ordre EDC/458/2005 dated 30th of November 2005 and published in the DOGC number 4525 the 7th of December 2005, and as such does not imply, in any case, a work relationship between the ‘${registre.empresa?.identificacio?.nom}’ and ${docentSenseTractament}, of the Catalan Department of Education.</p>")

            // Cobertura legal
            content.append("<p>Regarding the insurance of our personnel, the Catalan Government has a contract that offers coverage to all Departments, its representatives, employees and dependants during the exercise of their duty or professional activities, to cover the possible financial implications derived from patrimonial and civil responsibilities that could legally be applied.</p>")

            //content.append("<br/>")
            content.append("<p>All information related to this insurance coverage can be found at this address: ‘http://economia.gencat.cat/ca/ambits-actuacio/assegurances/gestio_de_riscos_i_assegurances/’.")

            // Closure
            content.append("<p>Should you have any questions, do not hesitate to contact us, either by email at fpestades@xtec.cat or during our office hours at the ‘Vocational Training Teacher Development Program Area’ (phone +34935516900, extension 3218).</p>")

            // Foot page
            setFootPageResponsableAnglesHTML(content)

            content.append("</div>")
            content.append("</body>")
            content.append("</html>")

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-empresa-angles.html"

                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
                return filename
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            }

            return null
        }

        /* Un cop acabada l'estada s'envia una carta d'agraïemnt a l'empresa */
        fun createCartaAgraimentHTML(registre: Registre): String? {

            var filename: String? = null

            val docent = registre.docent?.nom

            val professor = if (docent!!.startsWith("Sr.")) "professor" else "professora"

            val el = if (docent.startsWith("Sr.")) "el" else "la"

            val empresari = registre.empresa?.personaDeContacte?.nom!!

            val benvolgut = if (empresari.startsWith("Sra.")) "Bonvolguda,"
            else if (empresari.startsWith("Sr.")) "Benvolgut,"
            else "Bon dia,"

            val content: StringBuilder = StringBuilder()

            setupDocumentHtml(content, "Carta d'Agraïment")
            content.append("<br/>")

            content.append("${registre.empresa?.identificacio?.nom}<BR/>")
            content.append("A/A ${registre.empresa?.personaDeContacte?.nom}<BR/>")
            content.append("${registre.empresa?.identificacio?.direccio}<BR/>")
            content.append("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}")

            content.append("<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")

            content.append("$benvolgut<BR/>")
            content.append("<p>Volem  agrair-vos  la  participació  en  l'estada  de  formació  que $el ${registre.docent?.nom} ${professor} del centre educatiu '${registre.centre?.nom}', de ${registre.centre?.municipi}, ha realitzat a la vostra seu durant el curs ${currentCourseYear()}-${nextCourseYear()}.</p>")
            content.append("<p>Aquestes accions són de gran importància en l'actual Formació Professional, ja que el contacte directe amb el món laboral, com el que vosaltres heu facilitat, permet actualitzar la formació de base del professorat amb els procediments i tasques que es desenvolupen dia a dia en el món laboral, alhora que possibilita la consolidació de la relació del centre amb l'empresa. Tot plegat ha de servir per a planificar i realitzar la tasca docent d'acord amb els requeriments que les empreses i institucions demanen als seus treballadors actualment.</p>")
            content.append("Rebeu una cordial salutació,</BR>")

            content.append("<BR/>")

            setFootPageResponsableHTML(content)

            content.append("</div>")
            content.append("</body>")
            content.append("</html")

            try {
                filename =
                        "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-agraiment.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaEmpresaHTML ${error.message}").showAndWait()
            } finally {
            }

            return filename
        }

        /* Un cop acabada l'estada s'envia una carta d'agraïemnt a l'empresa */
        fun createCartaAgraimentCastellaHTML(registre: Registre): String? {

            var filename: String? = null

            val docent = registre.docent?.nom

            val professor = if (docent!!.startsWith("Sr.")) "profesor" else "profesora"

            val el = if (docent.startsWith("Sr.")) "el" else "la"

            val empresari = registre.empresa?.personaDeContacte?.nom!!

            val benvolgut = if (empresari.startsWith("Sra.")) "Señora,"
            else if (empresari.startsWith("Sr.")) "Señor,"
            else "Buenos dias,"

            val content: StringBuilder = StringBuilder()

            setupDocumentHtml(content, "Carta d'Agraïment")
            content.append("<br/>")

            content.append("${registre.empresa?.identificacio?.nom}<BR/>")
            content.append("A/A ${registre.empresa?.personaDeContacte?.nom}<BR/>")
            content.append("${registre.empresa?.identificacio?.direccio}<BR/>")
            content.append("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}")

            content.append("<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")

            content.append("$benvolgut<BR/>")
            content.append("<p>Queremos agradecerles la participación en la estancia de formación que $el ${registre.docent?.nom} ${professor} del centro educativo '${registre.centre?.nom}', de ${registre.centre?.municipi}, ha realizado en su entidad durante el curso ${currentCourseYear()}-${nextCourseYear()}.</p>")
            content.append("<p>Estas acciones son de gran importancia en el actual modelo de Formación Profesional, ya que el contacto directo con el mundo laboral, como el que Vds. han facilitado, permite actualizar la formación de base del profesorado con los procedimientos y tareas que se desarrollan día a día en el mundo laboral, a la vez que posibilita la consolidación de las relaciones del centro con la empresa. Todo ello, ha de servir para planificar y realizar la tarea docente de acuerdo con los requerimientos que las empresas e instituciones demandan a sus trebajadores actualmente.</p>")
            content.append("Reciba un cordial saludo,</BR>")

            content.append("<BR/>")

            setFootPageResponsableCastellaHTML(content)

            content.append("</div>")
            content.append("</body>")
            content.append("</html")

            try {
                filename =
                        "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-agraiment-castella.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaEmpresaHTML ${error.message}").showAndWait()
            } finally {
            }

            return filename
        }

        /* Un cop acabada l'estada s'envia una carta d'agraïemnt a l'empresa */
        fun createCartaAgraimentAnglesHTML(registre: Registre): String? {

            var filename: String? = null

            val docent = registre.docent?.nom

//            var professor = if (docent!!.startsWith("Sr.")) "professor" else "professor"

//            val el = if (docent!!.startsWith("Sr.")) "el" else "la"

            val professorSenseTractament = docent?.substring(docent.indexOf(" ") + 1)

            var empresari = registre.empresa?.personaDeContacte?.nom!!

            val benvolgut = if (empresari.startsWith("Sr.")) "Dear Sir,"
            else if (empresari.startsWith("Sra.")) "Dear Madam,"
            else "Dear,"

            empresari = empresari.replace("Sr.", "Mr.")
            empresari = empresari.replace("Sra.", "Ms.")

            val content: StringBuilder = StringBuilder()

            setupDocumentHtml(content, "Carta d'Agraïment")
            content.append("<br/>")

            content.append("${registre.empresa?.identificacio?.nom}<BR/>")
            content.append("${empresari}<BR/>")
            content.append("${registre.empresa?.identificacio?.direccio}<BR/>")
            content.append("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}")

            content.append("<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")
            content.append("<BR/>")

            content.append("$benvolgut<BR/>")
            content.append("<p>We want to express our most sincere gratitude for hosting ${professorSenseTractament} secondary school teacher at '${registre.centre?.nom}' from ${registre.centre?.municipi}, in your institution during the academic year ${currentCourseYear()}-${nextCourseYear()}.</p>")
            content.append("<p>These actions are of great importance in the current Vocational Training model, since direct contact with the working world such as the one that you have provided, allows us to update the basic training of the teaching staff, while enabling the consolidation of relations between educational institutions and companies.</p>")
            content.append("<p>This innovative experience will contribute to improve the planning and the teaching according to the requirements that companies and institutions require from their staff, bearing in mind the increasing demands of flexibility and adaptability that the rapidly changing world of work is placing on people today.</p>")
            content.append("<p>Let us thank you again for your collaboration in this educational stay,</p>")

            content.append("<BR/>")

            setFootPageResponsableAnglesHTML(content)

            content.append("</div>")
            content.append("</body>")
            content.append("</html")

            try {
                filename =
                        "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-agraiment-angles.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaEmpresaHTML ${error.message}").showAndWait()
            } finally {
            }

            return filename
        }

        /*
        * Aquesta carta la signa la responsable i es lliura a nom de la persona de contacte de la empresa
        *
        * <!DOCTYPE HTML><html><head><title>Estades Formatives en Empresa: Carta de certificació d tutor/a</title></head><body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'><img src='file:///H:/Mendez/gesticusv2/logos/logo_bn.jpg'/><div style='margin-left:25px; width:95%; font-family:courier; line-height: 1.6;' align='justify'><br/><br/><br/><p>Pilar Nus Rey, sub-directora General d'Ordenació de Formació Professional Inicial i d'Ensenyaments de Règim Especial de la Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial del Departament d'Educació de la Generalitat de Catalunya.</p><br/><p><h3>C E R T I  F I C O:</h3></p><br/><p>Que, segons consta en els nostres arxius, José Luis Criado amb DNI 39164789K, Gerent de la empresa Promo Raids Trading, S.L., ha realitzat la tutoria d'una estada formativa per al professorat del Departament d'Educació amb una durada de 20 hores, durant el curs escolar 2018-2019.</p><br/><p>I, perquè així consti, signo el present certificat.</p><br/><p >Barcelona, 24 de gener de 2019</p></div></body</html>
        *
        * */
        fun createCartaCertificatTutorHTML(registre: Registre, hores: Int, dniTutor: String): String? {

            var filename: String? = null

            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractament = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val numEstada = registre.estada?.numeroEstada

            val pos = numEstada?.indexOf("/", 0) ?: 0

            val cursEscolar = numEstada?.substring(pos + 1, numEstada.length)

            val professor = if (docentAmbTractamemt.startsWith("Sr.")) "professor"
            else if (docentAmbTractamemt.startsWith("Sra.")) "professora"
            else "Sr./Sra."

            val elLa = if (docentAmbTractamemt.startsWith("Sr.")) "el"
            else if (docentAmbTractamemt.startsWith("Sra.")) "la"
            else "el/la"

            val content: StringBuilder = StringBuilder()

            setupDocumentCertificatHtml(content, "Carta de certificació d tutor/a")

            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11px; line-height: 1.6;'>$RESPONSABLE, sub-directora General d'Ordenació de Formació Professional Inicial i Ensenyaments de Règim Especial de la Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial del Departament d'Educació de la Generalitat de Catalunya.</p>")
            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11px; line-height: 1.6;'><h3>C E R T I F I C O :</h3></p>")
            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11; line-height: 1.6;'>Que, segons consta en els nostres arxius, $elLa ${registre.empresa?.tutor?.nom} amb DNI ${dniTutor}, ${registre.empresa?.tutor?.carrec} de ${registre.empresa?.identificacio?.nom}, ha realitzat la tutoria d'una estada formativa per a ${docentSenseTractament} ${professor} del Departament d'Educació amb una durada de $hores hores, durant el curs escolar ${cursEscolar}.</p>")
            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11; line-height: 1.6;'>I, perquè així consti, signo el present certificat.</p>")
            content.append("<br/>")

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.append("<p style='font-family:Arial; size:11; line-height: 1.6;'>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}</p>")
            } else {
                content.append("<p style='font-family:Arial; size:11; line-height: 1.6;'>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}</p>")
            }

            content.append("</div>")
            content.append("</body")
            content.append("</html")

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-tutor-${System.currentTimeMillis()}.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCertificatTutorHTML ${error.message}").showAndWait()
            } finally {

            }

            return filename
        }

        /*
* Aquesta carta la signa la responsable i es lliura a nom de la persona de contacte de la empresa
*
* <!DOCTYPE HTML><html><head><title>Estades Formatives en Empresa: Carta de certificació d tutor/a</title></head><body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'><img src='file:///H:/Mendez/gesticusv2/logos/logo_bn.jpg'/><div style='margin-left:25px; width:95%; font-family:courier; line-height: 1.6;' align='justify'><br/><br/><br/><p>Pilar Nus Rey, sub-directora General d'Ordenació de Formació Professional Inicial i d'Ensenyaments de Règim Especial de la Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial del Departament d'Educació de la Generalitat de Catalunya.</p><br/><p><h3>C E R T I  F I C O:</h3></p><br/><p>Que, segons consta en els nostres arxius, José Luis Criado amb DNI 39164789K, Gerent de la empresa Promo Raids Trading, S.L., ha realitzat la tutoria d'una estada formativa per al professorat del Departament d'Educació amb una durada de 20 hores, durant el curs escolar 2018-2019.</p><br/><p>I, perquè així consti, signo el present certificat.</p><br/><p >Barcelona, 24 de gener de 2019</p></div></body</html>
*
* */
        fun createCartaCertificatTutorCastellaHTML(registre: Registre, hores: Int, dniTutor: String): String? {

            var filename: String? = null

            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractament = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val numEstada = registre.estada?.numeroEstada

            val pos = numEstada?.indexOf("/", 0) ?: 0

            val cursEscolar = numEstada?.substring(pos + 1, numEstada.length)

            val professor = if (docentAmbTractamemt.startsWith("Sr.")) "profesor" else "profesora"

            val content: StringBuilder = StringBuilder()

            setupDocumentCertificatHtml(content, "Carta de certificació d tutor/a")

            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11px; line-height: 1.6;'>$RESPONSABLE, Subdirectora General de Ordenació de Formación Profesional Inicial y Enseñanzas de Régimen Especial de la Dirección General de Formación Profesional del Departamento de Educación de la Generalitat de Catalunya.</p>")
            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11px; line-height: 1.6;'><h3>C E R T I F I C O :</h3></p>")
            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11; line-height: 1.6;'>Que, según consta en nuestros archivos, ${registre.empresa?.tutor?.nom} con DNI ${dniTutor}, ${registre.empresa?.tutor?.carrec} de ${registre.empresa?.identificacio?.nom}, ha realizado la tutoria de una estancia formativa para ${docentSenseTractament} ${professor} del Departamento de Educación con una duracion de $hores horas, durante el curso escolar ${cursEscolar}.</p>")
            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11; line-height: 1.6;'>Y, para que así conste, firmo el presente certificado.</p>")
            content.append("<br/>")

            content.append("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale("es", "ES")))}")

            content.append("</div>")
            content.append("</body")
            content.append("</html")

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-tutor-castella-${System.currentTimeMillis()}.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCertificatTutorHTML ${error.message}").showAndWait()
            } finally {

            }

            return filename
        }

        /*
        * Aquesta carta la signa la responsable i es lliura a nom de la persona de contacte de la empresa
        *
        * <!DOCTYPE HTML><html><head><title>Estades Formatives en Empresa: Carta de certificació d tutor/a</title></head><body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'><img src='file:///H:/Mendez/gesticusv2/logos/logo_bn.jpg'/><div style='margin-left:25px; width:95%; font-family:courier; line-height: 1.6;' align='justify'><br/><br/><br/><p>Pilar Nus Rey, sub-directora General d'Ordenació de Formació Professional Inicial i d'Ensenyaments de Règim Especial de la Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial del Departament d'Educació de la Generalitat de Catalunya.</p><br/><p><h3>C E R T I  F I C O:</h3></p><br/><p>Que, segons consta en els nostres arxius, José Luis Criado amb DNI 39164789K, Gerent de la empresa Promo Raids Trading, S.L., ha realitzat la tutoria d'una estada formativa per al professorat del Departament d'Educació amb una durada de 20 hores, durant el curs escolar 2018-2019.</p><br/><p>I, perquè així consti, signo el present certificat.</p><br/><p >Barcelona, 24 de gener de 2019</p></div></body</html>
        *
        * */
        fun createCartaCertificatTutorAnglesHTML(registre: Registre, hores: Int, dniTutor: String): String? {

            var filename: String? = null

//            val docentAmbTractamemt = registre.docent?.nom

//            val docentSenseTractament = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val numEstada = registre.estada?.numeroEstada

            val pos = numEstada?.indexOf("/", 0) ?: 0

            val cursEscolar = numEstada?.substring(pos + 1, numEstada.length)

            val content: StringBuilder = StringBuilder()

            setupDocumentCertificatHtml(content, "Carta de certificació d tutor/a")

            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11px; line-height: 1.6;'>$RESPONSABLE, $SUBDIRECCIO_ANGLES_LINIA_1 $SUBDIRECCIO_ANGLES_LINIA_2 from the Government of Catalonia.</p>")
            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11px; line-height: 1.6;'><h3>I   C E R T I F Y :</h3></p>")
            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11; line-height: 1.6;'>That, as recorded in our files, ${registre.empresa?.tutor?.nom} with national ID card number ${dniTutor}, ${registre.empresa?.tutor?.carrec}, has conducted the tutoring of a training stay for ${registre.empresa?.identificacio?.nom} teacher of the Department of Education with a duration of $hores hours, during the school year ${cursEscolar}.</p>")
            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11; line-height: 1.6;'>And for the record, I sign this certificate.</p>")
            content.append("<br/>")

            content.append("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.UK))}")

            content.append("</div>")
            content.append("</body")
            content.append("</html")

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-tutor-angles-${System.currentTimeMillis()}.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCertificatTutorHTML ${error.message}").showAndWait()
            } finally {

            }

            return filename
        }

        /* Aquesta carta la signa la responsable i es lliura a nom de la persona de contacte de la empresa
        *
        * <!DOCTYPE HTML><html><head><title>Estades Formatives en Empresa: Carta de certificació d tutor/a</title></head><body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'><img src='file:///H:/Mendez/gesticusv2/logos/logo_bn.jpg'/><div style='margin-left:25px; width:95%; font-family:courier; line-height: 1.6;' align='justify'><br/><br/><br/><p>Pilar Nus Rey, sub-directora General d'Ordenació de Formació Professional Inicial i d'Ensenyaments de Règim Especial de la Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial del Departament d'Educació de la Generalitat de Catalunya.</p><br/><p><h3>C E R T I  F I C O:</h3></p><br/><p>Que, segons consta en els nostres arxius, José Luis Criado amb DNI 39164789K, Gerent de la empresa Promo Raids Trading, S.L., ha realitzat la tutoria d'una estada formativa per al professorat del Departament d'Educació amb una durada de 20 hores, durant el curs escolar 2018-2019.</p><br/><p>I, perquè així consti, signo el present certificat.</p><br/><p >Barcelona, 24 de gener de 2019</p></div></body</html>
        *
        * */
        fun createCartaPendentsFamiliaHTML(familia: String, estadesPendents: List<EstadaPendent>): String? {

            if (estadesPendents.size == 0) return null

            var filename: String? = null

            val content: StringBuilder = StringBuilder()

            //setupDocumentCertificatHtml(content, "estades pendents de $familia")

            content.append("<!DOCTYPE HTML>")
            content.append("<html>")
            content.append("<head>")
            content.append("<title>Estades Formatives en Empresa</title>")
            content.append("</head>")
            content.append("<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'>")
            content.append("<img src='$PATH_TO_LOGO_HTML'/>")
            content.append("<div style='margin-left:25px; width:95%;'>")

            content.append("<br/>")
            content.append("<p style='font-family:Arial; size:11px; line-height: 1.6;'><center><h2>ESTADES PENDENTS DE LA FAMILIA: ${familia.toUpperCase()}</h2></center></p>")
            content.append("<br/>")

            content.append("<table style='width:100%;' border='1'><tr><th>NUM.</th><th>NIF</th><th>TRACTAMENT</th><th>NOM</th><th>TELEFON</th><th>EMAIL</th><th>ESPECIALITAT</th><th>MUNICIPI</th><th>NOM</th></tr>")
            var index = 1
            estadesPendents.forEach {
                content.append("<tr><td>${index++}</td><td>${it.professorsNif}</td><td>${it.professorsTractament}</td><td>${it.professorsNom}</td><td>${it.professorsTelefon}</td><td>${it.professorsEmail}</td><td>${it.professorsEspecialitat}</td><td>${it.centresMunicipi}</td><td>${it.centresNom}</td></tr>")
            }
            content.append("</table>")

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.append("<p style='font-family:Arial; size:11px; line-height:1.6;'>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}</p>")
            } else {
                content.append("<p style='font-family:Arial; size:11px; line-height:1.6;'>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}</p>")
            }

            content.append("</div>")
            content.append("</body")
            content.append("</html")

            try {
                filename = "$PATH_TO_LLISTATS\\estades_pendents_${familia.replace(" ", "_").toLowerCase()}_${currentCourseYear()}.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCertificatTutorHTML ${error.message}").showAndWait()
            } finally {

            }

            return filename
        }

        fun createInformeAllEstades(allEstades: MutableList<AllEstades>): String {

            var filename: String = ""

            val content: StringBuilder = StringBuilder()

            //setupDocumentCertificatHtml(content, "estades pendents de $familia")

            content.append("<!DOCTYPE HTML>")
            content.append("<html>")
            content.append("<head>")
            content.append("<title>Estades Formatives en Empresa</title>")
            content.append("</head>")
            content.append("<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'>")
            content.append("<img src='$PATH_TO_LOGO_HTML'/>")
            content.append("<div style='margin-left:25px; width:95%;'>")

            content.append("<p style='font-family:Arial; size:11px; line-height: 1.6;'><center><h1>ESTADES ${currentCourseYear()}-${nextCourseYear()}</h1></center></p>")

            val mapEstades: Map<String, List<AllEstades>> = allEstades.groupBy {
                it.familiaProfessor
            }

            mapEstades.forEach { entry ->
                content.append("<p style='font-family:Arial; size:11px; line-height: 1.6;'><center><h2>${entry.key.toUpperCase()}</h2></center></p>")
                content.append("<table style='width:100%;' border='0'><tr><th>#</th><th>CODI</th><th>NOM</th><th>DESTINACIÓ</th><th>ESPECIALITAT</th><th>MUNICIPI</th><th>INICI</th><th>FI</th></tr>")
                var index = 1
                entry.value.sortedBy {
                    it.municipiEmpresa.toUpperCase()
                }.forEach {
                    val especialitat = if (it.codiEspecialitatProfessor.startsWith("5") || it.codiEspecialitatProfessor.toUpperCase().startsWith("PS")) "PS"
                    else if (it.codiEspecialitatProfessor.startsWith("6") || it.codiEspecialitatProfessor.toUpperCase().startsWith("PT")) "PT" else "NI"
                    content.append("<tr><td>${index++}</td><td>${it.numeroEstada}</td><td>${it.cognom1Professor + " " + it.cognom2Professor + ", " + it.nomProfessor}</td><td>${it.destinacioProfessor}</td><td>${especialitat}</td><td>${it.municipiEmpresa.toLowerCase().capitalize()}</td><td>${LocalDate.parse(it.dataInici.substring(0, 10), americanDateTimeFormatter).format(dateTimeFormatter)}</td><td>${LocalDate.parse(it.dataFinal.substring(0, 10), americanDateTimeFormatter).format(dateTimeFormatter)}</td></tr>")
                }
                content.append("</table>")
            }

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.append("<p style='font-family:Arial; size:11px; line-height:1.6;'>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}</p>")
            } else {
                content.append("<p style='font-family:Arial; size:11px; line-height:1.6;'>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}</p>")
            }

            content.append("</div>")
            content.append("</body")
            content.append("</html")

            try {
                filename = "$PATH_TO_LLISTATS\\totes_les_estades_${currentCourseYear()}.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCertificatTutorHTML ${error.message}").showAndWait()
            } finally {

            }

            return filename
        }

        fun generaInformeVisites(visites: List<Visita>): String {

            var filename: String = ""

            val content: StringBuilder = StringBuilder()

            //setupDocumentCertificatHtml(content, "estades pendents de $familia")

            content.append("<!DOCTYPE HTML>")
            content.append("<html>")
            content.append("<head>")
            content.append("<title>Estades Formatives en Empresa</title>")
            content.append("</head>")
            content.append("<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'>")
            content.append("<img src='$PATH_TO_LOGO_HTML'/>")
            content.append("<div style='margin-left:25px; width:95%;'>")
            content.append("<p style='font-family:Arial; size:11px; line-height: 1.6;'><center><h1>VISITES ${currentCourseYear()}-${nextCourseYear()}</h1></center></p>")
            content.append("<table style='width:100%;' border='0'><tr><th>#</th><th>CODI</th><th>TIPUS</th><th>DATA</th><th>HORA</th><th>COMENTARIS</th></tr>")
            var index = 1
            visites
                    .sortedBy {
                        it.data
                    }
                    .forEach {
                content.append("<tr><td>${index++}</td><td>${it.estadesCodi}</td><td>${it.tipus}</td><td>${java.sql.Date.valueOf(it.data).toCatalanDateFormat()}</td><td>${it.hora}</td><td>${it.comentaris}</td></tr>")
            }
            content.append("</table>")
            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.append("<p style='font-family:Arial; size:11px; line-height:1.6;'>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}</p>")
            } else {
                content.append("<p style='font-family:Arial; size:11px; line-height:1.6;'>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}</p>")
            }

            content.append("</div>")
            content.append("</body")
            content.append("</html")

            try {
                filename = "$PATH_TO_LLISTATS\\informe_visites_empresa_${currentCourseYear()}.html"
                Files.write(Paths.get(filename), content.lines())
                Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCertificatTutorHTML ${error.message}").showAndWait()
            } finally {

            }
            return filename

        }
    }

}