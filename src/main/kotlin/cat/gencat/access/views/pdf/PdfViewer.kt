package cat.gencat.access.views.pdf

import cat.gencat.access.functions.Utils
import cat.gencat.access.model.PdfViewModel
import javafx.geometry.Pos
import javafx.scene.effect.BlurType
import javafx.scene.effect.InnerShadow
import javafx.scene.paint.Color
import tornadofx.*

class PdfViewer : Fragment(Utils.APP_TITLE + ": Lector PDF") {

    val pdfModel: PdfViewModel by inject()

    val path: String by params

    init {
        pdfModel.documentURIString.value = path.replace("\\", "/")
    }

    override val root = borderpane {
        top = hbox(spacing = 5) {
            alignment = Pos.CENTER
            paddingAll = 5
            button("|<") {
                action(pdfModel::firstPage)
                disableWhen(pdfModel.isFirst)
            }
            button("<") {
                action(pdfModel::previousPage)
                disableWhen(pdfModel.isFirst)
            }
            textfield(pdfModel.currentPageNumber + 1)
            label(pdfModel.pageCount)
            button(">") {
                action(pdfModel::nextPage)
                disableWhen(pdfModel.isLast)
            }
            button(">|") {
                action(pdfModel::lastPage)
                disableWhen(pdfModel.isLast)
            }
        }
        center {
            scrollpane {
                style {
                    padding = box(0.px, 60.px, 0.px, 60.px)
                    backgroundColor += Color.DARKGRAY
                    effect = InnerShadow(BlurType.THREE_PASS_BOX, Color.GRAY, 10.0, 10.0, 10.0, 10.0)
                }
                imageview(pdfModel.currentPage) {
                    minWidthProperty().bind(this@scrollpane.widthProperty())
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        currentStage?.minWidth = 300.0
        currentStage?.minHeight = 300.0
//        currentStage?.maxWidth = 600.0
//        currentStage?.maxHeight = 600.0
    }

}