package cr.ac.una.gimenayjeff.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cr.ac.una.gimenayjeff.clases.Preferencia

@Dao
interface PreferenciasDAO {
    @Insert
    suspend fun insert(preferencia: Preferencia): Long

    @Query("SELECT * FROM Preferencia LIMIT 1")
    suspend fun getFirstPreferencia(): Preferencia?

    @Update
    suspend fun update(preferencia: Preferencia)

}