package cr.ac.una.gimenayjeff.clases

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Preferencia (
    @PrimaryKey(autoGenerate = true) val id: Long?,
    var CantidadFavoritos : Int,
): Serializable