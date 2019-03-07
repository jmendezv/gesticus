package cat.gencat.access.events

import cat.gencat.access.model.EstadaSearch
import tornadofx.*

class EstadaSearchEvent(val estadaSearch: EstadaSearch) : FXEvent()