package cat.gencat.access.events

import cat.gencat.access.model.EstadaEnCurs
import cat.gencat.access.model.EstadaSearch
import tornadofx.*

class EstadaEnCursSearchEvent(val estadaSearch: EstadaEnCurs) : FXEvent()