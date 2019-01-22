package cat.gencat.access.views

import cat.gencat.access.functions.PATH_TO_ICONS
import cat.gencat.access.functions.PATH_TO_LOGO
import cat.gencat.access.styles.GesticusStyles
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*
import java.util.*
import kotlin.reflect.KClass

class GesticusApp: App(GesticusView::class, GesticusStyles::class) {

    override val primaryView: KClass<GesticusView> = GesticusView::class

    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
        setStageIcon(Image("file://${PATH_TO_ICONS}logo.png"))
    }

    override fun init() {
        super.init()
        FX.locale = Locale("es")
    }

}