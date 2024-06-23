package cr.ac.una.gimenayjeff.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cr.ac.una.gimenayjeff.clases.Movimiento
import cr.ac.una.gimenayjeff.clases.TopMovimiento

@Dao
interface MovimientoDAO {
    @Insert
    fun insert(entity: Movimiento)

    @Query("SELECT * FROM Movimiento ORDER BY id DESC")
    fun getAll(): List<Movimiento>

    @Query("""
        SELECT 
            Nombre, 
            Coordenadas, 
            FechayHora, 
            NombreWiki, 
            Imagen, 
            Descripcion, 
            URL, 
            COUNT(URL) as Count
        FROM Movimiento
        GROUP BY URL
        ORDER BY Count DESC
        LIMIT :limit
    """)
    fun getTopMovements(limit: Int): List<TopMovimiento>

    /*@Update
    fun update(entity: Movimiento)

    @Delete
    fun delete(entity: Movimiento)*/
}