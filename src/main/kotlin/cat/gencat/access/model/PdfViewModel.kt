package cat.gencat.access.model

import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import org.apache.http.client.methods.HttpGet
import tornadofx.*
import java.io.FileInputStream
import java.io.InputStream
import java.net.URI
import org.apache.http.impl.client.HttpClientBuilder
import java.io.File

class PdfViewModel : ViewModel() {
    var documentURIString = SimpleStringProperty("")
    var documentURI = SimpleObjectProperty<URI>(URI(""))
    var documentInputStream = SimpleObjectProperty<InputStream>()
    var document: PDDocument? = null
    var pdfRenderer: PDFRenderer? = null
    val currentPage = SimpleObjectProperty<Image>(WritableImage(1, 1))
    val currentPageNumber = SimpleIntegerProperty(0)
    val pageCount = SimpleIntegerProperty(0)
    val scale = SimpleFloatProperty(1.2f)

    init {
        documentInputStream.onChange { input ->
            if (input is InputStream) {
                document = PDDocument.load(input)
                pdfRenderer = PDFRenderer(document)
                pageCount.value = document?.pages?.count
                openPage(0)
            }
        }
        documentURIString.onChange {
            documentURI.value = File(it).toURI()
//            documentURI.value = URI(it) // Not working
        }
        documentURI.onChange { nuevaUri ->
            val input = when (nuevaUri!!.scheme) {
                "file" -> {
                    FileInputStream(nuevaUri.toURL().file)
                }
                "http", "https" -> {
                    val client = HttpClientBuilder.create().build()
                    val request = HttpGet(nuevaUri)
                    val response = client.execute(request)
                    response.entity.content
                }
                else -> {
                    null
                }
            }
            documentInputStream.value = input
        }
        currentPageNumber.onChange { n ->
            openPage(n)
            //log.info("Cambio a pagina ${n}")
        }
    }

    fun openPage(pageCounter: Int) {
        val bufferedImage = pdfRenderer?.renderImage(pageCounter, scale.value)
        pdfRenderer?.renderImageWithDPI(pageCounter, 300F)
        if (bufferedImage != null) {
            currentPage.value = SwingFXUtils.toFXImage(bufferedImage, null)
        }
    }

    fun firstPage() {
        currentPageNumber.value = 0
    }

    fun previousPage() {
        --currentPageNumber.value
    }

    fun nextPage() {
        ++currentPageNumber.value
    }

    fun lastPage() {
        currentPageNumber.value = pageCount.value - 1
    }

    var isFirst = currentPageNumber.isEqualTo(0)

    var isLast = currentPageNumber.isEqualTo(pageCount - 1)
}