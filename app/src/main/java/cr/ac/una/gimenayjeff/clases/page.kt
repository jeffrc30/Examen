package cr.ac.una.gimenayjeff.clases

import java.io.Serializable

data class page (
    var title: String,
    var thumbnail: thumbnail,
    var titles: titles,
    var extract :String
): Serializable