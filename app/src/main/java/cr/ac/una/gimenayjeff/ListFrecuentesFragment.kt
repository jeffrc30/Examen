package cr.ac.una.gimenayjeff

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.lifecycleScope
import cr.ac.una.gimenayjeff.adapter.FrecuentesAdapter
import cr.ac.una.gimenayjeff.dao.MovimientoDAO
import cr.ac.una.gimenayjeff.dao.PreferenciasDAO
import cr.ac.una.gimenayjeff.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListFrecuentesFragment : Fragment() {
    private lateinit var movimientoDao: MovimientoDAO
    private lateinit var preferenciasDao: PreferenciasDAO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_frecuentes, container, false)
        val appDatabase = AppDatabase.getInstance(requireContext())
        movimientoDao = appDatabase.ubicacionDao()
        preferenciasDao = appDatabase.preferenciasDao() // Inicializar el DAO de Preferencias
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView = view.findViewById<ListView>(R.id.listaFrequentes)

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                // Obtener la preferencia
                val numeroPreferido = withContext(Dispatchers.Default) {
                    val preferencia = preferenciasDao.getFirstPreferencia()
                    preferencia?.CantidadFavoritos ?: 3 // Valor predeterminado en caso de que no haya preferencia
                }

                // Obtener los movimientos top basados en la preferencia
                val topMovimientos = withContext(Dispatchers.Default) {
                    movimientoDao.getTopMovements(numeroPreferido)
                }

                Log.d("ListFrecuentesFragment", "Top movimientos obtenidos: ${topMovimientos.size}")

                val adapter = FrecuentesAdapter(requireContext(), topMovimientos, lifecycleScope)
                listView.adapter = adapter
            } catch (e: Exception) {
                Log.e("ListFrecuentesFragment", "Error al cargar datos desde la base de datos: ${e.message}")
            }
        }
    }
}
