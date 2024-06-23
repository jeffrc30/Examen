package cr.ac.una.gimenayjeff

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.lifecycle.lifecycleScope
import cr.ac.una.gimenayjeff.adapter.RecientesAdapter
import cr.ac.una.gimenayjeff.clases.Movimiento
import cr.ac.una.gimenayjeff.dao.MovimientoDAO
import cr.ac.una.gimenayjeff.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListRecientesFragment : Fragment() {
    private lateinit var movimientoDao: MovimientoDAO

    private lateinit var listView: ListView
    private lateinit var adapter: RecientesAdapter
    private lateinit var movimientos: List<Movimiento>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_recientes, container, false)

        movimientoDao = AppDatabase.getInstance(requireContext()).ubicacionDao()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView = view.findViewById<ListView>(R.id.listaMovimientos)

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val ubicaciones = withContext(Dispatchers.Default) {
                    movimientoDao.getAll() // Obtener los datos de la base de datos
                }

                // Registrar las ubicaciones en el log
                //Log.d("ListRecientesFragment", "Ubicaciones obtenidas: $ubicaciones")

                val adapter = RecientesAdapter(requireContext(), ubicaciones as List<Movimiento>, lifecycleScope)
                listView.adapter = adapter
            } catch (e: Exception) {
                // Manejar errores adecuadamente, como mostrar un mensaje de error al usuario
                Log.e("ListRecientesFragment", "Error al cargar datos desde la base de datos: ${e.message}")
            }
        }
    }
}