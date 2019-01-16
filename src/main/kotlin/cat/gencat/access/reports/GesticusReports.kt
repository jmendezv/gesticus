package cat.gencat.access.reports

import cat.gencat.access.db.Registre
import cat.gencat.access.functions.PATH_TO_LOGO
import cat.gencat.access.functions.PATH_TO_LOGO_HTML
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
const val FONT_SIZE_12 = 12F
const val FONT_SIZE_14 = 14F
const val FONT_SIZE_16 = 16F
const val FONT_SIZE_18 = 18F
const val INTER_LINE = -13F
const val INTER_LINE_FOOT = -11F

const val RESPONSABLE = "Pilar Nus Rey"

//const val RESPONSABLE_EMAIL = "formacioprofessional@gencat.cat"

const val SUBDIRECCIO_LINIA_0 =
        "sub-directora general d'Ordenació de la Formació Professional Inicial i d'Ensenyaments de Règim Especial"

const val SUBDIRECCIO_LINIA_1 = "Sub-directora general d'Ordenació de la Formació Professional"
const val SUBDIRECCIO_LINIA_2 = "Inicial i d'Ensenyaments de Règim Especial"

const val SUBDIRECCIO_SHORT = "Subdirecció General d'Ordenació de la Formació Professional"

const val TECNIC_DOCENT = "Pep Méndez"

const val TECNIC_DOCENT_CARREC_0 =
        "Tècnic docent del Servei de Programes i Projectes de Foment dels Ensenyaments Professional"
const val TECNIC_DOCENT_CARREC_1 = "Tècnic docent del Servei de Programes i Projectes"
const val TECNIC_DOCENT_CARREC_2 = "de Foment dels Ensenyaments Professional"

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
                "0508" to "Servei Territorial de Maresme - Valles Oriental",
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

            val docentSenseTractament = direAmbTractament?.substring(docentAmbTractamemt!!.indexOf(" ") + 1)

            val direSenseTractament = direAmbTractament?.substring(direAmbTractament.indexOf(" ", 5) + 1)

            val director = if (direAmbTractament!!.startsWith("Sr.")) "director" else "directora"

            val benvolgut = if (direAmbTractament!!.startsWith("Sr.")) "Benvolgut," else "Benvolguda,"

            val elProfessor = if (registre.docent!!.nom.startsWith("Sr.")) "el professor" else "la professora"

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
            content.showText("En relació amb la sol·licitud d'una estada formativa de tipus ${registre.estada?.tipusEstada} de $docentSenseTractament")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("a ${registre.empresa?.identificacio?.nom} amb seu a ${registre.empresa?.identificacio?.municipi},")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("us comunico que la Direcció General de la Formació Professional Inicial i Ensenyaments de Règim")
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

        /* Carta Centre HTML (per signar) i pdf per email */
        fun createCartaCentre(registre: Registre): String? {
            createCartaCentreHTML(registre)
            return createCartaCentrePDF(registre)
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
            content.showText("de la Formació Professional (telèfon 935516900, extensió 3218).")

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

        /*
        * Carta a la empresa
        * */
        fun createCartaEmpresa(registre: Registre): String? {
            createCartaEmpresaHTML(registre)
            return createCartaEmpresaPDF(registre)
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

            val docentSenseTractament = docent!!.substring(docent!!.indexOf(" ") + 1)

            val professor = if (docent.startsWith("Sr.")) "professor" else "professora"

            val esmetatProfe = if (docent.startsWith("Sr.")) "l'esmentat professor" else "l'esmentada professora"

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
            content.showText("Volem  agrair-vos  la  participació  en  l'estada  de  formació  que ${docentSenseTractament}")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("${professor} del Centre ${registre.centre?.nom}, de ${registre.centre?.municipi}, ha realitzat a la vostra seu.")

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

            val numEstada = registre.estada?.numeroEstada
            val pos = numEstada?.indexOf("/", 0) ?: 0
            val anyEscolar = numEstada?.substring(pos + 1, numEstada.length)

            content.beginText()
            content.setFont(font, FONT_SIZE_12)
            content.newLineAtOffset(MARGIN, pageH - imageH - MARGIN * 2)

            content.newLineAtOffset(0.0F, INTER_LINE * 5)
            content.showText("$RESPONSABLE, sub-directora General d'Ordenació de la Formació Professional Inicial i")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("d'Ensenyaments de Règim Especial de la Direcció General de Formació Professional Inicial i")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("Ensenyaments de Règim Especial del Departament d'Educació de la Generalitat de Catalunya.")

            content.newLineAtOffset(0.0F, INTER_LINE * 4)
            content.setFont(font, FONT_SIZE_18)
            content.showText("CERTIFICO")

            content.newLineAtOffset(0.0F, INTER_LINE * 3)
            content.setFont(font, FONT_SIZE_12)
            content.showText("Que, segons consta en els nostres arxius, ${registre.empresa?.tutor?.nom} amb DNI ${dniTutor},")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("de la empresa ${registre.empresa?.identificacio?.nom}, ha realitzat la tutoria d'una estada")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("formativa per al professorat del Departament d'Educació amb una durada de ${hores} hores,")
            content.newLineAtOffset(0.0F, INTER_LINE)
            content.showText("durant el curs escolar $anyEscolar")
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

            content.append("<p>En relació amb la sol·licitud d'una estada formativa de tipus ${registre.estada?.tipusEstada} de ${docentSenseTractamemt} a ${registre.empresa?.identificacio?.nom} amb seu a ${registre.empresa?.identificacio?.municipi}, us comunico que la Direcció General de la Formació Professional Inicial i Ensenyaments de Règim Especial ha resolt autoritzar-la amb el codi d'activitat número ${registre.estada?.numeroEstada}.</p>")

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

        /* Create carta empresa HTML (per signar) */
        fun createCartaEmpresaHTML(registre: Registre): String? {

            var filename: String?

            val dire = registre.centre?.director

            val direSenseTractament = dire?.substring(dire.indexOf(" ", 5) + 1)

            val director = if (dire!!.startsWith("Sr.")) "director" else "directora"

            // docentAmbTractament es de la forma Sr. ... o Sra.
            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractament = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val elProfessor = if (docentSenseTractament.startsWith("Sr.")) "el professor" else "la professora"

            val content: StringBuilder = StringBuilder()

            setupDocumentHtml(content, "Carta d'Empresa")

            //content.append("<br/>")
            content.append("${registre.empresa?.identificacio?.nom}<BR/>")
            content.append("A/A ${registre.empresa?.personaDeContacte?.nom}<BR/>")
            content.append("${registre.empresa?.identificacio?.direccio}<BR/>")
            content.append("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}<BR/>")
            content.append("<br/>")

            /* docent es de la forma Sr. xxx o Sra. xxx*/
            val docent = registre.docent?.nom

            val professor = if (docentAmbTractamemt.startsWith("Sr.")) "professor" else "professora"

            val esmetatProfe =
                    if (docentAmbTractamemt.startsWith("Sr.")) "l'esmentat professor" else "l'esmentada professora"

            content.append("Benvolgut/da,")
            //content.append("<br/>")
            content.append("<p>Hem rebut una sol·licitud de ${direSenseTractament}, ${director} del centre '${registre.centre?.nom}' demanant que ${docentSenseTractament}, ${professor} d’aquest centre, pugui fer una estada de formació a la vostra institució.</p>")
            content.append("<p>L’actual model educatiu preveu la col·laboració del sector empresarial i educatiu, per tal d'apropar, cada vegada més, la formació de l’alumnat de cicles formatius a les demandes reals de les empreses i institucions.</p>")
            content.append("<p>Amb aquest objectiu, i ateses les excel·lents possibilitats de formació que ofereix la vostra institució, us sol·licitem que ${esmetatProfe} pugui realitzar aquesta estada, la qual forma part del procés formatiu i està regulada per l'Ordre EDC/458/2005 de 30 de novembre de 2005 i publicada en el DOGC núm. 4525 de 7 de desembre de 2005 i, per tant, no constituiex en cap cas, una relació laboral o de serveis entre l'entitat '${registre.empresa?.identificacio?.nom}' i ${docentSenseTractament}, ${professor} del Departament d’Educació.</p>")

            // Cobertura legal
            content.append("<p>En relació amb l’assegurança del professorat, us comuniquem que la Generalitat de Catalunya té contractada una cobertura pels Departaments, els seus representants, els seus empleats i dependents en l’exercici de les seves funcions o de la seva activitat professional per compte d’aquells, als efectes de garantir les conseqüències econòmiques eventuals derivades de la responsabilitat patrimonial i civil que legalment els hi puguin correspondre.</p>")

            //content.append("<br/>")
            content.append("<p>La informació relativa a aquesta cobertura d’assegurança la podeu consultar a l’adreça 'http://economia.gencat.cat/', pestanya ‘Àmbits d’actuació’, enllaç ‘Gestió de riscos i assegurances' dins del grup ‘Assegurances’.")

            // Closure
            content.append("<p>Per a qualsevol dubte, podeu posar-vos en contacte amb l'Àrea de Formació de Professorat de la Formació Professional (telèfon 935516900, extensió 3218).</p>")

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

        /* Un cop acabada l'estada s'envia una carta d'agraïemnt a l'empresa */
        fun createCartaAgraimentHTML(registre: Registre): String? {

            var filename: String? = null

            val docent = registre.docent?.nom

            val docentSenseTractamemt = docent!!.substring(docent.indexOf(" ") + 1)

            val professor = if (docent.startsWith("Sr.")) "professor" else "professora"

            val esmetatProfe = if (docent.startsWith("Sr.")) "l'esmentat professor" else "l'esmentada professora"

            val content: StringBuilder = StringBuilder()

            setupDocumentHtml(content, "Carta d'Agraïment")
            content.append("<br/>")

            content.append("${registre.empresa?.identificacio?.nom}<BR/>")
            content.append("A/A ${registre.empresa?.personaDeContacte?.nom}<BR/>")
            content.append("${registre.empresa?.identificacio?.direccio}")
            content.append("${registre.empresa?.identificacio?.cp} ${registre.empresa?.identificacio?.municipi}")

            content.append("<BR/>")
            content.append("<BR/>")

            content.append("Benvolgut/da,<BR/>")
            content.append("<p>Volem  agrair-vos  la  participació  en  l'estada  de  formació  que ${registre.docent?.nom} ${professor} del Centre ${registre.centre?.nom}, de ${registre.centre?.municipi}, ha realitzat a la vostra seu.</p>")
            content.append("<p>Aquestes accions són de gran importància en l'actual formació professional, ja que el contacte directe amb el món laboral, com el que vosaltres heu facilitat, permet completar la formació de base del professorat amb els procediments i actituds que es desenvolupen en el dia a dia laboral, alhora que possibilita la consolidació de la relació del Centre amb l'empresa. Tot plegat l’ajudarà a planificar i realitzar la tasca docent d'acord amb els requeriments que actualment les empreses i institucions demanen als seus treballadors.</p>")
            content.append("Rebeu una cordial salutació,</BR>")

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

        /* Aquesta carta la signa la responsable i es lliura a nom de la persona de contacte de la empresa */
        fun createCartaCertificatTutorHTML(registre: Registre, hores: Int, dniTutor: String): String? {

            var filename: String? = null

            val dire = registre.centre?.director
            val direSenseTractament = dire?.substring(dire.indexOf(" ", 5) + 1)
            val director = if (dire!!.startsWith("Sr.")) "director" else "directora"

            val docentAmbTractamemt = registre.docent?.nom

            val docentSenseTractamemt = docentAmbTractamemt!!.substring(docentAmbTractamemt.indexOf(" ") + 1)

            val numEstada = registre.estada?.numeroEstada

            val pos = numEstada?.indexOf("/", 0) ?: 0

            val cursEscolar = numEstada?.substring(pos + 1, numEstada.length)

            val content: StringBuilder = StringBuilder()

            setupDocumentHtml(content, "Carta de certificació d tutor/a")

            content.append("<br/>")
            content.append("<p>$RESPONSABLE, sub-directora General d'Ordenació de la Formació Professional Inicial i d'Ensenyaments de Règim Especial de la Direcció General de Formació Professional Inicial i Ensenyaments de Règim Especial del Departament d'Educació de la Generalitat de Catalunya.</p>")
            content.append("<br/>")
            content.append("<p>CERTIFICO</p>")
            content.append("<br/>")
            content.append("<p>Que, segons consta en els nostres arxius, ${registre.empresa?.tutor?.nom} amb DNI ${dniTutor}, de la empresa ${registre.empresa?.identificacio?.nom}, ha realitzat la tutoria d'una estada formativa per al professorat del Departament d'Educació amb una durada de $hores hores, durant el curs escolar ${cursEscolar}.</p>")
            content.append("<br/>")
            content.append("<p>I, perquè així consti, signo el present certificat.</p>")
            content.append("<br/>")

            if (LocalDate.now().month.name.substring(0, 1).matches("[aeiouAEIOU]".toRegex())) {
                content.append("<p>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'd'`LLLL 'de' yyyy"))}</p>")
            } else {
                content.append("<p>Barcelona, ${LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' LLLL 'de' yyyy"))}</p>")
            }

            content.append("</div>")
            content.append("</body")
            content.append("</html")

            try {
                filename = "$PATH_TO_REPORTS\\${registre.estada?.numeroEstada?.replace("/", "-")}-carta-tutor.html"
                Files.write(Paths.get(filename), content.lines())
                // Alert(Alert.AlertType.INFORMATION, "S'ha creat el fitxer $filename correctament").showAndWait()
            } catch (error: Exception) {
                Alert(Alert.AlertType.ERROR, "createCartaCertificatTutorHTML ${error.message}").showAndWait()
            } finally {

            }

            return filename
        }

    }

}