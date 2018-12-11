package cat.gencat.access

import javafx.scene.control.Alert
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.Color
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


const val MARGIN = 35F
const val FONT_SIZE = 12F
const val FONT_SIZE_FOOT = 10F
const val INTER_LINE = -18F
const val INTER_LINE_FOOT = -15F

const val PDF_OUTPUT_PATH = "D:\\Users\\39164789k\\Desktop\\app_estades\\"

class GesticusReports {


    companion object {
        /*
   *
   * Informe SSTT
   *
   * */
        fun createSSTTReport(registre: Registre): Unit {
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
        fun createDocentReport(registre: Registre): Unit {

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
            content.showText("És molt important que comuniqueu qualsevol error al telèfono que trobareu al peu d'aquesta pàgina,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("i també cal que el vostre Centre comprovi que s'ha produït el nomenament i que reclami la substitució")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("amb la suficient anticipació.")

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
        fun createCartaDirector(registre: Registre): Unit {

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

            content.beginText()
            content.setFont(font, FONT_SIZE)
            content.newLineAtOffset(MARGIN, pageH - imageH - MARGIN * 2)
            content.showText("${registre.centre?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Sr./Sra. ${registre.centre?.director}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.centre?.direccio}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.centre?.cp} ${registre.centre?.municipi}")


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
                content.newLineAtOffset(0.0F, INTER_LINE * 2)
                content.showText("Aquesta modalitat d'estada formativa no preveu la substitució del professorat en les seves")
                content.newLineAtOffset(0.0F, INTER_LINE)
                content.showText("activitats lectives, això vol dir que ${registre.docent?.nom}")
                content.newLineAtOffset(0.0F, INTER_LINE)
                content.showText("ha d'atendre les seves activitats mentre duri l'estada.")
            }
            // B
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
            content.newLineAtOffset(0.0F, INTER_LINE * 3)
            content.showText("Ferran Castrillo Rey")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Cap de servei de Programes i Projectes de Foment dels Ensenyaments Professionals")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}")
            } else {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}")
            }

                      // Foot page
            content.newLineAtOffset(0.0F, INTER_LINE * 4)
            content.setNonStrokingColor(Color.BLACK)
            content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_FOOT)
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
        fun createCartaEmpresa(registre: Registre): Unit {

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

            content.beginText()
            content.setFont(font, FONT_SIZE)
            content.newLineAtOffset(MARGIN, pageH - imageH - MARGIN * 2)
            content.showText("A/A ${registre.empresa?.personaDeContacte?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.identificacio?.direccio}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Hem rebut una sol·licitud de ${registre.centre?.director}, director/a del Centre ${registre.centre?.nom} demanant que")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.docent?.nom}, professor/a d’aquest Centre, pugui fer una estada de formació")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("a la vostra institució.")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("L’actual model educatiu preveu la col·laboració del sector empresarial i educatiu, per tal d'apropar, cada vegada")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("més, la formació de l’alumnat de cicles formatius a les demandes reals de les empreses i institucions.")
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("Amb aquest objectiu, i ateses les excel·lents possibilitats de formació que ofereix la vostra institució us sol·licitem")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("que l'esmentat/da professor/a pugui realitzar aquesta estada, la qual forma part del procés formatiu i està regulada")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("per l'Ordre EDC/458/2005 de 30 de novembre de 2005 i publicada en el DOGC núm. 4525 de 7 de desembre")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de 2005 i, per tant, no constituiex en cap cas, una relació laboral o de serveis entre")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${registre.empresa?.identificacio?.nom} i ${registre.docent?.nom}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("professor/a del Departament d’Educació.")

            // Cobertura legal
            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("En relació amb l’assegurança del professorat, us comuniquem que la Generalitat de Catalunya té contractada una")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("cobertura pels Departaments, els seus representants, els seus empleats i dependents en l’exercici de les seves")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("funcions o de la seva activitat professional per compte d’aquells, als efectes de garantir les conseqüències")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("econòmiques eventuals derivades de la responsabilitat patrimonial i civil que legalment els hi puguin correspondre.")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)
            content.showText("La informació relativa a aquesta cobertura d’assegurança la podeu consultar a l’adreça 'http://economia.gencat.cat/',")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("pestanya ‘Àmbits d’actuació’, enllaç ‘Gestió de riscos i assegurances' dins del grup ‘Assegurances’")


            // Foot page
            content.newLineAtOffset(0.0F, INTER_LINE * 3)
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

        /*
        *
        * TODO("REVIEW")
        *
        * */
        fun createCartaAgraiment(registre: Registre): Unit {

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

            content.beginText()
            content.setFont(font, FONT_SIZE)
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
            content.showText("Ferran Castrillo Rey")
            content.newLineAtOffset(0.0F, INTER_LINE)

            content.showText("Cap de servei de Programes i Projectes de Foment dels Ensenyaments Professionals")

            content.newLineAtOffset(0.0F, INTER_LINE * 2)

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}")
            } else {
                content.showText("Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}")
            }

            // Foot page
            content.newLineAtOffset(0.0F, INTER_LINE * 6)
            content.setNonStrokingColor(Color.BLACK)
            content.setFont(PDType1Font.TIMES_ITALIC, FONT_SIZE_FOOT)
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
                val filename = "$PDF_OUTPUT_PATH\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-agraiment.pdf"
                document.save(filename)
                Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, error.message).showAndWait()
            } finally {
                document.close()
            }
        }
    }

}