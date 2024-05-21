package cr.ac.una.controlfinancierocamera.clases

import java.io.Serializable

data class page (
    var title: String,
    var thumbnail: thumbnail,
    var extract :String,
    var normalizedTitle: titles
): Serializable