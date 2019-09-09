package cat.gencat.access.views.app

import cat.gencat.access.functions.*
import cat.gencat.access.styles.GesticusStyles
import cat.gencat.access.views.main.GesticusView
import com.dlsc.preferencesfx.PreferencesFx
import com.dlsc.preferencesfx.util.PreferencesFxUtils
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.prefs.Preferences
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
            println()
        }
    }

    override fun init() {
        super.init()
        // universally unique identifier
        // val uuid = UUID.randomUUID()
        FX.locale = Locale("es")
        // És aques error handler per defecte
        Thread.setDefaultUncaughtExceptionHandler(DefaultErrorHandler())
        importStylesheet(GesticusStyles::class)
        /*
        * To have the Stylesheets reload automatically every time the Stage gains focus,
        * */
        reloadStylesheetsOnFocus()
    }

}

fun main(args: Array<String>) {
    Application.launch(GesticusApp::class.java, *args)
}