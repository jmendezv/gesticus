package cat.gencat.access.reports

import cat.gencat.access.db.Registre
import cat.gencat.access.functions.PATH_TO_LOGO
import cat.gencat.access.functions.PATH_TO_REPORTS
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
const val FONT_SIZE_11 = 11F
const val FONT_SIZE_12 = 12F
const val INTER_LINE = -13F
const val INTER_LINE_FOOT = -11F

const val CAP_DE_SERVEI = "Ferran Castrillo Rey"

const val TECNIC_DOCENT = "Pep Méndez"

const val LANGUAGE = "CAT"
const val AUTHOR = "Josep Méndez Valverde"
const val TITLE = "Informe Estades Formatives"
const val CREATOR = "Josep Méndez Valverde"
const val SUBJECT = "Estades Formatives"
const val KEYWORDS = "Estades Formacio FP Empresa"


const val CARTA_CENTRE_HTML =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Ha estat concedida una estada formativa en empresa de tipus B (<strong>amb substitució</strong>) a un/a docent d'aquest Centre.</p><p>Trobareu els detalls en el document adjunt.</p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"

const val CARTA_EMPRESA_HTML =
        "<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 14px'><meta charset='UTF-8'><p>Benvolgut/da,</p><br><p>Ha estat concedida una estada formativa a un/a professor/a de Formació Professional en la vostra entitat.</p><p>Trobareu els detalls de l'estada en el document adjunt.</p><br><p>Ben Cordialment,</p><p>Pep Méndez</p><br><br><p style='font-family:courier; font-size:10px;'><b><i>Formació Permanent del Professorat d'Ensenyaments Professionals</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Generalitat de Catalunya</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Departament d'Educació</i></b></p style='font-family:courier; font-size:10px;'><p style='font-family:courier; font-size:10px;'><b><i>Direcció General  de Formació Professional Inicial i Ensenyaments de Règim Especial</i></b></p><p style='font-family:courier; font-size:10px;'><b><i>Tel. 93 551 69 00 extensió 3218</i></b></p></body>"




class GesticusReports {

    companion object {

        /*
         * Informe SSTT
         * */
        fun createCartaSSTT(registre: Registre): String? {

            var filename: String? = null
            val document = PDDocument()
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

            val pageW = page.bBox.width
            val pageH = page.bBox.height

            document.addPage(page)
            val image =
                PDImageXObject.createFromFile(PATH_TO_LOGO, document)

            val imageW = image.width.toFloat()
            val imageH = image.height.toFloat()

            val font = PDType1Font.TIMES_ROMAN
            val content: PDPageContentStream = PDPageContentStream(document, page)
            content.drawImage(image, MARGIN, pageH - imageH - MARGIN)

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN + 30, pageH - imageH - MARGIN * 2)
            content.showText("Benvolgut/da,")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("En relació amb les estades formatives de professorat a empreses amb substitució, us trameto les dades i")
            content.newLineAtOffset(0.0f, INTER_LINE)
            content.showText("les dates en què ha estat concedida.")
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

            content.newLineAtOffset(-20.0F, INTER_LINE * 7)
            content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_10)
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText(TECNIC_DOCENT)
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("Formació Permanent del Professorat d'Ensenyaments Professionals")
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
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-sstt.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        /*
        * Informe Docent
        * */
        fun createCartaDocent(registre: Registre): String? {

            var filename: String? = null
            val document = PDDocument()
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

            val pageW = page.bBox.width
            val pageH = page.bBox.height

            document.addPage(page)
            val image =
                PDImageXObject.createFromFile(PATH_TO_LOGO, document)

            val imageW = image.width.toFloat()
            val imageH = image.height.toFloat()

            val font = PDType1Font.TIMES_ROMAN
            val content: PDPageContentStream = PDPageContentStream(document, page)
            content.drawImage(image, MARGIN, pageH - imageH - MARGIN)

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN + 30, pageH - imageH - MARGIN * 2)
            content.showText("Benvolgut/da,")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Us ha estat concedida l'estada número ${registre.estada?.numeroEstada}. I a tal efecte hem notificat el vostre SSTT")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("amb la següent informació per tal de què gestionin la vostra substitució:")

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

            // Foot page
            content.newLineAtOffset(0.0F, INTER_LINE * 7)
            content.setNonStrokingColor(Color.BLACK)
            content.showText("Ben cordialment")
            content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_10)
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText(TECNIC_DOCENT)
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("Formació Permanent del Professorat d'Ensenyaments Professionals")
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
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-docent.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            } finally {
                document.close()
            }

            return filename

        }

        /*
        *
        * Aquesta carta s'envia al Centre/Docent
        *
        * */
        private fun createCartaCentrePDF(registre: Registre): String? {

            var filename: String? = null
            val document = PDDocument()
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

            val pageW = page.bBox.width
            val pageH = page.bBox.height

            document.addPage(page)
            val image =
                    PDImageXObject.createFromFile(PATH_TO_LOGO, document)

            val imageW = image.width.toFloat()
            val imageH = image.height.toFloat()

            val font = PDType1Font.TIMES_ROMAN
            val content: PDPageContentStream = PDPageContentStream(document, page)
            content.drawImage(image, MARGIN, pageH - imageH - MARGIN)

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN + 30, pageH - imageH - MARGIN * 2)
            content.showText("${registre.centre?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Sr./Sra. ${registre.centre?.director}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.centre?.direccio}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.centre?.cp} ${registre.centre?.municipi}")

            content.newLineAtOffset(0.0f, INTER_LINE * 3)
            content.showText("En relació amb la sol·licitud d'una estada formativa de tipus ${registre.estada?.tipusEstada} de ${registre.docent?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("a ${registre.empresa?.identificacio?.nom}  amb seu a ${registre.empresa?.identificacio?.municipi},")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("us comunico que la Direcció General de la Formació Professional Inicial i Ensenyaments de Règim")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Especial ha resolt autoritzar-la amb el codi d'activitat ${registre.estada?.numeroEstada}.")

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
                content.showText("L'estada formativa no implica cap relació laboral entre la institució i el/la professor/a que la realitza.")
            }

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació del Professorat")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de Formació Professional (telèfon 935516900, extensió 3218)")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Atentament")
            content.newLineAtOffset(0.0F, INTER_LINE * 6)
            content.showText(CAP_DE_SERVEI)
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Cap de Servei de Programes i Projectes")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de Foment dels Ensenyaments Professionals")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}")
            } else {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}")
            }

            // Foot page
            content.newLineAtOffset(0.0F, INTER_LINE * 10)
            content.setNonStrokingColor(Color.BLACK)
            content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_10)
            content.showText("Via Augusta, 202-226")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("08021 Barcelona")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("Tel. 93 551 69 00")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("http://www.gencat.cat/ensenyament")

            content.endText()
            content.close()

            try {
                filename =
                        "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-centre.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        /*
        *
        * Aquesta carta s'envia al Centre per correu ordinari signada i amb registre de sortida
        *
        * */
        private fun createCartaCentreHTML(registre: Registre): Unit {

            var filename: String? = null

            val content: StringBuilder = StringBuilder()

            content.append("<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'>")
            content.append("<img src='$PATH_TO_LOGO'/>")
            content.append("<h1>${registre.centre?.nom}</h1>")
            content.append("<p>Sr./Sra. ${registre.centre?.director}</p>")
            content.append("<p>${registre.centre?.direccio}</p>")
            content.append("<p>${registre.centre?.cp} ${registre.centre?.municipi}</p>")

            content.append("<br/>")
            content.append("<p>En relació amb la sol·licitud d'una estada formativa de tipus ${registre.estada?.tipusEstada} de ${registre.docent?.nom} a ${registre.empresa?.identificacio?.nom} amb seu a ${registre.empresa?.identificacio?.municipi}, us comunico que la Direcció General de la Formació Professional Inicial i Ensenyaments de Règim  Especial ha resolt autoritzar-la amb el codi d'activitat ${registre.estada?.numeroEstada}.</p>")

            // Estada A
            if (registre.estada?.tipusEstada == "A") {
                content.append("<p>Aquesta modalitat d'estada formativa no preveu la substitució del professorat en les seves activitats lectives, això vol dir que ${registre.docent?.nom} ha d'atendre les seves activitats mentre duri l'estada.</p>")
            }
            // Estada B
            else {
                content.append("<p>Aquesta modalitat d'estada formativa preveu la substitució del professorat mentre duri aquesta estada a l’empresa.</p>")
                content.append("<p>L’inici estarà condicionat al nomenament i presa de possessió del/de la substitut/a.</p>")
                content.append("<p>Cal que contacteu amb el vostre Servei Territorial per tot el relacionat amb la substitució.</p>")
                content.append("<p>L'estada formativa no implica cap relació laboral entre la institució i el/la professor/a que la realitza.</p>")
            }

            content.append("<p>Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació del Professorat de Formació Professional (telèfon 935516900, extensió 3218)</p>")

            content.append("<br/>")
            content.append("<h6>Atentament</h6>")
            content.append("<h6>$CAP_DE_SERVEI</h6>")
            content.append("<h6>Cap de Servei de Programes i Projectes</h6>")
            content.append("<h6>de Foment dels Ensenyaments Professionals</h6>")

            content.append("<br/>")

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.append("<h6>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}</h6>")
            } else {
                content.append("<h6>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}</h6>")
            }

            // Foot page
            content.append("<br/>")
            content.append("<p style='font-family:courier; font-size:8px;'>Via Augusta, 202-226</p>")
            content.append("<p style='font-family:courier; font-size:8px;'>08021 Barcelona</p>")
            content.append("<p style='font-family:courier; font-size:8px;'>Tel. 93 551 69 00</p>")
            content.append("<p style='font-family:courier; font-size:8px;'>http://www.gencat.cat/ensenyament</p>")

            content.append("</body>")

            try {
                filename =
                        "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-centre.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            } finally {
            }

        }

        /*
        * Carta Centre
        * */
        fun createCartaCentre(registre: Registre): String? {

            createCartaCentreHTML(registre)
            return createCartaCentrePDF(registre)

        }

        private fun createCartaEmpresaPDF(registre: Registre): String? {

            var filename: String? = null
            val document = PDDocument()
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

            val pageW = page.bBox.width
            val pageH = page.bBox.height

            document.addPage(page)
            val image =
                    PDImageXObject.createFromFile(PATH_TO_LOGO, document)

            val imageW = image.width.toFloat()
            val imageH = image.height.toFloat()

            val font = PDType1Font.TIMES_ROMAN
            val content: PDPageContentStream = PDPageContentStream(document, page)
            content.drawImage(image, MARGIN, pageH - imageH - MARGIN)

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

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Hem rebut una sol·licitud de ${registre.centre?.director}, director/a del Centre ${registre.centre?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("demanant que ${registre.docent?.nom}, professor/a d’aquest Centre, pugui fer una")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("estada de formació a la vostra institució.")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("L’actual model educatiu preveu la col·laboració del sector empresarial i educatiu, per tal d'apropar,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("cada vegada més, la formació de l’alumnat de cicles formatius a les demandes reals de les empreses")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("i institucions.")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Amb aquest objectiu, i ateses les excel·lents possibilitats de formació que ofereix la vostra institució")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("us sol·licitem que l'esmentat/da professor/a pugui realitzar aquesta estada, la qual forma part del procés")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("formatiu i està regulada per l'Ordre EDC/458/2005 de 30 de novembre de 2005 i publicada en el DOGC")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("núm. 4525 de 7 de desembre de 2005 i, per tant, no constituiex en cap cas, una relació laboral o")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de serveis entre ${registre.empresa?.identificacio?.nom} i ${registre.docent?.nom} professor/a")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("del Departament d’Educació.")

            // Cobertura legal
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("En relació amb l’assegurança del professorat, us comuniquem que la Generalitat de Catalunya té")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("contractada una cobertura pels Departaments, els seus representants, els seus empleats i dependents")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("en l’exercici de les seves funcions o de la seva activitat professional per compte d’aquells,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("als efectes de garantir les conseqüències econòmiques eventuals derivades de la responsabilitat")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("patrimonial i civil que legalment els hi puguin correspondre.")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("La informació relativa a aquesta cobertura d’assegurança la podeu consultar a l’adreça")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("'http://economia.gencat.cat/', pestanya ‘Àmbits d’actuació’, enllaç ‘Gestió de riscos i assegurances'")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("dins del grup ‘Assegurances’")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació de Professorat de")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de la Formació Professional (telèfon 935516900, extensió 3218).")

            // Foot page
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Atentament,")

            content.newLineAtOffset(0.0F, INTER_LINE * 6)
            content.showText(CAP_DE_SERVEI)
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Cap de servei de Programes i Projectes")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de Foment dels Ensenyaments Professionals")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}")
            } else {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}")
            }
            // Foot page
//            content.newLineAtOffset(0.0F, INTER_LINE * 10)
//            content.setNonStrokingColor(Color.BLACK)
//            content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_10)
//            content.showText("Via Augusta, 202-226")
//            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
//            content.showText("08021 Barcelona")
//            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
//            content.showText("Tel. 93 551 69 00")
//            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
//            content.showText("http://www.gencat.cat/ensenyament")

            content.endText()
            content.close()

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-empresa.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        private fun createCartaEmpresaHTML(registre: Registre): Unit {

            var filename: String? = null
            val content: StringBuilder = StringBuilder()

            content.append("<body style='background-color:rgb(255, 255, 255); margin: 10px; padding: 5px; font-size: 16px'><meta charset='UTF-8'>")
            content.append("<img src='$PATH_TO_LOGO'/>")

            content.append("<h1>${registre.empresa?.identificacio?.nom}</h1>")
            content.append("<p>A/A ${registre.empresa?.personaDeContacte?.nom}</p>")
            content.append("<p>${registre.empresa?.identificacio?.direccio}</p>")
            content.append("<p>${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}</p>")

            content.append("<br/>")
            content.append("<p>Hem rebut una sol·licitud de ${registre.centre?.director}, director/a del Centre ${registre.centre?.nom} demanant que ${registre.docent?.nom}, professor/a d’aquest Centre, pugui fer una estada de formació a la vostra institució.</p>")
            content.append("<p>L’actual model educatiu preveu la col·laboració del sector empresarial i educatiu, per tal d'apropar, cada vegada més, la formació de l’alumnat de cicles formatius a les demandes reals de les empreses i institucions.</p>")
            content.append("<p>Amb aquest objectiu, i ateses les excel·lents possibilitats de formació que ofereix la vostra institució, us sol·licitem que l'esmentat/da professor/a pugui realitzar aquesta estada, la qual forma part del procés formatiu i està regulada per l'Ordre EDC/458/2005 de 30 de novembre de 2005 i publicada en el DOGC núm. 4525 de 7 de desembre de 2005 i, per tant, no constituiex en cap cas, una relació laboral o de serveis entre ${registre.empresa?.identificacio?.nom} i ${registre.docent?.nom}, professor/a del Departament d’Educació.</p>")

            // Cobertura legal
            content.append("<p>En relació amb l’assegurança del professorat, us comuniquem que la Generalitat de Catalunya té contractada una cobertura pels Departaments, els seus representants, els seus empleats i dependents en l’exercici de les seves funcions o de la seva activitat professional per compte d’aquells, als efectes de garantir les conseqüències econòmiques eventuals derivades de la responsabilitat patrimonial i civil que legalment els hi puguin correspondre.</p>")

            content.append("<br/>")
            content.append("<p>La informació relativa a aquesta cobertura d’assegurança la podeu consultar a l’adreça 'http://economia.gencat.cat/', pestanya ‘Àmbits d’actuació’, enllaç ‘Gestió de riscos i assegurances' dins del grup ‘Assegurances’")

            // Closure
            content.append("<p>Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació de Professorat de de la Formació Professional (telèfon 935516900, extensió 3218).</p>")

            // Foot page
            content.append("<p>Atentament,</p>")

            content.append("<p>$CAP_DE_SERVEI</p>")
            content.append("<p>Cap de servei de Programes i Projectes</p>")
            content.append("<p>de Foment dels Ensenyaments Professionals</p>")


            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.append("<p>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}</p>")
            } else {
                content.append("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}</p>")
            }
            // Foot page
//            content.newLineAtOffset(0.0F, INTER_LINE * 10)
//            content.setNonStrokingColor(Color.BLACK)
//            content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_10)
//            content.showText("Via Augusta, 202-226")
//            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
//            content.showText("08021 Barcelona")
//            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
//            content.showText("Tel. 93 551 69 00")
//            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
//            content.showText("http://www.gencat.cat/ensenyament")

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-empresa.html"

                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()

            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            } finally {
            }

        }

        /*
        * Carta a la empresa
        * */
        fun createCartaEmpresa(registre: Registre): String? {

            createCartaEmpresaHTML(registre)
            return createCartaCentrePDF(registre)

        }

        /*
        *
        * La carta d'agraïment s'envia un cop ha acabat l'estada al tutor/persona de contacte
        *
        * */
        fun createCartaAgraiment(registre: Registre): String? {

            var filename: String? = null
            val document = PDDocument()
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

            val pageW = page.bBox.width
            val pageH = page.bBox.height

            document.addPage(page)
            val image =
                PDImageXObject.createFromFile(PATH_TO_LOGO, document)

            val imageW = image.width.toFloat()
            val imageH = image.height.toFloat()

            val font = PDType1Font.TIMES_ROMAN
            val content: PDPageContentStream = PDPageContentStream(document, page)
            content.drawImage(image, MARGIN, pageH - imageH - MARGIN)

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


            content.newLineAtOffset(0.0f, INTER_LINE * 2)
            content.showText("Benvolgut/da,")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Volem  agrair-vos  la  participació  en  l'estada  de  formació  que ${registre.docent?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("del Centre ${registre.centre?.nom}, de ${registre.centre?.municipi}, ha realitzat a la vostra seu. ")
//            content.newLineAtOffset(0.0F, INTER_LINE)
//            content.showText("Volem  agrair-vos  la  participació  en  l'estada  de  formació  que ? de ? , ?, ?, ha realitzat a la vostra seu. ")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Aquestes accions són de gran importància en l'actual formació professional, ja que el contacte directe amb el")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("món laboral, com el que vosaltres heu facilitat, permet completar la formació de base del professorat amb els")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("procediments i actituds que es desenvolupen en el dia a dia laboral, alhora que possibilita la consolidació de")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("la relació del Centre amb l'empresa. Tot plegat l’ajudarà a planificar i realitzar la tasca docent d'acord amb")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("els requeriments que actualment les empreses i institucions demanen als seus treballadors.")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Rebeu una cordial salutació,")

            content.newLineAtOffset(0.0F, INTER_LINE * 5)
            content.showText(CAP_DE_SERVEI)
            content.newLineAtOffset(0.0F, INTER_LINE)

            content.showText("Cap de servei de Programes i Projectes de Foment dels Ensenyaments Professionals")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}")
            } else {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}")
            }

            // Foot page
            content.newLineAtOffset(
                0.0F, INTER_LINE * 6
            )
            content.setNonStrokingColor(Color.BLACK)
            content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_10)
            content.showText("Via Augusta, 202-226")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("08021 Barcelona")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("Tel. 93 551 69 00")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("http://www.gencat.cat/ensenyament")

            content.endText()
            content.close()

            try {
                filename =
                    "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-agraiment.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            } finally {
                document.close()
            }

            return filename
        }

        fun createCartaCertificatTutor(registre: Registre, hores: Int, dniTutor: String): String? {

            var filename: String? = null
            val document = PDDocument()
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

            val pageW = page.bBox.width
            val pageH = page.bBox.height

            document.addPage(page)
            val image =
                PDImageXObject.createFromFile(PATH_TO_LOGO, document)

            val imageW = image.width.toFloat()
            val imageH = image.height.toFloat()

            val font = PDType1Font.TIMES_ROMAN
            val content: PDPageContentStream = PDPageContentStream(document, page)
            content.drawImage(image, MARGIN, pageH - imageH - MARGIN)

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN, pageH - imageH - MARGIN * 2)

            content.newLineAtOffset(0.0F, INTER_LINE * 3)
            content.showText("$CAP_DE_SERVEI, cap de Servei i Projectes de Foment dels Ensenyaments Professionals,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de la Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("del Departament d'Educació de la Generalitat de Catalunya.")
//            content.newLineAtOffset(0.0F, INTER_LINE)
//            content.showText("Volem  agrair-vos  la  participació  en  l'estada  de  formació  que ? de ? , ?, ?, ha realitzat a la vostra seu. ")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Que, segons consta en els nostres arxius, ${registre.empresa?.tutor?.nom} amb DNI ${dniTutor},")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.tutor?.nom} de la empresa ${registre.empresa?.identificacio?.nom}, ha realitzat la tutoria")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("d'una estada formativa per al professorat del Departament d'Educació amb una durada de ${hores} hores,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            val numEstada = registre.estada?.numeroEstada
            val pos = numEstada?.indexOf("/", 0) ?: 0
            content.showText("durant el curs escolar ${numEstada?.substring(pos + 1, numEstada?.length)}")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("I, perquè així consti, signo el present certificat.")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}")
            } else {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}")
            }

            content.newLineAtOffset(0.0F, INTER_LINE * 12)
            content.setNonStrokingColor(Color.BLACK)
            content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_10)
            content.showText("Via Augusta, 202-226")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("08021 Barcelona")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("Tel. 93 551 69 00")
            content.newLineAtOffset(0.0F, INTER_LINE_FOOT)
            content.showText("http://www.gencat.cat/ensenyament")

            content.endText()
            content.close()

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-tutor.pdf"
                document.save(filename)
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            } finally {
                document.close()
            }

            return filename
        }
    }

}