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
    suspend fun insert(preferencias: Preferencia): Long

    @Query("SELECT * FROM Preferencia WHERE id = :id")
    suspend fun getPreferenciasById(id: Long): Preferencia?

    @Update
    suspend fun update(preferencias: Preferencia)

    @Delete
    suspend fun delete(preferencias: Preferencia)

}