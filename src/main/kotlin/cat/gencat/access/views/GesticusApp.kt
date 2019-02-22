package cat.gencat.access.views

import cat.gencat.access.functions.*
import cat.gencat.access.styles.GesticusStyles
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass

class GesticusApp : App(GesticusView::class, GesticusStyles::class) {

    override val configBasePath = Paths.get(PATH_TO_CONFIG)

    override val configPath = Paths.get("$PATH_TO_CONFIG\\gesticus.properties")

    override val primaryView: KClass<GesticusView> = GesticusView::class

    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
        setStageIcon(Image("file://${PATH_TO_ICONS}logo.png"))
        with(config) {
            set(CONFIG_KEY_LAST_TIME_OPEN to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString())
            save()
        }
    }

    override fun init() {
        super.init()
        FX.locale = Locale("es")
        Thread.setDefaultUncaughtExceptionHandler(DefaultErrorHandler())
    }

}

fun main(args: Array<String>) {
    Application.launch(GesticusApp::class.java, *args)
}