package cr.ac.menufragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
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
        val botonNuevo = view.findViewById<Button>(R.id.botonIngresar)
        botonNuevo.setOnClickListener {
            insertEntity()
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

    private fun insertEntity() {
        datoPruebaBusqueda = "Costa_Rica"
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val resultadoBusqueda = pageController.Buscar(datoPruebaBusqueda)
                withContext(Dispatchers.Main) {
                    Log.d("ResultadoBusqueda", resultadoBusqueda.toString())
                }
            }
        }
    }

    fun actualizarData() {
        val mainActivity = context as MainActivity
        //mainActivity.adapter.notifyDataSetChanged()
    }
}