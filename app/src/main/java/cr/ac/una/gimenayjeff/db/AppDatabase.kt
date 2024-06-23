package cr.ac.una.gimenayjeff.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cr.ac.una.gimenayjeff.clases.Movimiento
import cr.ac.una.gimenayjeff.clases.Preferencia
import cr.ac.una.gimenayjeff.dao.MovimientoDAO
import cr.ac.una.gimenayjeff.dao.PreferenciasDAO

@Database(entities = [Movimiento::class, Preferencia::class], version = 2) // Incrementa la versión a 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun ubicacionDao(): MovimientoDAO
    abstract fun preferenciasDao(): PreferenciasDAO // Añadir PreferenciasDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return try {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "DB-Movimiento"
                ).fallbackToDestructiveMigration() // Utiliza una migración destructiva si no se maneja una migración específica
                    .build()
            } catch (ex: Exception) {
                // Manejar excepciones durante la creación de la base de datos
                throw ex
            }
        }
    }
}
