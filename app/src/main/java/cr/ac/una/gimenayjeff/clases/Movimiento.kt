package cr.ac.una.gimenayjeff.clases

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Movimiento (
    @PrimaryKey(autoGenerate = true) val id: Long?,
    var Coordenadas : String,
    var FechayHora : String,
    var NombreWiki : String,
    var Nombre : String,
    var Imagen : String,
    var Descripcion : String,
    var URL : String
) : Serializable