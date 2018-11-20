package cat.gencat.access

import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import tornadofx.*

class GesticusView : View("Gèsticus v. 2.0") {

    override val root: BorderPane by fxml()

    val controller: GesticusController by inject()

    // Menu Database
    val databaseMenuItemCerca: MenuItem by fxid("database_menuitem_cerca")
    val databaseMenuItemAnalitzaPdf: MenuItem by fxid("database_menuitem_analitza_pdf")
    val databaseMenuItemTanca: MenuItem by fxid("database_menuitem_tanca")
    // Menu Comunicats
    val comunicatsMenuItemCorreuCentre: MenuItem by fxid("comunicats_menuitem_correu_centre")
    val comunicatsMenuItemCorreuEmpresa: MenuItem by fxid("comunicats_menuitem_correu_empresa")
    val comunicatsMenuItemCartaCentre: MenuItem by fxid("comunicats_menuitem_carta_centre")
    val comunicatsMenuItemCartaEmpresa: MenuItem by fxid("comunicats_menuitem_carta_empresa")
    // Menu Eines
    val einesMenuItemPreferencies: MenuItem by fxid("eines_menuitem_preferencies")
    val einesMenuItemModeEdicio: MenuItem by fxid("eines_menuitem_mode_edicio")
    // Menu Ajuda
    val ajudaMenuItemUs: MenuItem by fxid("ajuda_menuitem_us")
    val ajudaMenuItemSobreNosaltres: MenuItem by fxid("ajuda_menuitem_sobre_nosaltres")

    // Estada
    val estadaTextFieldNumeroEstada: TextField by fxid("estada_textfield_numero_estada")
    val estadaComboBoxTipusEstada: ComboBox<String> by fxid("estada_combobox_tipus_estada")
    val estadaDatePickerDataInici: DatePicker by fxid("estada_datepicker_data_inici")
    val estadaDatePickerDataFinal: DatePicker by fxid("estada_datepicker_data_final")
    val estadaTextFieldDescripcio: TextField by fxid("estada_textfield_descripcio")
    val estadaTextFieldComentaris: TextField by fxid("estada_textfield_comentaris")

    // Empresa
    val empresaIdentificacioTextFieldNif: TextField by fxid("empresa_identificacio_textfield_nif")
    val empresaIdentificacioTextFieldNom: TextField by fxid("empresa_identificacio_textfield_nom")
    val empresaIdentificacioTextFieldDireccio: TextField by fxid("empresa_identificacio_textfield_direccio")
    val empresaIdentificacioTextFieldCodiPostal: TextField by fxid("empresa_identificacio_textfield_codi_postal")
    val empresaIdentificacioTextFieldMunicipi: TextField by fxid("empresa_identificacio_textfield_municipi")
    val empresaPersonaContacteTextFieldNom: TextField by fxid("empresa_persona_contacte_textfield_nom")
    val empresaPersonaContacteTextFieldCarrec: TextField by fxid("empresa_persona_contacte_textfield_carrec")
    val empresaPersonaContacteTextFieldTelefon: TextField by fxid("empresa_persona_contacte_textfield_telefon")
    val empresaPersonaContacteTextFieldEmail: TextField by fxid("empresa_persona_contacte_textfield_email")
    val empresaTutorTextFieldNom: TextField by fxid("empresa_tutor_textfield_nom")
    val empresaTutorTextFieldCarrec: TextField by fxid("empresa_tutor_textfield_carrec")
    val empresaTutorTextFieldTelefon: TextField by fxid("empresa_tutor_textfield_telefon")
    val empresaTutorTextFieldEmail: TextField by fxid("empresa_tutor_textfield_email")

    // Docent
    val docentTextFieldDni: TextField by fxid("docent_textfield_dni")
    val docentTextFieldNom: TextField by fxid("docent_textfield_nom")
    val docentTextFieldDestinacio: TextField by fxid("docent_textfield_destinacio")
    val docentTextFieldEspecialitat: TextField by fxid("docent_textfield_expecialitat")
    val docentTextFieldEmail: TextField by fxid("docent_textfield_email")
    val docentTextFieldTelefon: TextField by fxid("docent_textfield_telefon")

    // Centre
    val centreTextFieldCodi: TextField by fxid("centre_textfield_codi")
    val centreTextFieldNom: TextField by fxid("centre_textfield_nom")
    val centreTextFieldMunicipi: TextField by fxid("centre_textfield_municipi")
    val centreTextFieldResponsable: TextField by fxid("centre_textfield_responsable")
    val centreTextFieldTelefon: TextField by fxid("centre_textfield_telefon")
    val centreTextFieldEmail: TextField by fxid("centre_textfield_email")

    // SSTT
    val ssttTextFieldCodi: TextField by fxid("sstt_textfield_codi")
    val ssttTextFieldNom: TextField by fxid("sstt_textfield_nom")
    val ssttTextFieldMunicipi: TextField by fxid("sstt_textfield_municipi")
    val ssttTextFieldCoordinador: TextField by fxid("sstt_textfield_coordinador")
    val ssttTextFieldTelefon: TextField by fxid("sstt_textfield_telefon")
    val ssttTextFieldEmail: TextField by fxid("sstt_textfield_email")

    // Toolbar
    val toolbarButtonCerca: Button by fxid("button_cerca")

    // ButtonBar
    val buttonBarButtonDesa: Button by fxid("button_desa")

    init {

        with(root) {
        }

        controller.preLoadData()

        // Menu Database
        databaseMenuItemCerca.setOnAction { }
        databaseMenuItemAnalitzaPdf.setOnAction {
            val (estada, empresa) = controller.loadEmpresaAndEstadaFromPdf(docentTextFieldDni.text)
            display(estada)
            display(empresa)
        }
        databaseMenuItemTanca.setOnAction { controller.menuTanca() }

        // Menu Comunicats
        comunicatsMenuItemCorreuCentre.setOnAction { }
        comunicatsMenuItemCorreuEmpresa.setOnAction { }
        comunicatsMenuItemCartaCentre.setOnAction { }
        comunicatsMenuItemCartaEmpresa.setOnAction { }

        // Menu Eines
        einesMenuItemPreferencies.setOnAction { }
        einesMenuItemModeEdicio.setOnAction { }

        // Menu Ajuda
        ajudaMenuItemUs.setOnAction { }
        ajudaMenuItemSobreNosaltres.setOnAction { }

        // Estada
        estadaTextFieldNumeroEstada.setOnAction { }
        estadaTextFieldNumeroEstada.focusedProperty().addListener { obj, oldValue, newValue ->
        }
        estadaComboBoxTipusEstada.items.addAll("Tipus A", "Tipus B")
        // Check it should be Monday and set estadaDatePickerDataFinal to second next Friday
        estadaDatePickerDataInici.setOnAction { }
        estadaDatePickerDataFinal.setOnAction { }

        // Docent
        docentTextFieldDni.setOnAction {
            val registre: Registre? = controller.findDataByDocentId(docentTextFieldDni.text)
            display(registre?.estada)
            display(registre?.empresa)
            display(registre?.docent)
            display(registre?.centre)
            display(registre?.sstt)
            Alert(Alert.AlertType.INFORMATION, "S'ha carregat el/la docent ${registre?.docent?.nom} correctament.").show()
        }

    } // init ends

    private fun display(estada: Estada?) {
        estada?.apply {
            estadaDatePickerDataInici.value = dataInici
            estadaDatePickerDataFinal.value = dataFinal
            estadaTextFieldDescripcio.text = descripcio
            estadaTextFieldComentaris.text = codiCentre
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
            centreTextFieldResponsable.text = responsable
            centreTextFieldTelefon.text = telefon
            centreTextFieldEmail.text = email
        }
    }

    private fun display(sstt: SSTT?) {
        sstt?.run {
            ssttTextFieldCodi.text = codi
            ssttTextFieldNom.text = nom
            ssttTextFieldMunicipi.text = municipi
            ssttTextFieldCoordinador.text = coordinador
            ssttTextFieldTelefon.text = telefon
            ssttTextFieldEmail.text = email
        }
    }
}
