package cr.ac.menufragment

import retrofit2.HttpException
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import cr.ac.una.controlfinanciero.adapter.MovimientoAdapter
import cr.ac.una.controlfinancierocamera.IngresarMovimientoFragment
import cr.ac.una.controlfinancierocamera.MainActivity
import cr.ac.una.controlfinancierocamera.R
import cr.ac.una.controlfinancierocamera.controller.MovimientoController
import cr.ac.una.controlfinancierocamera.controller.PageController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"


class ListControlFinancieroFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    lateinit var adapter: MovimientoAdapter
    val movimientoController = MovimientoController()
    val pageController = PageController();
    var datoPruebaBusqueda: String = ""
    private lateinit var botonBuscar: Button
    private lateinit var buscadorView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_control_financiero, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        botonBuscar = view.findViewById<Button>(R.id.botonIngresar)
        buscadorView = view.findViewById(R.id.buscadorView)

        botonBuscar.setOnClickListener {
            var textoBusqueda = buscadorView.query.toString()
            textoBusqueda = textoBusqueda.replace(" ", "_")
            Log.d("TextoBusqueda", textoBusqueda)
            insertEntity(textoBusqueda)
        }
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                movimientoController.listMovimientos()
                val list = view.findViewById<ListView>(R.id.listaMovimientos)
                adapter = MovimientoAdapter(requireContext(), movimientoController.listMovimientos())
                list.adapter = adapter
            }
        }
    }

    private fun insertEntity(textoBusqueda: String) {
        lifecycleScope.launch {
            try {
                val resultadoBusqueda = withContext(Dispatchers.IO) {
                    pageController.Buscar(textoBusqueda)
                }
                withContext(Dispatchers.Main) {
                    Log.d("ResultadoBusqueda", resultadoBusqueda.toString())
                    // Actualiza la interfaz de usuario con el resultado si es necesario
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Log.e("HTTP_ERROR", "Error: ${e.message}")
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ERROR", "Error: ${e.message}")
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun actualizarData() {
        val mainActivity = context as MainActivity
        //mainActivity.adapter.notifyDataSetChanged()
    }
}